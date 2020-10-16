package com.unlam.droidamp.activities.battery;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.unlam.droidamp.R;
import com.unlam.droidamp.activities.login.LoginActivity;

public class BatteryActivity extends AppCompatActivity  {

    TextView batteryPercentage;
    TextView batteryState;
    Button btnOkGenial;
    Intent batteryStatus;

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

    // Listener for login button
    private View.OnClickListener btnOkGenialListener = new View.OnClickListener()
    {
        // This method will be executed once the button is clicked
        public void onClick(View v)
        {
            redirect();
        }
    };

    private void redirect() {
        Intent loginActivity = new Intent(this, LoginActivity.class);
        startActivity(loginActivity);
    }
}
