package com.unlam.droidamp.auth;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.unlam.droidamp.R;
import com.unlam.droidamp.utilities.Encryption;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;

public class Auth {

    private Context context;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    // Instantiate new encryption object
    private Encryption enc;

    // Static fields
    public static final long TOKEN_EXPIRE_TIME_MINUTES = 30;
    public static final String PARAM_REFRESH_TOKEN = "refreshToken";
    public static final String PARAM_AUTH_TOKEN = "authToken";
    public static final String PARAM_REFRESH_TIMESTAMP = "refreshTimeStamp";

    public Auth(Context context)
    {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(context.getString(R.string.sharedPreferencesFile), Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        this.enc = new Encryption();
    }

    public String getRefreshToken() throws NullPointerException
    {
        synchronized (enc)
        {
            String refreshToken = sharedPreferences.getString(PARAM_REFRESH_TOKEN, "");
            return enc.decrypt(this.context, refreshToken.getBytes(StandardCharsets.UTF_8));
        }
    }

    public String getToken() throws NullPointerException
    {
        String decryptedToken = null;
        synchronized (enc)
        {
            try {
                String token = sharedPreferences.getString(PARAM_AUTH_TOKEN, "");
                decryptedToken = enc.decrypt(this.context, token.getBytes(StandardCharsets.UTF_8));
            }
            catch (Exception e)
            {
                Log.i("Exception", e.toString());
            }
        }
        return decryptedToken;
    }

    public Boolean checkIfTokenExpired()
    {
        try
        {
            String refreshTimeStamp = sharedPreferences.getString(PARAM_REFRESH_TIMESTAMP, "");

            Timestamp oldTime = Timestamp.valueOf(refreshTimeStamp);
            Timestamp currentTime = new Timestamp(System.currentTimeMillis());
            return compareTwoTimeStamps(currentTime, oldTime) >= TOKEN_EXPIRE_TIME_MINUTES;
        }
        catch (Exception e)
        {
            //Catch format exceptions or things like that
            return true;
        }
    }

    public static long compareTwoTimeStamps(Timestamp currentTime, Timestamp oldTime)
    {
        long milliseconds1 = oldTime.getTime();
        long milliseconds2 = currentTime.getTime();

        long diff = milliseconds2 - milliseconds1;
        // Diff in minutes
        return diff / (60 * 1000);
    }

    public void saveTokens(String authToken, String refreshToken)
    {
        // Encrypt both tokens
        String encrypted_auth = enc.encrypt(context, authToken);
        String encrypted_refresh = enc.encrypt(context, refreshToken);
        // Store encrypted tokens
        editor.putString(PARAM_AUTH_TOKEN, encrypted_auth);
        editor.putString(PARAM_REFRESH_TOKEN, encrypted_refresh);
        editor.apply();
    }

    public void saveRefreshTimestamp()
    {
        // Save the timestamp of the refresh token
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        editor.putString(PARAM_REFRESH_TIMESTAMP, timestamp.toString());
        editor.apply();
    }

    public void storeTokens(JSONObject responseJson) throws JSONException {
        // Extract auth token from response
        String authToken = responseJson.get("token").toString();
        // Exact refresh token from response
        String refreshToken = responseJson.get("token_refresh").toString();
        Log.i("Log", refreshToken);

        saveTokens(authToken, refreshToken);
        saveRefreshTimestamp();
    }

}
