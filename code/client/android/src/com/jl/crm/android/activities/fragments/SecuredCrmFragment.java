package com.jl.crm.android.activities.fragments;

import android.os.Bundle;
import android.util.Log;
import com.actionbarsherlock.app.SherlockFragment;
import com.jl.crm.android.activities.MainActivity;
import com.jl.crm.android.activities.MenuContributingFragment;
import com.jl.crm.android.activities.NamedFragment;
import com.jl.crm.android.activities.secure.AuthenticatedFragment;
import com.jl.crm.client.User;

public class SecuredCrmFragment extends SherlockFragment implements NamedFragment, AuthenticatedFragment, MenuContributingFragment {
    private User currentUser;
    private String title;
    private MainActivity mainActivity;

    public SecuredCrmFragment(MainActivity mainActivity, String title) {
        super();
        this.mainActivity = mainActivity;
        this.title = title;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        setMenuVisibility(true);
    }

    public MainActivity getMainActivity() {
        return this.mainActivity;
    }

    @Override
    public String getTitle() {
        Log.d(getClass().getName() + "", "title is '" + this.title +
                "' for class '" + getClass().getName() + "'");
        return this.title;
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