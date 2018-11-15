package www.ucforward.com.emchat.api.impl;

import www.ucforward.com.emchat.api.SendMessageAPI;
import www.ucforward.com.emchat.comm.EasemobAPI;
import www.ucforward.com.emchat.comm.OrgInfo;
import www.ucforward.com.emchat.comm.ResponseHandler;
import www.ucforward.com.emchat.comm.TokenUtil;
import io.swagger.client.ApiException;
import io.swagger.client.api.MessagesApi;
import io.swagger.client.model.Msg;

public class EasemobSendMessage implements SendMessageAPI {
    private ResponseHandler responseHandler = new ResponseHandler();
    private MessagesApi api = new MessagesApi();
    @Override
    public Object sendMessage(final Object payload) {
        return responseHandler.handle(new EasemobAPI() {
            @Override
            public Object invokeEasemobAPI() throws ApiException {
                return api.orgNameAppNameMessagesPost(OrgInfo.ORG_NAME,OrgInfo.APP_NAME,TokenUtil.getAccessToken(), (Msg) payload);
            }
        });
    }
}
