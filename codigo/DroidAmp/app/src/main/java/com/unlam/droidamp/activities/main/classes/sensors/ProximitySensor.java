package com.unlam.droidamp.activities.main.classes.sensors;

import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.util.Log;

import com.unlam.droidamp.activities.main.fragments.MusicPlayerFragment;
import com.unlam.droidamp.auth.Auth;
import com.unlam.droidamp.models.event.Event;

public class ProximitySensor extends DroidAmpSensor {
    // Constants
    private static final int PROXIMITY_DISTANCE = 4;
    public ProximitySensor(Context context, Auth auth, SharedPreferences sharedPreferences, MusicPlayerFragment musicPlayerFragment)
    {
        super(context, auth, Sensor.TYPE_PROXIMITY, sharedPreferences, musicPlayerFragment);
        this.sensorKey = DroidAmpSensor.PROXIMITY_KEY;
    }

    @Override
    public void handleSensorEvent(SensorEvent event)
    {
        float value = event.values[0];
        if (value >= -PROXIMITY_DISTANCE && value <= PROXIMITY_DISTANCE) {
            // Detected near
            try {
                this.musicPlayerFragment.play();
            }
            catch (Exception e)
            {
                Log.i("Exception", e.toString());
            }
            sendEvent(new Event(Event.TYPE_SENSOR, "Proximity sensor detected near"));
        }
        this.lastEventMsg = "Proximity sensor value: " + value;
        saveEventInSharedPref(this.lastEventMsg);
    }
}
