package com.unlam.droidamp.activities.main.classes.sensors;

import android.content.Context;
import android.hardware.SensorEvent;


import com.unlam.droidamp.activities.main.MainActivity;
import com.unlam.droidamp.activities.main.fragments.MusicPlayerFragment;
import com.unlam.droidamp.activities.main.fragments.NetworkEventFragment;
import com.unlam.droidamp.auth.Auth;
import com.unlam.droidamp.models.Event;

public class DroidAmpSensor {

    protected NetworkEventFragment networkEventFragment;
    protected MusicPlayerFragment musicPlayerFragment;
    protected Auth auth;
    protected int sensorType;
    protected MainActivity mainActivity;

    public DroidAmpSensor(Context context, Auth auth, int sensorType)
    {
        this.auth = auth;
        this.mainActivity =  (MainActivity) context;
        this.musicPlayerFragment = MusicPlayerFragment.getInstance(mainActivity.getSupportFragmentManager());
        this.networkEventFragment = NetworkEventFragment.getInstance(NetworkEventFragment.class, mainActivity.getSupportFragmentManager());
        this.sensorType = sensorType;
    }

    public void handleSensorEvent(SensorEvent event)
    {

    }

    public void sendEvent(Event event)
    {
        networkEventFragment.startEventTask(event, this.auth);
    }

    public int getSensorType() {
        return sensorType;
    }
}
