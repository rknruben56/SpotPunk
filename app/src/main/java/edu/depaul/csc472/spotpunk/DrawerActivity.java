package edu.depaul.csc472.spotpunk;

import android.support.v7.app.AppCompatActivity;

import edu.depaul.csc472.spotpunk.helpers.NavDrawerHelper;

/**
 * Base Drawer Activity that adds the drawer to the implemented activity
 * Created by rrodr on 11/15/2017.
 */

public abstract class DrawerActivity extends AppCompatActivity {

    void initializeNavDrawer(AppSingleton.APP_SCREEN source) {
        NavDrawerHelper drawerHelper = new NavDrawerHelper(this,
                findViewById(R.id.drawerView),
                findViewById(R.id.drawerLayout),
                findViewById(R.id.toolbarLayout),
                source);
        drawerHelper.setupNavDrawer();
    }
}
