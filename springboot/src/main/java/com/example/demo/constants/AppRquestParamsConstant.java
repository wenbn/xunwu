package com.example.demo.constants;

/**
 * Author:wenbn
 * Date:2018/1/15
 * Description:
 * <li>sign:传递SIGN的MD5加密</li>
 * <li>param:传递param的所有参数BASE64加密 (暂时未处理)</li>
 * <li>user:合作商编号</li>
 * <li>phone:手机唯一串</li>
 * <li>version:接口版本号</li>
 * <li>time:手机当前时间</li>
 * <li>phoneType:手机类型</li>
 */
public class AppRquestParamsConstant {



    public static String REQUEST_PARAM_CODE = "param";

    public static String REQUEST_USER_CODE = "appId";//合作商编号,每个用户对应一个appId

    public static String REQUEST_PHONE_CODE = "phone";//手机唯一串

    public static String REQUEST_VERSION_CODE = "version";//接口版本号

    public static String REQUEST_TIME_CODE = "time";//当前时间

    public static String REQUEST_PHONE_TYPE_CODE = "pt";//手机类型

    public static String REQUEST_VERSION_CODE_VALUE = "V 1.0.0";

    public static String REQUEST_Method_CODE = "method"; //请求接口名

    public static String REQUEST_SIGN_CODE = "sign";//签名参数

    public static int APP_PAGE_SIZE = 10;//分页默认查询10条
}
