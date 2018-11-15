package www.ucforward.com.umeng.android;


import www.ucforward.com.umeng.AndroidNotification;

/**
 * 单播
 * 向指定的设备发送消息
 */
public class AndroidUnicast extends AndroidNotification {
	public AndroidUnicast(String appkey,String appMasterSecret) throws Exception {
			setAppMasterSecret(appMasterSecret);
			setPredefinedKeyValue("appkey", appkey);
			this.setPredefinedKeyValue("type", "unicast");	
	}
	
	public void setDeviceToken(String token) throws Exception {
    	setPredefinedKeyValue("device_tokens", token);
    }

}