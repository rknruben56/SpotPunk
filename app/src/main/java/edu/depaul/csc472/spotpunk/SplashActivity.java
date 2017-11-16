package edu.depaul.csc472.spotpunk;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.spotify.sdk.android.player.Config;
import com.spotify.sdk.android.player.ConnectionStateCallback;
import com.spotify.sdk.android.player.Error;
import com.spotify.sdk.android.player.Spotify;
import com.spotify.sdk.android.player.SpotifyPlayer;

import edu.depaul.csc472.spotpunk.tasks.LoginTask;

/**
 * Main App Launcher activity.
 * NOTE: Login code follows the example from the Spotify SDK site
 * tutorial: https://developer.spotify.com/technologies/spotify-android-sdk/tutorial/
 */
public class SplashActivity extends Activity implements
        ConnectionStateCallback {

    /**
     * Singleton object containing shared data
     */
    private AppSingleton singleton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        singleton = AppSingleton.getInstance();

        // Login
        new LoginTask().execute(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        // Check if the result comes from the correct activity
        if (requestCode == AppSingleton.getRequestCode()) {
            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);
            if (response.getType() == AuthenticationResponse.Type.TOKEN) {
                // Initialize the main Spotify service
                Log.d("MainActivity", response.getAccessToken());
                singleton.initializeService(response.getAccessToken());

                // Set the media player
                Config playerConfig = new Config(this, response.getAccessToken(), singleton.getClientId());
                Spotify.getPlayer(playerConfig, this, new SpotifyPlayer.InitializationObserver() {

                    @Override
                    public void onInitialized(SpotifyPlayer spotifyPlayer) {
                        singleton.setmPlayer(spotifyPlayer);
                        singleton.getmPlayer().addConnectionStateCallback(SplashActivity.this);
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        Log.e("SplashActivity", "Could not initialize player: "
                                + throwable.getMessage());
                    }
                });
            }
        }
    }

    @Override
    public void onLoggedIn() {
        // Once the user is logged in, go to the main page
        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        intent.putExtra("Source", AppSingleton.APP_SCREEN.Splash);
        startActivity(intent);
    }

    @Override
    public void onLoggedOut() {
        Log.d("SplashActivity", "User logged out");
    }

    @Override
    public void onLoginFailed(Error error) {
        Log.d("SplashActivity", "Login failed");
    }

    @Override
    public void onTemporaryError() {
        Log.d("SplashActivity", "Temporary error occurred");
    }

    @Override
    public void onConnectionMessage(String message) {
        Log.d("SplashActivity", "Received connection message: " + message);
    }
}
