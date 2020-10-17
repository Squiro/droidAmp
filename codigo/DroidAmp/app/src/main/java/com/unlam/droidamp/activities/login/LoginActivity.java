package com.unlam.droidamp.activities.login;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.unlam.droidamp.R;
import com.unlam.droidamp.activities.main.MainActivity;
import com.unlam.droidamp.auth.Auth;
import com.unlam.droidamp.auth.Encryption;
import com.unlam.droidamp.activities.login.fragments.NetworkLoginFragment;
import com.unlam.droidamp.interfaces.LoginCallback;
import com.unlam.droidamp.network.BroadcastConnectivity;
import com.unlam.droidamp.network.NetworkHandler;


public class LoginActivity extends AppCompatActivity implements LoginCallback<String> {
    // UI elements
    private EditText txtEmail;
    private EditText txtPassword;
    private TextView txtRegister;
    private TextView txtError;
    private Button btnLogin;

    // Network related properties
    private NetworkLoginFragment networkLoginFragment;
    private boolean logginIn;

    Auth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        BroadcastConnectivity broadcastConnectivity = new BroadcastConnectivity(this);
        this.registerReceiver(broadcastConnectivity, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        // Generate encription key first time run
        Encryption enc = new Encryption();
        enc.generateKey();

        // Do this before all the heavy work. No need to do the rest if we are going to switch
        // activities
        this.auth = new Auth(this);

        if (!auth.checkIfTokenExpired())
        {
            startMainActivity();
        }
        // -------- UI ELEMENTS --------

        // We "fetch" the UI elements here, so it's easier to reference them later
        txtEmail = findViewById(R.id.txtEmail);
        txtPassword = findViewById(R.id.txtPassword);
        txtRegister = findViewById(R.id.txtRegister);
        txtError = findViewById(R.id.txtError);
        btnLogin = findViewById(R.id.btnLogin);
        // We set an on click listener for the login button
        btnLogin.setOnClickListener(btnLoginListener);

        // -------- NETWORK LOGIN --------
        // We instantiate the network login fragment that will handle the login action from the user in background
        networkLoginFragment = NetworkLoginFragment.getInstance(getSupportFragmentManager(), "http://so-unlam.net.ar/api/api/login");
        this.logginIn = false;
    }

    // Listener for login button
    private View.OnClickListener btnLoginListener = new View.OnClickListener()
    {
        // This method will be executed once the button is clicked
        public void onClick(View v)
        {
            login();
        }
    };

    // Listener for login button
    private View.OnClickListener txtRegisterListener = new View.OnClickListener()
    {
        // This method will be executed once the button is clicked
        public void onClick(View v)
        {
            startRegisterActivity();
        }
    };

    private void startRegisterActivity()
    {
        Intent registerActivityIntent = new Intent(this, MainActivity.class);
        startActivity(registerActivityIntent);
    }

    private void login() {
        if (!logginIn && networkLoginFragment != null) {
            // Execute the async login.
            networkLoginFragment.startLogin(txtEmail.getText().toString(), txtPassword.getText().toString());
            logginIn = true;
        }
    }

    @Override
    public void updateFromLogin(String result) {
        Log.i("Log", result);
    }

    @Override
    public NetworkInfo getActiveNetworkInfo() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo;
    }

    @Override
    public void onProgressUpdate(int progressCode, int percentComplete) {
        switch(progressCode) {
            // You can add UI behavior for progress updates here.
            case Progress.ERROR:

                break;
            case Progress.CONNECT_SUCCESS:

                break;
            case Progress.GET_INPUT_STREAM_SUCCESS:

                break;
            case Progress.PROCESS_INPUT_STREAM_IN_PROGRESS:

                break;
            case Progress.PROCESS_INPUT_STREAM_SUCCESS:

                break;
        }
    }

    @Override
    public void finishLogin(Intent intent) {
        logginIn = false;

        if (networkLoginFragment != null) {
            networkLoginFragment.cancelLogin();
        }

        if (intent != null) {
            String authToken = intent.getStringExtra(Auth.PARAM_AUTH_TOKEN);
            String refreshToken = intent.getStringExtra(Auth.PARAM_REFRESH_TOKEN);

            auth.saveTokens(authToken, refreshToken);
            auth.saveRefreshTimestamp();

            startMainActivity();
        }
    }

    public void startMainActivity()
    {
        Intent mainActivityIntent = new Intent(this, MainActivity.class);
        startActivity(mainActivityIntent);
    }
}
