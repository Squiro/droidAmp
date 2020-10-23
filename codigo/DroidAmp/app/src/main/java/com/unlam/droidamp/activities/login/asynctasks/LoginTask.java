package com.unlam.droidamp.activities.login.asynctasks;

import com.unlam.droidamp.auth.Auth;
import com.unlam.droidamp.interfaces.RequestCallback;
import com.unlam.droidamp.network.NetworkHandler;
import com.unlam.droidamp.network.NetworkTask;

import org.json.JSONException;
import org.json.JSONObject;
import java.net.URL;

public class LoginTask extends NetworkTask {
    private String email;
    private String password;
    private Auth auth;

    public LoginTask(RequestCallback<NetworkTask.Result> callback, String email, String password, Auth auth) {
        super(callback);
        this.email = email;
        this.password = password;
        this.auth = auth;
    }

    @Override
    public String request(URL url) throws Exception {
        JSONObject data = new JSONObject();
        data.put("email", email);
        data.put("password", password);
        data.put("env", "PROD");
        return NetworkHandler.handleConnection(url, NetworkHandler.REQUEST_TYPE_POST, data, null);
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
