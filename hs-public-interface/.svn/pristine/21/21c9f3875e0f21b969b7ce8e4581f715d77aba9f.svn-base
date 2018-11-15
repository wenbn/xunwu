package www.ucforward.com.umeng.android;


import www.ucforward.com.umeng.AndroidNotification;

/**
 * 广播
 * 向安装该App的所有设备发送消息
 */
public class AndroidBroadcast extends AndroidNotification {
	public AndroidBroadcast(String appkey,String appMasterSecret) throws Exception {
			setAppMasterSecret(appMasterSecret);
			setPredefinedKeyValue("appkey", appkey);
			this.setPredefinedKeyValue("type", "broadcast");	
	}
}
