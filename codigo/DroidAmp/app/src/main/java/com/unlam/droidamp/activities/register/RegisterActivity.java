package com.unlam.droidamp.activities.register;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.unlam.droidamp.R;
import com.unlam.droidamp.activities.album.AlbumActivity;
import com.unlam.droidamp.activities.base.BaseActivity;
import com.unlam.droidamp.auth.AuthFragment;
import com.unlam.droidamp.models.User;
import com.unlam.droidamp.models.event.Event;
import com.unlam.droidamp.network.NetworkTask;
import com.unlam.droidamp.utilities.InputValidatorHelper;

public class RegisterActivity extends BaseActivity {

    private TextView txtError;
    private TextView txtNombre;
    private TextView txtApellido;
    private TextView txtEmail;
    private TextView txtPassword;
    private TextView txtDNI;
    private TextView txtComision;
    private Button btnRegister;
    private ProgressBar pgBarRegister;

    private AuthFragment authFragment;
    private boolean registerInProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // -------- UI ELEMENTS --------
        txtNombre = findViewById(R.id.txtNombre);
        txtApellido = findViewById(R.id.txtApellido);
        txtEmail = findViewById(R.id.txtEmail);
        txtPassword = findViewById(R.id.txtPassword);
        txtDNI = findViewById(R.id.txtDni);
        txtComision = findViewById(R.id.txtComision);
        txtError = findViewById(R.id.txtError);
        btnRegister = findViewById(R.id.btnRegister);
        pgBarRegister = findViewById(R.id.pgBarRegister);

        // -------- LISTENERS --------
        btnRegister.setOnClickListener(btnRegisterListener);

        // -------- NETWORK FRAGMENT --------
        // We instantiate the network fragment that will handle the register action from the user in background
        authFragment = AuthFragment.getInstance(AuthFragment.class, getSupportFragmentManager(), AuthFragment.TAG);
        this.registerInProgress = false;
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
            if (validateInputs())
            {
                User user = new User(txtNombre.getText().toString(), txtApellido.getText().toString(), Integer.parseInt(txtDNI.getText().toString()),
                        txtEmail.getText().toString(), txtPassword.getText().toString(), Integer.parseInt(txtComision.getText().toString()));
                // Execute the async login.
                authFragment.startRegister(user, auth);
                registerInProgress = true;
                pgBarRegister.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void updateFromRequest(NetworkTask.Result result) {
        Log.i("Log", result.resultValue);
        pgBarRegister.setVisibility(View.INVISIBLE);
        if (result.success)
        {
            this.startActivity(AlbumActivity.class, true);
            networkEventFragment.startEventTask(new Event(Event.TYPE_REGISTER, Event.DESCRIPTION_REGISTER), this.auth);
            networkEventFragment.startEventTask(new Event(Event.TYPE_BACKGROUND, Event.DESCRIPTION_BACKGROUND), this.auth);
        }
        else
        {
            txtError.setText(result.resultValue);
        }
    }

    @Override
    public void finishRequest(int taskType) {
        switch (taskType)
        {
            case NetworkTask.TYPE_TOKEN_TASK:
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
        txtError.setText("");

        if (!InputValidatorHelper.isValidEmail(txtEmail.getText().toString()))
        {
            txtError.setText(R.string.invalid_email);
            return false;
        }

        if (!InputValidatorHelper.isLengthyEnough(txtPassword.getText().toString(), 8))
        {
            txtError.setText(R.string.invalid_password);
            return false;
        }

        if (!InputValidatorHelper.isNumeric(txtDNI.getText().toString())) {
            txtError.setText(R.string.invalid_dni);
            return false;
        }

        if (!InputValidatorHelper.isNumeric(txtComision.getText().toString())) {
            txtError.setText(R.string.invalid_commission);
            return false;
        }

        if (InputValidatorHelper.isNullOrEmpty(txtApellido.getText().toString()))
        {
            Log.i("Log", txtApellido.getText().toString());
            txtError.setText(R.string.invalid_surname);
            return false;
        }

        if (InputValidatorHelper.isNullOrEmpty(txtNombre.getText().toString()))
        {
            txtError.setText(R.string.invalid_name);
            return false;
        }

        return true;
    }
}
