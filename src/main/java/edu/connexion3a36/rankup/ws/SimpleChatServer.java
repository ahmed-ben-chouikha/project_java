package edu.connexion3a36.rankup.ws;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class SimpleChatServer extends WebSocketServer {

    // Map connection -> teamId
    private final Map<WebSocket, Integer> teamByConn = Collections.synchronizedMap(new HashMap<>());

    public SimpleChatServer(int port) {
        super(new InetSocketAddress(port));
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        try {
            String query = handshake.getResourceDescriptor(); // e.g. /chat?teamId=1&userId=2
            int teamId = parseTeamId(query);
            teamByConn.put(conn, teamId);
            conn.send("[system] Welcome to team " + teamId + " chat");
        } catch (Exception e) {
            conn.send("[system] Welcome");
        }
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        teamByConn.remove(conn);
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        Integer teamId = teamByConn.get(conn);
        if (teamId == null) teamId = -1;
        synchronized (teamByConn) {
            for (Map.Entry<WebSocket, Integer> e : teamByConn.entrySet()) {
                if (e.getValue().equals(teamId)) {
                    try { e.getKey().send(message); } catch (Exception ignored) {}
                }
            }
        }
    }

    @Override
    public void onError(WebSocket conn, Exception ex) { }

    @Override
    public void onStart() { }

    private int parseTeamId(String resourceDesc) {
        try {
            // resourceDesc like: /chat?teamId=1&userId=2
            URI uri = new URI("ws://localhost" + resourceDesc);
            String q = uri.getQuery();
            if (q == null) return -1;
            for (String part : q.split("&")) {
                if (part.startsWith("teamId=")) return Integer.parseInt(part.substring("teamId=".length()));
            }
        } catch (Exception ignored) {}
        return -1;
    }

    // Convenience main to run server locally
    public static void main(String[] args) {
        SimpleChatServer server = new SimpleChatServer(TeamChatServerManager.CHAT_PORT);
        server.start();
        System.out.println("SimpleChatServer started on port " + TeamChatServerManager.CHAT_PORT);
    }
}
