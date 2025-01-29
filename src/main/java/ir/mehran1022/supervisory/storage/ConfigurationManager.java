package ir.mehran1022.supervisory.storage;

import lombok.Getter;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;

public final class ConfigurationManager {

    private @Getter
    static YamlConfiguration configuration;

    public static void initialize(Plugin plugin) {
        File settingsFile = new File(plugin.getDataFolder(), "settings.yml");

        if (!settingsFile.exists()) {
            plugin.saveResource("settings.yml", false);
        }

        configuration = YamlConfiguration.loadConfiguration(settingsFile);
    }

    public static <T> T getSetting(String path, Class<T> type) {
        Object value = configuration.get(path);

        if (value == null) {
            return null;
        }

        if (type.equals(Long.class) && value instanceof Number) {
            return type.cast(((Number) value).longValue());
        } else if (type.equals(Integer.class) && value instanceof Number) {
            return type.cast(((Number) value).intValue());
        } else if (type.equals(String.class)) {
            return type.cast(value.toString());
        }

        return type.cast(value);
    }
}
