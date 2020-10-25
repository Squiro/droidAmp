package com.unlam.droidamp.network;

import android.os.AsyncTask;
import com.unlam.droidamp.interfaces.RequestCallback;

import org.json.JSONObject;
import java.net.URL;

public class NetworkTask extends AsyncTask<String, Integer, NetworkTask.Result> {

    private RequestCallback<NetworkTask.Result> callback;

    public NetworkTask(RequestCallback<NetworkTask.Result> callback)
    {
        this.callback = callback;
    }

    /**
     * Wrapper class that serves as a union of a result value and an exception. When the request
     * task has completed, either the result value or exception can be a non-null value.
     * This allows you to pass exceptions to the UI thread that were thrown during doInBackground().
     */
    public class Result {
        public String resultValue;
        public Exception exception;
        public boolean success;

        public Result(String resultValue, boolean success) {
            this.resultValue = resultValue;
            this.success = success;
        }
        public Result(Exception exception, boolean success) {
            this.exception = exception;
            this.success = success;
        }
    }

    /**
     * Cancel background network operation if we do not have network connectivity.
     */
    @Override
    protected void onPreExecute() {
        if (callback != null)
        {
            BroadcastConnectivity broadcastConnectivity = callback.getBroadcastConnectivity();
            if (broadcastConnectivity == null || !broadcastConnectivity.isConnected())
            {
                // If no connectivity, cancel task and update Callback with null data.
                //callback.updateFromRequest("No hay conexión a internet.");
                cancel(true);
            }
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
                    result = new Result(resultString, true);
                }
            } catch(Exception e) {
                    result = new Result(e, false);
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
                //callback.updateFromRequest(result.exception.getMessage());
                callback.updateFromRequest(result);
            } else if (result.resultValue != null)
            {
                callback.updateFromRequest(result);
            }
            callback.finishRequest();
        }
    }

    /**
     * Override to add special behavior for cancelled AsyncTask.
     */
    @Override
    protected void onCancelled(NetworkTask.Result result) {
        callback.finishRequest();
    }

}
