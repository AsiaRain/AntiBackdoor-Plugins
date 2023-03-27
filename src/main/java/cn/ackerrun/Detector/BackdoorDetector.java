package cn.ackerrun.Detector;

import cn.ackerrun.config.ConfigManager;

import javax.xml.bind.DatatypeConverter;
import java.util.*;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
/***********************
 *   @Author: Rain
 *   @Date: 2023/3/27
 * **********************
 */
public class BackdoorDetector {

   private static final Set<String> KNOWN_BACKDOOR_PLUGINS = new HashSet<>();
   private static final Pattern CLASS_NAME_PATTERN = Pattern.compile("^[a-zA-Z][a-zA-Z0-9_]*(\\$[a-zA-Z][a-zA-Z0-9_]*)*$");

/*   static {
      // 添加已知的后门插件名称
      KNOWN_BACKDOOR_PLUGINS.add("EvilPlugin");
   }*/

   /**
    * 检查指定的插件是否为后门插件。
    *
    * @param pluginName 插件名称
    * @param filePath   插件文件路径
    * @param authors    插件作者列表
    * @return true 如果该插件被检测到为后门插件；false 反之。
    * @throws IOException 如果在读取文件时出现错误。
    */
   public static boolean isBackdoorPlugin(String pluginName, String filePath, String[] authors, ConfigManager configManager) throws IOException {
      if (configManager.isKnownBackdoorPlugin(pluginName)) {
         return true;
      }

      ZipFile zip = new ZipFile(filePath);
      Set<String> classNames = new HashSet<>();

      try {
         for (ZipEntry entry : Collections.list(zip.entries())) {
            String name = entry.getName();

            // 如果不是.class文件，则跳过
            if (!name.endsWith(".class")) {
               continue;
            }

            // 获取类名，并进行格式验证
            String className = name.replace('/', '.').substring(0, name.length() - 6);
            if (!CLASS_NAME_PATTERN.matcher(className).matches()) {
               continue;
            }

            classNames.add(className);
         }
      } finally {
         zip.close();
      }

      // 检查作者是否是匿名
      for (String author : authors) {
         if (author.equalsIgnoreCase("anonymous")) {
            return true;
         }
      }

      // 检查是否包含敏感类或代码
      for (String className : classNames) {
         if (className.contains("$") || className.startsWith("org.bukkit.plugin.java.") || className.startsWith("net.md_5.")) {
            continue;
         }

         try {
            Class<?> clazz = Class.forName(className);

            if (clazz.isAnnotationPresent(Deprecated.class) ||
                    clazz.getName().contains("sun.reflect.")) {
               return true;
            }

            if (clazz.getPackage() == null) {
               return true;
            }

            // 检查字节码是否包含特定的字节数组
            byte[] bytes = Files.readAllBytes(Paths.get(filePath));
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(bytes);
            if (containsMagicCode(digest)) {
               return true;
            }
         } catch (ClassNotFoundException e) {
            // 如果无法加载类，直接跳过
         } catch (Exception e) {
            e.printStackTrace();
         }
      }

      return false;
   }

   /**
    * 判断指定的插件名称是否为已知的后门插件。
    *
    * @param pluginName 插件名称
    * @return true 如果该插件名称为后门插件；false 反之。
    */
   public static boolean isKnownBackdoorPlugin(String pluginName) {
      return KNOWN_BACKDOOR_PLUGINS.contains(pluginName);
   }

   /**
    * 判断指定的字节数组是否包含特定的字节数组。
    *
    * @param bytes 要检查的字节数组
    * @return true 如果该字节数组包含特定的字节数组；false 反之。
    */
   private static boolean containsMagicCode(byte[] bytes) {
      String hex = DatatypeConverter.printHexBinary(bytes);
      return hex.contains("4A6F794D63212121") || hex.contains("4E65726F707820");
   }

}
