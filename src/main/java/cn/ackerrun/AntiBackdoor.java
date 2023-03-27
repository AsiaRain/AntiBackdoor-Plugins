package cn.ackerrun;

import cn.ackerrun.Detector.BackdoorDetector;
import cn.ackerrun.Utils.PluginUtils;
import cn.ackerrun.config.ConfigManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;


public class AntiBackdoor extends JavaPlugin implements Listener {
    private ConfigManager configManager;

    @Override
    public void onEnable() {
        this.configManager = new ConfigManager(this);
        getServer().getPluginManager().registerEvents(this, this);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPluginEnable(PluginEnableEvent event) {
        try {
            PluginDescriptionFile description = event.getPlugin().getDescription();
            String pluginName = description.getName();
            String pluginFilePath = PluginUtils.getPluginFilePath(getDataFolder(), description.getName());
            String[] authors = PluginUtils.getPluginAuthors(description);

            if (BackdoorDetector.isBackdoorPlugin(pluginName, pluginFilePath, authors, this.configManager)) {
                getLogger().warning("Detected backdoor plugin " + pluginName + ", disabling...");
                event.getPlugin().getPluginLoader().disablePlugin(event.getPlugin());
                return;
            }

            if (PluginUtils.hasClassInDefaultPackage(event.getPlugin().getClass())) {
                getLogger().warning("Plugin " + pluginName + " contains a class in the default package, disabling...");
                event.getPlugin().getPluginLoader().disablePlugin(event.getPlugin());
                return;
            }

            for (String dependentName : description.getDepend()) {
                Plugin dependentPlugin = getServer().getPluginManager().getPlugin(dependentName);

                if (dependentPlugin != null && this.configManager.isKnownBackdoorPlugin(dependentName)) {
                    getLogger().warning("Plugin " + pluginName + " depends on a known backdoor plugin " + dependentName + ", disabling...");
                    event.getPlugin().getPluginLoader().disablePlugin(event.getPlugin());
                    return;
                }
            }

            if (PluginUtils.containsSensitiveAPICall()) {
                getLogger().warning("Plugin " + pluginName + " calls sensitive API, disabling...");
                event.getPlugin().getPluginLoader().disablePlugin(event.getPlugin());
                return;
            }
        } catch (IOException e) {
            getLogger().warning("Error occurred during plugin check: " + e.getMessage());
            event.getPlugin().getPluginLoader().disablePlugin(event.getPlugin());
        }
    }
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!sender.hasPermission("antibackdoor.manage")) {
            sender.sendMessage("You do not have permission to use this command.");
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage("Usage: /antibackdoor add|remove pluginName");
            return true;
        }

        String action = args[0].toLowerCase();
        String pluginName = args[1];

        switch (action) {
            case "add":
                this.configManager.addKnownBackdoorPlugin(pluginName);
                sender.sendMessage("Added " + pluginName + " to known backdoor plugins.");
                break;
            case "remove":
                this.configManager.removeKnownBackdoorPlugin(pluginName);
                sender.sendMessage("Removed " + pluginName + " from known backdoor plugins.");
                break;
            default:
                sender.sendMessage("Invalid action. Usage: /antibackdoor add|remove pluginName");
                break;
        }

        return true;
    }
}
