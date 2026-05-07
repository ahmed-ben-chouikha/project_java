package edu.connexion3a36.rankup.api;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;

/**
 * Embedded HTTP server that exposes the token-based authentication API.
 *
 * Endpoints (all relative to http://localhost:8080/api):
 *   POST /auth/login           – authenticate and receive a JWT token
 *   POST /auth/register        – create a new account
 *   GET  /auth/validate        – validate a JWT token (?token=...)
 *   POST /auth/forgot-password – request an OTP
 *   POST /auth/verify-otp      – verify the OTP
 *   POST /auth/reset-password  – reset password after OTP verification
 *
 * Start the server by calling ApiServer.start() from MainFxApp.
 * The server runs on a daemon thread so it shuts down automatically
 * when the JavaFX application exits.
 */
public class ApiServer {

    private static final int PORT = 8080;
    private static HttpServer server;

    /** Start the API server. Safe to call multiple times – only starts once. */
    public static synchronized void start() {
        if (server != null) return;
        try {
            server = HttpServer.create(new InetSocketAddress(PORT), 0);
            server.setExecutor(Executors.newCachedThreadPool());

            AuthApiController controller = new AuthApiController();

            server.createContext("/api/auth/login",           new LoginHandler(controller));
            server.createContext("/api/auth/register",        new RegisterHandler(controller));
            server.createContext("/api/auth/validate",        new ValidateHandler(controller));
            server.createContext("/api/auth/forgot-password", new ForgotPasswordHandler(controller));
            server.createContext("/api/auth/verify-otp",      new VerifyOtpHandler(controller));
            server.createContext("/api/auth/reset-password",  new ResetPasswordHandler(controller));

            server.start();
            System.out.println("[API] Server started on http://localhost:" + PORT + "/api");
        } catch (IOException e) {
            System.err.println("[API] Failed to start server: " + e.getMessage());
        }
    }

