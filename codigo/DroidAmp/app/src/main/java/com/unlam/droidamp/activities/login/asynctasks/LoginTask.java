package com.unlam.droidamp.activities.login.asynctasks;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import com.unlam.droidamp.auth.Auth;
import com.unlam.droidamp.interfaces.LoginCallback;
import com.unlam.droidamp.network.NetworkHandler;

import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class LoginTask extends AsyncTask<String, Integer, LoginTask.Result> {

    private LoginCallback<String> callback;
    private String email;
    private String password;

    public LoginTask(LoginCallback<String> callback, String email, String password) {
        setCallback(callback);
        this.email = email;
        this.password = password;
    }

    void setCallback(LoginCallback<String> callback) {
        this.callback = callback;
    }

    /**
     * Wrapper class that serves as a union of a result value and an exception. When the download
     * task has completed, either the result value or exception can be a non-null value.
     * This allows you to pass exceptions to the UI thread that were thrown during doInBackground().
     */
    static class Result {
        public String resultValue;
        public Exception exception;
        public Intent intent;

        public Result(String resultValue, Intent intent) {
            this.resultValue = resultValue;
            this.intent = intent;
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
            NetworkInfo networkInfo = callback.getActiveNetworkInfo();
            if (networkInfo == null || !networkInfo.isConnected() ||
                    (networkInfo.getType() != ConnectivityManager.TYPE_WIFI
                            && networkInfo.getType() != ConnectivityManager.TYPE_MOBILE))
            {
                // If no connectivity, cancel task and update Callback with null data.
                callback.updateFromLogin(null);
                cancel(true);
            }
        }
    }

    /**
     * Defines work to perform on the background thread.
     */
    @Override
    protected LoginTask.Result doInBackground(String... urls) {
        Result result = null;
        if (!isCancelled() && urls != null && urls.length > 0) {
            String urlString = urls[0];
            try {
                URL url = new URL(urlString);

                String resultString = login(url);

                if (resultString != null) {
                    // Create json object from response string
                    JSONObject responseJson = new JSONObject(resultString);
                    // Extract auth token from response
                    String authToken = responseJson.get("token").toString();
                    // Exact refresh token from response
                    String refreshToken = responseJson.get("token_refresh").toString();

                    final Intent intent = new Intent();
                    intent.putExtra(Auth.PARAM_AUTH_TOKEN, authToken);
                    intent.putExtra(Auth.PARAM_REFRESH_TOKEN, refreshToken);

                    result = new Result(resultString, intent);
                } else {
                    throw new IOException("No response received.");
                }

            } catch(Exception e) {
                result = new Result(e);
            }
        }
        return result;
    }

    /**
     * Updates the LoginCallback with the result.
     */
    @Override
    protected void onPostExecute(Result result) {
        if (result != null && callback != null) {
            if (result.exception != null) {
                callback.updateFromLogin(result.exception.getMessage());
            } else if (result.resultValue != null) {
                callback.updateFromLogin(result.resultValue);
            }
            callback.finishLogin(result.intent);
        }
    }

    /**
     * Override to add special behavior for cancelled AsyncTask.
     */
    @Override
    protected void onCancelled(Result result) {

    }

    /**
     * Given a URL, sets up a connection and gets the HTTP response body from the server.
     * If the network request is successful, it returns the response body in String form. Otherwise,
     * it will throw an IOException.
     */
    private String login(URL url) throws IOException {
        InputStream stream = null;
        DataOutputStream connectionOutputstream = null;
        HttpURLConnection connection = null;
        String result = null;
        JSONObject jsonObject = new JSONObject();

        try {
            connection = NetworkHandler.getRequest(url, NetworkHandler.REQUEST_TYPE_POST);

            // We get the outputstream from the connection
            connectionOutputstream = new DataOutputStream(connection.getOutputStream());

            // Add fields to the json object
            jsonObject.put("email", this.email);
            jsonObject.put("password", this.password);
            // Write json object to output stream
            connectionOutputstream.writeBytes(jsonObject.toString());

            Log.i("JSON Input", jsonObject.toString());

            connectionOutputstream.flush();
            connectionOutputstream.close();

            // Open communications link (network traffic occurs here).
            connection.connect();

            publishProgress(LoginCallback.Progress.CONNECT_SUCCESS);
            int responseCode = connection.getResponseCode();

            if (responseCode != HttpsURLConnection.HTTP_OK) {
                return null;
                //throw new IOException("HTTP error code: " + responseCode);
            }

            // Retrieve the response body as an InputStream.
            stream = connection.getInputStream();
            publishProgress(LoginCallback.Progress.GET_INPUT_STREAM_SUCCESS, 0);
            if (stream != null) {
                // Converts Stream to String with max length of 500.
                result = NetworkHandler.readStream(stream, 500);
            }
        }
        catch (Exception e) {
            Log.i("Exception", "Exception encountered in LoginTask after httpConection");
            Log.i("Exception", e.toString());
        }
        finally {
            // Close Stream and disconnect HTTPS connection.
            if (stream != null) {
                stream.close();
            }
            if (connection != null) {
                connection.disconnect();
            }
        }
        return result;
    }
}
