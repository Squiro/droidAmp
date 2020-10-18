package com.unlam.droidamp.network;

import android.os.AsyncTask;
import com.unlam.droidamp.interfaces.RequestCallback;

public class NetworkTask extends AsyncTask<String, Integer, NetworkTask.Result> {

    private RequestCallback<String> callback;

    public NetworkTask(RequestCallback<String> callback)
    {
        this.callback = callback;
    }

    /**
     * Wrapper class that serves as a union of a result value and an exception. When the request
     * task has completed, either the result value or exception can be a non-null value.
     * This allows you to pass exceptions to the UI thread that were thrown during doInBackground().
     */
    protected class Result {
        public String resultValue;
        public Exception exception;

        public Result(String resultValue) {
            this.resultValue = resultValue;
        }
        public Result(Exception exception) {
            this.exception = exception;
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
            if (!broadcastConnectivity.isConnected())
            {
                // If no connectivity, cancel task and update Callback with null data.
                callback.updateFromRequest("No hay conexión a internet.");
                cancel(true);
            }
        }
    }

    @Override
    protected NetworkTask.Result doInBackground(String... urls)
    {
        return null;
    }

    /**
     * Updates the LoginCallback with the result.
     */
    @Override
    protected void onPostExecute(NetworkTask.Result result) {
        if (result != null && callback != null) {
            if (result.exception != null) {
                callback.updateFromRequest(result.exception.getMessage());
            } else if (result.resultValue != null) {
                callback.updateFromRequest(result.resultValue);
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
