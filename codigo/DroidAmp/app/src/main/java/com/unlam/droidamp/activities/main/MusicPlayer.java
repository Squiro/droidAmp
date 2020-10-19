package com.unlam.droidamp.activities.main;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.Log;

public class MusicPlayer {

    private MediaPlayer mediaPlayer;

    public MusicPlayer()
    {
        this.mediaPlayer = new MediaPlayer();
    }

    public void start(String path)
    {
        try
        {
            //mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setDataSource(path);
            mediaPlayer.prepare();
            mediaPlayer.start();
        }
        catch (Exception e)
        {
            Log.i("Exception", e.toString());
        }
    }

    public void resume()
    {
        mediaPlayer.start();
    }

    public void pause()
    {
        mediaPlayer.pause();
    }

    public void stop()
    {
        mediaPlayer.stop();
        mediaPlayer.release();
    }

}
