package com.unlam.droidamp.activities.login.asynctasks;

import android.content.Intent;
import android.util.Log;

import com.unlam.droidamp.auth.Auth;

import com.unlam.droidamp.interfaces.RequestCallback;
import com.unlam.droidamp.network.NetworkHandler;
import com.unlam.droidamp.network.NetworkTask;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.net.URL;

public class LoginTask extends NetworkTask {
    private String email;
    private String password;
    private Auth auth;

    public LoginTask(RequestCallback<String> callback, String email, String password, Auth auth) {
        super(callback);
        this.email = email;
        this.password = password;
        this.auth = auth;
    }

    /**
     * Defines work to perform on the background thread.
     */
    @Override
    protected NetworkTask.Result doInBackground(String... urls) {
        Result result = null;
        Log.i("Log", "Login task");
        if (!isCancelled() && urls != null && urls.length > 0) {
            String urlString = urls[0];
            try {
                URL url = new URL(urlString);

                String resultString = login(url);

                if (resultString != null) {
                    // Create json object from response string
                    JSONObject responseJson = new JSONObject(resultString);

                    auth.storeTokens(responseJson);

                    result = new Result(resultString);
                }
            } catch(Exception e) {
                result = new Result(e);
            }
        }
        return result;
    }

    private String login(URL url) throws IOException, JSONException {
        JSONObject data = new JSONObject();
        data.put("email", email);
        data.put("password", password);
        data.put("env", "PROD");
        return NetworkHandler.handleConnection(url, NetworkHandler.REQUEST_TYPE_POST, data, null);
    }
}
