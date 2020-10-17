package com.unlam.droidamp.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

public class BroadcastConnectivity extends BroadcastReceiver {

    Context appContext;
    ConnectivityManager cm;
    NetworkInfo activeNetwork;
    public BroadcastConnectivity (Context context)
    {
        this.appContext = context;
    }

    public void onReceive(Context context, Intent intent) {
        // boolean noConnectivity = intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);
        String reason = intent.getStringExtra(ConnectivityManager.EXTRA_REASON);
        // boolean isFailover = intent.getBooleanExtra(ConnectivityManager.EXTRA_IS_FAILOVER, false);
        cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        checkConnectivity();
    }

    public void checkConnectivity()
    {
        activeNetwork = cm.getActiveNetworkInfo();
        if(activeNetwork == null || !activeNetwork.isConnectedOrConnecting()){
            Toast.makeText(appContext, "No hay conexión a internet.", Toast.LENGTH_LONG).show();
        }
    }
};

