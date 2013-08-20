package com.jl.crm.android.activities.fragments;

import android.util.Log;
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.jl.crm.android.activities.MainActivity;
import com.jl.crm.android.activities.MenuContributingFragment;
import com.jl.crm.android.activities.secure.AuthenticatedFragment;
import com.jl.crm.android.activities.NamedFragment;
import com.jl.crm.client.User;

public class SecuredCrmFragment extends SherlockFragment implements NamedFragment, AuthenticatedFragment, MenuContributingFragment {
    private User currentUser;
    private String title;
    private MainActivity mainActivity;

    public MainActivity getMainActivity(){
        return this.mainActivity ;
    }
    public SecuredCrmFragment(MainActivity mainActivity, String title) {
        this.mainActivity = mainActivity;
        this.title = title;
    }

    @Override
    public String getTitle() {
        return this.title;
    }

    protected void  contributeToMenuWhenAuthenticated(Menu menu ){
        MenuItem.OnMenuItemClickListener onMenuItemClickListener = new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Log.d(getTag(), "clicked on " + title + ", so showing " + getClass().getName() + ".");
                mainActivity.show(SecuredCrmFragment.this);
                return true;
            }
        };
        menu.add(title).setTitle(title).setOnMenuItemClickListener(onMenuItemClickListener);
    }
    @Override
    public final void contributeToMenu(Menu menu) {
        if(isAuthenticated()){
            contributeToMenuWhenAuthenticated(menu);
        }
    }

    protected User getCurrentUser() {
        return this.currentUser;
    }

    @Override
    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
        Log.d(getTag(), "the current user has been set! " + getClass().getName());
    }

    @Override
    public boolean isAuthenticated() {
        return this.currentUser != null;
    }
}