package com.unlam.droidamp.activities.register.asynctasks;


import com.unlam.droidamp.auth.Auth;
import com.unlam.droidamp.interfaces.RequestCallback;
import com.unlam.droidamp.models.User;

import com.unlam.droidamp.network.NetworkHandler;
import com.unlam.droidamp.network.NetworkTask;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.net.URL;

public class RegisterTask extends NetworkTask {
    private User user;
    private Auth auth;

    public RegisterTask(RequestCallback<NetworkTask.Result> callback, User user, Auth auth) {
        super(callback);
        this.user = user;
        this.auth = auth;
    }

    /**
     * Defines work to perform on the background thread.
     */
    @Override
    protected NetworkTask.Result doInBackground(String... urls) {
        Result result = null;
        if (!isCancelled() && urls != null && urls.length > 0) {
            String urlString = urls[0];
            try {
                URL url = new URL(urlString);

                String resultString = register(url);

                if (resultString != null) {
                    // Create json object from response string
                    JSONObject responseJson = new JSONObject(resultString);

                    auth.storeTokens(responseJson);

                    result = new Result(resultString, true);
                }
            } catch(Exception e) {
                result = new Result(e, false);
            }
        }
        return result;
    }

    private String register(URL url) throws Exception {
        JSONObject data = user.toJSONObject();
        data.put("env", "TEST");
        return NetworkHandler.handleConnection(url, NetworkHandler.REQUEST_TYPE_POST, data, null);
    }
}
