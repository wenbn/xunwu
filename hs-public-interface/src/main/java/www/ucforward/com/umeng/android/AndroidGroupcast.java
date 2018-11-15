package www.ucforward.com.umeng.android;


import org.json.JSONObject;
import www.ucforward.com.umeng.AndroidNotification;

/**
 * 组播
 * 向满足特定条件的设备集合发送消息，例如: “特定版本”、”特定地域”等
 */
public class AndroidGroupcast extends AndroidNotification {
	public AndroidGroupcast(String appkey,String appMasterSecret) throws Exception {
			setAppMasterSecret(appMasterSecret);
			setPredefinedKeyValue("appkey", appkey);
			this.setPredefinedKeyValue("type", "groupcast");	
	}
	
	public void setFilter(JSONObject filter) throws Exception {
    	setPredefinedKeyValue("filter", filter);
    }
}
