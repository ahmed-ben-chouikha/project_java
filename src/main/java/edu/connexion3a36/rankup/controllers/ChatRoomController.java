package edu.connexion3a36.rankup.controllers;

import edu.connexion3a36.rankup.app.RankUpApp;
import edu.connexion3a36.rankup.ws.TeamChatClient;
import edu.connexion3a36.rankup.ws.TeamChatServerManager;
import edu.connexion3a36.services.ChatMessageService;
import edu.connexion3a36.services.TeamService;
import edu.connexion3a36.tools.MyConnection;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;

import java.net.URI;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.concurrent.TimeUnit;

public class ChatRoomController {

    @FXML private ScrollPane messagesScrollPane;
    @FXML private VBox messagesContainer;
    @FXML private TextField messageField;
    @FXML private Label chatTitle;

    private TeamChatClient client;
    private int currentUserId;
    private int teamId = -1;
    private ChatMessageService chatService;

    @FXML
    private void initialize() {
        chatService = new ChatMessageService();
        currentUserId = RankUpApp.getCurrentUserId();
        // Prefer manager-owned teams, otherwise fallback to player's own team membership.
        try {
            TeamService ts = new TeamService();
            var teams = ts.getTeamsByCreatorId(currentUserId);
            if (teams != null && teams.size() > 0) {
                teamId = teams.get(0).getId();
            } else {
                teamId = findPlayerTeamId(currentUserId);
            }
            
            // Set chat title with team name
            if (teamId > 0) {
                try {
                    TeamService teamService = new TeamService();
                    var team = teamService.getTeamById(teamId);
                    if (team != null && chatTitle != null) {
                        chatTitle.setText(team.getName() + " - Chat");
                    }
                } catch (Exception e) {
                    System.err.println("Failed to load team name: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            teamId = findPlayerTeamId(currentUserId);
        }

        // Load previous messages
        if (teamId > 0) {
            loadPreviousMessages();
        }

        try {
            TeamChatServerManager.startIfNeeded();
            connectWithRetry();
            if (teamId <= 0) {
                Label systemLabel = new Label("[system] You are not assigned to a team yet.");
                systemLabel.getStyleClass().add("system-message");
                messagesContainer.getChildren().add(systemLabel);
            }
        } catch (Exception e) {
            // log to UI
            Label errorLabel = new Label("Unable to connect to chat server: " + e.getMessage());
            errorLabel.getStyleClass().add("system-message");
            messagesContainer.getChildren().add(errorLabel);
        }
    }

    private void connectWithRetry() {
        Thread connectThread = new Thread(() -> {
            Exception lastError = null;
            for (int attempt = 1; attempt <= 8; attempt++) {
                TeamChatClient candidate = null;
                try {
                    URI uri = new URI("ws://localhost:" + TeamChatServerManager.CHAT_PORT + "/chat?teamId=" + teamId + "&userId=" + currentUserId);
                    candidate = new TeamChatClient(uri, this::onMessageReceived);
                    if (candidate.connectBlocking(2, TimeUnit.SECONDS)) {
                        client = candidate;
                        return;
                    }
                    candidate.close();
                } catch (Exception e) {
                    lastError = e;
                    if (candidate != null) {
                        try {
                            candidate.close();
                        } catch (Exception ignored) {
                        }
                    }
                }

                try {
                    Thread.sleep(350L);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }

            final String reason = lastError != null ? lastError.getMessage() : "connection timeout";
            Platform.runLater(() -> {
                Label errorLabel = new Label("[system] Unable to connect to chat server: " + reason);
                errorLabel.getStyleClass().add("system-message");
                messagesContainer.getChildren().add(errorLabel);
            });
        }, "team-chat-connect");

        connectThread.setDaemon(true);
        connectThread.start();
    }

    private int findPlayerTeamId(int userId) {
        if (userId <= 0) {
            return -1;
        }
        String sql = "SELECT team_id FROM player WHERE id = ? LIMIT 1";
        try {
            Connection cnx = MyConnection.getInstance().getCnx();
            if (cnx == null) {
                return -1;
            }
            try (PreparedStatement ps = cnx.prepareStatement(sql)) {
                ps.setInt(1, userId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next() && rs.getObject("team_id") != null) {
                        return rs.getInt("team_id");
                    }
                }
            }
        } catch (Exception ignored) {
        }
        return -1;
    }

    private void loadPreviousMessages() {
        try {
            var messages = chatService.getRecentMessagesByTeam(teamId, 50); // Load last 50 messages
            for (var msg : messages) {
                HBox messageBubble = createMessageBubble(msg.getUsername(), msg.getMessage(), msg.getCreatedAt(), msg.getUserId() == currentUserId);
                messagesContainer.getChildren().add(messageBubble);
            }
            // Scroll to bottom
            Platform.runLater(() -> messagesScrollPane.setVvalue(1.0));
        } catch (Exception e) {
            System.err.println("Failed to load previous messages: " + e.getMessage());
        }
    }

    private HBox createMessageBubble(String username, String message, String timestamp, boolean isOwnMessage) {
        HBox bubbleContainer = new HBox();
        bubbleContainer.setSpacing(8);
        bubbleContainer.setPadding(new Insets(8, 12, 8, 12));

        if (isOwnMessage) {
            // Own messages on the right
            bubbleContainer.setAlignment(Pos.CENTER_RIGHT);
            
            VBox messageBox = new VBox();
            messageBox.setAlignment(Pos.CENTER_RIGHT);
            messageBox.setSpacing(4);
            messageBox.setStyle("-fx-padding: 8 12 8 12;");

            Label usernameLabel = new Label(username);
            usernameLabel.getStyleClass().add("message-username-own");
            usernameLabel.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: #7dd3fc;");
            
            Label messageLabel = new Label(message);
            messageLabel.getStyleClass().add("own-message-bubble");
            messageLabel.setWrapText(true);
            messageLabel.setMaxWidth(300);
            messageLabel.setStyle("-fx-font-size: 13px;");

            messageBox.getChildren().addAll(usernameLabel, messageLabel);
            bubbleContainer.getChildren().add(messageBox);
        } else {
            // Other messages on the left
            bubbleContainer.setAlignment(Pos.CENTER_LEFT);

            // Avatar placeholder
            Circle avatar = new Circle(15);
            avatar.getStyleClass().add("message-avatar");
            avatar.setStyle("-fx-fill: #3b82f6;");

            VBox messageBox = new VBox();
            messageBox.setAlignment(Pos.CENTER_LEFT);
            messageBox.setSpacing(4);
            messageBox.setStyle("-fx-padding: 8 12 8 12;");

            Label usernameLabel = new Label(username);
            usernameLabel.getStyleClass().add("message-username-other");
            usernameLabel.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: #22d3ee;");
            
            Label messageLabel = new Label(message);
            messageLabel.getStyleClass().add("other-message-bubble");
            messageLabel.setWrapText(true);
            messageLabel.setMaxWidth(300);
            messageLabel.setStyle("-fx-font-size: 13px;");

            messageBox.getChildren().addAll(usernameLabel, messageLabel);
            bubbleContainer.getChildren().addAll(avatar, messageBox);
        }

        return bubbleContainer;
    }

    private String getUsernameById(int userId) {
        // Simple cache or DB lookup
        try {
            String sql = "SELECT username FROM user WHERE id = ?";
            Connection cnx = MyConnection.getInstance().getCnx();
            if (cnx != null) {
                try (PreparedStatement ps = cnx.prepareStatement(sql)) {
                    ps.setInt(1, userId);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            return rs.getString("username");
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to get username: " + e.getMessage());
        }
        return "User " + userId;
    }

    private void onMessageReceived(String msg) {
        Platform.runLater(() -> {
            // Parse message
            int colonIndex = msg.indexOf(": ");
            if (colonIndex > 0) {
                String userIdStr = msg.substring(0, colonIndex);
                String messageText = msg.substring(colonIndex + 2);

                try {
                    int userId = Integer.parseInt(userIdStr.trim());
                    String username = getUsernameById(userId);
                    HBox messageBubble = createMessageBubble(username, messageText, null, userId == currentUserId);
                    messagesContainer.getChildren().add(messageBubble);
                    // Scroll to bottom
                    messagesScrollPane.setVvalue(1.0);
                    // Save message to database
                    saveMessageToDB(msg);
                } catch (NumberFormatException e) {
                    // Fallback for system messages
                    Label systemLabel = new Label(msg);
                    systemLabel.getStyleClass().add("system-message");
                    messagesContainer.getChildren().add(systemLabel);
                }
            }
        });
    }

    private void saveMessageToDB(String msg) {
        try {
            // Parse message format: "userId: message"
            int colonIndex = msg.indexOf(": ");
            if (colonIndex > 0) {
                String userIdStr = msg.substring(0, colonIndex);
                String messageText = msg.substring(colonIndex + 2);

                try {
                    int userId = Integer.parseInt(userIdStr.trim());
                    var chatMessage = new edu.connexion3a36.entities.ChatMessage(teamId, userId, messageText);
                    chatService.saveMessage(chatMessage);
                } catch (NumberFormatException e) {
                    System.err.println("Invalid user ID in message: " + msg);
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to save message to DB: " + e.getMessage());
        }
    }

    @FXML
    private void sendMessage() {
        String text = messageField.getText();
        if (text == null || text.isBlank()) return;

        String username = getUsernameById(currentUserId);
        HBox messageBubble = createMessageBubble(username, text, null, true);
        messagesContainer.getChildren().add(messageBubble);
        messagesScrollPane.setVvalue(1.0);

        String payload = currentUserId + ": " + text;
        if (client != null && client.isOpen()) {
            client.send(payload);
            // Message will be saved when received via WebSocket
        } else {
            // Save locally when not connected
            saveMessageToDB(payload);
        }
        messageField.clear();
    }

    public void shutdown() {
        try { if (client != null) client.close(); } catch (Exception ignored) {}
    }
}
