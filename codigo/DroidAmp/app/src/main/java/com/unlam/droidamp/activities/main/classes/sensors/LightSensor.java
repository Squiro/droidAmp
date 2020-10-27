package com.unlam.droidamp.activities.main.classes.sensors;

import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;

import com.unlam.droidamp.activities.main.fragments.MusicPlayerFragment;
import com.unlam.droidamp.auth.Auth;
import com.unlam.droidamp.models.event.Event;

public class LightSensor extends DroidAmpSensor {

    // Constants
    private static final int LIGHT_THRESHOLD = 10;
    private static final int MIN_TIME_BETWEEN_DETECTION_MILLISECS = 2000;
    private long mLastLightChangeTime;

    public LightSensor(Context context, Auth auth, SharedPreferences sharedPreferences, MusicPlayerFragment musicPlayerFragment)
    {
        super(context, auth, Sensor.TYPE_LIGHT, sharedPreferences, musicPlayerFragment);
        this.sensorKey = DroidAmpSensor.LIGHT_KEY;
    }

    @Override
    public void handleSensorEvent(SensorEvent event)
    {
        long curTime = System.currentTimeMillis();
        float value = event.values[0];

        if ((curTime - mLastLightChangeTime) > MIN_TIME_BETWEEN_DETECTION_MILLISECS) {
            if (value <= LIGHT_THRESHOLD) {
                // Detected near
                this.musicPlayerFragment.changeVolume();
                sendEvent(new Event(Event.TYPE_SENSOR, "Light sensor detected change"));
                mLastLightChangeTime = curTime;
            }
        }
        saveEventInSharedPref("Light sensor value: " + value);
    }
}
