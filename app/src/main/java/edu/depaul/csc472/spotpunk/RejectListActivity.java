package edu.depaul.csc472.spotpunk;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import edu.depaul.csc472.spotpunk.adapters.TrackAdapter;
import edu.depaul.csc472.spotpunk.helpers.ITrackHelper;
import edu.depaul.csc472.spotpunk.helpers.NavDrawerHelper;
import edu.depaul.csc472.spotpunk.helpers.TrackHelper;
import kaaes.spotify.webapi.android.models.Track;

public class RejectListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reject_list);

        // Get singleton instance
        AppSingleton singleton = AppSingleton.getInstance();

        // Initialize the navigation drawer
        initializeNavDrawer();

        // Get the listView
        ListView listView = findViewById(R.id.rejectListView);

        // Define an adapter
        ITrackHelper trackHelper = new TrackHelper();
        TrackAdapter trackAdapter = new TrackAdapter(this, singleton.getRejectList(), trackHelper);

        // Assign adapter to the ListView
        listView.setAdapter(trackAdapter);
    }

    private void initializeNavDrawer() {
        // Update text of the toolbar to match the page
        TextView toolbarText = findViewById(R.id.toolbarText);
        toolbarText.setText(getString(R.string.screen_rejectList));

        // Initialize drawer contents
        NavDrawerHelper drawerHelper = new NavDrawerHelper(this,
                findViewById(R.id.drawerView),
                findViewById(R.id.drawerLayoutRejectList),
                findViewById(R.id.toolbarRejectList),
                AppSingleton.APP_SCREEN.RejectList
        );
        drawerHelper.setupNavDrawer();
    }
}
