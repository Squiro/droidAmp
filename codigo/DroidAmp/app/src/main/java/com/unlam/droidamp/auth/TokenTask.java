package com.unlam.droidamp.auth;
import com.unlam.droidamp.interfaces.RequestCallback;
import com.unlam.droidamp.network.NetworkHandler;
import com.unlam.droidamp.network.NetworkTask;

import org.json.JSONException;
import org.json.JSONObject;
import java.net.URL;

public class TokenTask extends NetworkTask {

    private Auth auth;

    public TokenTask(RequestCallback<NetworkTask.Result> callback, Auth auth) {
        super(callback, TYPE_TOKEN_TASK, auth);
        this.auth = auth;
    }

    @Override
    public String request(URL url) throws Exception {
        String refreshToken = auth.getRefreshToken();
        return NetworkHandler.handleConnection(url, NetworkHandler.REQUEST_TYPE_PUT, null, refreshToken);
    }

    @Override
    public void processResponse(JSONObject responseJson) {
        try {
            auth.storeTokens(responseJson);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
