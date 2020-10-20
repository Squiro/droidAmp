package com.unlam.droidamp.activities.main.fragments;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

public class MusicPlayerFragment extends Fragment {

    public static final String TAG = "MusicPlayerFragment";
    private static final String POS_KEY = "Position";
    private static MediaPlayer mediaPlayer;
    private static boolean isPlaying;
    private static boolean isMuted;

    public static MusicPlayerFragment getInstance(FragmentManager fragmentManager) {
        // Recover NetworkFragment in case we are re-creating the Activity due to a config change.
        // This is necessary because NetworkFragment might have a task that began running before
        // the config change occurred and has not finished yet.
        // The NetworkFragment is recoverable because it calls setRetainInstance(true).
        MusicPlayerFragment networkFragment = (MusicPlayerFragment) fragmentManager.findFragmentByTag(MusicPlayerFragment.TAG);
        if (networkFragment == null) {
            networkFragment = new MusicPlayerFragment();
            mediaPlayer = new MediaPlayer();
            isPlaying = false;
            fragmentManager.beginTransaction().add(networkFragment, TAG).commit();
        }
        return networkFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Retain this Fragment across configuration changes in the host Activity.
        setRetainInstance(true);
    }

    @Override
    public void onResume() {
        super.onResume();
    }
    public void start(String path)
    {
        try
        {
            if(isPlaying)
            {
                this.reset();
            }
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

    public void mute()
    {
        if (isMuted)
            mediaPlayer.setVolume(1,1);
        else
            mediaPlayer.setVolume(0, 0);

        isMuted = !isMuted;
    }
}