    /** Stop the API server gracefully. */
    public static synchronized void stop() {
        if (server != null) {
            server.stop(1);
            server = null;
            System.out.println("[API] Server stopped.");
        }
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    /** Read the full request body as a UTF-8 string. */
    private static String readBody(HttpExchange exchange) throws IOException {
        try (InputStream is = exchange.getRequestBody()) {
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    /** Send a JSON response. */
    private static void sendJson(HttpExchange exchange, int statusCode, String json) throws IOException {
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.sendResponseHeaders(statusCode, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    /** Convert a Map to a simple JSON string (no external library needed). */
    static String toJson(Map<String, Object> map) {
        StringBuilder sb = new StringBuilder("{");
        boolean first = true;
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (!first) sb.append(",");
            first = false;
            sb.append("\"").append(escape(entry.getKey())).append("\":");
            appendValue(sb, entry.getValue());
        }
        sb.append("}");
        return sb.toString();
    }

    @SuppressWarnings("unchecked")
    private static void appendValue(StringBuilder sb, Object value) {
        if (value == null) {
            sb.append("null");
        } else if (value instanceof Boolean || value instanceof Number) {
            sb.append(value);
        } else if (value instanceof Map) {
            sb.append(toJson((Map<String, Object>) value));
        } else {
            sb.append("\"").append(escape(value.toString())).append("\"");
        }
    }

    private static String escape(String s) {
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    /**
     * Very simple JSON body parser.
     * Handles flat objects with string/number values — sufficient for auth payloads.
     */
    static Map<String, String> parseJsonBody(String body) {
        Map<String, String> result = new HashMap<>();
        if (body == null || body.isBlank()) return result;
        // Strip outer braces
        body = body.trim();
        if (body.startsWith("{")) body = body.substring(1);
        if (body.endsWith("}")) body = body.substring(0, body.length() - 1);

        // Split on commas that are not inside strings (simplified – works for flat objects)
        String[] pairs = body.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
        for (String pair : pairs) {
            String[] kv = pair.split(":", 2);
            if (kv.length == 2) {
                String key   = kv[0].trim().replaceAll("^\"|\"$", "");
                String value = kv[1].trim().replaceAll("^\"|\"$", "");
                result.put(key, value);
            }
        }
        return result;
    }

    // -------------------------------------------------------------------------
    // Handlers
    // -------------------------------------------------------------------------

    private static class LoginHandler implements HttpHandler {
        private final AuthApiController controller;
        LoginHandler(AuthApiController c) { this.controller = c; }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                sendJson(exchange, 405, "{\"error\":\"Method Not Allowed\"}");
                return;
            }
            Map<String, String> body = parseJsonBody(readBody(exchange));
            Map<String, Object> response = controller.login(body.get("email"), body.get("password"));
            int status = response.containsKey("code") ? ((Number) response.get("code")).intValue() : 200;
            sendJson(exchange, status, toJson(response));
        }
    }

    private static class RegisterHandler implements HttpHandler {
        private final AuthApiController controller;
        RegisterHandler(AuthApiController c) { this.controller = c; }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                sendJson(exchange, 405, "{\"error\":\"Method Not Allowed\"}");
                return;
            }
            Map<String, String> body = parseJsonBody(readBody(exchange));
            Map<String, Object> response = controller.register(
                    body.get("email"), body.get("password"), body.get("username"));
            int status = response.containsKey("code") ? ((Number) response.get("code")).intValue() : 201;
            sendJson(exchange, status, toJson(response));
        }
    }

    private static class ValidateHandler implements HttpHandler {
        private final AuthApiController controller;
        ValidateHandler(AuthApiController c) { this.controller = c; }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                sendJson(exchange, 405, "{\"error\":\"Method Not Allowed\"}");
                return;
            }
            // Accept token from query string (?token=...) or Authorization header
            String token = null;
            String query = exchange.getRequestURI().getQuery();
            if (query != null && query.startsWith("token=")) {
                token = query.substring(6);
            }
            String authHeader = exchange.getRequestHeaders().getFirst("Authorization");
            if (token == null && authHeader != null && authHeader.startsWith("Bearer ")) {
                token = authHeader.substring(7);
            }
            Map<String, Object> response = controller.validateToken(token);
            int status = response.containsKey("code") ? ((Number) response.get("code")).intValue() : 200;
            sendJson(exchange, status, toJson(response));
        }
    }

    private static class ForgotPasswordHandler implements HttpHandler {
        private final AuthApiController controller;
        ForgotPasswordHandler(AuthApiController c) { this.controller = c; }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                sendJson(exchange, 405, "{\"error\":\"Method Not Allowed\"}");
                return;
            }
            Map<String, String> body = parseJsonBody(readBody(exchange));
            Map<String, Object> response = controller.forgotPassword(body.get("email"));
            int status = response.containsKey("code") ? ((Number) response.get("code")).intValue() : 200;
            sendJson(exchange, status, toJson(response));
        }
    }

    private static class VerifyOtpHandler implements HttpHandler {
        private final AuthApiController controller;
        VerifyOtpHandler(AuthApiController c) { this.controller = c; }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                sendJson(exchange, 405, "{\"error\":\"Method Not Allowed\"}");
                return;
            }
            Map<String, String> body = parseJsonBody(readBody(exchange));
            Map<String, Object> response = controller.verifyOtp(body.get("email"), body.get("otp"));
            int status = response.containsKey("code") ? ((Number) response.get("code")).intValue() : 200;
            sendJson(exchange, status, toJson(response));
        }
    }

    private static class ResetPasswordHandler implements HttpHandler {
        private final AuthApiController controller;
        ResetPasswordHandler(AuthApiController c) { this.controller = c; }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                sendJson(exchange, 405, "{\"error\":\"Method Not Allowed\"}");
                return;
            }
            Map<String, String> body = parseJsonBody(readBody(exchange));
            Map<String, Object> response = controller.resetPassword(
                    body.get("email"), body.get("newPassword"));
            int status = response.containsKey("code") ? ((Number) response.get("code")).intValue() : 200;
            sendJson(exchange, status, toJson(response));
        }
    }
}
