package cn.ackerrun.Detector;

import java.util.Arrays;
import java.util.List;

/***********************
 *   @Author: Rain
 *   @Date: 2023/3/27
 * **********************
 */
public class BackdoorDetector {

   private static final List<String> BLACKLISTED_PLUGIN_NAMES = Arrays.asList(
           "BackdoorPlugin", "EvilPlugin", "HackerPlugin"
   );

   private static final List<String> BLACKLISTED_AUTHORS = Arrays.asList(
           "hacker123", "evilguy", "backdoorinc"
   );

   private BackdoorDetector() {
   }

   public static boolean isBackdoorPlugin(String pluginName, String fileName, String[] authors) {
      return isInBlacklist(pluginName)
              || containsKeyword(fileName, "backdoor")
              || containsKeyword(fileName, "hack")
              || containsAnyKeyword(authors, BLACKLISTED_AUTHORS);
   }

   private static boolean isInBlacklist(String pluginName) {
      return BLACKLISTED_PLUGIN_NAMES.contains(pluginName);
   }

   private static boolean containsKeyword(String string, String keyword) {
      return string.toLowerCase().contains(keyword.toLowerCase());
   }

   private static boolean containsAnyKeyword(String[] strings, List<String> keywords) {
      for (String string : strings) {
         if (containsAnyKeyword(string, keywords)) {
            return true;
         }
      }
      return false;
   }

   private static boolean containsAnyKeyword(String string, List<String> keywords) {
      for (String keyword : keywords) {
         if (containsKeyword(string, keyword)) {
            return true;
         }
      }
      return false;
   }

}
