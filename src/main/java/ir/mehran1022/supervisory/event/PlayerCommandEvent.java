package ir.mehran1022.supervisory.event;

import ir.mehran1022.supervisory.manager.SessionManager;
import ir.mehran1022.supervisory.storage.ConfigurationManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.List;

public final class PlayerCommandEvent implements Listener {

    @EventHandler
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        String command = event.getMessage().split(" ")[0].substring(1).toLowerCase();

        List<String> restrictedCommands = (List<String>) ConfigurationManager.getSetting("restricted-commands", List.class);

        if (restrictedCommands.contains(command)) {
            if (!SessionManager.isSessionValid(player)) {
                event.setCancelled(true);
                player.sendMessage(ChatColor.RED + "[Supervisory] You can't execute this command.");
            }
        }
    }
}
