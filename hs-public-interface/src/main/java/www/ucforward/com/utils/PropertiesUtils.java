package www.ucforward.com.utils;

import java.io.*;
import java.util.Properties;


/**
 * Author:wenbn
 * Date:2018/1/2
 * Description:properties工具类
 */
public class PropertiesUtils {
    private static Properties properties;
    /**
     * 加载属性文件
     * @param filePath 文件路径
     * @return
     */
    public static Properties loadProps(String filePath) {
        properties = new Properties();
        try {
//            String path = PropertiesUtils.class.getClassLoader().getResource(filePath).getPath();
//            InputStream in = new BufferedInputStream(new FileInputStream(path));
//            properties.load(in);

            InputStream ips = PropertiesUtils.class.getResourceAsStream(filePath);
            BufferedReader ipss = new BufferedReader(new InputStreamReader(ips));
            properties.load(ipss);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return properties;
    }

    /**
     * 读取配置文件
     * @param properties 配置文件
     * @param key
     * @return
     */
    public static String getString(Properties properties, String key) {
        return properties.getProperty(key);
    }
}
