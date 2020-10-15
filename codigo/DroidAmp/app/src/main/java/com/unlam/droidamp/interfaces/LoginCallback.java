package com.unlam.droidamp.interfaces;

import android.content.Intent;
import android.net.NetworkInfo;

public interface LoginCallback<T> {
    interface Progress {
        int ERROR = -1;
        int CONNECT_SUCCESS = 0;
        int GET_INPUT_STREAM_SUCCESS = 1;
        int PROCESS_INPUT_STREAM_IN_PROGRESS = 2;
        int PROCESS_INPUT_STREAM_SUCCESS = 3;
    }

    /**
     * Indicates that the callback handler needs to update its appearance or information based on
     * the result of the task. Expected to be called from the main thread.
     */
    void updateFromLogin(T result);

    /**
     * Get the device's active network status in the form of a NetworkInfo object.
     */
    NetworkInfo getActiveNetworkInfo();

    /**
     * Indicate to callback handler any progress update.
     * @param progressCode must be one of the constants defined in LoginCallback.Progress.
     * @param percentComplete must be 0-100.
     */
    void onProgressUpdate(int progressCode, int percentComplete);

    /**
     * Indicates that the login operation has finished. This method is called even if the
     * login hasn't completed successfully.
     */
    void finishLogin(Intent intent);
}
