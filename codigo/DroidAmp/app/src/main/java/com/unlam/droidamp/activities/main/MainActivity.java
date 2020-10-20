package com.unlam.droidamp.activities.main;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.unlam.droidamp.R;
import com.unlam.droidamp.activities.login.fragments.NetworkLoginFragment;
import com.unlam.droidamp.activities.main.fragments.MusicPlayerFragment;
import com.unlam.droidamp.interfaces.BtnListener;

import android.database.Cursor;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    // UI Elements
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    // Audio reproduction
    private ArrayList<MusicFile> musicFiles;
    private MusicPlayerFragment musicPlayerFragment;

    // Sensors
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private Sensor mProximity;

    // Constants
    private static final int PROXIMITY_DISTANCE = 4;
    // Shake detection
    private static final float SHAKE_THRESHOLD = 12.5f; // m/S**2
    private static final int MIN_TIME_BETWEEN_SHAKES_MILLISECS = 1000;
    private long mLastShakeTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // ----- SENSORS -----
        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mProximity = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);

        // Start the music player class
        //musicPlayer = new MusicPlayer();
        musicPlayerFragment = MusicPlayerFragment.getInstance(getSupportFragmentManager());

        // ----- RECYCLER VIEW -----

        // Get files from android storage
        musicFiles = new ArrayList();
        getMusicInfo();

        // Create the recycler view
        recyclerView = findViewById(R.id.rvMusicFiles);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // specify an adapter (see also next example)
        mAdapter = new MediaAdapter(musicFiles, new BtnListener() {
            // OnClick handler for the music files
            @Override
            public void onClick(View v, int position) {
                //musicFiles.get(position).getPath();
                // musicPlayer.start(musicFiles.get(position).getPath());
                musicPlayerFragment.start(musicFiles.get(position).getPath());
            }
        });
        recyclerView.setAdapter(mAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mProximity, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this, mAccelerometer);
        mSensorManager.unregisterListener(this, mProximity);
    }

    public void getMusicInfo()
    {
        try {
            String[] projection = new String[]{MediaStore.Audio.Media._ID,  MediaStore.Audio.Media.TITLE,
                    MediaStore.Audio.Media.ARTIST, MediaStore.Audio.Media.DATA, MediaStore.Audio.Media.ALBUM_KEY};
            Cursor cursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, projection, null, null, null);

            if (cursor != null)
            {
                while(cursor.moveToNext())
                {
                    //String name = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));

                    long id = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media._ID));
                    String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                    String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                    // int albumId = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID);
                    String data = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                    String albumkey = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_KEY));

                    musicFiles.add(new MusicFile(id, title, artist, data, albumkey));
                }

                cursor.close();
            }
        } catch (Exception e)
        {
            Log.i("Exception", e.toString());
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_PROXIMITY) {
            handleProximitySensor(event.values[0]);
        }

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            handleAccelerometerSensor(event);
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void handleAccelerometerSensor(SensorEvent event)
    {
        detectShake(event);
        detectRoll(event);
    }

    public void detectShake(SensorEvent event)
    {
        long curTime = System.currentTimeMillis();

        if ((curTime - mLastShakeTime) > MIN_TIME_BETWEEN_SHAKES_MILLISECS) {

            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            double acceleration = Math.sqrt(Math.pow(x, 2) +
                    Math.pow(y, 2) +
                    Math.pow(z, 2)) - SensorManager.GRAVITY_EARTH;
            //Log.d("Log", "Acceleration is " + acceleration + "m/s^2");

            if (acceleration > SHAKE_THRESHOLD) {
                mLastShakeTime = curTime;
                //Log.d("Log", "Shake detected");
                //musicPlayer.mute();
                musicPlayerFragment.mute();
            }
        }
    }

    public void detectRoll(SensorEvent event)
    {

    }

    public void handleProximitySensor(float value)
    {
        if (value >= -PROXIMITY_DISTANCE && value <= PROXIMITY_DISTANCE) {
            // Detected near
            //musicPlayer.play();
            musicPlayerFragment.play();
        }
    }
}
