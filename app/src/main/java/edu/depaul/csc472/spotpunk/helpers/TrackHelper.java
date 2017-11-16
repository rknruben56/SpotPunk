package edu.depaul.csc472.spotpunk.helpers;

import java.util.StringJoiner;

import kaaes.spotify.webapi.android.models.ArtistSimple;
import kaaes.spotify.webapi.android.models.Track;

/**
 * Helper class for Tracks
 * Created by rrodr on 11/15/2017.
 */

public class TrackHelper implements ITrackHelper{

    public String getArtists(Track track) {
        // Concat the list of artist strings
        StringJoiner joiner = new StringJoiner(",");
        for(ArtistSimple artist : track.artists) {
            joiner.add(artist.name);
        }
        return joiner.toString();
    }
}
