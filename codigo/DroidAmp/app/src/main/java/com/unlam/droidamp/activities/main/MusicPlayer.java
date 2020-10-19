package com.unlam.droidamp.activities.main;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.Log;

public class MusicPlayer {

    private MediaPlayer mediaPlayer;
    private boolean isPlaying;

    public MusicPlayer()
    {
        this.mediaPlayer = new MediaPlayer();
        isPlaying = false;
    }

    public void start(String path)
    {
        try
        {
            if(isPlaying)
            {
                this.reset();
            }
            //mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setDataSource(path);
            mediaPlayer.prepare();
            mediaPlayer.start();
            isPlaying = true;
        }
        catch (Exception e)
        {
            Log.i("Exception", e.toString());
        }
    }

    public void resume()
    {
        mediaPlayer.start();
        isPlaying = true;
    }

    public void pause()
    {
        mediaPlayer.pause();
        isPlaying = false;
    }

    public void stop()
    {
        mediaPlayer.stop();
        isPlaying = false;
        //mediaPlayer.release();
    }

    public void reset()
    {
        mediaPlayer.reset();
    }

    public void play()
    {
        if (isPlaying)
            pause();
        else
            resume();
    }

}
