package com.unlam.droidamp.activities.main.classes.sensors;

import android.content.Context;
import android.content.SharedPreferences;
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
    protected String sensorKey;

    // SharedPref
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;


    protected static final String ACCELEROMETER_KEY = "ACCELEROMETER_SENSOR";
    protected static final String PROXIMITY_KEY = "PROXIMITY_SENSOR";

    public DroidAmpSensor(Context context, Auth auth, int sensorType, SharedPreferences sharedPreferences)
    {
        this.auth = auth;
        this.mainActivity =  (MainActivity) context;
        this.musicPlayerFragment = MusicPlayerFragment.getInstance(mainActivity.getSupportFragmentManager());
        this.networkEventFragment = NetworkEventFragment.getInstance(NetworkEventFragment.class, mainActivity.getSupportFragmentManager());
        this.sensorType = sensorType;
        this.sharedPreferences = sharedPreferences;
        this.editor = sharedPreferences.edit();
    }

    public void handleSensorEvent(SensorEvent event)
    {

    }

    public void saveEventInSharedPref(Event event)
    {
        this.editor.putString(sensorKey, event.toString());
        editor.apply();
    }

    public String getSensorEventFromSharedPref()
    {
        return sharedPreferences.getString(sensorKey, "");
    }

    public void sendEvent(Event event)
    {
        saveEventInSharedPref(event);
        networkEventFragment.startEventTask(event, this.auth);
    }

    public int getSensorType() {
        return sensorType;
    }
}
