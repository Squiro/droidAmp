package com.unlam.droidamp.activities.battery;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.unlam.droidamp.R;
import com.unlam.droidamp.activities.album.AlbumActivity;
import com.unlam.droidamp.activities.base.BaseActivity;
import com.unlam.droidamp.activities.login.LoginActivity;
import com.unlam.droidamp.auth.AuthFragment;
import com.unlam.droidamp.models.event.Event;
import com.unlam.droidamp.network.NetworkTask;

import java.util.ArrayList;

public class BatteryActivity extends BaseActivity {

    private TextView batteryPercentage;
    private TextView batteryState;
    private Button btnContinuar;
    private Intent batteryStatus;
    private ProgressBar progressBar;

    //private Auth auth;
    private AuthFragment authFragment;
    public static final int MULTIPLE_PERMISSIONS = 10;

    String[] permissions= new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.ACCESS_WIFI_STATE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_battery);

        batteryPercentage = findViewById(R.id.txtBatteryPercent);
        batteryState = findViewById(R.id.txtBatteryState);
        btnContinuar = findViewById(R.id.btnContinuar);
        progressBar = findViewById(R.id.pgBarBattery);
        // We set an on click listener for the button
        btnContinuar.setOnClickListener(btnContinuarListener);

        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        batteryStatus = this.registerReceiver(null, ifilter);

        int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);

        if (isCharging(status))
            batteryState.setText("cargándose.");
        else
            batteryState.setText("descargándose.");

        batteryPercentage.setText(getBatteryPct().toString() + "%");

        // Instantiate auth fragment
        authFragment = AuthFragment.getInstance(AuthFragment.class, getSupportFragmentManager(), AuthFragment.TAG);
    }

    public boolean isCharging(int status) {
        return status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL;
    }

    public Float getBatteryPct()
    {
        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        // Return battery percentage
        return level * 100 / (float)scale;
    }

    private View.OnClickListener btnContinuarListener = new View.OnClickListener()
    {
        // This method will be executed once the button is clicked
        public void onClick(View v)
        {
        if (checkPermissions())
        {
            afterPermissionCheck();
        }
        }
    };

    private void afterPermissionCheck(){
        progressBar.setVisibility(View.VISIBLE);
        checkTokens();
    }

    public void checkTokens()
    {
       // If token isn't expired start the album activity
        if (!auth.checkIfTokenExpired())
        {
            startActivity(AlbumActivity.class, true);
        }
        else
        {
            // Otherwise, try to refresh them
            authFragment.startRefreshToken(auth);
        }
    }

    @Override
    public void updateFromRequest(NetworkTask.Result result) {
        if (result.success)
        {
            startActivity(AlbumActivity.class, true);
            this.networkEventFragment.startEventTask(new Event(Event.TYPE_BACKGROUND, Event.DESCRIPTION_BACKGROUND), auth);
            this.networkEventFragment.startEventTask(new Event(Event.TYPE_TOKEN, Event.DESCRIPTION_TOKEN), auth);
        }
        else
        {
            Log.i("Log", result.resultValue);
            startActivity(LoginActivity.class, true);
        }
        progressBar.setVisibility(View.INVISIBLE);
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

    private  boolean checkPermissions() {
        int result;
        ArrayList<String> listPermissionsNeeded = new ArrayList<>();

        //Se chequea si la version de Android es menor a la 6
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }

        for (String p : permissions) {
            result = ContextCompat.checkSelfPermission(this, p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), MULTIPLE_PERMISSIONS );
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == MULTIPLE_PERMISSIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                afterPermissionCheck();
            } else {
                Toast.makeText(this, "DroidAmp necesita ciertos permisos para funcionar correctamente", Toast.LENGTH_LONG).show();
                this.finish();
            }
        }
    }
}
