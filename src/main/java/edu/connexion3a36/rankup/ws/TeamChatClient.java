package edu.connexion3a36.rankup.ws;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.function.Consumer;

public class TeamChatClient extends WebSocketClient {

    private final Consumer<String> messageHandler;

    public TeamChatClient(URI serverUri, Consumer<String> messageHandler) {
        super(serverUri);
        this.messageHandler = messageHandler;
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        if (messageHandler != null) messageHandler.accept("[system] Connected to chat server");
    }

    @Override
    public void onMessage(String message) {
        if (messageHandler != null) messageHandler.accept(message);
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        if (messageHandler != null) messageHandler.accept("[system] Disconnected: " + reason);
    }

    @Override
    public void onError(Exception ex) {
        if (messageHandler != null) messageHandler.accept("[system] Error: " + ex.getMessage());
    }
}
