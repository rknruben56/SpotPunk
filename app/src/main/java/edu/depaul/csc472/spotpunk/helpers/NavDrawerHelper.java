package edu.depaul.csc472.spotpunk.helpers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.mindorks.placeholderview.PlaceHolderView;

import edu.depaul.csc472.spotpunk.AppSingleton;
import edu.depaul.csc472.spotpunk.DrawerMenuItem;
import edu.depaul.csc472.spotpunk.MainActivity;
import edu.depaul.csc472.spotpunk.PlaylistActivity;
import edu.depaul.csc472.spotpunk.R;
import edu.depaul.csc472.spotpunk.RejectListActivity;
import edu.depaul.csc472.spotpunk.listeners.INavDrawerListener;

/**
 * Helper class that instantiates the Navigation drawer content and handles
 * navigating throughout the app
 * NOTE: This code follows the structure from this example:
 * https://medium.com/@janishar.ali/navigation-drawer-android-example-8dfe38c66f59
 * Created by rrodr on 11/15/2017.
 */

public class NavDrawerHelper implements INavDrawerListener {

    private PlaceHolderView drawerView;
    private DrawerLayout drawer;
    private Toolbar toolbar;
    private Activity activity;
    private AppSingleton.APP_SCREEN source;

    public NavDrawerHelper(Activity activity,
                           PlaceHolderView drawerView,
                           DrawerLayout drawer,
                           Toolbar toolbar,
                           AppSingleton.APP_SCREEN source) {
        this.activity = activity;
        this.drawer = drawer;
        this.drawerView = drawerView;
        this.toolbar = toolbar;
        this.source = source;
    }

    public void setupNavDrawer() {
        // Add the navigation items to the drawer
        Context context = activity.getApplicationContext();
        drawerView
                .addView(new DrawerMenuItem(context, AppSingleton.APP_SCREEN.Main, this))
                .addView(new DrawerMenuItem(context, AppSingleton.APP_SCREEN.Playlist, this))
                .addView(new DrawerMenuItem(context, AppSingleton.APP_SCREEN.RejectList, this));

        // Set the drawer toggle thingy
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(activity, drawer, toolbar, R.string.open_drawer, R.string.closed_drawer){
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };

        // Attach the drawer toggle to the drawer
        drawer.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
    }

    @Override
    public void navigate(AppSingleton.APP_SCREEN screen) {
        // Get the new activity we're going to
        Class newActivityClass = MainActivity.class;
        switch (screen) {
            case Main:
                newActivityClass = MainActivity.class;
                break;
            case RejectList:
                newActivityClass = RejectListActivity.class;
                break;
            case Playlist:
                newActivityClass = PlaylistActivity.class;
                break;
        }
        Intent intent = new Intent(activity.getApplicationContext(), newActivityClass);
        intent.putExtra("Source", source);
        activity.startActivity(intent);
    }
}
