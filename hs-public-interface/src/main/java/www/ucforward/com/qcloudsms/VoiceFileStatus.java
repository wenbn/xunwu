package www.ucforward.com.qcloudsms;

import www.ucforward.com.qcloudsms.httpclient.HTTPClient;
import www.ucforward.com.qcloudsms.httpclient.HTTPException;
import www.ucforward.com.qcloudsms.httpclient.HTTPMethod;
import www.ucforward.com.qcloudsms.httpclient.HTTPRequest;
import www.ucforward.com.qcloudsms.httpclient.HTTPResponse;
import www.ucforward.com.qcloudsms.httpclient.DefaultHTTPClient;

import org.json.JSONObject;
import org.json.JSONException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.net.URISyntaxException;


public class VoiceFileStatus extends SmsBase {

    private String url = "https://cloud.tim.qq.com/v5/tlsvoicesvr/statusvoicefile";

    public VoiceFileStatus(int appid, String appkey) {
        super(appid, appkey, new DefaultHTTPClient());
    }

    public VoiceFileStatus(int appid, String appkey, HTTPClient httpclient) {
        super(appid, appkey, httpclient);
    }

    /**
     * 查询语音文件审核状态
     *
     * @param fid  语音文件fid
     * @return {@link}VoiceFileStatusResult
     * @throws HTTPException  http status exception
     * @throws JSONException  json parse exception
     * @throws IOException    network problem
     */
    public VoiceFileStatusResult get(String fid)
            throws HTTPException, JSONException, IOException {

        long random = SmsSenderUtil.getRandom();
        long now = SmsSenderUtil.getCurrentTime();
        JSONObject body = new JSONObject()
            .put("fid", fid)
            .put("sig", SmsSenderUtil.calculateFStatusSignature(
                this.appkey, random, now, fid))
            .put("time", now);

        HTTPRequest req = new HTTPRequest(HTTPMethod.POST, this.url)
            .addHeader("Conetent-Type", "application/json")
            .addQueryParameter("sdkappid", this.appid)
            .addQueryParameter("random", random)
            .setConnectionTimeout(60 * 1000)
            .setRequestTimeout(60 * 1000)
            .setBody(body.toString());

        try {
            // May throw IOException and URISyntaxexception
            HTTPResponse res = httpclient.fetch(req);

            // May throw HTTPException
            handleError(res);

            // May throw JSONException
            return (new VoiceFileStatusResult()).parseFromHTTPResponse(res);
        } catch(URISyntaxException e) {
            throw new RuntimeException("API url has been modified, current url: " + url);
        }
    }

}
