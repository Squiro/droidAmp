package com.unlam.droidamp.network;

import android.os.AsyncTask;
import android.util.Log;

import com.unlam.droidamp.auth.Auth;
import com.unlam.droidamp.auth.TokenTask;
import com.unlam.droidamp.interfaces.RequestCallback;
import com.unlam.droidamp.models.event.Event;
import com.unlam.droidamp.models.event.EventTask;

import org.json.JSONObject;
import java.net.URL;

public class NetworkTask extends AsyncTask<String, Integer, NetworkTask.Result> {

    protected RequestCallback<NetworkTask.Result> callback;
    protected int taskType;
    protected Auth auth;
    public static final int TYPE_EVENT_TASK = 0;
    public static final int TYPE_LOGIN_TASK = 1;
    public static final int TYPE_REGISTER_TASK = 2;
    public static final int TYPE_TOKEN_TASK = 3;
    public static final int TYPE_ALBUMRESOLVER_TASK = 4;
    public static final int TYPE_MUSICRESOLVER_TASK = 5;

    public NetworkTask(RequestCallback<NetworkTask.Result> callback, int taskType, Auth auth)
    {
        this.callback = callback;
        this.taskType = taskType;
        this.auth = auth;
    }

    /**
     * Wrapper class that serves as a union of a result value and an exception. When the request
     * task has completed, either the result value or exception can be a non-null value.
     * This allows you to pass exceptions to the UI thread that were thrown during doInBackground().
     */
    public class Result {
        public String resultValue;
        public Exception exception;
        public int taskType;
        public boolean success;

        public Result(String resultValue, boolean success, int taskType) {
            this.resultValue = resultValue;
            this.success = success;
            this.taskType = taskType;
        }
        public Result(Exception exception, boolean success, int taskType) {
            this.exception = exception;
            this.success = success;
            this.taskType = taskType;
        }
    }

    /**
     * Cancel background network operation if we do not have network connectivity.
     */
    @Override
    protected void onPreExecute() {
        if (callback != null)
        {
            checkConnection();
        }
    }

    protected void checkConnection()
    {
        BroadcastConnectivity broadcastConnectivity = callback.getBroadcastConnectivity();

        if (broadcastConnectivity == null || !broadcastConnectivity.isConnected())
        {
            // If no connectivity, cancel task and update Callback with null data.
            cancel(true);
        }
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

                String resultString = request(url);

                if (resultString != null) {
                    // Create json object from response string
                    JSONObject responseJson = new JSONObject(resultString);
                    processResponse(responseJson);
                    result = new Result(resultString, true, this.taskType);
                }
            } catch(Exception e) {
                    result = new Result(e, false, this.taskType);
            }
        }
        return result;
    }

    public String request(URL url) throws Exception {
        return null;
    }

    public void processResponse(JSONObject responseJson) {
    }

    /**
     * Updates the RequestCallback with the result.
     */
    @Override
    protected void onPostExecute(NetworkTask.Result result) {
        if (result != null && callback != null)
        {
            if (result.exception != null)
            {
                callback.updateFromRequest(result);
            } else if (result.resultValue != null)
            {
                callback.updateFromRequest(result);
            }
            callback.finishRequest(taskType);
        }
    }

    /**
     * Override to add special behavior for cancelled AsyncTask.
     */
    @Override
    protected void onCancelled() {
        callback.finishRequest(taskType);
    }

}
