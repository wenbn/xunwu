
package www.ucforward.com.yuzhixun.client;


import net.sf.json.JSONObject;
import www.ucforward.com.utils.HttpClientUtil;

public class JsonReqClient extends AbsRestClient {




	@Override
	public String sendSms(String sid, String token, String appid, String templateid, String param, String mobile, 
			String uid) {
		String result = "";
		try {
			String url = getStringBuffer().append("/sendsms").toString();
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("sid", sid);
			jsonObject.put("token", token);
			jsonObject.put("appid", appid);
			jsonObject.put("templateid", templateid);
			jsonObject.put("param", param);
			jsonObject.put("mobile", mobile);
			jsonObject.put("uid", uid);
			System.out.println("body = " + jsonObject.toString());
			result = HttpClientUtil.httpPost2(url, jsonObject);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public String sendSmsBatch(String sid, String token, String appid, String templateid, String param, String mobile,
			String uid) {
		String result = "";
		try {
			String url = getStringBuffer().append("/sendsms_batch").toString();
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("sid", sid);
			jsonObject.put("token", token);
			jsonObject.put("appid", appid);
			jsonObject.put("templateid", templateid);
			jsonObject.put("param", param);
			jsonObject.put("mobile", mobile);
			jsonObject.put("uid", uid);
			System.out.println("body = " + jsonObject.toString());
			result = HttpClientUtil.httpPost2(url, jsonObject);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public String addSmsTemplate(String sid, String token, String appid, String type, String template_name,
			String autograph, String content) {
		String result = "";
		try {
			String url = getStringBuffer().append("/addsmstemplate").toString();
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("sid", sid);
			jsonObject.put("token", token);
			jsonObject.put("appid", appid);
			jsonObject.put("type", type);
			jsonObject.put("template_name", template_name);
			jsonObject.put("autograph", autograph);
			jsonObject.put("content", content);
			System.out.println("body = " + jsonObject.toString());
			result = HttpClientUtil.httpPost2(url, jsonObject);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public String getSmsTemplate(String sid, String token, String appid, String templateid, String page_num,
			String page_size) {
		String result = "";
		try {
			String url = getStringBuffer().append("/getsmstemplate").toString();
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("sid", sid);
			jsonObject.put("token", token);
			jsonObject.put("appid", appid);
			jsonObject.put("templateid", templateid);
			jsonObject.put("page_num", page_num);
			jsonObject.put("page_size", page_size);
			System.out.println("body = " + jsonObject.toString());
			result = HttpClientUtil.httpPost2(url, jsonObject);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public String editSmsTemplate(String sid, String token, String appid, String templateid, String type,
			String template_name, String autograph, String content) {
		String result = "";
		try {
			String url = getStringBuffer().append("/editsmstemplate").toString();
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("sid", sid);
			jsonObject.put("token", token);
			jsonObject.put("appid", appid);
			jsonObject.put("templateid", templateid);
			jsonObject.put("type", type);
			jsonObject.put("template_name", template_name);
			jsonObject.put("autograph", autograph);
			jsonObject.put("content", content);
			System.out.println("body = " + jsonObject.toString());
			result = HttpClientUtil.httpPost2(url, jsonObject);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public String deleterSmsTemplate(String sid, String token, String appid, String templateid) {
		String result = "";
		try {
			String url = getStringBuffer().append("/deletesmstemplate").toString();
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("sid", sid);
			jsonObject.put("token", token);
			jsonObject.put("appid", appid);
			jsonObject.put("templateid", templateid);
			System.out.println("body = " + jsonObject.toString());
			result = HttpClientUtil.httpPost2(url, jsonObject);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
}
