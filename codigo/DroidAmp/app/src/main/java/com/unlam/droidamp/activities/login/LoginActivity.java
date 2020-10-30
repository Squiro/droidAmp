package com.unlam.droidamp.activities.login;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.unlam.droidamp.R;
import com.unlam.droidamp.activities.album.AlbumActivity;
import com.unlam.droidamp.activities.base.BaseActivity;
import com.unlam.droidamp.activities.register.RegisterActivity;
import com.unlam.droidamp.auth.AuthFragment;
import com.unlam.droidamp.models.event.Event;
import com.unlam.droidamp.models.User;
import com.unlam.droidamp.network.NetworkTask;
import com.unlam.droidamp.utilities.Encryption;
import com.unlam.droidamp.utilities.InputValidatorHelper;

public class LoginActivity extends BaseActivity {
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Generate encryption key first time run
        Encryption enc = new Encryption();
        enc.generateKey();

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

        // -------- NETWORK FRAGMENT --------
        this.logginIn = false;
        // Instantiate auth fragment
        authFragment = AuthFragment.getInstance(AuthFragment.class, getSupportFragmentManager(), AuthFragment.TAG);
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
            startActivity(RegisterActivity.class, false);
        }
    };

    private void login() {
        if (validateInputs())
        {
            if (!logginIn && authFragment != null) {
                // Execute the async login.
                progressBar.setVisibility(View.VISIBLE);
                User user = new User(txtEmail.getText().toString(), txtPassword.getText().toString());
                authFragment.startLogin(user, auth);
                logginIn = true;
            }
        }
    }
    @Override
    public void updateFromRequest(NetworkTask.Result result) {
        Log.i("Log", "LOGIN ------- UpdateFromRequest");

        if (result.success)
        {
            startActivity(AlbumActivity.class, true);
            // Register execution of background event
            networkEventFragment.startEventTask(new Event(Event.TYPE_BACKGROUND, Event.DESCRIPTION_BACKGROUND), this.auth);
            this.networkEventFragment.startEventTask(new Event(Event.TYPE_LOGIN, Event.DESCRIPTION_LOGIN), this.auth);
        }
        else
        {
            txtError.setText(result.resultValue);
        }
        progressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    public void finishRequest(int taskType) {
        Log.i("Log", "Finish REQUEST " + taskType);
        switch (taskType)
        {
            case NetworkTask.TYPE_LOGIN_TASK:
                logginIn = false;

                if (authFragment != null) {
                    authFragment.cancelTask();
                }
                break;

            case NetworkTask.TYPE_EVENT_TASK:
                if (networkEventFragment != null)
                {
                    Log.i("Log", "Event Task Finished");
                    networkEventFragment.cancelTask();
                }
                break;
        }
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
