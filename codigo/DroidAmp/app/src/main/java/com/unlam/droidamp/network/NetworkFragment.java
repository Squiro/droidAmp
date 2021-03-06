package com.unlam.droidamp.network;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import com.unlam.droidamp.interfaces.RequestCallback;


public class NetworkFragment extends Fragment {

    public static final String TAG = "NetworkFragment";
    protected RequestCallback<NetworkTask.Result> callback;

    /**
     * Static initializer for NetworkFragment that sets the URL of the host it will be downloading
     * from.
     */
    public static <T extends NetworkFragment> T getInstance (Class<T> mClass, FragmentManager fragmentManager, String tag) {
        try {
            // Recover NetworkFragment in case we are re-creating the Activity due to a config change.
            // This is necessary because NetworkFragment might have a task that began running before
            // the config change occurred and has not finished yet.
            // The NetworkFragment is recoverable because it calls setRetainInstance(true).
            T networkFragment = (T) fragmentManager.findFragmentByTag(tag);
            if (networkFragment == null) {
                networkFragment = mClass.newInstance();
                fragmentManager.beginTransaction().add(networkFragment, TAG).commit();
            }
            return networkFragment;
        } catch (java.lang.InstantiationException | IllegalAccessException e) {
            /**
             * Error thrown
             */
        }
        return null;
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
        callback = (RequestCallback<NetworkTask.Result>) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        // Clear reference to host Activity to avoid memory leak.
        callback = null;
    }
}
