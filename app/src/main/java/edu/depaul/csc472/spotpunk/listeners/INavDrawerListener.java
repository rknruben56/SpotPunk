package edu.depaul.csc472.spotpunk.listeners;

import edu.depaul.csc472.spotpunk.AppSingleton;

/**
 * Contract for a Navigation Drawer Listener
 * Created by rrodr on 11/15/2017.
 */

public interface INavDrawerListener {
    void navigate(AppSingleton.APP_SCREEN screen);
}
