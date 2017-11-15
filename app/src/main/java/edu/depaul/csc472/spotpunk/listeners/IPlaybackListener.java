package edu.depaul.csc472.spotpunk.listeners;

import com.spotify.sdk.android.player.PlaybackState;

/**
 * Contract to handle music playback
 * Created by rrodr on 11/14/2017.
 */

public interface IPlaybackListener {
    void updatePlaybackState(boolean getNextSong, boolean playSong);
    PlaybackState getPlaybackState();
    void setPlaybackState(PlaybackState playbackState);
}
