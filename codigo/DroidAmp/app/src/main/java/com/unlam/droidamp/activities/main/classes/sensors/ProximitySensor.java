package com.unlam.droidamp.activities.main.classes.sensors;

import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.SensorEvent;
import com.unlam.droidamp.auth.Auth;
import com.unlam.droidamp.models.Event;

public class ProximitySensor extends DroidAmpSensor {
    // Constants
    private static final int PROXIMITY_DISTANCE = 4;


    public ProximitySensor(Context context, Auth auth, int sensorType, SharedPreferences sharedPreferences)
    {
        super(context, auth, sensorType, sharedPreferences);
        this.sensorKey = DroidAmpSensor.PROXIMITY_KEY;
    }

    @Override
    public void handleSensorEvent(SensorEvent event)
    {
        float value = event.values[0];
        if (value >= -PROXIMITY_DISTANCE && value <= PROXIMITY_DISTANCE) {
            // Detected near
            this.musicPlayerFragment.play();
            sendEvent(new Event(Event.TYPE_SENSOR, "Proximity sensor detected near", System.currentTimeMillis()));
        }
    }
}
