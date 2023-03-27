package cn.ackerrun.Utils;

import org.bukkit.plugin.PluginDescriptionFile;

import java.io.File;
import java.util.List;

/***********************
 *   @Author: Rain
 *   @Date: 2023/3/27
 * **********************
 */
public class PluginUtils {

   public static String getPluginFilePath(File dataFolder, String pluginName) {
      File pluginFile = new File("plugins/" + pluginName + ".jar");
      return pluginFile.getAbsolutePath();
   }

   public static String[] getPluginAuthors(PluginDescriptionFile description) {
      List<String> authors = description.getAuthors();
      return authors.toArray(new String[0]);
   }

   public static boolean hasClassInDefaultPackage(Class<?> clazz) {
      return clazz.getPackage() == null;
   }

   public static boolean containsSensitiveAPICall() {
      for (StackTraceElement element : Thread.currentThread().getStackTrace()) {
         String className = element.getClassName();
         String methodName = element.getMethodName();

         if (className.equals("java.lang.ClassLoader") && methodName.equals("loadClass")) {
            return true;
         } else if (className.startsWith("org.reflections.")) {
            return true;
         }
      }

      return false;
   }
}
