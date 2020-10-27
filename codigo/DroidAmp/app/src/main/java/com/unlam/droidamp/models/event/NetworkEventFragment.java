package com.unlam.droidamp.models.event;

import android.os.AsyncTask;

import com.unlam.droidamp.auth.Auth;
import com.unlam.droidamp.network.NetworkFragment;
import com.unlam.droidamp.network.NetworkHandler;

public class NetworkEventFragment extends NetworkFragment {

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
        eventTask = new EventTask(this.callback, event, auth);
        eventTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, NetworkHandler.API_ENDPOINT + "event");
        //eventTask.execute();
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
