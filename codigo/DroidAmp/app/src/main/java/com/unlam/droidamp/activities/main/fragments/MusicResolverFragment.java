package com.unlam.droidamp.activities.main.fragments;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.unlam.droidamp.activities.album.AlbumActivity;
import com.unlam.droidamp.activities.main.asynctask.MusicResolverTask;
import com.unlam.droidamp.models.MusicFile;
import com.unlam.droidamp.interfaces.MusicResolverCallback;

import java.util.ArrayList;

public class MusicResolverFragment extends Fragment {

    private static final String TAG = "MusicResolverFragment";
    protected MusicResolverCallback<ArrayList<MusicFile>> callback;
    private MusicResolverTask musicResolverTask;
    private String album;

    /**
     * Static initializer for NetworkFragment that sets the URL of the host it will be downloading
     * from.
     */
    public static MusicResolverFragment getInstance(FragmentManager fragmentManager, String album) {
        MusicResolverFragment musicResolverFragment = (MusicResolverFragment) fragmentManager.findFragmentByTag(MusicResolverFragment.TAG);
        if (musicResolverFragment == null) {
            musicResolverFragment = new MusicResolverFragment();
            Bundle args = new Bundle();
            args.putString(AlbumActivity.ALBUM_ID_KEY, album);
            musicResolverFragment.setArguments(args);
            fragmentManager.beginTransaction().add(musicResolverFragment, TAG).commit();
        }
        return musicResolverFragment;
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
        this.album = getArguments().getString(AlbumActivity.ALBUM_ID_KEY);
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
        Log.i("Log", "MusicResolver Task Starting");
        musicResolverTask = new MusicResolverTask(this.callback, context, this.album);
        musicResolverTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
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
