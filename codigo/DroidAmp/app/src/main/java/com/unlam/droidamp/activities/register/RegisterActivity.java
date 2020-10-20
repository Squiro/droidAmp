package com.unlam.droidamp.activities.register;

import androidx.appcompat.app.AppCompatActivity;

import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.unlam.droidamp.R;
import com.unlam.droidamp.auth.Auth;
import com.unlam.droidamp.auth.AuthFragment;
import com.unlam.droidamp.interfaces.RequestCallback;
import com.unlam.droidamp.models.User;
import com.unlam.droidamp.network.BroadcastConnectivity;

public class RegisterActivity extends AppCompatActivity implements RequestCallback<String> {

    AuthFragment authFragment;
    Button btnRegister;
    boolean registerInProgress;

    private Auth auth;
    private BroadcastConnectivity broadcastConnectivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        this.broadcastConnectivity = new BroadcastConnectivity(this);
        this.registerReceiver(broadcastConnectivity, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        this.auth = new Auth(this);

        // -------- UI ELEMENTS --------
        btnRegister = findViewById(R.id.btnRegister);

        // -------- LISTENERS --------
        btnRegister.setOnClickListener(btnRegisterListener);

        // -------- NETWORK FRAGMENT --------
        // We instantiate the network fragment that will handle the register action from the user in background
        authFragment = AuthFragment.getInstance(AuthFragment.class, getSupportFragmentManager());
        this.registerInProgress = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.unregisterReceiver(broadcastConnectivity);
    }

    // Listener for login button
    private View.OnClickListener btnRegisterListener = new View.OnClickListener()
    {
        // This method will be executed once the button is clicked
        public void onClick(View v)
        {
            register();
        }
    };

    public void register()
    {
        if (!registerInProgress && authFragment != null) {
            User user = new User("email", "pass");
            // Execute the async login.
            authFragment.startRegister(user, auth);
            registerInProgress = true;
        }
    }

    @Override
    public void updateFromRequest(String result) {

    }

    @Override
    public BroadcastConnectivity getBroadcastConnectivity() {
        return this.broadcastConnectivity;
    }

    @Override
    public void finishRequest() {
        Log.i("Log", "Request finished");
    }
}
