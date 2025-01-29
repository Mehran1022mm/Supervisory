package ir.mehran1022.supervisory.storage;

import lombok.Getter;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class LogManager {

    private static File logFile;
    private static YamlConfiguration logConfiguration;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private @Getter
    static boolean enabled = false;

    public static void initialize(Plugin plugin) {
        logFile = new File(plugin.getDataFolder(), "log.yml");

        if (!logFile.exists()) {
            plugin.saveResource("log.yml", false);
        }

        logConfiguration = YamlConfiguration.loadConfiguration(logFile);

        enabled = ConfigurationManager.getSetting("logging.enabled", Boolean.class);
    }

    public static void log(String action, String details) {
        if (!enabled) return;

        String timestamp = LocalDateTime.now().format(FORMATTER);
        String logEntry = String.format("[%s] %s - %s", timestamp, action, details);

        logConfiguration.set("logs." + timestamp, logEntry);
        save();
    }

    private static void save() {
        try {
            logConfiguration.save(logFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
