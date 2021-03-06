package com.unlam.droidamp.models.event;

import android.os.AsyncTask;
import android.util.Log;

import com.unlam.droidamp.auth.Auth;
import com.unlam.droidamp.network.NetworkFragment;
import com.unlam.droidamp.network.NetworkHandler;

public class NetworkEventFragment extends NetworkFragment {

    public static final String TAG = "NetworkEventFragment";
    private EventTask eventTask;

    @Override
    public void onDestroy() {
        // Cancel task when Fragment is destroyed.
        cancelTask();
        super.onDestroy();
    }

    /**
     * Start non-blocking execution of EventTask.
     */
    public void startEventTask(Event event, Auth auth) {
        //cancelTask();
        Log.i("Log", "Event Task Starting");
        eventTask = new EventTask(this.callback, event, auth);
        eventTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, NetworkHandler.API_ENDPOINT + "event");
    }

    /**
     * Cancel (and interrupt if necessary) any ongoing Task execution.
     */
    public void cancelTask()
    {
        if (eventTask != null) {
            eventTask.cancel(true);
        }
    }
}
