package edu.depaul.csc472.spotpunk;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.mindorks.placeholderview.SwipeDecor;
import com.mindorks.placeholderview.SwipePlaceHolderView;
import com.spotify.sdk.android.player.Error;
import com.spotify.sdk.android.player.PlayerEvent;
import com.spotify.sdk.android.player.Spotify;
import com.spotify.sdk.android.player.SpotifyPlayer;

import edu.depaul.csc472.spotpunk.helpers.ITrackHelper;
import edu.depaul.csc472.spotpunk.helpers.NavDrawerHelper;
import edu.depaul.csc472.spotpunk.helpers.TrackHelper;
import edu.depaul.csc472.spotpunk.listeners.IPlaybackListener;
import edu.depaul.csc472.spotpunk.listeners.IPlaylistListener;
import edu.depaul.csc472.spotpunk.listeners.IUpdateTrackListener;
import edu.depaul.csc472.spotpunk.listeners.PlaybackListener;
import edu.depaul.csc472.spotpunk.listeners.IUIListener;
import edu.depaul.csc472.spotpunk.listeners.PlaylistListener;
import edu.depaul.csc472.spotpunk.listeners.UpdateTrackListener;
import kaaes.spotify.webapi.android.models.Track;

public class MainActivity extends AppCompatActivity implements
        SpotifyPlayer.NotificationCallback, IUIListener {

    /**
     * UI controls that can only be enabled after login
     */
    private static final int[] REQUIRES_INITIALIZED_STATE = {
            R.id.rejectBtn,
            R.id.acceptBtn
    };

    /**
     * Singleton object containing shared data
     */
    private AppSingleton singleton;

    /**
     * Swipe view that renders the cards
     */
    private SwipePlaceHolderView swipeView;

    /**
     * UI context
     */
    private Context context;

    /**
     * Updates the playback status
     */
    private IPlaybackListener playbackListener;

    /**
     * Updates the set of random tracks
     */
    private IUpdateTrackListener updateTrackListener;

    /**
     * Updates the playlist
     */
    private IPlaylistListener playlistListener;

    /**
     * Helper for Tracks
     */
    private ITrackHelper trackHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        singleton = AppSingleton.getInstance();

        trackHelper = new TrackHelper();

        // Initialize the navigation drawer
        initializeNavDrawer();

        // Initialize the Swipe view and animation objects
        initializeSwipeView();

        // Set up listeners for playback and spotify
        initializeListeners();

        Intent intent = getIntent();

        // Setup the music player notification callback
        singleton.getmPlayer().addNotificationCallback(MainActivity.this);

        if (intent != null &&
                intent.getSerializableExtra("Source") == AppSingleton.APP_SCREEN.Splash) {
            // Get the initial set of random tracks
            updateTrackListener.updateRandomTracks(true);
        } else {
            updateUI(false);
        }
    }

    private void initializeNavDrawer() {
        NavDrawerHelper drawerHelper = new NavDrawerHelper(this,
                findViewById(R.id.drawerView),
                findViewById(R.id.drawerLayoutMain),
                findViewById(R.id.toolbarMain),
                AppSingleton.APP_SCREEN.Main);
        drawerHelper.setupNavDrawer();
    }

    private void initializeSwipeView() {
        swipeView = findViewById(R.id.swipeView);
        context = getApplicationContext();

        swipeView.getBuilder()
                .setDisplayViewCount(3)
                .setSwipeDecor(new SwipeDecor()
                        .setPaddingTop(10)
                        .setRelativeScale(0.01f)
                        .setSwipeInMsgLayoutId(R.layout.swipe_in_msg_view)
                        .setSwipeOutMsgLayoutId(R.layout.swipe_out_msg_view));
    }

    private void initializeListeners() {
        playbackListener = new PlaybackListener(null);
        updateTrackListener = new UpdateTrackListener(playbackListener,
                new SearchTermRepository(), this);
        playlistListener = new PlaylistListener(updateTrackListener, MainActivity.this);
    }

    @Override
    protected void onDestroy() {
        Spotify.destroyPlayer(this);
        super.onDestroy();
    }

    @Override
    public void onPlaybackEvent(PlayerEvent playerEvent) {
        Log.d("MainActivity", "Playback event received: " + playerEvent.name());
        playbackListener.setPlaybackState(singleton.getmPlayer().getPlaybackState());
    }

    @Override
    public void onPlaybackError(Error error) {
        Log.d("MainActivity", "Playback error received: " + error.name());
        switch (error) {
            // Handle error type as necessary
            default:
                break;
        }
    }

    /**
     * Updates the list of track cards
     */
    public void updateUI(boolean getFreshTrackList) {
        Log.d("MainActivity", "Updating UI!");
        boolean isInitialized = isInitialized();

        if (getFreshTrackList) {
            // Remove any strangling songs
            swipeView.removeAllViews();
        }

        // Enable any widgets as necessary
        for (int id : REQUIRES_INITIALIZED_STATE) {
            findViewById(id).setEnabled(isInitialized);
        }

        // add Track cards
        for(Track track : singleton.getTracks()) {
            swipeView.addView(new TrackCard(playbackListener, updateTrackListener,
                    playlistListener, trackHelper, context, track, swipeView));
        }
    }

    private boolean isInitialized() {
        return singleton.getmPlayer() != null;
    }

    /**
     * Gets the next track to display
     *
     * @param view next button
     */
    public void onRejectButtonClick(View view) {
        swipeView.doSwipe(false);
    }

    /**
     * OnClick handler for the Add Button
     * @param view
     */
    public void onAddButtonClick(View view) {
        swipeView.doSwipe(true);
    }

}
