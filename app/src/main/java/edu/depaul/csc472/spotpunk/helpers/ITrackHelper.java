package edu.depaul.csc472.spotpunk.helpers;

import kaaes.spotify.webapi.android.models.Track;

/**
 * Contract for a TrackHelper
 * Created by rrodr on 11/15/2017.
 */

public interface ITrackHelper {
    String getArtists(Track track);
}
