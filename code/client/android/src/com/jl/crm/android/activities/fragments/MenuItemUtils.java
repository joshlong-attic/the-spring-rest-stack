package com.jl.crm.android.activities.fragments;


import android.app.Activity;
import android.util.Log;
import android.view.View;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public abstract class MenuItemUtils {

    public static MenuItem menuItem(Menu menu, Activity activity, String title, View actionView, MenuItem.OnMenuItemClickListener click, int showAsAction) {
        MenuItem menuItem = menuItem(menu, activity, title, actionView, click);
        menuItem.setShowAsAction(showAsAction);
        return menuItem;
    }

    public static MenuItem menuItem(Menu menu, Activity activity, String title, View actionView, MenuItem.OnMenuItemClickListener onMenuItemClickListener) {
        MenuItem menuItem = menu.add(title);
        menuItem.setTitle(title);

        if (actionView != null)
            menuItem.setActionView(actionView);

        if (onMenuItemClickListener != null)
            menuItem.setOnMenuItemClickListener(onMenuItemClickListener);


        Log.d(MenuItemUtils.class.getName(), "Add a menu item for " + title + " to activity (" + activity.getClass().getName() + ")");
        return menuItem;
    }
}
