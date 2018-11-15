package www.ucforward.com.utils;


import org.apache.commons.codec.digest.DigestUtils;
import org.utils.StringUtil;
import www.ucforward.com.constants.AppRquestParamsConstant;
import www.ucforward.com.constants.ResultConstant;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Author:wenbn
 * Date:2018/1/15
 * Description: 校验传入参数，是否符合接口约束
 */
public class ExcuteUtil {

    //验证请求参数
    public static Map<Object, Object> checkRequest(HttpServletRequest request,Map<Object, Object> map){
        Map<Object,Object> checkMap = new HashMap<Object, Object>();
        String requestType = StringUtil.trim(map.get("requestType"));//请求类型 1：app 2 :PC
        if(requestType.equals("2")){//若是PC则直接通过
            checkMap.put("state",0);
            return checkMap;
        }
        String user_code = StringUtil.trim(map.get(AppRquestParamsConstant.REQUEST_USER_CODE));//app_id
        String phone_code = StringUtil.trim(map.get(AppRquestParamsConstant.REQUEST_PHONE_CODE));
        String version_code = StringUtil.trim(map.get(AppRquestParamsConstant.REQUEST_VERSION_CODE));
        String time_code = StringUtil.trim(map.get(AppRquestParamsConstant.REQUEST_TIME_CODE));
        String phone_type_code = StringUtil.trim(map.get(AppRquestParamsConstant.REQUEST_PHONE_TYPE_CODE));
        String method_code = StringUtil.trim(map.get(AppRquestParamsConstant.REQUEST_Method_CODE));//请求方法
        String sign_code = StringUtil.trim(map.get(AppRquestParamsConstant.REQUEST_SIGN_CODE));//签名值
//        String user_code = request.getParameter(AppRquestParamsConstant.REQUEST_USER_CODE);
//        String phone_code = request.getParameter(AppRquestParamsConstant.REQUEST_PHONE_CODE);
//        String version_code = request.getParameter(AppRquestParamsConstant.REQUEST_VERSION_CODE);
//        String time_code = request.getParameter(AppRquestParamsConstant.REQUEST_TIME_CODE);
//        String phone_type_code = request.getParameter(AppRquestParamsConstant.REQUEST_PHONE_TYPE_CODE);
//        String method_code = request.getParameter(AppRquestParamsConstant.REQUEST_Method_CODE);
//        String sign_code = request.getParameter(AppRquestParamsConstant.REQUEST_SIGN_CODE);
        if(user_code==null || phone_code==null ||  version_code==null || time_code==null ||  method_code==null || phone_type_code==null || sign_code==null){
            checkMap.put("state",ResultConstant.SYS_REQUIRED_PARAMETER_ERROR);
            checkMap.put("message", ResultConstant.SYS_REQUIRED_PARAMETER_ERROR_VALUE);
           // return checkMap;
        }
        String contextPath = request.getContextPath();
        String uri = request.getRequestURI();
        uri = uri.substring(contextPath.length(),uri.length());
        if(!method_code.equalsIgnoreCase(EncryptionUtil.getBase64(uri))){
            checkMap.put("state",ResultConstant.SYS_REQUIRED_METHOD_NAME_ERROR);
            checkMap.put("message",ResultConstant.SYS_REQUIRED_METHOD_NAME_ERROR_VALUE);
            //return checkMap;
        }
        if(!version_code.equalsIgnoreCase(EncryptionUtil.getBase64(AppRquestParamsConstant.REQUEST_VERSION_CODE_VALUE))){
            checkMap.put("state",ResultConstant.SYS_REQUIRED_VERSION_ERROR);
            checkMap.put("message",ResultConstant.SYS_REQUIRED_VERSION_ERROR_VALUE);
           // return checkMap;
        }
        Map<String,String> params = new HashMap<String,String>();
        params.put(AppRquestParamsConstant.REQUEST_USER_CODE,user_code);
        params.put(AppRquestParamsConstant.REQUEST_PHONE_CODE,phone_code);
        params.put(AppRquestParamsConstant.REQUEST_VERSION_CODE,version_code);
        params.put(AppRquestParamsConstant.REQUEST_TIME_CODE,time_code);
        params.put(AppRquestParamsConstant.REQUEST_PHONE_TYPE_CODE,phone_type_code);
        params.put(AppRquestParamsConstant.REQUEST_SIGN_CODE,sign_code);
        return checkData(params);
    }

    //验签
    private static Map<Object,Object> checkData(Map<String, String> params) {
        Map<Object, Object> checkMap = new HashMap<Object, Object>();
        Set<String> keySet = params.keySet();
        String[] keyArray = keySet.toArray(new String[keySet.size()]);
        Arrays.sort(keyArray);
        StringBuilder sb = new StringBuilder();
        for (String k : keyArray) {
            if (k.equals(AppRquestParamsConstant.REQUEST_SIGN_CODE)) {
                continue;
            }
            if (params.get(k).trim().length() > 0) // 参数值为空，则不参与签名
                sb.append(k).append("=").append(params.get(k).trim()).append("&");
        }
        String nes = sb.toString();
        String checkSign = DigestUtils.md5Hex(EncryptionUtil.getContentBytes(sb.toString(), "utf-8"));
        String beforeSign = params.get(AppRquestParamsConstant.REQUEST_SIGN_CODE);
        if(checkSign.equalsIgnoreCase(beforeSign)){
            checkMap.put("state",0);
            checkMap.put("message","验签成功");
        }else{
            checkMap.put("state", ResultConstant.SYS_REQUIRED_SIGN_ERROR);
            checkMap.put("message",ResultConstant.SYS_REQUIRED_SIGN_ERROR_VALUE);
        }
        return checkMap;
    }
}
