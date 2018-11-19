package com.example.demo.constants;

/**
 * 全局消息常量类
 * Author:wenbn
 * Date:2018/1/11
 * Description:
 */
public class ResultConstant {

    ////////////////////////////////系统常量//////////////////////////////////

    ////////////////http请求常见状态码 start/////////////
    public static final int HTTP_STATUS_BAD_REQUEST = 400;//用户未登录
    public static final String HTTP_STATUS_BAD_REQUEST_VALUE = "Bad request";
//    public static final String HTTP_STATUS_BAD_REQUEST_VALUE = "错误请求";

    public static final int HTTP_STATUS_NOT_FOUND = 404;//用户未登录
    public static final String HTTP_STATUS_NOT_FOUND_VALUE = "The requested resource is unavailable";
//    public static final String HTTP_STATUS_NOT_FOUND_VALUE = "请求的资源不可用";

    public static final int HTTP_STATUS_METHOD_NOT_ALLOWED = 405;//不合法的请求方法
    public static final String HTTP_STATUS_METHOD_NOT_ALLOWED_VALUE = "Illegal request method";
//    public static final String HTTP_STATUS_METHOD_NOT_ALLOWED_VALUE = "不合法的请求方法";

    public static final int HTTP_STATUS_UNSUPPORTED_MEDIA_TYPE = 415;//内容类型不支持
    public static final String HTTP_STATUS_UNSUPPORTED_MEDIA_TYPE_VALUE = "Content types are not supported";
//    public static final String HTTP_STATUS_UNSUPPORTED_MEDIA_TYPE_VALUE = "内容类型不支持";

    public static final int HTTP_STATUS_INTERNAL_SERVER_ERROR = 500;//内部服务错误
    public static final String HTTP_STATUS_INTERNAL_SERVER_ERROR_VALUE = "Internal service error";
//    public static final String HTTP_STATUS_INTERNAL_SERVER_ERROR_VALUE = "内部服务错误";
    ////////////////http请求常见状态码 end///////////////

    public static final int SYS_REQUIRED_UNLOGIN_ERROR = 1001;//用户未登录
    public static final String SYS_REQUIRED_UNLOGIN_ERROR_VALUE = "Please login first";
//    public static final String SYS_REQUIRED_UNLOGIN_ERROR_VALUE = "请先登录";

    public static final int SYS_REQUIRED_LOGIN_SUCCESS = 1002;//登录成功
    public static final String SYS_REQUIRED_LOGIN_SUCCESS_VALUE = "login successfully";
//    public static final String SYS_REQUIRED_LOGIN_SUCCESS_VALUE = "登录成功";

    public static final int SYS_REQUIRED_UNAUTHORIZED_ERROR = 1003;//没有访问权限
    public static final String SYS_REQUIRED_UNAUTHORIZED_ERROR_VALUE = "No access";
//    public static final String SYS_REQUIRED_UNAUTHORIZED_ERROR_VALUE = "没有访问权限";

    public static final int SYS_REQUIRED_PARAMETER_ERROR = 1004;//请求参数错误
    public static final String SYS_REQUIRED_PARAMETER_ERROR_VALUE = "Request parameter error";
//    public static final String SYS_REQUIRED_PARAMETER_ERROR_VALUE = "请求参数错误";

    public static final int SYS_REQUIRED_TIMEOUT_ERROR = 1005;//请求超时
    public static final String SYS_REQUIRED_TIMEOUT_ERROR_VALUE = "The request timeout";
//    public static final String SYS_REQUIRED_TIMEOUT_ERROR_VALUE = "请求超时";

    public static final int SYS_REQUIRED_SUCCESS = 0;//请求成功
    public static final String SYS_REQUIRED_SUCCESS_VALUE = "The request is successful";
//    public static final String SYS_REQUIRED_SUCCESS_VALUE = "请求成功";

    public static final int SYS_IMG_CODE_IS_OVERDUE = 1006;//验证码过期
    public static final String SYS_IMG_CODE_IS_OVERDUE_VALUE = "Verification code expiration";
//    public static final String SYS_IMG_CODE_IS_OVERDUE_VALUE = "验证码过期";

    public static final int SYS_REQUIRED_UNOPERATION_ERROR = 1007;//没有操作权限
    public static final String SYS_REQUIRED_UNOPERATION_ERROR_VALUE = "No operation permission";
    //public static final String SYS_REQUIRED_UNOPERATION_ERROR_VALUE = "没有操作权限";

    public static final int SYS_IMG_CODE_ERROR = 1008;//验证码错误
    public static final String SYS_IMG_CODE_ERROR_VALUE = "Verification code error";
   // public static final String SYS_IMG_CODE_ERROR_VALUE = "验证码错误";

    public static final int SYS_REQUIRED_FAILURE = -1;//请求失败
    public static final String SYS_REQUIRED_FAILURE_VALUE = "The request failed";
    //public static final String SYS_REQUIRED_FAILURE_VALUE = "请求失败";

    public static final int SYS_REQUIRED_SIGN_ERROR = 11111;//验签失败
    public static final String SYS_REQUIRED_SIGN_ERROR_VALUE = "Attestation of failure";
//    public static final String SYS_REQUIRED_SIGN_ERROR_VALUE = "验签失败";

    public static final int SYS_REQUIRED_VERSION_ERROR = 33333;//版本号错误
    public static final String SYS_REQUIRED_VERSION_ERROR_VALUE = "Version number error";
//    public static final String SYS_REQUIRED_VERSION_ERROR_VALUE = "版本号错误";

    public static final int SYS_REQUIRED_METHOD_NAME_ERROR = 22222;//请求接口名称错误
    public static final String SYS_REQUIRED_METHOD_NAME_ERROR_VALUE = "Request interface name error";
//    public static final String SYS_REQUIRED_METHOD_NAME_ERROR_VALUE = "请求接口名称错误";

