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
import com.unlam.droidamp.activities.register.RegisterActivity;
import com.unlam.droidamp.auth.Auth;
import com.unlam.droidamp.utilities.Encryption;
import com.unlam.droidamp.activities.login.fragments.NetworkLoginFragment;
import com.unlam.droidamp.interfaces.LoginCallback;
import com.unlam.droidamp.network.BroadcastConnectivity;
import com.unlam.droidamp.utilities.InputValidatorHelper;
import com.unlam.droidamp.utilities.TextValidator;


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
    private BroadcastConnectivity broadcastConnectivity;

    private Auth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        broadcastConnectivity = new BroadcastConnectivity(this);
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
        btnLogin = findViewById(R.id.btnLogin);
        txtError = findViewById(R.id.txtError);

        // -------- LISTENERS (hey, listen!) --------

        // We set an on click listener for the login button
        btnLogin.setOnClickListener(btnLoginListener);
        txtRegister.setOnClickListener(txtRegisterListener);

        //setValidationListeners();

        // -------- NETWORK LOGIN --------
        // We instantiate the network login fragment that will handle the login action from the user in background
        networkLoginFragment = NetworkLoginFragment.getInstance(getSupportFragmentManager(), "http://so-unlam.net.ar/api/api/login");
        this.logginIn = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.unregisterReceiver(broadcastConnectivity);
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
        Intent registerActivityIntent = new Intent(this, RegisterActivity.class);
        startActivity(registerActivityIntent);
    }

    private void login() {
        if (validateInputs())
        {
            if (!logginIn && networkLoginFragment != null) {
                // Execute the async login.
                networkLoginFragment.startLogin(txtEmail.getText().toString(), txtPassword.getText().toString());
                logginIn = true;
            }
        }
    }

    @Override
    public void updateFromLogin(String result) {
        Log.i("Log", "UpdateFromLogin");
        Log.i("Log", result);
    }

    @Override
    public BroadcastConnectivity getBroadcastConnectivity() {
        return this.broadcastConnectivity;
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
        this.finish();
    }

    private void setValidationListeners()
    {
        // Email
        txtEmail.addTextChangedListener(new TextValidator(txtEmail)
        {
            @Override
            public void validate(TextView textView, String text)
            {
                if (!InputValidatorHelper.isValidEmail(txtEmail.getText().toString()))
                {
                    txtError.setText(R.string.invalid_email);
                }
            }
        });
    }

    public boolean validateInputs()
    {
        boolean valida = true;

        txtError.setText("");

        if (!InputValidatorHelper.isValidEmail(txtEmail.getText().toString()))
        {
            txtError.setText(R.string.invalid_email);
            valida = false;
        }

        if (!InputValidatorHelper.isLengthyEnough(txtPassword.getText().toString(), 8))
        {
            txtError.setText(R.string.invalid_password);
            valida = false;
        }

        return valida;
    }
}
