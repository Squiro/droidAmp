package com.unlam.droidamp.activities.main;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.unlam.droidamp.R;
import com.unlam.droidamp.activities.album.AlbumActivity;
import com.unlam.droidamp.activities.base.BaseActivity;
import com.unlam.droidamp.activities.main.classes.EventAdapter;
import com.unlam.droidamp.activities.main.classes.sensors.AccelerometerSensor;
import com.unlam.droidamp.activities.main.classes.sensors.LightSensor;
import com.unlam.droidamp.activities.main.classes.sensors.ProximitySensor;
import com.unlam.droidamp.activities.main.classes.sensors.DroidAmpSensor;
import com.unlam.droidamp.activities.main.fragments.MusicResolverFragment;
import com.unlam.droidamp.auth.Auth;
import com.unlam.droidamp.interfaces.MusicResolverCallback;
import com.unlam.droidamp.activities.main.classes.MediaAdapter;
import com.unlam.droidamp.models.MusicFile;
import com.unlam.droidamp.activities.main.fragments.MusicPlayerFragment;
import com.unlam.droidamp.models.event.NetworkEventFragment;
import com.unlam.droidamp.interfaces.BtnListener;
import com.unlam.droidamp.network.NetworkTask;

import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends BaseActivity implements SensorEventListener, MusicResolverCallback<ArrayList<MusicFile>> {
    // UI Elements
    private ProgressBar pgBarMain;
    private RecyclerView rvMusicFiles;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView eventRecylcler;
    private RecyclerView.Adapter eventAdapter;
    private ArrayList<String> eventList;

    private TextView txtNowPlaying;
    private TextView txtAlbum;
    private TextView txtArtist;
    private TextView txtCurrentTime;
    private ImageButton btnPlay;
    private ImageButton btnNext;
    private ImageButton btnPrev;
    private String album;
    private String albumID;
    private String artist;

    // Network fragment
    private  NetworkEventFragment networkEventFragment;
    private Auth auth;

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

        this.currentPosition = 0;
        this.auth = new Auth(this);
        this.albumID = getIntent().getExtras().getString(AlbumActivity.ALBUM_ID_KEY);
        this.album = getIntent().getExtras().getString(AlbumActivity.ALBUM_KEY);
        this.artist = getIntent().getExtras().getString(AlbumActivity.ARTIST_KEY);

        fetchUI();
        setListeners();
        instantiateFragments();
        setUpRecyclerViews();
        getSensors();

        txtAlbum.setText(this.album);
        txtArtist.setText(this.artist);
    }

    public void fetchUI()
    {
        this.pgBarMain = findViewById(R.id.pgBarMain);
        this.pgBarMain.setVisibility(View.VISIBLE);
        this.txtNowPlaying = findViewById(R.id.txtNowPlaying);
        this.txtAlbum = findViewById(R.id.txtAlbum);
        this.txtArtist = findViewById(R.id.txtArtist);
        this.txtCurrentTime = findViewById(R.id.txtCurrentTime);
        this.btnNext = findViewById(R.id.btnNext);
        this.btnPlay = findViewById(R.id.btnPlay);
        this.btnPrev = findViewById(R.id.btnPrev);
        txtNowPlaying.setSelected(true);
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
        this.sensorList.add(new ProximitySensor(this, this.auth, sharedPreferences, musicPlayerFragment));
        this.sensorList.add(new AccelerometerSensor(this, this.auth, sharedPreferences, musicPlayerFragment));
        this.sensorList.add(new LightSensor(this, this.auth, sharedPreferences, musicPlayerFragment));
    }

    public void instantiateFragments()
    {
        musicResolverFragment = MusicResolverFragment.getInstance(getSupportFragmentManager(), this.albumID);
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

    public void playMusicFile(int position)
    {
        this.currentPosition = position;
        MusicFile song = musicFiles.get(this.currentPosition);
        txtNowPlaying.setText(song.getTitle());
        musicPlayerFragment.start(song.getPath());
        setTimerTask();
    }

    public void playNext()
    {
        if (this.currentPosition >= this.musicFiles.size()-1)
            this.currentPosition = -1;
        this.playMusicFile(this.currentPosition+1);
    }

    public void playPrev()
    {
        if (this.currentPosition <= 0)
            this.currentPosition = this.musicFiles.size();
        this.playMusicFile(this.currentPosition-1);
    }

    public void setTimerTask()
    {
        final Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (MusicPlayerFragment.getMediaPlayer() != null ) {
                            txtCurrentTime.post(new Runnable() {
                                @Override
                                public void run() {
                                    txtCurrentTime.setText(musicPlayerFragment.getCurrentTime());
                                }
                            });
                        } else {
                            timer.cancel();
                            timer.purge();
                        }
                    }
                });
            }
        }, 0, 1000);
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
        // Hide the progress bar
        this.pgBarMain.setVisibility(View.INVISIBLE);
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

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void updateFromRequest(NetworkTask.Result result) {

    }

    @Override
    public void finishRequest() {
        if (musicResolverFragment != null) {
            musicResolverFragment.cancelTask();
        }
    }

    public void setListeners()
    {
        btnPlay.setOnClickListener(btnPlayListener);
        btnNext.setOnClickListener(btnNextListener);
        btnPrev.setOnClickListener(btnPrevListener);
    }

    private View.OnClickListener btnPlayListener = new View.OnClickListener()
    {
        // This method will be executed once the button is clicked
        public void onClick(View v)
        {
            musicPlayerFragment.play();
        }
    };

    private View.OnClickListener btnNextListener = new View.OnClickListener()
    {
        // This method will be executed once the button is clicked
        public void onClick(View v)
        {
            playNext();
        }
    };

    private View.OnClickListener btnPrevListener = new View.OnClickListener()
    {
        // This method will be executed once the button is clicked
        public void onClick(View v)
        {
            playPrev();
        }
    };
}
