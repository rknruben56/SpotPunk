package edu.depaul.csc472.spotpunk.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import edu.depaul.csc472.spotpunk.R;
import edu.depaul.csc472.spotpunk.helpers.ITrackHelper;
import kaaes.spotify.webapi.android.models.Track;

/**
 * Adapter for rendering Tracks
 * Created by rrodr on 11/15/2017.
 */

public class TrackAdapter extends ArrayAdapter<Track> {

    private ITrackHelper trackHelper;

    public TrackAdapter(Context context, ArrayList<Track> tracks, ITrackHelper trackHelper){
        super(context, 0, tracks);
        this.trackHelper = trackHelper;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the track
        Track track = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.reject_list_item, null);
        }
        // Get UI elements to update
        TextView trackName = convertView.findViewById(R.id.trackName);
        TextView trackArtist = convertView.findViewById(R.id.trackArtist);

        // Set the text accordingly
        trackName.setText(track.name);
        trackArtist.setText(trackHelper.getArtists(track));
        
        return convertView;
    }
}
