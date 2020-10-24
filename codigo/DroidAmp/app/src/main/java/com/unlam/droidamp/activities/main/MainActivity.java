package com.unlam.droidamp.activities.main;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.unlam.droidamp.R;
import com.unlam.droidamp.activities.main.classes.EventAdapter;
import com.unlam.droidamp.activities.main.classes.sensors.AccelerometerSensor;
import com.unlam.droidamp.activities.main.classes.sensors.ProximitySensor;
import com.unlam.droidamp.activities.main.classes.sensors.DroidAmpSensor;
import com.unlam.droidamp.activities.main.fragments.MusicResolverFragment;
import com.unlam.droidamp.auth.Auth;
import com.unlam.droidamp.interfaces.MusicResolverCallback;
import com.unlam.droidamp.interfaces.RequestCallback;
import com.unlam.droidamp.activities.main.classes.MediaAdapter;
import com.unlam.droidamp.activities.main.classes.MusicFile;
import com.unlam.droidamp.activities.main.fragments.MusicPlayerFragment;
import com.unlam.droidamp.activities.main.fragments.NetworkEventFragment;
import com.unlam.droidamp.interfaces.BtnListener;
import com.unlam.droidamp.network.BroadcastConnectivity;
import com.unlam.droidamp.network.NetworkTask;

import android.content.Context;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
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
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView eventRecylcler;
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
    private ArrayList<DroidAmpSensor> sensorList;

    // Misc
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.auth = new Auth(this);

        this.pgBarMain = findViewById(R.id.pgBarMain);
        this.pgBarMain.setVisibility(View.VISIBLE);

        this.sharedPreferences = getSharedPreferences(getString(R.string.sharedPreferencesFile), Context.MODE_PRIVATE);
        this.editor = sharedPreferences.edit();


        instantiateFragments();
        configureRecyclerView();
        registerBroadcastConnectivity();
        getSensors();
    }

    public void getSensors()
    {
        // ----- SENSORS -----
        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mProximity = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);

        // Add our sensors classes to the array list
        this.sensorList = new ArrayList<>();
        this.sensorList.add(new ProximitySensor(this, this.auth, Sensor.TYPE_PROXIMITY));
        this.sensorList.add(new AccelerometerSensor(this, this.auth, Sensor.TYPE_ACCELEROMETER));
    }

    public void instantiateFragments()
    {
        musicResolverFragment = MusicResolverFragment.getInstance(getSupportFragmentManager());
        musicPlayerFragment = MusicPlayerFragment.getInstance(getSupportFragmentManager());
        networkEventFragment = NetworkEventFragment.getInstance(NetworkEventFragment.class, getSupportFragmentManager());
    }

    public void configureRecyclerView()
    {
        // ----- RECYCLER VIEW FOR EVENTS -----
        // Get stored events
        eventList = new ArrayList<>();
        //getEventList();
        // Create the recycler view
        eventRecylcler = findViewById(R.id.rvEvents);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        eventRecylcler.setHasFixedSize(true);

        // use a linear layout manager
        eventRecylcler.setLayoutManager(new LinearLayoutManager(this));

        // specify an adapter
        eventRecylcler.setAdapter(new EventAdapter(eventList));

        // ----- RECYCLER VIEW FOR MUSIC FILES -----

        // Get files from android storage
        musicFiles = new ArrayList<>();
        //getMusicInfo();

        // Create the recycler view
        rvMusicFiles = findViewById(R.id.rvMusicFiles);
        this.rvMusicFiles.setVisibility(View.INVISIBLE);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        rvMusicFiles.setHasFixedSize(true);
        // use a linear layout manager
        layoutManager = new LinearLayoutManager(this);
        rvMusicFiles.setLayoutManager(layoutManager);

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
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this, mAccelerometer);
        mSensorManager.unregisterListener(this, mProximity);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.unregisterReceiver(broadcastConnectivity);
    }

    public void getMusicInfo()
    {

        /*try {
            String[] projection = new String[]{MediaStore.Audio.Media._ID,  MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.DATA, MediaStore.Audio.Media.ALBUM_KEY};
            Cursor cursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, projection, null, null, null);

            if (cursor != null)
            {
                while(cursor.moveToNext())
                {
                    long id = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media._ID));
                    String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                    // int albumId = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID);
                    String data = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                    String albumkey = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_KEY));

                    musicFiles.add(new MusicFile(id, title, data, albumkey));
                }

                cursor.close();
            }
        } catch (Exception e)
        {
            Log.i("Exception", e.toString());
        }*/
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

    @Override
    public void onSensorChanged(SensorEvent event) {
        int type = event.sensor.getType();
        for (DroidAmpSensor sensor: sensorList)
        {
            if (sensor.getSensorType() == type)
                sensor.handleSensorEvent(event);
        }
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

    public void getEventList()
    {

    }
}
