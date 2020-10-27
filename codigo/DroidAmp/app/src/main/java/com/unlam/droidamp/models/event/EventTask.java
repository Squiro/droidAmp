package com.unlam.droidamp.models.event;

import android.util.Log;

import com.unlam.droidamp.auth.Auth;
import com.unlam.droidamp.auth.TokenTask;
import com.unlam.droidamp.interfaces.RequestCallback;
import com.unlam.droidamp.network.NetworkHandler;
import com.unlam.droidamp.network.NetworkTask;

import org.json.JSONObject;
import java.net.URL;

public class EventTask extends NetworkTask {

    private Auth auth;
    private Event event;

    public EventTask(RequestCallback<NetworkTask.Result> callback, Event event, Auth auth) {
        super(callback, TYPE_EVENT_TASK, auth);
        this.auth = auth;
        this.event = event;
    }

    @Override
    public String request(URL url) throws Exception {
        // Check if tokens are expired
        if (auth.checkIfTokenExpired())
        {
            Log.i("Log", "Token expired in event task");
            TokenTask tokenTask = new TokenTask(this.callback, auth);
            // Get freezes thread until task is resolved. We need this, because we don't want to POST the event with
            // expired tokens
            tokenTask.execute(NetworkHandler.API_ENDPOINT + "refresh").get();
        }
        Log.i("Log", "After token check");
        String token = auth.getToken();
        JSONObject payload = event.toJSONObject();
        payload.put("env", "TEST");
        return NetworkHandler.handleConnection(url, NetworkHandler.REQUEST_TYPE_POST, payload, token);
    }

    @Override
    public void processResponse(JSONObject responseJson) {

    }

    /**
     * Override onPostExecute from parent class.
     * We won't really update the UI based on the result of an EventTask... so we just finish the task
     */
    @Override
    protected void onPostExecute(NetworkTask.Result result) {
        if (result != null && this.callback != null)
        {
            callback.finishRequest(this.taskType);
        }
    }
}
