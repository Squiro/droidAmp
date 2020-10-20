package com.unlam.droidamp.activities.battery;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.BatteryManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.unlam.droidamp.R;
import com.unlam.droidamp.activities.login.LoginActivity;
import com.unlam.droidamp.activities.main.MainActivity;
import com.unlam.droidamp.auth.Auth;
import com.unlam.droidamp.auth.AuthFragment;
import com.unlam.droidamp.interfaces.RequestCallback;
import com.unlam.droidamp.network.BroadcastConnectivity;

public class BatteryActivity extends AppCompatActivity implements RequestCallback<String> {

    TextView batteryPercentage;
    TextView batteryState;
    Button btnOkGenial;
    Intent batteryStatus;

    private Auth auth;
    private AuthFragment authFragment;
    private BroadcastConnectivity broadcastConnectivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_battery);

        batteryPercentage = findViewById(R.id.txtBatteryPercent);
        batteryState = findViewById(R.id.txtBatteryState);
        btnOkGenial = findViewById(R.id.btnOkGenial);

        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        batteryStatus = this.registerReceiver(null, ifilter);

        int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);

        if (isCharging(status))
            batteryState.setText("se está cargando.");
        else
            batteryState.setText("se está descargando.");

        batteryPercentage.setText(getBatteryPct().toString() + "%");

        // We set an on click listener for the button
        btnOkGenial.setOnClickListener(btnOkGenialListener);

        // ----- AUTH -----
        // Instantiate auth class
        this.auth = new Auth(this);

        // Instantiate auth fragment
        authFragment = AuthFragment.getInstance(AuthFragment.class, getSupportFragmentManager());

        broadcastConnectivity = new BroadcastConnectivity(this);
        this.registerReceiver(broadcastConnectivity, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    public boolean isCharging(int status) {
        return status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL;
    }

    public Float getBatteryPct()
    {
        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

        float batteryPct = level * 100 / (float)scale;

        return batteryPct;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.unregisterReceiver(broadcastConnectivity);
    }

    // Listener for login button
    private View.OnClickListener btnOkGenialListener = new View.OnClickListener()
    {
        // This method will be executed once the button is clicked
        public void onClick(View v)
        {
            checkTokens();
        }
    };

    private <T> void startActivity(Class<T> clazz)
    {
        Intent activity = new Intent(this, clazz);
        startActivity(activity);
        this.finish();
    }

    public void checkTokens()
    {
        // If token isn't expired start the mainActivity
        if (!auth.checkIfTokenExpired())
        {
            startActivity(MainActivity.class);
        }
        else
        {
            // Otherwise, try to refresh them
            authFragment.startRefreshToken(auth);
        }
    }

    @Override
    public void updateFromRequest(String result) {
        Log.i("Log", "Update From Request");
        Log.i("Log", result);
    }

    @Override
    public BroadcastConnectivity getBroadcastConnectivity() {
        return broadcastConnectivity;
    }

    @Override
    public void finishRequest() {
        Log.i("Log", "finishRequest");
        if (authFragment != null) {
            authFragment.cancelTask();
        }
        startActivity(MainActivity.class);
    }
}
