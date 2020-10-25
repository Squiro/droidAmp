package com.unlam.droidamp.activities.album.fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.unlam.droidamp.activities.album.asynctasks.AlbumResolverTask;
import com.unlam.droidamp.models.Album;
import com.unlam.droidamp.interfaces.MusicResolverCallback;

import java.util.ArrayList;

public class AlbumResolverFragment extends Fragment {

    private static final String TAG = "AlbumResolverFragment";
    protected MusicResolverCallback<ArrayList<Album>> callback;
    private AlbumResolverTask albumResolverTask;

    /**
     * Static initializer for NetworkFragment that sets the URL of the host it will be downloading
     * from.
     */
    public static AlbumResolverFragment getInstance(FragmentManager fragmentManager) {
        AlbumResolverFragment albumFragment = (AlbumResolverFragment) fragmentManager.findFragmentByTag(AlbumResolverFragment.TAG);
        if (albumFragment == null) {
            albumFragment = new AlbumResolverFragment();
            fragmentManager.beginTransaction().add(albumFragment, TAG).commit();
        }
        return albumFragment;
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
        callback = (MusicResolverCallback<ArrayList<Album>>) context;
        start(context);
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
    public void start(Context context) {
        cancelTask();
        albumResolverTask = new AlbumResolverTask(this.callback, context);
        albumResolverTask.execute();
    }

    /**
     * Cancel (and interrupt if necessary) any ongoing Task execution.
     */
    public void cancelTask()
    {
        if (albumResolverTask != null) {
            albumResolverTask.cancel(true);
        }
    }
}
