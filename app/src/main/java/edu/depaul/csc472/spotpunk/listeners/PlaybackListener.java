package edu.depaul.csc472.spotpunk.listeners;

import com.spotify.sdk.android.player.PlaybackState;

import edu.depaul.csc472.spotpunk.AppSingleton;

/**
 * Listener that updates the playback accordingly
 * Created by rrodr on 11/14/2017.
 */

public class PlaybackListener implements IPlaybackListener {

    private AppSingleton singleton;
    private PlaybackState playbackState;

    public PlaybackListener(PlaybackState playbackState) {
        singleton = AppSingleton.getInstance();
        this.playbackState = playbackState;
    }

    @Override
    public void updatePlaybackState(boolean getNextSong, boolean isButtonClick) {
        if (getNextSong) {
            // have to get a new song and start playing
            singleton.setCurrentTrack(singleton.getTracks().peek());
            singleton.getmPlayer().playUri(null, singleton.getCurrentTrack().uri, 0, 0);
        } else {
            // resume or pause the current song
            if (playbackState != null && playbackState.isPlaying && isButtonClick) {
                singleton.getmPlayer().pause(null);
            } else {
                singleton.getmPlayer().resume(null);
            }
        }
    }

    public PlaybackState getPlaybackState() {
        return playbackState;
    }

    public void setPlaybackState(PlaybackState playbackState) {
        this.playbackState = playbackState;
    }
}
