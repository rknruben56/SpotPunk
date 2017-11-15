package edu.depaul.csc472.spotpunk;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.mindorks.placeholderview.SwipeDecor;
import com.mindorks.placeholderview.SwipePlaceHolderView;
import com.spotify.sdk.android.player.Error;
import com.spotify.sdk.android.player.PlayerEvent;
import com.spotify.sdk.android.player.Spotify;
import com.spotify.sdk.android.player.SpotifyPlayer;

import edu.depaul.csc472.spotpunk.listeners.IPlaybackListener;
import edu.depaul.csc472.spotpunk.listeners.IPlaylistListener;
import edu.depaul.csc472.spotpunk.listeners.IUpdateTrackListener;
import edu.depaul.csc472.spotpunk.listeners.PlaybackListener;
import edu.depaul.csc472.spotpunk.listeners.IUIListener;
import edu.depaul.csc472.spotpunk.listeners.PlaylistListener;
import edu.depaul.csc472.spotpunk.listeners.UpdateTrackListener;
import kaaes.spotify.webapi.android.models.Track;

public class MainActivity extends Activity implements
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        singleton = AppSingleton.getInstance();

        // Initialize the Swipe view and animation objects
        swipeView = findViewById(R.id.swipeView);
        context = getApplicationContext();

        swipeView.getBuilder()
                .setDisplayViewCount(3)
                .setSwipeDecor(new SwipeDecor()
                        .setPaddingTop(10)
                        .setRelativeScale(0.01f)
                        .setSwipeInMsgLayoutId(R.layout.swipe_in_msg_view)
                        .setSwipeOutMsgLayoutId(R.layout.swipe_out_msg_view));

        // Set up listeners
        playbackListener = new PlaybackListener(null);
        updateTrackListener = new UpdateTrackListener(playbackListener,
                new SearchTermRepository(), this);
        playlistListener = new PlaylistListener(updateTrackListener, MainActivity.this);

        // Setup the music player notification callback
        singleton.getmPlayer().addNotificationCallback(MainActivity.this);

        // Get the initial set of random tracks
        updateTrackListener.updateRandomTracks(false);
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
     * Gets the next track to display
     *
     * @param view next button
     */
    public void onRejectButtonClicked(View view) {
        swipeView.doSwipe(false);
    }

    /**
     * Updates the list of track cards 
     */
    public void updateUI() {
        Log.d("MainActivity", "Updating UI!");
        boolean isInitialized = isInitialized();

        // Remove any strangling songs
        swipeView.removeAllViews();

        // Enable any widgets as necessary
        for (int id : REQUIRES_INITIALIZED_STATE) {
            findViewById(id).setEnabled(isInitialized);
        }

        // add new Track cards
        for(Track track : singleton.getTracks()) {
            swipeView.addView(new TrackCard(playbackListener, updateTrackListener,
                    playlistListener, context, track, swipeView));
        }
    }

    private boolean isInitialized() {
        return singleton.getmPlayer() != null;
    }

    /**
     * OnClick handler for the Add Button
     * @param view
     */
    public void onAddButtonClick(View view) {
        swipeView.doSwipe(true);
    }
}
