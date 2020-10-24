package com.unlam.droidamp.activities.main.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.unlam.droidamp.activities.main.asynctask.MusicResolverTask;
import com.unlam.droidamp.activities.main.classes.MusicFile;
import com.unlam.droidamp.interfaces.MusicResolverCallback;

import java.util.ArrayList;

public class MusicResolverFragment extends Fragment {

    private static final String TAG = "MusicResolverFragment";
    protected MusicResolverCallback<ArrayList<MusicFile>> callback;
    private MusicResolverTask musicResolverTask;

    /**
     * Static initializer for NetworkFragment that sets the URL of the host it will be downloading
     * from.
     */
    public static MusicResolverFragment getInstance(FragmentManager fragmentManager) {
        MusicResolverFragment networkFragment = (MusicResolverFragment) fragmentManager.findFragmentByTag(MusicResolverFragment.TAG);
        if (networkFragment == null) {
            networkFragment = new MusicResolverFragment();
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
    public void onAttach(Context context) {
        super.onAttach(context);
        // Host Activity will handle callbacks from task.
        callback = (MusicResolverCallback<ArrayList<MusicFile>>) context;
        startMusicResolverTask(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        // Clear reference to host Activity to avoid memory leak.
        callback = null;
    }


    @Override
    public void onDestroy() {
        // Cancel task when Fragment is destroyed.
        cancelTask();
        super.onDestroy();
    }

    /**
     * Start non-blocking execution of LoginTask.
     */
    public void startMusicResolverTask(Context context) {
        cancelTask();
        musicResolverTask = new MusicResolverTask(this.callback, context);
        musicResolverTask.execute();
    }

    /**
     * Cancel (and interrupt if necessary) any ongoing Task execution.
     */
    public void cancelTask()
    {
        if (musicResolverTask != null) {
            musicResolverTask.cancel(true);
        }
    }
}
