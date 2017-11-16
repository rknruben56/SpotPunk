package edu.depaul.csc472.spotpunk;

import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import edu.depaul.csc472.spotpunk.adapters.TrackAdapter;
import edu.depaul.csc472.spotpunk.helpers.ITrackHelper;
import edu.depaul.csc472.spotpunk.helpers.TrackHelper;
import kaaes.spotify.webapi.android.models.Pager;
import kaaes.spotify.webapi.android.models.Playlist;
import kaaes.spotify.webapi.android.models.PlaylistTrack;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.UserPrivate;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class PlaylistActivity extends DrawerActivity {

    private AppSingleton singleton;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist);

        // Get the singleton instance
        singleton = AppSingleton.getInstance();

        // Update text of the toolbar to match the page and initialize the nav drawer
        TextView toolbarText = findViewById(R.id.toolbarText);
        toolbarText.setText(getString(R.string.screen_playlist));
        initializeNavDrawer(AppSingleton.APP_SCREEN.Playlist);

        // Get the playlist list view
        listView = findViewById(R.id.playlistView);

        // Call the API to get the tracks to render
        getPlaylistTracks();
    }

    private void getPlaylistTracks() {
        String userID = singleton.getCurrentUserID();
        // If the user hasn't been set yet, get the user details and continue from there
        if (userID == null) {
            getUser();
            return;
        }
        // If the playlist hasn't been created yet, create it and continue from there
        String playlistID = singleton.getPlaylistID();
        if (playlistID == null) {
            createPlaylist();
            return;
        }
        // We got all the info we need, get the playlist tracks
        getTracks();
    }

    private void getUser() {
        singleton.spotify().getMe(new Callback<UserPrivate>() {
            @Override
            public void success(UserPrivate userPrivate, Response response) {
                singleton.setCurrentUserID(userPrivate.id);
                if (singleton.getPlaylistID() == null) {
                    createPlaylist();
                } else {
                    getTracks();
                }
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                Log.d("PlaylistActivity", "Error getting user");
            }
        });
    }

    private void createPlaylist() {
        Map<String, Object> options = new HashMap<>();
        options.put("name", getString(R.string.playlist_name));
        options.put("public", true);
        singleton.spotify().createPlaylist(singleton.getCurrentUserID(), options, new Callback<Playlist>() {
            @Override
            public void success(Playlist playlist, Response response) {
                singleton.setPlaylistID(playlist.id);
                getTracks();
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                Log.d("PlaylistActivity", "Error creating playlist");
            }
        });
    }

    private void getTracks() {
        String userID = singleton.getCurrentUserID();
        String playlistID = singleton.getPlaylistID();
        Map<String, Object> playlistOptions = new HashMap<>();

        singleton.spotify().getPlaylistTracks(userID, playlistID, playlistOptions, new Callback<Pager<PlaylistTrack>>() {
            @Override
            public void success(Pager<PlaylistTrack> playlistTrackPager, Response response) {
                ArrayList<Track> tracks = new ArrayList<>();
                for (PlaylistTrack playlistTrack : playlistTrackPager.items) {
                    tracks.add(playlistTrack.track);
                }
                // Define an adapter
                ITrackHelper trackHelper = new TrackHelper();
                TrackAdapter trackAdapter = new TrackAdapter(PlaylistActivity.this, tracks, trackHelper);

                // Assign adapter to the ListView
                listView.setAdapter(trackAdapter);
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                Log.d("PlaylistActivity", "Error getting playlist tracks");
            }
        });
    }
}
