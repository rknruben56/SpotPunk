package edu.depaul.csc472.spotpunk.listeners;

import com.spotify.sdk.android.player.PlaybackState;

import edu.depaul.csc472.spotpunk.AppSingleton;
import kaaes.spotify.webapi.android.models.Track;

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
    public void updatePlaybackState(boolean getNextSong, boolean playSong) {
        // If it's not null, already have a song so just control the play/pause
        if (playbackState != null && !getNextSong) {
            if (playbackState.isPlaying) {
                singleton.getmPlayer().pause(null);
            } else {
                singleton.getmPlayer().resume(null);
            }
        } else {
            // have to get a new song and start playing
            if (playSong) {
                Track currentSong = singleton.getTracks().poll();
                singleton.getmPlayer().playUri(null, currentSong.uri, 0, 0);
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
