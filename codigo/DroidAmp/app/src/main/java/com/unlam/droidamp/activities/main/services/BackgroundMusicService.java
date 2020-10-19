package com.unlam.droidamp.activities.main.services;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

public class BackgroundMusicService extends Service {

    private static final String TAG = null;
    MediaPlayer player;

    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        try {
            player = new MediaPlayer();
            player.setDataSource(Environment.getExternalStorageDirectory().getAbsolutePath() );
            player.setVolume(100,100);
        }
        catch (Exception e)
        {
            Log.i("Exception", e.toString());
        }
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        try
        {
            player.prepare();
            player.start();
        }
        catch (Exception e)
        {
            Log.i("Exception", e.toString());
        }

        return START_STICKY;
    }

    public IBinder onUnBind(Intent arg0) {
        // TO DO Auto-generated method
        return null;
    }

    public void onStop() {

    }
    public void onPause() {

    }
    @Override
    public void onDestroy() {
        player.stop();
        player.release();
    }

    @Override
    public void onLowMemory() {

    }
}
