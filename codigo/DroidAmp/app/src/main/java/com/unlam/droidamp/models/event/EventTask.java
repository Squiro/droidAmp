package com.unlam.droidamp.models.event;

import com.unlam.droidamp.auth.Auth;
import com.unlam.droidamp.interfaces.RequestCallback;
import com.unlam.droidamp.network.NetworkHandler;
import com.unlam.droidamp.network.NetworkTask;

import org.json.JSONObject;

import java.net.URL;

public class EventTask extends NetworkTask {

    private Auth auth;
    private Event event;

    public EventTask(RequestCallback<NetworkTask.Result> callback, Event event, Auth auth) {
        super(callback, TYPE_EVENT_TASK);
        this.auth = auth;
        this.event = event;
    }

    @Override
    public String request(URL url) throws Exception {
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
