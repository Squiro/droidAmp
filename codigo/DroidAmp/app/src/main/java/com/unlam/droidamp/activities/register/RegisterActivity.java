package com.unlam.droidamp.activities.register;

import androidx.appcompat.app.AppCompatActivity;

import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.unlam.droidamp.R;
import com.unlam.droidamp.auth.Auth;
import com.unlam.droidamp.auth.AuthFragment;
import com.unlam.droidamp.interfaces.RequestCallback;
import com.unlam.droidamp.models.User;
import com.unlam.droidamp.network.BroadcastConnectivity;
import com.unlam.droidamp.network.NetworkTask;
import com.unlam.droidamp.utilities.InputValidatorHelper;

public class RegisterActivity extends AppCompatActivity implements RequestCallback<NetworkTask.Result> {

    private TextView txtError;
    private TextView txtNombre;
    private TextView txtApellido;
    private TextView txtEmail;
    private TextView txtPassword;
    private TextView txtDNI;
    private TextView txtComision;
    private Button btnRegister;


    private AuthFragment authFragment;

    private boolean registerInProgress;
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
        txtNombre = findViewById(R.id.txtNombre);
        txtApellido = findViewById(R.id.txtApellido);
        txtEmail = findViewById(R.id.txtEmail);
        txtPassword = findViewById(R.id.txtPassword);
        txtDNI = findViewById(R.id.txtDni);
        txtComision = findViewById(R.id.txtComision);
        txtError = findViewById(R.id.txtError);
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
            if (validateInputs())
            {
                User user = new User(txtNombre.getText().toString(), txtApellido.getText().toString(), Integer.parseInt(txtDNI.getText().toString()),
                        txtEmail.getText().toString(), txtPassword.getText().toString(), Integer.parseInt(txtComision.getText().toString()));
                // Execute the async login.
                authFragment.startRegister(user, auth);
                registerInProgress = true;
            }
        }
    }

    @Override
    public void updateFromRequest(NetworkTask.Result result) {
        if (result.success)
        {

            Log.i("Log", result.resultValue);
        }
        else
        {
            Log.i("Log", result.exception.toString());
        }
    }

    @Override
    public BroadcastConnectivity getBroadcastConnectivity() {
        return this.broadcastConnectivity;
    }

    @Override
    public void finishRequest() {
        if (authFragment != null)
        {
            authFragment.cancelTask();
        }
        Log.i("Log", "Request finished");
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
