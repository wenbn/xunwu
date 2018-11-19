package com.example.demo.constants;

/**
 * Author:wenbn
 * Date:2018/1/6
 * Description:存放redis的key值
 */
public class RedisConstant {

    public static final String SYS_DICTCODE_CACHE_KEY = "SYS_DICTCODE_CACHE_KEY";//存放数据字典缓存键
    public static final String SYS_HOUSE_CONFIG_DICTCODE_CACHE_KEY = "SYS_HOUSE_CONFIG_DICTCODE_CACHE_KEY";//存放数据字典缓存键
    public static final String SYS_HOUSE_DECORATION_DICTCODE_CACHE_KEY = "SYS_HOUSE_DECORATION_DICTCODE_CACHE_KEY";//存放数据字典缓存键
    public static final String SYS_HOUSE_FLOOR_DICTCODE_CACHE_KEY = "SYS_HOUSE_FLOOR_DICTCODE_CACHE_KEY";//存放数据字典缓存键
    public static final String SYS_HOUSE_LABEL_DICTCODE_CACHE_KEY = "SYS_HOUSE_LABEL_DICTCODE_CACHE_KEY";//存放数据字典缓存键
    public static final String SYS_HOUSE_ORIENTATION_DICTCODE_CACHE_KEY = "SYS_HOUSE_ORIENTATION_DICTCODE_CACHE_KEY";//存放数据字典缓存键
    public static final String SYS_HOUSE_TYPE_DICTCODE_CACHE_KEY = "SYS_HOUSE_TYPE_DICTCODE_CACHE_KEY";//存放数据字典缓存键
    public static final String SYS_HOUSING_TYPE_DICTCODE_CACHE_KEY = "SYS_HOUSING_TYPE_DICTCODE_CACHE_KEY";//存放数据字典缓存键
    public static final String SYS_REGIONNAME_CACHE_KEY = "SYS_REGIONNAME_CACHE_KEY";//存放登录时城市区号信息

    public static final int SYS_DICTCODE_CACHE_KEY_TIME = 60*60*24*30;//存放数据字典缓存键存在时间
    public static final String SYS_SUPPORT_CITIES_CACHE_KEY_KEY = "SYS_SUPPORT_CITIES_CACHE_KEY_KEY";//存放支持城市列表缓存键存在时间
    public static final int SYS_SUPPORT_CITIES_CACHE_KEY_TIME = 60*60*24*30*12;//存放支持城市列表缓存键存在时间

    public static final String SYS_USER_REGISTER_IMG_CODE_KEY = "SYS_USER_REGISTER_IMG_CODE_KEY";//存放用户注册缓存键

    public static final String SYS_USER_UPATE_VALIDATE_CODE_KEY = "SYS_USER_UPATE_VALIDATE_CODE_KEY"; //修改用户信息验证码

    public static final int SYS_USER_REGISTER_IMG_CODE_TIME = 60*10;//存放用户注册缓存键存在时间

    public static final String SYS_MEMBER_SUBSCRIBE_CACHE_KEY = "m_subscribe_"; //会员订阅信息

    public static final String SYS_MEMBER_APPOINTMENT_CACHE_KEY = "m_appointment_"; //会员预约信息


    public static final String SYS_GOLD_RULE_CACHE_KEY = "SYS_GOLD_RULE_CACHE_KEY";//存放积分缓存键
    public static final int SYS_GOLD_RULE_CACHE_KEY_TIME = 60*60*24*30;//存放数据字典缓存键存在时间

    public static final String SYS_PLATFORM_SETTING_CACHE_KEY = "SYS_PLATFORM_SETTING_CACHE_KEY";//存放积分缓存键
    public static final int SYS_PLATFORM_SETTING_CACHE_KEY_TIME = 60*60*24*30;//存放数据字典缓存键存在时间

    /**
     * 订单状态
     */
    public static final String ORDER_STATUS = "ORDER_STATUS";

    /**
     * 订单状态缓存时间
     */
    public static final int ORDER_STATUS_TIME = 60*60*24*30;
    /**
     * 房源进度缓存
     */
    public static final String HOUSE_PROGRESS = "HOUSE_PROGRESS";

    /**
     * 房源进度缓存时间
     */
    public static final int HOUSE_PROGRESS_TIME = 60*60*24*30;


}
