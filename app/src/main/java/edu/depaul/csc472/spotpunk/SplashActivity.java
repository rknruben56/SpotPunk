package edu.depaul.csc472.spotpunk;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.spotify.sdk.android.player.Config;
import com.spotify.sdk.android.player.ConnectionStateCallback;
import com.spotify.sdk.android.player.Error;
import com.spotify.sdk.android.player.Spotify;
import com.spotify.sdk.android.player.SpotifyPlayer;

public class SplashActivity extends Activity implements
        ConnectionStateCallback {

    private static final int REQUEST_CODE = 1337;
    private static final String REDIRECT_URI = "testschema://callback";

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
        AuthenticationRequest.Builder builder =
                new AuthenticationRequest.Builder(singleton.getClientId(), AuthenticationResponse.Type.TOKEN, REDIRECT_URI);
        builder.setScopes(new String[]{"user-read-private", "user-read-birthdate", "user-read-email", "streaming", "playlist-modify-public"});
        AuthenticationRequest request = builder.build();

        AuthenticationClient.openLoginActivity(this, REQUEST_CODE, request);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        // Check if the result comes from the correct activity
        if (requestCode == REQUEST_CODE) {
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
