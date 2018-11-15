package www.ucforward.com.utils;


import org.apache.commons.lang.StringUtils;
import org.utils.DateUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.UUID;

/**
 * @author wenbn
 * @version 1.0
 * @date 2018/6/6
 */
public class RandomUtils {

    public static final String NUMBERS_AND_LETTERS = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    public static final String NUMBERS             = "0123456789";
    public static final String LETTERS             = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    public static final String CAPITAL_LETTERS     = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    public static final String LOWER_CASE_LETTERS  = "abcdefghijklmnopqrstuvwxyz";

    private RandomUtils() {
        throw new AssertionError();
    }


    /**
     * 获取UUID
     * @return
     */
    public static String getUUID(){
        return UUID.randomUUID().toString().replace("-","");
    }

    /**
     * 得到一个固定长度的随机字符串，它是大写、小写字母和数字的混合
     * @param length 长度
     * @return
     */
    public static String getRandomNumbersAndLetters(int length) {
        return getRandom(NUMBERS_AND_LETTERS, length);
    }

    /**
     * 得到一个固定长度的随机字符串，它是数字的混合
     * @param length  length
     * @return
     */
    public static String getRandomNumbers(int length) {
        return getRandom(NUMBERS, length);
    }

    /**
     * 得到一个固定长度的随机字符串，它是大写字母和小写字母的混合
     * @param length  length
     * @return
     */
    public static String getRandomLetters(int length) {
        return getRandom(LETTERS, length);
    }

    /**
     * 得到一个固定长度的随机字符串，它是大写字母的混合物
     * @param length  length
     * @return
     */
    public static String getRandomCapitalLetters(int length) {
        return getRandom(CAPITAL_LETTERS, length);
    }


    /**
     * 得到一个4长度的随机大写字母+yyyyMMddHHmmss+3位随机数
     * @return
     */
    public static String getRandomCode() {
        Date date = new Date();
        SimpleDateFormat simpledateformat = new SimpleDateFormat("yyyyMMddHHmmss");
        return getRandom(CAPITAL_LETTERS, 4)+simpledateformat.format(date)+ new Random().nextInt(1000);
    }

    /**
     * 得到一个固定长度的随机字符串，它是小写字母的混合物
     * @param length  length
     * @return  get a fixed-length random string, its a mixture of lowercase letters
     */
    public static String getRandomLowerCaseLetters(int length) {
        return getRandom(LOWER_CASE_LETTERS, length);
    }

    /**
     * 得到一个固定长度的随机字符串，它是源字符的混合
     * @param source  source
     * @param length  length
     * @return
     */
    public static String getRandom(String source, int length) {
        return StringUtils.isEmpty(source) ? null : getRandom(source.toCharArray(), length);
    }

    /**
     * 得到一个固定长度的随机字符串，它是sourceChar的混合物
     * @param sourceChar    sourceChar
     * @param length  length
     * @return
     */
    public static String getRandom(char[] sourceChar, int length) {
        if (sourceChar == null || sourceChar.length == 0 || length < 0) {
            return null;
        }

        StringBuilder str = new StringBuilder(length);
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            str.append(sourceChar[random.nextInt(sourceChar.length)]);
        }
        return str.toString();
    }


    /**
     * @param max  接受的数值
     * @return  返回一个随机的数值
     */
    public static int getRandom(int max) {
        return getRandom(0, max);
    }


    public static int getRandomByLenth(int length) {
        StringBuilder str=new StringBuilder();//定义变长字符串
        Random random=new Random();
            //随机生成数字，并添加到字符串
        for(int i=0;i<length;i++){
            str.append(random.nextInt(10));
        }
        //将字符串转换为数字并输出
        int num=Integer.parseInt(str.toString());
        System.out.println(num);
        return num;
    }



    /**
     *
     * @param min  最小
     * @param max  最大
     * @return  返回一个范围的数值
     */
    public static int getRandom(int min, int max) {
        if (min > max) {
            return 0;
        }
        if (min == max) {
            return min;
        }
        return min + new Random().nextInt(max - min);
    }

    /**
     * 移动算法，使用默认的随机性源随机分配指定的数组
     * @param objArray  数组
     * @return 从新的数组
     */
    public static boolean shuffle(Object[] objArray) {
        if (objArray == null) {
            return false;
        }
        return shuffle(objArray, getRandom(objArray.length));
    }

    /**
     * 移动算法，随机分配指定的数组
     * @param objArray  数组
     * @param shuffleCount  洗的个数
     * @return  是否成功
     */
    public static boolean shuffle(Object[] objArray, int shuffleCount) {
        int length;
        if (objArray == null || shuffleCount < 0 || (length = objArray.length) < shuffleCount) {
            return false;
        }
        for (int i = 1; i <= shuffleCount; i++) {
            int random = getRandom(length - i);
            Object temp = objArray[length - i];
            objArray[length - i] = objArray[random];
            objArray[random] = temp;
        }
        return true;
    }

    /**
     * 移动算法，使用默认的随机性源随机分配指定的int数组
     * @param intArray  数组
     * @return  洗牌之后
     */
    public static int[] shuffle(int[] intArray) {
        if (intArray == null) {
            return null;
        }
        return shuffle(intArray, getRandom(intArray.length));
    }

    /**
     * 变换算法，随机permutes指定的整数数组
     * @param intArray   数组
     * @param shuffleCount  范围
     * @return  新的数组
     */
    public static int[] shuffle(int[] intArray, int shuffleCount) {
        int length;
        if (intArray == null || shuffleCount < 0 || (length = intArray.length) < shuffleCount) {
            return null;
        }
        int[] out = new int[shuffleCount];
        for (int i = 1; i <= shuffleCount; i++) {
            int random = getRandom(length - i);
            out[i - 1] = intArray[random];
            int temp = intArray[length - i];
            intArray[length - i] = intArray[random];
            intArray[random] = temp;
        }
        return out;
    }

    public static void main(String[] args) {
        //System.out.println(RandomUtils.getRandomNumbersAndLetters(28));
       // System.out.println(RandomUtils.getRandomCode());
        RandomUtils.getRandomByLenth(8);
    }
}