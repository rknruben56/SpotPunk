package edu.depaul.csc472.spotpunk.listeners;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import edu.depaul.csc472.spotpunk.AppSingleton;
import edu.depaul.csc472.spotpunk.SearchTermRepository;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.TracksPager;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Track listener that updates the track listing
 * Created by rrodr on 11/14/2017.
 */

public class UpdateTrackListener implements IUpdateTrackListener {

    private static final int SEARCH_RESULT_LIMIT = 2;
    private static final int SEARCH_OFFSET = 4000;
    private static final int REFRESH_RATE = 1;

    private AppSingleton singleton;
    private SearchTermRepository searchTermRepository;
    private IPlaybackListener playbackListener;
    private IUIListener uiListener;

    public UpdateTrackListener(IPlaybackListener playbackListener,
                               SearchTermRepository searchTermRepository,
                               IUIListener uiListener) {
        singleton = AppSingleton.getInstance();
        this.searchTermRepository = searchTermRepository;
        this.playbackListener = playbackListener;
        this.uiListener = uiListener;
    }

    @Override
    public void updateRandomTracks(boolean startPlaying) {
        if (singleton.getTracks().size() <= REFRESH_RATE) {
            // running out of songs, fetch some new ones
            getNewTracks(startPlaying);
            return;
        }
        playbackListener.updatePlaybackState(true, true);
    }

    private void getNewTracks(boolean startPlaying) {
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
                uiListener.updateUI(true);
                playbackListener.updatePlaybackState(true, startPlaying);
            }

            @Override
            public void failure(RetrofitError retrofitError) {

            }
        });
    }
}
