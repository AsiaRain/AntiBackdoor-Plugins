package cn.ackerrun.config;

/***********************
 *   @Author: Rain
 *   @Date: 2023/3/27
 * **********************
 */
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.Set;

public class ConfigManager {
   private final JavaPlugin plugin;
   private FileConfiguration config;

   private Set<String> knownBackdoorPlugins;

   public ConfigManager(JavaPlugin plugin) {
      this.plugin = plugin;

      this.reloadConfig();
   }

   public void reloadConfig() {
      this.plugin.saveDefaultConfig();
      this.config = this.plugin.getConfig();

      this.knownBackdoorPlugins = new HashSet<>(this.config.getStringList("known-backdoor-plugins"));
   }

   public boolean isKnownBackdoorPlugin(String pluginName) {
      return this.knownBackdoorPlugins.contains(pluginName);
   }

   public void addKnownBackdoorPlugin(String pluginName) {
      if (!this.knownBackdoorPlugins.contains(pluginName)) {
         this.knownBackdoorPlugins.add(pluginName);
         this.config.set("known-backdoor-plugins", new java.util.ArrayList<>(this.knownBackdoorPlugins));
         this.plugin.saveConfig();
      }
   }

   public void removeKnownBackdoorPlugin(String pluginName) {
      if (this.knownBackdoorPlugins.contains(pluginName)) {
         this.knownBackdoorPlugins.remove(pluginName);
         this.config.set("known-backdoor-plugins", new java.util.ArrayList<>(this.knownBackdoorPlugins));
         this.plugin.saveConfig();
      }
   }
}
