package com.unlam.droidamp.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.unlam.droidamp.asynctasks.LoginTask;
import com.unlam.droidamp.interfaces.LoginCallback;

public class NetworkLoginFragment extends Fragment {
    public static final String TAG = "NetworkLoginFragment";

    private static final String URL_KEY = "UrlKey";

    private LoginCallback<String> callback;
    private LoginTask loginTask;
    private String urlString;

    /**
     * Static initializer for NetworkFragment that sets the URL of the host it will be downloading
     * from.
     */
    public static NetworkLoginFragment getInstance(FragmentManager fragmentManager, String url) {
        // Recover NetworkFragment in case we are re-creating the Activity due to a config change.
        // This is necessary because NetworkFragment might have a task that began running before
        // the config change occurred and has not finished yet.
        // The NetworkFragment is recoverable because it calls setRetainInstance(true).
        NetworkLoginFragment networkFragment = (NetworkLoginFragment) fragmentManager.findFragmentByTag(NetworkLoginFragment.TAG);
        if (networkFragment == null) {
            networkFragment = new NetworkLoginFragment();
            Bundle args = new Bundle();
            args.putString(URL_KEY, url);
            networkFragment.setArguments(args);
            fragmentManager.beginTransaction().add(networkFragment, TAG).commit();
        }
        return networkFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Retain this Fragment across configuration changes in the host Activity.
        setRetainInstance(true);

        urlString = getArguments().getString(URL_KEY);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // Host Activity will handle callbacks from task.
        callback = (LoginCallback<String>) context;
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
        cancelLogin();
        super.onDestroy();
    }

    /**
     * Start non-blocking execution of LoginTask.
     */
    public void startLogin(String email, String password) {
        cancelLogin();
        loginTask = new LoginTask(callback, email, password);
        loginTask.execute(urlString);
    }

    /**
     * Cancel (and interrupt if necessary) any ongoing LoginTask execution.
     */
    public void cancelLogin() {
        if (loginTask != null) {
            loginTask.cancel(true);
        }
    }


}
