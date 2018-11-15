package www.ucforward.com.utils;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtilsBean;
import org.apache.commons.lang.StringEscapeUtils;
import org.utils.StringUtil;
import www.ucforward.com.vo.PayLoadVo;

import javax.servlet.http.HttpServletRequest;
import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by wenbn on 2018/1/6.
 * 请求工具类
 */
public class RequestUtil{

    /**
     * 获取用户真实IP地址，不使用request.getRemoteAddr();的原因是有可能用户使用了代理软件方式避免真实IP地址,
     * 参考文章： http://developer.51cto.com/art/201111/305181.htm
     *
     * 可是，如果通过了多级反向代理的话，X-Forwarded-For的值并不止一个，而是一串IP值，究竟哪个才是真正的用户端的真实IP呢？
     * 答案是取X-Forwarded-For中第一个非unknown的有效IP字符串。
     *
     * 如：X-Forwarded-For：192.168.1.110, 192.168.1.120, 192.168.1.130,
     * 192.168.1.100
     *
     * 用户真实IP为： 192.168.1.110
     *
     * @param request
     * @return
     */
    public static String getIpAddress(HttpServletRequest request) {

        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }


    /**
     * 解析token返回用户名
     * @param token
     * @return
     */
    public static PayLoadVo analysisToken(String token){
        PayLoadVo payLoadVo = null;
        try{
            String[] params = token.split("\\.");
            if(params.length != 3){
                return null;
            }
            payLoadVo = JsonUtil.jsonToObjectT(EncryptionUtil.getFromBase64(params[1]),PayLoadVo.class);
            return payLoadVo;
        }catch ( Exception e){
            return null;
        }
    }

    /**
     * 校验redis中的缓存是否存在
     * @param key
     * @param code
     * @return
     */
//    public static boolean checkCode(String key,String code){
//        String rdsCode = RedisUtil.safeGet(key);
//        if(rdsCode != null && rdsCode.equals(code)){
//            return true;
//        }else{
//            return false;
//        }
//    }


    /**
     * 转化request请求中的参数为普通map
     * @param request
     * @return
     */
    @SuppressWarnings("unchecked")
    public static Map getParameterMap(HttpServletRequest request) {
        // 参数 Map
        Map properties = request.getParameterMap();

        // 返回值Map
        Map returnMap = new HashMap();
        Iterator entries = properties.entrySet().iterator();
        Map.Entry entry;
        String name = "";
        String value = "";
        while (entries.hasNext()) {
            entry = (Map.Entry) entries.next();
            name = (String) entry.getKey();
            Object valueObj = entry.getValue();
            if(null == valueObj){
                value = "";
            }else if(valueObj instanceof String[]){
                String[] values = (String[])valueObj;
                for(int i=0;i<values.length;i++){
                    value = values[i] + ",";
                }
                value = value.substring(0, value.length()-1);
            }else{
                value = valueObj.toString();
            }
            //防止xss攻击
            if(StringUtil.hasText(value)){
                value = StringEscapeUtils.escapeHtml(value);
                value = StringEscapeUtils.escapeJavaScript(value);
                value = StringEscapeUtils.escapeSql(value);
            }
            returnMap.put(name, value);
        }
        return returnMap;
    }

    /**
     * 转化request请求中的参数为普通map
     * @param request
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> T getParameterMap2Bean(HttpServletRequest request, T bean) throws InvocationTargetException, IllegalAccessException {
        // 参数 Map
        Map map = getParameterMap(request);
        BeanUtils.populate(bean, map);
        return bean;
    }

    /**
     * 处理请求参数为javaBean类型的数据
     * @param obj
     * @return
     * @throws IllegalAccessException
     */
    public static Object handleRequestBeanData(Object obj) throws IllegalAccessException{
        Class TClass = (Class) obj.getClass(); // studentClass
        Field[] field = TClass.getDeclaredFields();

        for (int i = 0; i < field.length; i++) {
            String value = "";
            Field f = field[i];
            int size = field.length;// 属性个数
            f.setAccessible(true); // 设置些属性是可以访问的
            String type = f.getType().toString();// 得到此属性的类型
            String key = f.getName();// key:得到属性名
            Object valueObj = f.get(obj); //获取属性的值

            if( null == valueObj){
               if("java.lang.String".equals(type)){
                   value = "";
                   if(valueObj instanceof String[]){
                       String[] values = (String[])valueObj;
                       for(int j=0;j<values.length;j++){
                           value = values[j] + ",";
                       }
                       value = value.substring(0, value.length()-1);
                   }else{
                       value = valueObj.toString();
                   }

                   //防止xss攻击
                   if(StringUtil.hasText(value)){
                       value = StringEscapeUtils.escapeHtml(value);
                       value = StringEscapeUtils.escapeJavaScript(value);
                       value = StringEscapeUtils.escapeSql(value);
                   }
                   f.set(obj,value);
               }else{
                   f.set(obj,valueObj);
               }
            }

        }

        return obj;
    }


