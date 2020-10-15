package com.unlam.droidamp.network;

import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

public class NetworkHandler {

    private static final int READ_TIMEOUT = 5000;
    private static final int CONNECT_TIMEOUT = 5000;

    public static final String REQUEST_TYPE_POST = "POST";
    public static final String REQUEST_TYPE_GET = "GET";
    public static final String REQUEST_TYPE_PUT = "PUT";

    public static HttpURLConnection getRequest(URL url, String requestType)
    {
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) url.openConnection();
            configureConnection(connection);
            // For this use case, set HTTP method to POST.
            connection.setRequestMethod(requestType);
        } catch (Exception e)
        {
            Log.i("Exception", e.toString());
        }
        return connection;
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
    public static String readStream(InputStream stream, int maxReadSize)
            throws IOException, UnsupportedEncodingException {
        InputStreamReader reader = new InputStreamReader(stream, "UTF-8");
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
