package www.ucforward.com.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

/**
 * 系统编号生成
 * @author wenbn
 * @version 1.0
 * @date 2018/5/24
 */
public class SystemNumberUtil {

    //生成唯一编号
    public static String createCode(String prefix){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        StringBuffer sb = new StringBuffer();
        sb.append(prefix);
        sb.append(dateFormat.format(new Date()));
        sb.append(new Random().nextInt(1000));
        return sb.toString();
    }
}