    /**
     * 一个类把属性值赋值给另一个类的相同的属性
     * @param source
     * @param dest
     * @throws Exception
     */
    public static void copy(Object source, Object dest) throws Exception {
        // 获取属性
        BeanInfo sourceBean = Introspector.getBeanInfo(source.getClass(),Object.class);
        PropertyDescriptor[] sourceProperty = sourceBean.getPropertyDescriptors();
        BeanInfo destBean = Introspector.getBeanInfo(dest.getClass(),Object.class);
        PropertyDescriptor[] destProperty = destBean.getPropertyDescriptors();
        try {
            for (int i = 0; i < sourceProperty.length; i++) {
                for (int j = 0; j < destProperty.length; j++) {
                    if (sourceProperty[i].getName().equals(destProperty[j].getName()) && sourceProperty[i].getPropertyType() == destProperty[j].getPropertyType() ) {
                        // 调用source的getter方法和dest的setter方法
                        destProperty[j].getWriteMethod().invoke(dest,sourceProperty[i].getReadMethod().invoke(source));
                        break;
                    }
                }
            }
        } catch (Exception e) {
            throw new Exception("属性复制失败:" + e.getMessage());
        }
    }



    /**
     * 获取请求来源
     * @param request
     * @return
     */
    public static String getReHeard(HttpServletRequest request){
        //获取浏览器信息
        String ua = request.getHeader("User-Agent").toLowerCase();
        //转成UserAgent对象
        //UserAgent userAgent = UserAgent.parseUserAgentString(ua);
        //获取浏览器信息
        //Browser browser = userAgent.getBrowser();
        //浏览器名称
        //browser.getName();
        //获取系统信息
        //OperatingSystem os = userAgent.getOperatingSystem();
        //系统名称
        //os.getName();
        String resource = "";
        if(ua.indexOf("android") != -1){
            resource = "android";
        }else if(ua.indexOf("iphone") != -1){
            resource = "ios";
        }else if(ua.indexOf("ios") != -1){
            resource = "ios";
        }else{
            resource = "PC";
        }
        return resource;
    }

    /**
     * 比较两个对象的属性值
     * @param obj1
     * @param obj2
     * @return
     */
    public static boolean contrastObj(Object obj1,Object obj2){
        try {
            PropertyUtilsBean propertyUtilsBean = new PropertyUtilsBean();
            PropertyDescriptor[] descriptors = propertyUtilsBean.getPropertyDescriptors(obj1);
            PropertyDescriptor[] descriptors2 = propertyUtilsBean.getPropertyDescriptors(obj2);
            int j = 0;
            for(int i = 0;i < descriptors.length;i++){
                String name = descriptors[i].getName(); //获取属性名
                String fieldNameValue = name +":" +propertyUtilsBean.getNestedProperty(obj1,name);
                for(int k = j;k<descriptors2.length;j++){
                    j++;
                    String name2 = descriptors2[k].getName();
                    String fieldNameValue2 = name2 + ":" + propertyUtilsBean.getNestedProperty(obj2,name2);
                    if(!fieldNameValue.equals(fieldNameValue2)){
                        System.out.println(fieldNameValue+"与"+fieldNameValue2+"不相等");
                        return false;
                    }else{
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

}
