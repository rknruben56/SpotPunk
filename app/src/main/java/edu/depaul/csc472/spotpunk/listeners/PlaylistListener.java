package edu.depaul.csc472.spotpunk.listeners;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

import edu.depaul.csc472.spotpunk.AppSingleton;
import kaaes.spotify.webapi.android.models.Pager;
import kaaes.spotify.webapi.android.models.Playlist;
import kaaes.spotify.webapi.android.models.PlaylistTrack;
import kaaes.spotify.webapi.android.models.UserPrivate;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Listener that updates the Spotify playlist
 * Created by rrodr on 11/15/2017.
 */

public class PlaylistListener implements IPlaylistListener {

    private AppSingleton singleton;
    private IUpdateTrackListener updateTrackListener;
    private Context context;

    public PlaylistListener(IUpdateTrackListener updateTrackListener, Context context) {
        singleton = AppSingleton.getInstance();
        this.updateTrackListener = updateTrackListener;
        this.context = context;
    }

    @Override
    public void addTrackToPlaylist() {
        // Get the Current user's info
        String userID = singleton.getCurrentUserID();
        // If userID is null, have to fetch it from the server and continue the process from there
        if (userID == null) {
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
        addTrack();
    }

    private void getUser() {
        singleton.spotify().getMe(new Callback<UserPrivate>() {
            @Override
            public void success(UserPrivate userPrivate, Response response) {
                singleton.setCurrentUserID(userPrivate.id);
                // If there's no playlist, have to create one
                if (singleton.getPlaylistID() == null) {
                    createPlaylist();
                } else {
                    addTrackToPlaylist();
                }
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                Log.d("PlaylistListener", "Error getting user from Spotify");
            }
        });
    }

    private void createPlaylist() {
        // set the POST options
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
                Log.d("PlaylistListener", "Error creating a playlist");
            }
        });
    }

    private void addTrack() {
        // Set User ID parameter
        String userID = singleton.getCurrentUserID();

        // Set playlist ID parameter
        String playlistID = singleton.getPlaylistID();

        // Set the other POST options
        Map<String, Object> playlistOptions = new HashMap<>();
        String[] songs = {singleton.getTrackToAdd().uri};
        playlistOptions.put("uris", songs);

        singleton.spotify().addTracksToPlaylist(userID, playlistID, null, playlistOptions, new Callback<Pager<PlaylistTrack>>() {

            @Override
            public void success(Pager<PlaylistTrack> playlistTrackPager, Response response) {
                Toast.makeText(context, "Song added!", Toast.LENGTH_SHORT).show();
                updateTrackListener.updateRandomTracks();
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                Log.d("PlaylistListener", "Error adding a new track");
            }
        });
    }
}
