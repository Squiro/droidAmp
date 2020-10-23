package com.unlam.droidamp.network;

import android.util.Log;

import androidx.annotation.Nullable;

import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import javax.net.ssl.HttpsURLConnection;

public class NetworkHandler {

    private static final int READ_TIMEOUT = 5000;
    private static final int CONNECT_TIMEOUT = 5000;

    public static final String REQUEST_TYPE_POST = "POST";
    public static final String REQUEST_TYPE_GET = "GET";
    public static final String REQUEST_TYPE_PUT = "PUT";
    public static final String API_ENDPOINT = "http://so-unlam.net.ar/api/api/";

    /**
     * Given a URL, sets up a connection and gets the HTTP response body from the server.
     * If the network request is successful, it returns the response body in String form. Otherwise,
     * it returns null.
     */
    public static String handleConnection(URL url, String requestType, @Nullable JSONObject payload, @Nullable String token) throws Exception {
        InputStream stream = null;
        HttpURLConnection connection = null;
        String result = null;

        try {
            connection = NetworkHandler.getRequest(url, requestType);

            configureConnection(connection, token);

            // Write the JSONObject to the connection
            if (payload != null)
                writePayload(connection, payload);

            // Open communications link (network traffic occurs here).
            connection.connect();

            int responseCode = connection.getResponseCode();
            Log.i("Log", "Connection response");
            Log.i("Log", Integer.toString(responseCode));

            if (responseCode == HttpsURLConnection.HTTP_OK || responseCode == HttpsURLConnection.HTTP_CREATED)
            {
                // Retrieve the response body as an InputStream.
                stream = connection.getInputStream();
            } else
            {
                // Retrieve the error stream
                stream = connection.getErrorStream();
                throw new Exception(Integer.toString(responseCode));
            }

            if (stream != null) {
                // Converts Stream to String with max length of 500.
                result = NetworkHandler.readStream(stream, 500);
            }
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

    public static void writePayload(HttpURLConnection connection, JSONObject payload) throws IOException {
        // We get the outputstream from the connection
        DataOutputStream connectionOutputstream = new DataOutputStream(connection.getOutputStream());

        // Write json object to output stream
        connectionOutputstream.writeBytes(payload.toString());

        connectionOutputstream.flush();
        connectionOutputstream.close();
    }

    public static HttpURLConnection getRequest(URL url, String requestType)
    {
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) url.openConnection();
            // For this use case, set HTTP method to POST.
            connection.setRequestMethod(requestType);
        } catch (Exception e)
        {
            Log.i("Exception", e.toString());
        }
        return connection;
    }

    private static void configureConnection(HttpURLConnection connection, @Nullable String token)
    {
        configureConnection(connection);
        if (token != null)
            connection.setRequestProperty("Authorization", "Bearer " + token);
    }

    private static void configureConnection(HttpURLConnection connection)
    {
        // Timeout for reading InputStream arbitrarily set to 5000ms.
        connection.setReadTimeout(READ_TIMEOUT);
        // Timeout for connection.connect() arbitrarily set to 5000ms.
        connection.setConnectTimeout(CONNECT_TIMEOUT);

        connection.setUseCaches(false);
        connection.setAllowUserInteraction(false);

        // We set the content type of the request
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Accept", "application/json");

        // Already true by default but setting just in case; needs to be true since this request
        // is carrying an input (response) body.
        connection.setDoInput(true);
        connection.setDoOutput(true);
    }

    /**
     * Converts the contents of an InputStream to a String.
     */
    public static String readStream(InputStream stream, int maxReadSize) throws IOException {
        InputStreamReader reader = new InputStreamReader(stream, StandardCharsets.UTF_8);
        char[] rawBuffer = new char[maxReadSize];
        int readSize;
        StringBuffer buffer = new StringBuffer();
        while (((readSize = reader.read(rawBuffer)) != -1) && maxReadSize > 0) {
            if (readSize > maxReadSize) {
                readSize = maxReadSize;
            }
            buffer.append(rawBuffer, 0, readSize);
            maxReadSize -= readSize;
        }
        return buffer.toString();
    }
}
