package www.ucforward.com.utils;

import org.utils.StringUtil;

import java.io.IOException;
import java.util.Properties;

/**
 * 图片前缀处理
 */
public class ImageUtil {
    private static Properties properties = new Properties();
    public static String IMG_URL_PREFIX;
    static {
        try {
            properties.load(ImageUtil.class.getResourceAsStream("/fastdfs/fastdfs.properties"));
            IMG_URL_PREFIX = StringUtil.trim(properties.get("fastdfs_host"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 处理图片路径返回值
     * @param str
     * @return
     */
    public static String imgResultUrl(String str){
        if(StringUtil.hasText(str)){
            String[] split = str.split(",");
            StringBuffer sb = new StringBuffer();
            for (String s : split) {
                if(s.startsWith(IMG_URL_PREFIX)){
                    sb.append(s);
                    continue;
                }
                sb.append(IMG_URL_PREFIX)
                        .append(s)
                        .append(",");
            }
            String splice = sb.toString();
            if(splice.endsWith(",")){
                splice = splice.substring(0,splice.length() - 1);
            }
            str = splice;
        }
        return str;
    }

}
