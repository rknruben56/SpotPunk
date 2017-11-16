package edu.depaul.csc472.spotpunk;

import android.content.Context;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mindorks.placeholderview.SwipePlaceHolderView;
import com.mindorks.placeholderview.annotations.Layout;
import com.mindorks.placeholderview.annotations.NonReusable;
import com.mindorks.placeholderview.annotations.Resolve;
import com.mindorks.placeholderview.annotations.View;
import com.mindorks.placeholderview.annotations.swipe.SwipeIn;
import com.mindorks.placeholderview.annotations.swipe.SwipeOut;

import java.util.StringJoiner;

import edu.depaul.csc472.spotpunk.helpers.ITrackHelper;
import edu.depaul.csc472.spotpunk.listeners.IPlaybackListener;
import edu.depaul.csc472.spotpunk.listeners.IPlaylistListener;
import edu.depaul.csc472.spotpunk.listeners.IUpdateTrackListener;
import kaaes.spotify.webapi.android.models.ArtistSimple;
import kaaes.spotify.webapi.android.models.Image;
import kaaes.spotify.webapi.android.models.Track;

/**
 * Track View Model for the card.
 * NOTE: This code references examples from the Tinder Swipe
 * example: https://blog.mindorks.com/android-tinder-swipe-view-example-3eca9b0d4794
 * Created by rrodr on 11/14/2017.
 */
@Layout(R.layout.track_card_view)
@NonReusable
class TrackCard {

    @View(R.id.albumImageView)
    private ImageView albumImageView;

    @View(R.id.titleText)
    private TextView titleText;

    @View(R.id.artistText)
    private TextView artistText;

    @View(R.id.playBtn)
    private Button playButton;

    private Track track;
    private Context context;
    private SwipePlaceHolderView swipeView;

    private IPlaybackListener playbackListener;
    private IUpdateTrackListener updateTrackListener;
    private IPlaylistListener playlistListener;

    private ITrackHelper trackHelper;

    private AppSingleton singleton;

    TrackCard(IPlaybackListener playbackListener,
              IUpdateTrackListener updateTrackListener,
              IPlaylistListener playlistListener,
              ITrackHelper trackHelper,
              Context context,
              Track track,
              SwipePlaceHolderView swipeView) {
        this.playbackListener = playbackListener;
        this.updateTrackListener = updateTrackListener;
        this.playlistListener = playlistListener;
        this.trackHelper = trackHelper;
        this.context = context;
        this.track = track;
        this.swipeView = swipeView;

        singleton = AppSingleton.getInstance();
    }

    @Resolve
    private void onResolved(){
        // Get the album image
        Image image = track.album.images.get(0);

        // load UI contents to the card
        Glide.with(context).load(image.url).into(albumImageView);
        titleText.setText(track.name);
        artistText.setText(trackHelper.getArtists(track));
        playButton.setText(context.getString(R.string.button_pause));

        // Set the onClickListener for the play/pause button
        playButton.setOnClickListener(v -> {
            Log.d("TrackCard", "Play button clicked");
            Button button = (Button)v;
            boolean getNewSong = playbackListener.getPlaybackState() == null;

            // Set the play/pause text
            button.setText(getPlayButtonText());

            // Update the playback accordingly
            playbackListener.updatePlaybackState(getNewSong, true);
        });
    }

    @SwipeOut
    private void onSwipedOut(){
        singleton.updateRejectList();
        updateTrackListener.updateRandomTracks(true);
        swipeView.addView(this);
    }

    @SwipeIn
    private void onSwipeIn() {
        playlistListener.addTrackToPlaylist();
    }

    private String getPlayButtonText() {
        Log.d("TrackCard", "getting play text");
        if (playbackListener.getPlaybackState() == null) {
            return context.getString(R.string.button_play);
        }
        else {
            return playbackListener.getPlaybackState().isPlaying ?
                    context.getString(R.string.button_play)
                    : context.getString(R.string.button_pause);
        }
    }
}
