package ir.mehran1022.supervisory;

import ir.mehran1022.supervisory.command.SupervisoryCommand;
import ir.mehran1022.supervisory.event.PlayerCommandEvent;
import ir.mehran1022.supervisory.manager.SessionManager;
import ir.mehran1022.supervisory.storage.ConfigurationManager;
import ir.mehran1022.supervisory.storage.DataManager;
import ir.mehran1022.supervisory.storage.LogManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class Supervisory extends JavaPlugin {

    @Override
    public void onEnable() {
        initialize();
        getLogger().info("Supervisory plugin enabled.");
    }

    @Override
    public void onDisable() {
        shutdown();
        getLogger().info("Supervisory plugin disabled.");
    }

    private void initialize() {
        ConfigurationManager.initialize(this);
        DataManager.initialize(this);
        LogManager.initialize(this);

        Objects.requireNonNull(getCommand("supervisory")).setExecutor(new SupervisoryCommand());

        getServer().getPluginManager().registerEvents(new PlayerCommandEvent(), this);
    }

    private void shutdown() {
        SessionManager.cleanUpExpiredSessions();
    }
}