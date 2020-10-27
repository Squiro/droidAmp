package com.unlam.droidamp.activities.main.fragments;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.unlam.droidamp.models.MusicFile;

public class MusicPlayerFragment extends Fragment {

    private static final String TAG = "MusicPlayerFragment";
    private static MediaPlayer mediaPlayer;
    private static boolean isPlaying;
    private static boolean isMuted;
    private static final int maxVolume = 100;
    private static int currVolume;

    public static MusicPlayerFragment getInstance(FragmentManager fragmentManager) {
        MusicPlayerFragment musicPlayerFragment = (MusicPlayerFragment) fragmentManager.findFragmentByTag(MusicPlayerFragment.TAG);
        if (musicPlayerFragment == null) {
            musicPlayerFragment = new MusicPlayerFragment();
            mediaPlayer = new MediaPlayer();
            isPlaying = false;
            fragmentManager.beginTransaction().add(musicPlayerFragment, TAG).commit();
        }
        return musicPlayerFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Retain this Fragment across configuration changes in the host Activity.
        setRetainInstance(true);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.stop();
    }

    public void start(String path)
    {
        try
        {
            this.reset();
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
        mediaPlayer.release();
        isPlaying = false;
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

    public void mute()
    {
        if (isMuted)
            mediaPlayer.setVolume(1,1);
        else
            mediaPlayer.setVolume(0, 0);

        isMuted = !isMuted;
    }

    public void changeVolume()
    {
        if (isPlaying)
        {
            if (currVolume >= maxVolume)
                currVolume = 0;
            currVolume += 20;
            Toast.makeText(this.getContext(), "Volumen actual: " + (maxVolume-currVolume), Toast.LENGTH_LONG).show();
            float log1= (float) (Math.log(maxVolume-currVolume)/Math.log(maxVolume));
            mediaPlayer.setVolume(log1, log1);
        }
    }

    public static boolean isPlaying()
    {
        return isPlaying;
    }

    public static MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    public String getCurrentTime()
    {
        return MusicFile.formateMilliSeccond(mediaPlayer.getCurrentPosition());
    }
}
