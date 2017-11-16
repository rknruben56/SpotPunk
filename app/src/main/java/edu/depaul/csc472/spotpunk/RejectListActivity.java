package edu.depaul.csc472.spotpunk;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import edu.depaul.csc472.spotpunk.adapters.TrackAdapter;
import edu.depaul.csc472.spotpunk.helpers.ITrackHelper;
import edu.depaul.csc472.spotpunk.helpers.TrackHelper;

public class RejectListActivity extends DrawerActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reject_list);

        // Get singleton instance
        AppSingleton singleton = AppSingleton.getInstance();

        // Update text of the toolbar to match the page and initialize the nav drawer
        TextView toolbarText = findViewById(R.id.toolbarText);
        toolbarText.setText(getString(R.string.screen_rejectList));
        initializeNavDrawer(AppSingleton.APP_SCREEN.RejectList);

        // Get the listView
        ListView listView = findViewById(R.id.rejectListView);

        // Define an adapter
        ITrackHelper trackHelper = new TrackHelper();
        TrackAdapter trackAdapter = new TrackAdapter(this, singleton.getRejectList(), trackHelper);

        // Assign adapter to the ListView
        listView.setAdapter(trackAdapter);
    }
}
