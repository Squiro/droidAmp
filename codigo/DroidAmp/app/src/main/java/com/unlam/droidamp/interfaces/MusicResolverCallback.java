package com.unlam.droidamp.interfaces;
import com.unlam.droidamp.network.BroadcastConnectivity;

public interface MusicResolverCallback<T> {
    /**
     * Indicates that the callback handler needs to update its appearance or information based on
     * the result of the task. Expected to be called from the main thread.
     */
    void updateFromMusicResolver(T result);

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
