package edu.depaul.csc472.spotpunk.tasks;

import android.app.Activity;
import android.os.AsyncTask;

import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;

import edu.depaul.csc472.spotpunk.AppSingleton;

/**
 * Login Task that opens up the Spotify Login Activity
 * Created by rrodr on 11/15/2017.
 */

public class LoginTask extends AsyncTask<Activity, Void, Void> {

    private AppSingleton singleton;
    private static final String REDIRECT_URI = "testschema://callback";
    private static final String[] SCOPES = {
            "user-read-private",
            "user-read-birthdate",
            "user-read-email",
            "streaming",
            "playlist-modify-public"};

    public LoginTask() {
        singleton = AppSingleton.getInstance();
    }

    @Override
    protected Void doInBackground(Activity... params) {
        // Get the Activity that will receive the login result
        Activity activity = params[0];

        // build the request
        AuthenticationRequest.Builder builder =
                new AuthenticationRequest.Builder(singleton.getClientId(), AuthenticationResponse.Type.TOKEN, REDIRECT_URI);
        builder.setScopes(SCOPES);
        AuthenticationRequest request = builder.build();

        // open up the Login activity
        AuthenticationClient.openLoginActivity(activity, AppSingleton.getRequestCode(), request);
        return null;
    }
}
