package edu.depaul.csc472.spotpunk;

import com.spotify.sdk.android.player.Player;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Track;

/**
 * App Singleton
 * Created by rrodr on 11/10/2017.
 */
public class AppSingleton {

    // ClientID used to talk to Spotify API
    private static final String CLIENT_ID = "87539cec4af546cfa0a9beccbbb92eae";

    // Spotify service that makes API calls
    private SpotifyService service;

    // Current user's ID
    private String currentUserID;

    // Playlist used to add songs
    private String playlistID;

    // singleton instance
    private static final AppSingleton ourInstance = new AppSingleton();

    // TracksPager containing tracks
    private Queue<Track> tracks;

    /**
     * Music player from Spotify SDK
     */
    private Player mPlayer;

    /**
     * Returns the singleton instance
     * @return
     */
    public static AppSingleton getInstance() {
        return ourInstance;
    }

    private AppSingleton() {
        tracks = new LinkedList<>();
    }

    /**
     * Initializes the Spotify Service
     */
    void initializeService(String accessToken) {
        // initialize service
        SpotifyApi api = new SpotifyApi();
        api.setAccessToken(accessToken);
        service = api.getService();
    }

    /**
     * Gets the Spotify music Player
     * @return
     */
    public Player getmPlayer() {
        return mPlayer;
    }

    /**
     * Sets the Spotify Music Player
     * @param mPlayer
     */
    void setmPlayer(Player mPlayer) {
        this.mPlayer = mPlayer;
    }

    /**
     * Gets the Current User's ID
     * @return currentUserID
     */
    public String getCurrentUserID() {
        return currentUserID;
    }

    /**
     * Updates the current user's ID stored
     * @param currentUserID
     */
    public void setCurrentUserID(String currentUserID) {
        this.currentUserID = currentUserID;
    }

    /**
     * Returns the playList ID
     * @return playListID
     */
    public String getPlaylistID() {
        return playlistID;
    }

    /**
     * Sets the playList ID
     * @param playlistID
     */
    public void setPlaylistID(String playlistID) {
        this.playlistID = playlistID;
    }

    /**
     * Returns the CLIENT ID used to communicate with Spotify
     * @return client ID string
     */
    String getClientId() {
        return CLIENT_ID;
    }

    /**
     * Returns the Spotify Service
     * @return spotify service
     */
    public SpotifyService spotify() {
        return service;
    }

    /**
     * Get the Tracks object
     * @return TracksPager
     */
    public Queue<Track> getTracks() {
        return tracks;
    }

    /**
     * Sets the Tracks object
     * @param tracks
     */
    public void setTracks(List<Track> tracks) {
        this.tracks.addAll(tracks);
    }
}
