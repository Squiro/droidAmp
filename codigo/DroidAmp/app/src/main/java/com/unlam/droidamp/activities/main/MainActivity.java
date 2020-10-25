package com.unlam.droidamp.activities.main;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.unlam.droidamp.R;
import com.unlam.droidamp.activities.main.classes.EventAdapter;
import com.unlam.droidamp.activities.main.classes.sensors.AccelerometerSensor;
import com.unlam.droidamp.activities.main.classes.sensors.LightSensor;
import com.unlam.droidamp.activities.main.classes.sensors.ProximitySensor;
import com.unlam.droidamp.activities.main.classes.sensors.DroidAmpSensor;
import com.unlam.droidamp.activities.main.fragments.MusicResolverFragment;
import com.unlam.droidamp.auth.Auth;
import com.unlam.droidamp.interfaces.MusicResolverCallback;
import com.unlam.droidamp.interfaces.RequestCallback;
import com.unlam.droidamp.activities.main.classes.MediaAdapter;
import com.unlam.droidamp.models.MusicFile;
import com.unlam.droidamp.activities.main.fragments.MusicPlayerFragment;
import com.unlam.droidamp.activities.main.fragments.NetworkEventFragment;
import com.unlam.droidamp.interfaces.BtnListener;
import com.unlam.droidamp.network.BroadcastConnectivity;
import com.unlam.droidamp.network.NetworkTask;

import android.content.Context;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements SensorEventListener, RequestCallback<NetworkTask.Result>, MusicResolverCallback<ArrayList<MusicFile>> {
    // UI Elements
    private ProgressBar pgBarMain;
    private RecyclerView rvMusicFiles;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView eventRecylcler;
    private RecyclerView.Adapter eventAdapter;
    private ArrayList<String> eventList;

    // Network fragment
    private  NetworkEventFragment networkEventFragment;
    private Auth auth;
    private BroadcastConnectivity broadcastConnectivity;

    // Audio reproduction
    private ArrayList<MusicFile> musicFiles;
    private MusicPlayerFragment musicPlayerFragment;
    private MusicResolverFragment musicResolverFragment;
    private int currentPosition;

    // Sensors
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private Sensor mProximity;
    private Sensor mLight;
    private ArrayList<DroidAmpSensor> sensorList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.auth = new Auth(this);

        this.pgBarMain = findViewById(R.id.pgBarMain);
        this.pgBarMain.setVisibility(View.VISIBLE);
        instantiateFragments();
        setUpRecyclerViews();
        registerBroadcastConnectivity();
        getSensors();
    }

    public void getSensors()
    {
        // ----- SENSORS -----
        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mProximity = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        mLight = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);;

        // Add our sensors classes to the array list
        this.sensorList = new ArrayList<>();
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.sharedPreferencesFile), Context.MODE_PRIVATE);
        this.sensorList.add(new ProximitySensor(this, this.auth, sharedPreferences));
        this.sensorList.add(new AccelerometerSensor(this, this.auth, sharedPreferences));
        this.sensorList.add(new LightSensor(this, this.auth, sharedPreferences));
    }

    public void instantiateFragments()
    {
        musicResolverFragment = MusicResolverFragment.getInstance(getSupportFragmentManager());
        musicPlayerFragment = MusicPlayerFragment.getInstance(getSupportFragmentManager());
        networkEventFragment = NetworkEventFragment.getInstance(NetworkEventFragment.class, getSupportFragmentManager());
    }

    public void setUpRecyclerViews()
    {
        // ----- RECYCLER VIEW FOR EVENTS -----
        // Get stored events
        eventList = new ArrayList<>();

        eventRecylcler = findViewById(R.id.rvEvents);
        eventRecylcler.setHasFixedSize(true);
        eventRecylcler.setLayoutManager(new LinearLayoutManager(this));
        eventAdapter = new EventAdapter(eventList);
        eventRecylcler.setAdapter(eventAdapter);

        // ----- RECYCLER VIEW FOR MUSIC FILES -----
        musicFiles = new ArrayList<>();

        // Create the recycler view
        rvMusicFiles = findViewById(R.id.rvMusicFiles);
        this.rvMusicFiles.setVisibility(View.INVISIBLE);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        rvMusicFiles.setHasFixedSize(true);
        // use a linear layout manager
        rvMusicFiles.setLayoutManager(new LinearLayoutManager(this));

        // specify an adapter
        mAdapter = new MediaAdapter(musicFiles, new BtnListener() {
            // OnClick handler for the music files
            @Override
            public void onClick(View v, int position) {
                playMusicFile(position);
            }
        });
        rvMusicFiles.setAdapter(mAdapter);
    }

    public void registerBroadcastConnectivity()
    {
        broadcastConnectivity = new BroadcastConnectivity(this);
        this.registerReceiver(broadcastConnectivity, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mProximity, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mLight, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this, mAccelerometer);
        mSensorManager.unregisterListener(this, mProximity);
        mSensorManager.unregisterListener(this, mLight);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.unregisterReceiver(broadcastConnectivity);
    }

    public void playMusicFile(int position)
    {
        this.currentPosition = position;
        musicPlayerFragment.start(musicFiles.get(this.currentPosition).getPath());
    }

    public void playNext()
    {
        this.playMusicFile(this.currentPosition+1);
    }

    public void playPrev()
    {
        if (this.currentPosition > 0)
            this.playMusicFile(this.currentPosition-1);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        int type = event.sensor.getType();
        for (DroidAmpSensor sensor: sensorList)
        {
            if (sensor.getSensorType() == type)
                sensor.handleSensorEvent(event);
        }
        getEventList();
    }

    public void getEventList()
    {
        eventList = new ArrayList<>();
        for (DroidAmpSensor sensor: sensorList)
        {
            eventList.add(sensor.getSensorEventFromSharedPref());
        }
        eventAdapter = new EventAdapter(eventList);
        eventRecylcler.setAdapter(eventAdapter);
        eventAdapter.notifyDataSetChanged();
    }

    public void updateFromMusicResolver(ArrayList<MusicFile> result)
    {
        this.pgBarMain.setVisibility(View.INVISIBLE);
        this.rvMusicFiles.setVisibility(View.VISIBLE);
        this.musicFiles =  result;
        mAdapter = new MediaAdapter(musicFiles, new BtnListener() {
            // OnClick handler for the music files
            @Override
            public void onClick(View v, int position) {
                playMusicFile(position);
            }
        });
        rvMusicFiles.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void updateFromRequest(NetworkTask.Result result) {

    }

    @Override
    public BroadcastConnectivity getBroadcastConnectivity() {
        return broadcastConnectivity;
    }

    @Override
    public void finishRequest() {
        if (networkEventFragment != null) {
            networkEventFragment.cancelTask();
        }

        if (musicResolverFragment != null)
        {
            musicResolverFragment.cancelTask();
        }
    }
}
