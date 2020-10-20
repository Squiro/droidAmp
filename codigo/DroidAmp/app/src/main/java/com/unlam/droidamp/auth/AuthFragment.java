package com.unlam.droidamp.auth;

import com.unlam.droidamp.activities.login.asynctasks.LoginTask;
import com.unlam.droidamp.activities.register.asynctasks.RegisterTask;
import com.unlam.droidamp.models.User;
import com.unlam.droidamp.network.NetworkFragment;

public class AuthFragment extends NetworkFragment {

    private RegisterTask registerTask;
    private LoginTask loginTask;
    private TokenTask tokenTask;
    private static final String API_ENDPOINT = "http://so-unlam.net.ar/api/api/";

    @Override
    public void onDestroy() {
        // Cancel task when Fragment is destroyed.
        cancelTask();
        super.onDestroy();
    }

    /**
     * Start non-blocking execution of LoginTask.
     */
    public void startLogin(String email, String password, Auth auth) {
        cancelTask();
        loginTask = new LoginTask(this.callback, email, password, auth);
        loginTask.execute(API_ENDPOINT + "login");
    }

    /**
     * Start non-blocking execution of RegisterTask.
     */
    public void startRegister(User user, Auth auth) {
        cancelTask();
        registerTask = new RegisterTask(this.callback, user, auth);
        registerTask.execute(API_ENDPOINT + "register");
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

    public void startRefreshToken(Auth auth)
    {
        cancelTask();
        tokenTask = new TokenTask(this.callback, auth);
        tokenTask.execute(API_ENDPOINT + "refresh");
    }
}
