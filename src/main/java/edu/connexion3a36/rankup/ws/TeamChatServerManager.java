package edu.connexion3a36.rankup.ws;

import java.util.concurrent.atomic.AtomicBoolean;

public final class TeamChatServerManager {

    public static final int CHAT_PORT = 8090;
    private static final AtomicBoolean STARTED = new AtomicBoolean(false);
    private static SimpleChatServer server;

    private TeamChatServerManager() {
    }

    public static synchronized void startIfNeeded() {
        if (STARTED.get()) {
            return;
        }
        try {
            server = new SimpleChatServer(CHAT_PORT);
            server.setReuseAddr(true);
            server.start();
            STARTED.set(true);
        } catch (Exception ignored) {
            // If binding fails, assume another server instance is already running.
        }
    }
}
