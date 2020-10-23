package com.unlam.droidamp.activities.main.asynctask;

import android.util.Log;

import com.unlam.droidamp.models.Event;
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
        super(callback);
        this.auth = auth;
        this.event = event;
    }

    @Override
    public String request(URL url) throws Exception {
        String token = auth.getToken();
        Log.i("Log", token);
        JSONObject payload = event.toJSONObject();
        payload.put("env", "TEST");

        return NetworkHandler.handleConnection(url, NetworkHandler.REQUEST_TYPE_POST, payload, token);
    }

    @Override
    public void processResponse(JSONObject responseJson) {

    }
}
