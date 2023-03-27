package cn.ackerrun;

import cn.ackerrun.Detector.BackdoorDetector;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;


public class AntiBackdoor extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPluginEnable(PluginEnableEvent event) {
        PluginDescriptionFile description = event.getPlugin().getDescription();
        String pluginName = description.getName();
        File pluginFile = new File(getDataFolder(), description.getName() + ".jar");
        String pluginFilePath = pluginFile.getAbsolutePath();
        String[] authors = description.getAuthors().toArray(new String[0]);


        if (BackdoorDetector.isBackdoorPlugin(pluginName, pluginFilePath, authors)) {
            getLogger().warning("Detected backdoor plugin " + pluginName + ", disabling...");
            event.getPlugin().getPluginLoader().disablePlugin(event.getPlugin());
        }
    }

}