    public static final int SYS_REQUIRED_ERROR = 1000; //系统错误
    public static final String SYS_REQUIRED_ERROR_VALUE = "system error";
//    public static final String SYS_REQUIRED_ERROR_VALUE = "系统错误";

    public static final int HOUSES_DATA_EXCEPTION = 1100;
    public static final String HOUSES_DATA_EXCEPTION_VALUE = "Abnormal housing data";
//    public static final String HOUSES_DATA_EXCEPTION_VALUE = "房源数据异常";

    public static final int ORDER_DATA_EXCEPTION = 1101;
    public static final String ORDER_DATA_EXCEPTION_VALUE = "Order data exception";
//    public static final String ORDER_DATA_EXCEPTION_VALUE = "订单数据异常";

    public static final int NEGOTIATION_SUCCESS = 1102;
    public static final String NEGOTIATION_SUCCESS_VALUE = "议价成功";

    public static final int GET_CITY_DATA_ERROR = 1103;
    public static final String GET_CITY_DATA_ERROR_VALUE = "Failed to obtain city information";
//    public static final String GET_CITY_DATA_ERROR_VALUE = "获取城市信息失败";

    public static final int KEY_IS_NOT_EXPIRED_ERROR = 1104;
    public static final String KEY_IS_NOT_EXPIRED_ERROR_VALUE = "The key is not expired";
//    public static final String KEY_IS_NOT_EXPIRED_ERROR_VALUE = "钥匙未过期";

    public static final int KEY_IS_EXPIRED_ERROR = 1105;
    public static final String KEY_IS_EXPIRED_ERROR_VALUE = "The key has expired";
//    public static final String KEY_IS_EXPIRED_ERROR_VALUE = "钥匙已过期";

    public static final int USER_IS_LOCKED_OR_DELETE = 1106;
    public static final String USER_IS_LOCKED_OR_DELETE_VALUE = "The user is disabled or deleted, please contact the system administrator";
//    public static final String USER_IS_LOCKED_OR_DELETE_VALUE = "用户被禁用或删除，请联系系统管理员";


    ////////////////////////////////业务常量//////////////////////////////////
    public static final String BUS_MEMBER_CODE_VALUE = "m_";
    public static final String BUS_TAG_TOKEN = "token";

    public static final int BUS_MEMBER_LOGIN_TIMEOUT = 2001;//登录超时
    public static final String BUS_MEMBER_LOGIN_TIMEOUT_VALUE = "Token abnormal";
//    public static final String BUS_MEMBER_LOGIN_TIMEOUT_VALUE = "token异常";

    public static final int BUS_MEMBER_APPOINTMENT_FAILURE = 3001; //预约失败
    public static final String BUS_MEMBER_APPOINTMENT_FALIURE_VALUE = "Make an appointment to failure";
//    public static final String BUS_MEMBER_APPOINTMENT_FALIURE_VALUE = "预约失败";

    public static final int BUS_MEMBER_SUBSCRIBE_FAILURE = 4001; //订阅失败
    public static final String BUS_MEMBER_SUBSCRIBE_FAILURE_VALUE = "Subscribe to the failure";
//    public static final String BUS_MEMBER_SUBSCRIBE_FAILURE_VALUE = "订阅失败";

    public static final int BUS_USER_TRANSFER_ORDER_FAILURE = 5001; //转单失败
    public static final String BUS_USER_TRANSFER_ORDER_FAILURE_VALUE = "Turn a single failure";
//    public static final String BUS_USER_TRANSFER_ORDER_FAILURE_VALUE = "转单失败";

    public static final int BUS_USER_PASSWORD_ERROR = 6001;
    public static final String BUS_USER_PASSWORD_ERROR_VALUE = "wrong password";
//    public static final String BUS_USER_PASSWORD_ERROR_VALUE = "密码错误";

    public static final int BUS_MEMBER_NEGOTIATION_SUCCESS = 7001;
    public static final String BUS_MEMBER_NEGOTIATION_SUCCESS_VALUE = "Negotiation success";
//    public static final String BUS_MEMBER_NEGOTIATION_SUCCESS_VALUE = "议价成功";

    public static final int BUS_MEMBER_NEGOTIATION_FAILURE = 7002;
    public static final String BUS_MEMBER_NEGOTIATION_FAILURE_VALUE = "Negotiation fails";
//    public static final String BUS_MEMBER_NEGOTIATION_FAILURE_VALUE = "议价失败";

    public static final int BUS_MEMBER_SEND_SUCCESS = 7003;
    public static final String BUS_MEMBER_SEND_SUCCESS_VALUE = "The bargaining message was sent successfully";
//    public static final String BUS_MEMBER_SEND_SUCCESS_VALUE = "议价消息发送成功";

    public static final int BUS_MEMBER_ALREADY_RESERVED = 7004;
    public static final String BUS_MEMBER_ALREADY_RESERVED_VALUE = "You have booked the current time";
//    public static final String BUS_MEMBER_ALREADY_RESERVED_VALUE = "您已经预约过当前时间";

    public static final int NOT_ARRIVE_HOUSE_POSITION_ERROR = 7005;
    public static final String NOT_ARRIVE_HOUSE_POSITION_ERROR_VALUE = "Unable to operate until specified location is reached";
//    public static final String NOT_ARRIVE_HOUSE_POSITION_ERROR_VALUE = "未到达指定地点，无法操作";

    public static final int USER_NOT_REGISTERED = 7006;
    public static final String USER_NOT_REGISTERED_VALUE = "User not registered";
//    public static final String USER_NOT_REGISTERED_VALUE = "用户未注册";

}
