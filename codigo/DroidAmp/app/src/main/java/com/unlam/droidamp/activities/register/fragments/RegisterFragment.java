package com.unlam.droidamp.activities.register.fragments;

import com.unlam.droidamp.activities.register.asynctasks.RegisterTask;
import com.unlam.droidamp.auth.Auth;
import com.unlam.droidamp.models.User;
import com.unlam.droidamp.network.NetworkFragment;

public class RegisterFragment extends NetworkFragment {
    private RegisterTask registerTask;

    @Override
    public void onDestroy() {
        // Cancel task when Fragment is destroyed.
        cancelRegister();
        super.onDestroy();
    }

    /**
     * Start non-blocking execution of RegisterTask.
     */
    public void startRegister(User user, Auth auth) {
        cancelRegister();
        registerTask = new RegisterTask(this.callback, user, auth);
        registerTask.execute(this.urlString);
    }

    /**
     * Cancel (and interrupt if necessary) any ongoing RegisterTask execution.
     */
    public void cancelRegister() {
        if (registerTask != null) {
            registerTask.cancel(true);
        }
    }
}
