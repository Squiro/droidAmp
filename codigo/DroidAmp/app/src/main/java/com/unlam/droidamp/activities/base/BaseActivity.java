package com.unlam.droidamp.activities.base;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.unlam.droidamp.R;
import com.unlam.droidamp.activities.album.AlbumActivity;
import com.unlam.droidamp.activities.login.LoginActivity;
import com.unlam.droidamp.auth.AuthFragment;
import com.unlam.droidamp.models.event.Event;
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
        Log.i("Log", "BASE ACTIVITY ------- UpdateFromRequest");

        if (result.taskType == NetworkTask.TYPE_TOKEN_TASK)
        {
            if (result.success)
            {
                this.networkEventFragment.startEventTask(new Event(Event.TYPE_TOKEN, Event.DESCRIPTION_TOKEN), this.auth);
            }
            else {
                startActivity(LoginActivity.class, true);
            }
        }

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
