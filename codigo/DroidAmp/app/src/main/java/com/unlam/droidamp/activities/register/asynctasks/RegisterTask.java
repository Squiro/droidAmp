package com.unlam.droidamp.activities.register.asynctasks;

import com.unlam.droidamp.auth.Auth;
import com.unlam.droidamp.interfaces.RequestCallback;
import com.unlam.droidamp.models.User;

import com.unlam.droidamp.network.NetworkHandler;
import com.unlam.droidamp.network.NetworkTask;

import org.json.JSONException;
import org.json.JSONObject;
import java.net.URL;

public class RegisterTask extends NetworkTask {
    private User user;
    private Auth auth;

    public RegisterTask(RequestCallback<NetworkTask.Result> callback, User user, Auth auth) {
        super(callback, TYPE_REGISTER_TASK);
        this.user = user;
        this.auth = auth;
    }

    @Override
    public String request(URL url) throws Exception {
        JSONObject data = user.toJSONObject();
        data.put("env", "TEST");
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
