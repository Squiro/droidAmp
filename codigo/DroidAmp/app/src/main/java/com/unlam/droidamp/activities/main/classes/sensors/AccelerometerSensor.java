package com.unlam.droidamp.activities.main.classes.sensors;

import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.util.Log;

import com.unlam.droidamp.activities.main.fragments.MusicPlayerFragment;
import com.unlam.droidamp.auth.Auth;
import com.unlam.droidamp.models.event.Event;

import java.util.Locale;

public class AccelerometerSensor extends DroidAmpSensor {
    // Constants
    private static final float SHAKE_THRESHOLD = 10.5f; // m/S**2
    private static final int MIN_TIME_BETWEEN_SHAKES_MILLISECS = 1000;
    private long mLastShakeTime;

    public AccelerometerSensor(Context context, Auth auth, SharedPreferences sharedPreferences, MusicPlayerFragment musicPlayerFragment)
    {
        super(context, auth, Sensor.TYPE_ACCELEROMETER, sharedPreferences, musicPlayerFragment);
        this.sensorKey = DroidAmpSensor.ACCELEROMETER_KEY;
    }

    @Override
    public void handleSensorEvent(SensorEvent event)
    {
        detectShake(event);
    }

    private void detectShake(SensorEvent event)
    {
        long curTime = System.currentTimeMillis();

        if ((curTime - mLastShakeTime) > MIN_TIME_BETWEEN_SHAKES_MILLISECS) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            double acceleration = Math.sqrt(Math.pow(x, 2) +
                    Math.pow(y, 2) +
                    Math.pow(z, 2)) - SensorManager.GRAVITY_EARTH;

            if (acceleration > SHAKE_THRESHOLD) {
                try {
                    mainActivity.playNext();
                }
                catch (Exception e)
                {
                    Log.i("Log", e.toString());
                }
                mLastShakeTime = curTime;
                sendEvent(new Event(Event.TYPE_SENSOR, "Accelerometer shake detected"));
            }
            this.lastEventMsg = "Accelerometer sensor value: " + String.format(Locale.ENGLISH, "%.4f", acceleration) + "m/s^2";
            saveEventInSharedPref(this.lastEventMsg);
        }
    }
}
