package com.unlam.droidamp.activities.base;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;

import com.unlam.droidamp.R;
import com.unlam.droidamp.models.event.NetworkEventFragment;
import com.unlam.droidamp.auth.Auth;
import com.unlam.droidamp.interfaces.RequestCallback;
import com.unlam.droidamp.network.BroadcastConnectivity;
import com.unlam.droidamp.network.NetworkTask;

public class BaseActivity extends AppCompatActivity implements RequestCallback<NetworkTask.Result> {

    protected BroadcastConnectivity broadcastConnectivity;
    protected NetworkEventFragment networkEventFragment;
    protected Auth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        this.auth = new Auth(this);

        this.networkEventFragment = NetworkEventFragment.getInstance(NetworkEventFragment.class, getSupportFragmentManager());
        this.broadcastConnectivity = new BroadcastConnectivity(this);
        this.registerReceiver(broadcastConnectivity, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.unregisterReceiver(broadcastConnectivity);
    }

    @Override
    public BroadcastConnectivity getBroadcastConnectivity() {
        return this.broadcastConnectivity;
    }

    @Override
    public void updateFromRequest(NetworkTask.Result result) {

    }

    @Override
    public void finishRequest(int taskType) {
        if (networkEventFragment != null)
        {
            Log.i("Log", "Event Task Finished");
            networkEventFragment.cancelTask();
        }
    }

    protected  <T> void startActivity(Class<T> clazz, boolean finishCurrent)
    {
        Intent activity = new Intent(this, clazz);
        startActivity(activity);
        if (finishCurrent)
            this.finish();
    }
}
