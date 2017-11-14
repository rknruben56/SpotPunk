package edu.depaul.csc472.spotpunk;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.spotify.sdk.android.player.Error;
import com.spotify.sdk.android.player.PlaybackState;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.PlayerEvent;
import com.spotify.sdk.android.player.Spotify;
import com.spotify.sdk.android.player.SpotifyPlayer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import kaaes.spotify.webapi.android.models.Pager;
import kaaes.spotify.webapi.android.models.Playlist;
import kaaes.spotify.webapi.android.models.PlaylistTrack;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.TracksPager;
import kaaes.spotify.webapi.android.models.UserPrivate;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MainActivity extends Activity implements
        SpotifyPlayer.NotificationCallback {

    private static final int SEARCH_RESULT_LIMIT = 2;
    private static final int SEARCH_OFFSET = 4000;
    private static final int REFRESH_RATE = 1;

    /**
     * UI controls that can only be enabled after login
     */
    private static final int[] REQUIRES_INITIALIZED_STATE = {
            R.id.play_button,
            R.id.next_button
    };

    /**
     * Music player from Spotify SDK
     */
    private Player mPlayer;

    /**
     * Current playback state from mPlayer
     */
    private PlaybackState mCurrentPlaybackState;

    /**
     * Singleton object containing shared data
     */
    private AppSingleton singleton;

    /**
     * Library containing random search terms
     */
    private SearchTermRepository searchTermRepository;

    /**
     * Current song being played and/or displayed
     */
    private Track currentSong;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        singleton = AppSingleton.getInstance();

        // Initialize search term repo for random searches
        searchTermRepository = new SearchTermRepository();

        // Setup the music player
        singleton.getmPlayer().addNotificationCallback(MainActivity.this);
        mPlayer = singleton.getmPlayer();

        // Get the initial set of random tracks
        updateRandomTracks(false);
    }

    @Override
    protected void onDestroy() {
        Spotify.destroyPlayer(this);
        super.onDestroy();
    }

    @Override
    public void onPlaybackEvent(PlayerEvent playerEvent) {
        Log.d("MainActivity", "Playback event received: " + playerEvent.name());
        mCurrentPlaybackState = mPlayer.getPlaybackState();
        updateUI();
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
     * Plays and pauses the current song. If there's no song currently
     * playing, it fetches a new one based on the URI
     *
     * @param view play button
     */
    public void onPlayButtonClicked(View view) {
        updatePlayState(view, mCurrentPlaybackState == null, true);
    }

    /**
     * Gets the next track to display
     *
     * @param view next button
     */
    public void onNextButtonClicked(View view) {
        checkTrackQueue();
    }

    private void updatePlayState(View view, boolean getNextSong, boolean playSong) {
        Button button = (Button) view;
        String text = getString(R.string.button_play);
        // If it's not null, already have a song so just control the play/pause
        if (mCurrentPlaybackState != null && !getNextSong) {
            if (mCurrentPlaybackState.isPlaying) {
                text = getString(R.string.button_play);
                mPlayer.pause(null);
            } else {
                text = getString(R.string.button_pause);
                mPlayer.resume(null);
            }
        } else {
            // have to get a new song and start playing
            currentSong = singleton.getTracks().peek();
            if (playSong) {
                mPlayer.playUri(null, currentSong.uri, 0, 0);
                currentSong = singleton.getTracks().poll();
                text = getString(R.string.button_pause);
            }
        }
        button.setText(text);
        updateUI();
    }

    private void updateUI() {
        boolean isInitialized = isInitialized();

        // Enable any widgets as necessary
        for (int id : REQUIRES_INITIALIZED_STATE) {
            findViewById(id).setEnabled(isInitialized);
        }

        TextView songText = findViewById(R.id.text_song);
        TextView artistText = findViewById(R.id.text_artist);

        if (currentSong != null) {
            songText.setText(currentSong.name);
            artistText.setText(currentSong.artists.get(0).name);
        }
    }

    private boolean isInitialized() {
        return mPlayer != null;
    }

    private void updateRandomTracks(boolean startPlaying) {
        Random rand = new Random();

        // Set random offset for the API search
        Map<String, Object> options = new HashMap<>();
        int offset = rand.nextInt(SEARCH_OFFSET);
        options.put("offset", offset);
        options.put("limit", SEARCH_RESULT_LIMIT);

        singleton.spotify().searchTracks(searchTermRepository.getSearchTerm(), options, new Callback<TracksPager>() {

            @Override
            public void success(TracksPager tracksPager, Response response) {
                List<Track> tracks = tracksPager.tracks.items;
                singleton.setTracks(tracks);
                updatePlayState(findViewById(R.id.play_button), true, startPlaying);
            }

            @Override
            public void failure(RetrofitError retrofitError) {

            }
        });
    }

    /**
     * OnClick handler for the Add Button
     * @param view
     */
    public void onAddButtonClick(View view) {
        // Get the Current user's info
        String userID = singleton.getCurrentUserID();
        if (userID == null) {
            // have to fetch it from the server and continue the process from there
            getUser();
            return;
        }
        // If playlist ID is null, have to create a new playlist and continue from there
        String playlistID = singleton.getPlaylistID();
        if (playlistID == null) {
            createPlaylist();
            return;
        }
        // If we have everything, simply add the track
        addTrackToPlaylist();
    }

    private void getUser() {
        singleton.spotify().getMe(new Callback<UserPrivate>() {
            @Override
            public void success(UserPrivate userPrivate, Response response) {
                singleton.setCurrentUserID(userPrivate.id);
                if (singleton.getPlaylistID() == null) {
                    createPlaylist();
                } else {
                    addTrackToPlaylist();
                }
            }

            @Override
            public void failure(RetrofitError retrofitError) {

            }
        });
    }

    private void createPlaylist() {
        Map<String, Object> options = new HashMap<>();
        options.put("name", "SpotPunk Playlist");
        options.put("public", true);
        singleton.spotify().createPlaylist(singleton.getCurrentUserID(), options, new Callback<Playlist>() {
            @Override
            public void success(Playlist playlist, Response response) {
                singleton.setPlaylistID(playlist.id);
                addTrackToPlaylist();
            }

            @Override
            public void failure(RetrofitError retrofitError) {

            }
        });
    }

    private void addTrackToPlaylist() {
        String userID = singleton.getCurrentUserID();
        String playlistID = singleton.getPlaylistID();
        Map<String, Object> playlistOptions = new HashMap<>();
        String[] songs = {currentSong.uri};
        playlistOptions.put("uris", songs);
        singleton.spotify().addTracksToPlaylist(userID, playlistID, null, playlistOptions, new Callback<Pager<PlaylistTrack>>() {

            @Override
            public void success(Pager<PlaylistTrack> playlistTrackPager, Response response) {
                Toast.makeText(MainActivity.this, "Song added!", Toast.LENGTH_SHORT).show();
                checkTrackQueue();
            }

            @Override
            public void failure(RetrofitError retrofitError) {

            }
        });
    }

    private void checkTrackQueue() {
        if (singleton.getTracks().size() <= REFRESH_RATE) {
            // running out of songs, fetch some new ones
            updateRandomTracks(true);
            return;
        }
        updatePlayState(findViewById(R.id.play_button), true, true);
    }
}
