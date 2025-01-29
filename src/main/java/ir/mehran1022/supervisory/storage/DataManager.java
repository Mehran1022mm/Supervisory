package ir.mehran1022.supervisory.storage;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public final class DataManager {

    private static File dataFile;
    private static YamlConfiguration dataConfiguration;
    private static final Map<String, Long> activeCodes = new HashMap<>();
    private static DateTimeFormatter formatter;
    private static ZoneId timezone;

    public static void initialize(Plugin plugin) {
        dataFile = new File(plugin.getDataFolder(), "data.yml");

        if (!dataFile.exists()) {
            plugin.saveResource("data.yml", false);
        }

        dataConfiguration = YamlConfiguration.loadConfiguration(dataFile);
        loadTimezone();
        loadActiveCodes();
    }

    private static void loadTimezone() {
        String timezoneString = ConfigurationManager.getSetting("timezone", String.class);
        if (timezoneString != null) {
            timezone = ZoneId.of(timezoneString);
        } else {
            timezone = ZoneId.of("UTC");
        }

        formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(timezone);
    }

    private static void loadActiveCodes() {
        if (dataConfiguration.contains("active-codes")) {
            for (String code : dataConfiguration.getConfigurationSection("active-codes").getKeys(false)) {
                String expirationStr = dataConfiguration.getString(String.format("active-codes.%s.expires-at", code));
                if (expirationStr != null) {
                    LocalDateTime expirationTime = LocalDateTime.parse(expirationStr, formatter);
                    long expirationMillis = expirationTime.atZone(timezone).toInstant().toEpochMilli();
                    activeCodes.put(code, expirationMillis);
                }
            }
        }
    }

    public static void saveActiveCodes() {
        dataConfiguration.set("active-codes", null);

        for (Map.Entry<String, Long> entry : activeCodes.entrySet()) {
            String formattedExpiration = LocalDateTime.ofInstant(Instant.ofEpochMilli(entry.getValue()), timezone)
                    .format(formatter);
            dataConfiguration.set(String.format("active-codes.%s.expires-at", entry.getKey()), formattedExpiration);
        }

        try {
            dataConfiguration.save(dataFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static long getRemainingTime(String code) {
        if (isCodeActive(code)) {
            long expirationTime = activeCodes.get(code);
            return (expirationTime - System.currentTimeMillis()) / 1000;
        }
        return 0;
    }

    public static boolean isCodeActive(String code) {
        return activeCodes.get(code) != null && activeCodes.get(code) > System.currentTimeMillis();
    }

    public static void addCode(String code, long expiryTime) {
        activeCodes.put(code, expiryTime);
        saveActiveCodes();
    }

    public static void removeCode(String code) {
        activeCodes.remove(code);
        saveActiveCodes();
    }
}