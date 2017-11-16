package edu.depaul.csc472.spotpunk;

import android.content.Context;
import android.widget.TextView;

import com.mindorks.placeholderview.annotations.Click;
import com.mindorks.placeholderview.annotations.Layout;
import com.mindorks.placeholderview.annotations.Resolve;
import com.mindorks.placeholderview.annotations.View;

import edu.depaul.csc472.spotpunk.listeners.INavDrawerListener;

/**
 * Navigation Menu Item
 * Created by rrodr on 11/15/2017.
 */
@Layout(R.layout.drawer_item)
public class DrawerMenuItem {

    private AppSingleton.APP_SCREEN screen;
    private Context context;
    private INavDrawerListener navDrawerListener;

    @View(R.id.screenName)
    private TextView screenName;

    public DrawerMenuItem(Context context, AppSingleton.APP_SCREEN screen, INavDrawerListener navDrawerListener) {
        this.context = context;
        this.screen = screen;
        this.navDrawerListener = navDrawerListener;
    }

    @Resolve
    private void onResolved() {
        switch (screen) {
            case Main:
                screenName.setText(context.getString(R.string.screen_main));
                break;
            case Playlist:
                screenName.setText(context.getString(R.string.screen_playlist));
                break;
            case RejectList:
                screenName.setText(context.getString(R.string.screen_rejectList));
        }
    }

    @Click(R.id.mainView)
    private void onMenuItemCLick() {
        switch (screen) {
            case Main:
                navDrawerListener.navigate(AppSingleton.APP_SCREEN.Main);
                break;
            case Playlist:
                navDrawerListener.navigate(AppSingleton.APP_SCREEN.Playlist);
                break;
            case RejectList:
                navDrawerListener.navigate(AppSingleton.APP_SCREEN.RejectList);
        }
    }
}
