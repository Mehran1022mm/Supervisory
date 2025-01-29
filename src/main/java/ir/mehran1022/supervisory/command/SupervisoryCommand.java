package ir.mehran1022.supervisory.command;

import ir.mehran1022.supervisory.manager.SessionManager;
import ir.mehran1022.supervisory.storage.ConfigurationManager;
import ir.mehran1022.supervisory.storage.DataManager;
import ir.mehran1022.supervisory.storage.LogManager;
import ir.mehran1022.supervisory.utility.CharacterUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.List;

public final class SupervisoryCommand implements TabExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (args.length < 1) {
            sender.sendMessage(ChatColor.RED + "[Supervisory] Incorrect usage. (/Supervisory <subcommand>)");
            return true;
        }

        final int codeLength = ConfigurationManager.getSetting("verification.code-length", Integer.class);
        final long codeExpiry = ConfigurationManager.getSetting("verification.code-expiry", Long.class);

        switch (args[0].toLowerCase()) {
            case "generate":
                handleGenerate(sender, codeLength, codeExpiry);
                break;
            case "pass":
                handlePass(sender, args);
                break;
            case "reload":
                if (sender.isOp()) {
                    handleReload(sender);
                } else {
                    sender.sendMessage(ChatColor.RED + "[Supervisory] You do not have permission to reload the configuration.");
                }
                break;
            default:
                sender.sendMessage(ChatColor.RED + "[Supervisory] Invalid subcommand.");
                break;
        }
        return true;
    }

    private void handleGenerate(CommandSender sender, int codeLength, long codeExpiry) {
        if (!(sender instanceof ConsoleCommandSender)) {
            sender.sendMessage(ChatColor.RED + "[Supervisory] This command can only be run by console.");
            return;
        }

        String code = CharacterUtils.generateRandom(codeLength);
        long expiryTime = System.currentTimeMillis() + (codeExpiry * 1000);
        DataManager.addCode(code, expiryTime);

        log("Code Generated", "Code: " + code + ", Expiry: " + codeExpiry + " seconds.");
        sender.sendMessage(ChatColor.GREEN + String.format("[Supervisory] New OTP generated: %s. Please do not share this code with anyone.", code));
    }

    private void handlePass(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "[Supervisory] This command can only be run by a player.");
            return;
        }

        if (args.length != 2) {
            sender.sendMessage(ChatColor.RED + "[Supervisory] Incorrect usage. Please provide the code. (/Supervisory pass <code>)");
            return;
        }

        String code = args[1];

        if (DataManager.isCodeActive(code)) {
            long remainingTime = DataManager.getRemainingTime(code);
            SessionManager.setSession((Player) sender);
            DataManager.removeCode(code);

            log("Code Verified", "Code: " + code + " used by: " + sender.getName());
            sender.sendMessage(ChatColor.GREEN + String.format("[Supervisory] You've been granted access for a limited amount of time. Remaining time: %s seconds.", remainingTime));
        } else if (ConfigurationManager.getSetting("backup-key", String.class).equals(code)) {
            SessionManager.setSession((Player) sender);
            sender.sendMessage(ChatColor.GREEN + "[Supervisory] You've been granted access using the backup key.");
        } else {
            log("Failed Verification", "Invalid/Expired Code Attempt by: " + sender.getName() + ", Code: " + code);
            sender.sendMessage(ChatColor.RED + "[Supervisory] The code is either invalid or expired.");
        }
    }

    private void handleReload(CommandSender sender) {
        try {
            ConfigurationManager.initialize(sender.getServer().getPluginManager().getPlugin("Supervisory"));
            LogManager.initialize(sender.getServer().getPluginManager().getPlugin("Supervisory"));
            sender.sendMessage(ChatColor.GREEN + "[Supervisory] Configuration reloaded successfully.");
        } catch (Exception e) {
            sender.sendMessage(ChatColor.RED + "[Supervisory] Failed to reload the configuration.");
            e.printStackTrace();
        }
    }

    private void log(String action, String details) {
        if (LogManager.isEnabled()) {
            LogManager.log(action, details);
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String s, String[] args) {
        if (args.length == 1) {
            return List.of("pass", "generate", "reload");
        }
        return List.of();
    }
}
