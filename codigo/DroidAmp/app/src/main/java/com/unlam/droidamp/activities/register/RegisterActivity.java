package com.unlam.droidamp.activities.register;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.unlam.droidamp.R;
import com.unlam.droidamp.activities.login.fragments.NetworkLoginFragment;
import com.unlam.droidamp.activities.register.fragments.RegisterFragment;
import com.unlam.droidamp.auth.Auth;
import com.unlam.droidamp.models.User;

public class RegisterActivity extends AppCompatActivity {

    RegisterFragment registerFragment;
    Button btnRegister;
    boolean registerInProgress;

    private Auth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        this.auth = new Auth(this);

        // -------- UI ELEMENTS --------
        btnRegister = findViewById(R.id.btnRegister);

        // -------- LISTENERS --------
        btnRegister.setOnClickListener(btnRegisterListener);

        // -------- NETWORK FRAGMENT --------
        // We instantiate the network fragment that will handle the register action from the user in background
        registerFragment = (RegisterFragment) RegisterFragment.getInstance(getSupportFragmentManager(), "http://so-unlam.net.ar/api/api/login");
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
        if (!registerInProgress && registerFragment != null) {
            User user = new User("email", "pass");
            // Execute the async login.
            registerFragment.startRegister(user, auth);
            registerInProgress = true;
        }
    }


}
