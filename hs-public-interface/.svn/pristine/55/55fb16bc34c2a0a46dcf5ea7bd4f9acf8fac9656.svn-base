package www.ucforward.com.utils;

import org.apache.commons.lang.StringUtils;
import www.ucforward.com.yuzhixun.client.AbsRestClient;
import www.ucforward.com.yuzhixun.client.JsonReqClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * 云之讯短信接口
 * @author wenbn
 * @version 1.0
 * @date 2018/6/14
 */
public class SMSUtil {

    private static final String APPID = "fadf7340e4604926ae65b43b40ffe3bf";
    private static final String SID = "22fb5025a088b7246c61f26312af12f2";
    private static final String TOKEN = "588e2fea5bb84c8e22be0863457ad6ae";


    static AbsRestClient InstantiationRestAPI() {
        return new JsonReqClient();
    }

    /**
     * 发送短信
     * @param templateid 创建短信模板时系统分配的唯一标示
     * @param param 模板中的替换参数，如该模板不存在参数则无需传该参数或者参数为空，如果有多个参数则需要写在同一个字符串中，以英文逗号分隔 （如：“a,b,c”）
     * @param mobile 接收的单个手机号，暂仅支持国内号码
     * @param uid
     */
    public static void sendSms(String templateid, String param, String mobile, String uid){
        try {
            String result=InstantiationRestAPI().sendSms(SID, TOKEN, APPID, templateid, param, mobile, uid);
            System.out.println("Response content is: " + result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 批量发送短信
     * @param sid
     * @param token
     * @param appid
     * @param templateid
     * @param param
     * @param mobile
     * @param uid
     */
    public static void sendSmsBatch(String sid, String token, String appid, String templateid, String param, String mobile, String uid){
        try {
            String result=InstantiationRestAPI().sendSmsBatch(SID, TOKEN, APPID, templateid, param, mobile, uid);
            System.out.println("Response content is: " + result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void addSmsTemplate(String sid, String token, String appid, String type, String template_name, String autograph, String content){
        try {
            String result=InstantiationRestAPI().addSmsTemplate(SID, TOKEN, APPID,type, template_name, autograph, content);
            System.out.println("Response content is: " + result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void getSmsTemplate(String sid, String token, String appid, String templateid, String page_num, String page_size){
        try {
            String result=InstantiationRestAPI().getSmsTemplate(SID, TOKEN, APPID, templateid, page_num, page_size);
            System.out.println("Response content is: " + result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void editSmsTemplate(String sid, String token, String appid, String templateid, String type, String template_name, String autograph, String content){
        try {
            String result=InstantiationRestAPI().editSmsTemplate(SID, TOKEN, APPID, templateid, type, template_name, autograph, content);
            System.out.println("Response content is: " + result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void deleterSmsTemplate(String sid, String token, String appid, String templateid){
        try {
            String result=InstantiationRestAPI().deleterSmsTemplate(SID, TOKEN, APPID, templateid);
            System.out.println("Response content is: " + result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 测试说明  启动main方法后，请在控制台输入数字(数字对应 相应的调用方法)，回车键结束
     * 参数名称含义，请参考rest api 文档
     * @throws IOException
     * @method main
     */
    public static void main(String[] args) throws Exception {
        System.out.println("请输入方法对应的数字(例如1),Enter键结束:");
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        String methodNumber = br.readLine();

        if (StringUtils.isBlank(methodNumber)){
            System.out.println("请输入正确的数字，不可为空");
            return;
        }

        if (methodNumber.equals("1")) {  //指定模板单发
            String templateid = "336689";
            String param = "test_order";
            String mobile = "00971508138014";
            String uid = "1212";
            sendSms(templateid, param, mobile, uid);
        } else if (methodNumber.equals("2")) { //指定模板群发
            String sid = "";
            String token = "";
            String appid = "";
            String templateid = "";
            String param = "";
            String mobile = "";
            String uid = "";
            sendSmsBatch(sid, token, appid, templateid, param, mobile, uid);
        } else if (methodNumber.equals("3")) {  //增加模板
            String sid = "";
            String token = "";
            String appid = "";
            String type = "";
            String template_name = "";
            String autograph = "";
            String content = "";
            addSmsTemplate(sid, token, appid, type, template_name, autograph, content);
        } else if (methodNumber.equals("4")) {  //查询模板
            String sid = "";
            String token = "";
            String appid = "";
            String templateid = "";
            String page_num = "";
            String page_size = "";
            getSmsTemplate(sid, token, appid, templateid, page_num, page_size);
        } else if (methodNumber.equals("5")) {  //编辑模板
            String sid = "";
            String token = "";
            String appid = "";
            String templateid = "";
            String type = "";
            String template_name = "";
            String autograph = "";
            String content = "";
            editSmsTemplate(sid, token, appid, templateid, type, template_name, autograph, content);
        } else if (methodNumber.equals("6")) {  //删除模板
            String sid = "";
            String token = "";
            String appid = "";
            String templateid = "";
            deleterSmsTemplate(sid, token, appid, templateid);
        }
    }
}
