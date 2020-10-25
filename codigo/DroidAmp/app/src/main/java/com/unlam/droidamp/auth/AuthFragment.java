package com.unlam.droidamp.auth;

import android.util.Log;

import com.unlam.droidamp.activities.login.asynctasks.LoginTask;
import com.unlam.droidamp.activities.register.asynctasks.RegisterTask;
import com.unlam.droidamp.models.User;
import com.unlam.droidamp.network.NetworkFragment;
import com.unlam.droidamp.network.NetworkHandler;

public class AuthFragment extends NetworkFragment {

    private RegisterTask registerTask;
    private LoginTask loginTask;
    private TokenTask tokenTask;

    @Override
    public void onDestroy() {
        // Cancel task when Fragment is destroyed.
        cancelTask();
        super.onDestroy();
    }

    /**
     * Start non-blocking execution of LoginTask.
     */
    public void startLogin(User user, Auth auth) {
        cancelTask();
        loginTask = new LoginTask(this.callback, user, auth);
        loginTask.execute(NetworkHandler.API_ENDPOINT + "login");
    }

    /**
     * Start non-blocking execution of RegisterTask.
     */
    public void startRegister(User user, Auth auth) {
        cancelTask();
        registerTask = new RegisterTask(this.callback, user, auth);
        registerTask.execute(NetworkHandler.API_ENDPOINT + "register");
    }

    public void startRefreshToken(Auth auth)
    {
        cancelTask();
        tokenTask = new TokenTask(this.callback, auth);
        tokenTask.execute(NetworkHandler.API_ENDPOINT + "refresh");
    }

    /**
     * Cancel (and interrupt if necessary) any ongoing Task execution.
     */
    public void cancelTask()
    {
        if (registerTask != null) {
            registerTask.cancel(true);
        }
        if (loginTask != null) {
            loginTask.cancel(true);
        }
        if (tokenTask != null) {
            tokenTask.cancel(true);
        }
    }
}
