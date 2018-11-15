package www.ucforward.com.umeng.android;


import www.ucforward.com.umeng.AndroidNotification;

/**
 * 文件播
 * 开发者将批量的device_token或者alias存放到文件，通过文件ID进行消息发送
 */
public class AndroidFilecast extends AndroidNotification {
	public AndroidFilecast(String appkey,String appMasterSecret) throws Exception {
			setAppMasterSecret(appMasterSecret);
			setPredefinedKeyValue("appkey", appkey);
			this.setPredefinedKeyValue("type", "filecast");	
	}
	
	public void setFileId(String fileId) throws Exception {
    	setPredefinedKeyValue("file_id", fileId);
    }
}