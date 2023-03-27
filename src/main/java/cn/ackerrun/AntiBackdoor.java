package cn.ackerrun;

import cn.ackerrun.Detector.BackdoorDetector;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;
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

        // 检查插件是否为后门插件
        try {
            if (BackdoorDetector.isBackdoorPlugin(pluginName, pluginFilePath, authors)) {
                getLogger().warning("Detected backdoor plugin " + pluginName + ", disabling...");
                event.getPlugin().getPluginLoader().disablePlugin(event.getPlugin());
                return;
            }
        } catch (IOException e) {
            getLogger().warning("Error reading plugin file: " + pluginFilePath);
            e.printStackTrace();
            return;
        }

        // 检查插件的类是否位于默认包中
        for (Class<?> clazz : event.getPlugin().getClass().getClasses()) {
            if (clazz.getPackage() == null) {
                getLogger().warning("Plugin " + pluginName + " contains a class in the default package, disabling...");
                event.getPlugin().getPluginLoader().disablePlugin(event.getPlugin());
                return;
            }
        }

        // 检查插件是否依赖其他插件
        for (String dependentName : description.getDepend()) {
            Plugin dependentPlugin = getServer().getPluginManager().getPlugin(dependentName);

            if (dependentPlugin != null) {
                PluginDescriptionFile dependentDescription = dependentPlugin.getDescription();

                // 检查依赖的插件是否为已知的后门插件
                if (BackdoorDetector.isKnownBackdoorPlugin(dependentName)) {
                    getLogger().warning("Plugin " + pluginName + " depends on a known backdoor plugin " + dependentName + ", disabling...");
                    event.getPlugin().getPluginLoader().disablePlugin(event.getPlugin());
                    return;
                }
            }
        }

        // 检查插件是否包含敏感API调用
        for (StackTraceElement element : Thread.currentThread().getStackTrace()) {
            String className = element.getClassName();
            String methodName = element.getMethodName();


            if (className.equals("java.lang.ClassLoader") && methodName.equals("loadClass")) {
                getLogger().warning("Plugin " + pluginName + " calls sensitive API java.lang.ClassLoader.loadClass, disabling...");
                event.getPlugin().getPluginLoader().disablePlugin(event.getPlugin());
                return;
            } else if (className.startsWith("org.reflections.")) {
                getLogger().warning("Plugin " + pluginName + " uses the reflections library, disabling...");
                event.getPlugin().getPluginLoader().disablePlugin(event.getPlugin());
                return;
            }
        }
    }

}
