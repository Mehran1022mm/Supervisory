package ir.mehran1022.supervisory.manager;

import ir.mehran1022.supervisory.storage.ConfigurationManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class SessionManager {

    private static final Map<UUID, Long> activeSessions = new ConcurrentHashMap<>();

    public static void setSession(Player player) {
        Long sessionDuration = ConfigurationManager.getSetting("verification.session-duration", Long.class);

        if (sessionDuration == null || sessionDuration <= 0) {
            sessionDuration = 600L;
            Bukkit.getLogger().warning("Invalid session-duration configuration, defaulting to 600 seconds.");
        }

        long expirationTime = System.currentTimeMillis() + (sessionDuration * 1000);

        activeSessions.put(player.getUniqueId(), expirationTime);
    }

    public static boolean isSessionValid(Player player) {
        Long expirationTime = activeSessions.get(player.getUniqueId());

        if (expirationTime == null || expirationTime <= 0) {
            return false;
        }

        return expirationTime > System.currentTimeMillis();
    }

    public static void cleanUpExpiredSessions() {
        long currentTime = System.currentTimeMillis();
        activeSessions.entrySet().removeIf(entry -> entry.getValue() <= currentTime);
    }
}
