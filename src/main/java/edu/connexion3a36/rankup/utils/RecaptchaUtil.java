package edu.connexion3a36.rankup.utils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import org.json.JSONObject;

/**
 * Utility class for handling Google reCAPTCHA v2 verification
 */
public class RecaptchaUtil {
    private static String SECRET_KEY;
    private static String VERIFY_URL;
    private static final String RECAPTCHA_PROPERTIES = "recaptcha.properties";

    static {
        loadProperties();
    }

    /**
     * Load reCAPTCHA configuration from properties file
     */
    private static void loadProperties() {
        Properties props = new Properties();
        try (InputStream input = RecaptchaUtil.class.getClassLoader()
                .getResourceAsStream(RECAPTCHA_PROPERTIES)) {
            if (input != null) {
                props.load(input);
                SECRET_KEY = props.getProperty("recaptcha.secret.key");
                VERIFY_URL = props.getProperty("recaptcha.verify.url");
            } else {
                System.err.println("Warning: recaptcha.properties not found");
            }
        } catch (IOException e) {
            System.err.println("Error loading recaptcha properties: " + e.getMessage());
        }
    }

    /**
     * Verify reCAPTCHA token with Google servers
     * @param token The reCAPTCHA token from the client
     * @return true if verification is successful, false otherwise
     */
    public static boolean verifyToken(String token) {
        if (token == null || token.isEmpty() || SECRET_KEY == null || VERIFY_URL == null) {
            return false;
        }

        try {
            URL url = new URL(VERIFY_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            // Prepare the request body
            String postData = "secret=" + SECRET_KEY + "&response=" + token;

            try (OutputStream os = connection.getOutputStream()) {
                os.write(postData.getBytes(StandardCharsets.UTF_8));
            }

            // Read the response
            int responseCode = connection.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                System.err.println("reCAPTCHA verification failed with code: " + responseCode);
                return false;
            }

            StringBuilder response = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
            }

            // Parse JSON response
            JSONObject jsonResponse = new JSONObject(response.toString());
            boolean success = jsonResponse.getBoolean("success");
            double score = jsonResponse.optDouble("score", 0.5);

            // For reCAPTCHA v2 checkbox, just check success flag
            // For v3, you might also check the score (0.0 - 1.0, where 1.0 is very likely human)
            return success && score > 0.5;

        } catch (Exception e) {
            System.err.println("Error verifying reCAPTCHA: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Get the site key for client-side display
     * @return Site key from properties
     */
    public static String getSiteKey() {
        Properties props = new Properties();
        try (InputStream input = RecaptchaUtil.class.getClassLoader()
                .getResourceAsStream(RECAPTCHA_PROPERTIES)) {
            if (input != null) {
                props.load(input);
                return props.getProperty("recaptcha.site.key");
            }
        } catch (IOException e) {
            System.err.println("Error loading recaptcha site key: " + e.getMessage());
        }
        return "";
    }
}

