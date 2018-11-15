package www.ucforward.com.utils;

import org.json.JSONException;
import www.ucforward.com.qcloudsms.SmsMultiSender;
import www.ucforward.com.qcloudsms.SmsMultiSenderResult;
import www.ucforward.com.qcloudsms.httpclient.HTTPException;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Administrator on 2018/7/31.
 */
public class SendSmsUtil {

    private static  final int APPID = 1400115377; //APPID
    private static  final String APPKEY = "b5e52c92971e1714cd8194b5532eab23";  // 短信应用SDK AppKey

    public static  String sendSms(String nationCode, String[] phoneNumbers,int templateId, String[] params, String sign, String extend, String ext){
        String resultStr = "success";
        try {
            SmsMultiSender msender = new SmsMultiSender(APPID,APPKEY);
            SmsMultiSenderResult result = msender.sendWithParam(nationCode,phoneNumbers,templateId,params,sign,extend,ext);
            ArrayList<SmsMultiSenderResult.Detail> details = result.details;
            //发送短信的状态为0，并且详细的发送消息状态为0,则发送成功
            if(result.result != 0 || (result.result == 0 && details.get(0).result != 0)){
                resultStr = "error";
            }
        }catch (HTTPException e) {
            // HTTP响应码错误
            e.printStackTrace();
        } catch (JSONException e) {
            // json解析错误
            e.printStackTrace();
        } catch (IOException e) {
            // 网络IO错误
            e.printStackTrace();
        }
        return resultStr;
    }
}
