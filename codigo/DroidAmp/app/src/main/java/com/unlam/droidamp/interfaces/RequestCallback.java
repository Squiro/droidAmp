package com.unlam.droidamp.interfaces;
import com.unlam.droidamp.network.BroadcastConnectivity;

public interface RequestCallback<T> {

    interface ResponseCode {
        int BAD_REQUEST = 400;
        int CONNECT_SUCCESS = 0;
        int GET_INPUT_STREAM_SUCCESS = 1;
        int PROCESS_INPUT_STREAM_IN_PROGRESS = 2;
        int PROCESS_INPUT_STREAM_SUCCESS = 3;
    }

    /**
     * Indicates that the callback handler needs to update its appearance or information based on
     * the result of the task. Expected to be called from the main thread.
     */
    void updateFromRequest(T result);

    /**
     * Get the BroadcastConnectivity instance form the activity.
     */
    BroadcastConnectivity getBroadcastConnectivity();

    /**
     * Indicates that the register operation has finished. This method is called even if the
     * register hasn't completed successfully.
     */
    void finishRequest();
}
