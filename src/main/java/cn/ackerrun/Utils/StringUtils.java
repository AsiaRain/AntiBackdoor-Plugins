package cn.ackerrun.Utils;

/***********************
 *   @Author: Rain
 *   @Date: 2023/3/27
 * **********************
 */
public class StringUtils {

   private StringUtils() {
   }

   public static boolean isNullOrEmpty(String str) {
      return str == null || str.isEmpty();
   }

   public static String getFileExtension(String fileName) {
      int dotIndex = fileName.lastIndexOf('.');
      if (dotIndex < 0) {
         return "";
      } else {
         return fileName.substring(dotIndex + 1);
      }
   }

}
