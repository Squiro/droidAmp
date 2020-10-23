package com.unlam.droidamp.activities.login;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Network;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.unlam.droidamp.R;
import com.unlam.droidamp.activities.main.MainActivity;
import com.unlam.droidamp.activities.register.RegisterActivity;
import com.unlam.droidamp.auth.Auth;
import com.unlam.droidamp.auth.AuthFragment;
import com.unlam.droidamp.interfaces.RequestCallback;
import com.unlam.droidamp.network.NetworkTask;
import com.unlam.droidamp.utilities.Encryption;
import com.unlam.droidamp.network.BroadcastConnectivity;
import com.unlam.droidamp.utilities.InputValidatorHelper;
import com.unlam.droidamp.utilities.TextValidator;


public class LoginActivity extends AppCompatActivity implements RequestCallback<NetworkTask.Result> {
    // UI elements
    private EditText txtEmail;
    private EditText txtPassword;
    private TextView txtRegister;
    private TextView txtError;
    private Button btnLogin;
    private ProgressBar progressBar;

    // Network related properties
    private AuthFragment authFragment;
    private boolean logginIn;
    private BroadcastConnectivity broadcastConnectivity;

    private Auth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Generate encryption key first time run
        Encryption enc = new Encryption();
        enc.generateKey();

        // Instantiate auth class
        this.auth = new Auth(this);

        // -------- UI ELEMENTS --------

        // We "fetch" the UI elements here, so it's easier to reference them later
        txtEmail = findViewById(R.id.txtEmail);
        txtPassword = findViewById(R.id.txtPassword);
        txtRegister = findViewById(R.id.txtRegister);
        btnLogin = findViewById(R.id.btnLogin);
        txtError = findViewById(R.id.txtError);
        progressBar = findViewById(R.id.pgBarLogin);

        // -------- LISTENERS (hey, listen!) --------

        // We set an on click listener for the login button
        btnLogin.setOnClickListener(btnLoginListener);
        txtRegister.setOnClickListener(txtRegisterListener);

        //setValidationListeners();

        // -------- NETWORK FRAGMENT --------
        this.logginIn = false;
        // Instantiate auth fragment
        authFragment = AuthFragment.getInstance(AuthFragment.class, getSupportFragmentManager());

        broadcastConnectivity = new BroadcastConnectivity(this);
        this.registerReceiver(broadcastConnectivity, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
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
            if (!logginIn && authFragment != null) {
                // Execute the async login.
                progressBar.setVisibility(View.VISIBLE);
                authFragment.startLogin(txtEmail.getText().toString(), txtPassword.getText().toString(), auth);
                logginIn = true;
            }
        }
    }
    @Override
    public void updateFromRequest(NetworkTask.Result result) {
        Log.i("Log", "UpdateFromRequest");

        if (result.success)
        {
            startMainActivity();
        }
        else
        {
            switch (Integer.parseInt(result.exception.getMessage()))
            {
                case ResponseCode.BAD_REQUEST:
                    txtError.setText(R.string.invalid_email_or_pass);
                    break;

                default:
                    txtError.setText(result.exception.getMessage());
                    break;

            }
            //txtError.setText(result.exception.getMessage());
            Log.i("Log", "Not successful");
        }
        progressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    public BroadcastConnectivity getBroadcastConnectivity() {
        return this.broadcastConnectivity;
    }

    @Override
    public void finishRequest() {
        logginIn = false;

        if (authFragment != null) {
            authFragment.cancelTask();
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
