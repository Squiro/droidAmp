package com.unlam.droidamp.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.unlam.droidamp.R;

public class BroadcastConnectivity extends BroadcastReceiver {

    Context appContext;
    ConnectivityManager cm;
    NetworkInfo activeNetwork;

    public BroadcastConnectivity (Context context)
    {
        this.appContext = context;
    }

    public void onReceive(Context context, Intent intent) {
        cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        isConnected();
    }

    public boolean isConnected()
    {
        if (cm != null)
        {
            activeNetwork = cm.getActiveNetworkInfo();
            if(activeNetwork == null || !activeNetwork.isConnectedOrConnecting() || (activeNetwork.getType() != ConnectivityManager.TYPE_WIFI && activeNetwork.getType() != ConnectivityManager.TYPE_MOBILE)){
                Toast.makeText(appContext, R.string.no_connection, Toast.LENGTH_LONG).show();
                return false;
            }
            return  true;
        }
        return  false;
    }
}

