package com.unlam.droidamp.auth;

import com.unlam.droidamp.interfaces.RequestCallback;
import com.unlam.droidamp.network.NetworkHandler;
import com.unlam.droidamp.network.NetworkTask;
import org.json.JSONObject;
import java.io.IOException;
import java.net.URL;

public class TokenTask extends NetworkTask {

    private Auth auth;

    public TokenTask(RequestCallback<String> callback, Auth auth) {
        super(callback);
        this.auth = auth;
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

                String resultString = refreshToken(url);

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

    private String refreshToken(URL url) throws IOException {
        String refreshToken = auth.getRefreshToken();
        return NetworkHandler.handleConnection(url, NetworkHandler.REQUEST_TYPE_PUT, null, refreshToken);
    }

}
