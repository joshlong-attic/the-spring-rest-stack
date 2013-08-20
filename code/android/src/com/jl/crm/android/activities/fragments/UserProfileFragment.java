package com.jl.crm.android.activities.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.jl.crm.android.activities.MainActivity;
import com.jl.crm.android.utils.DaggerInjectionUtils;

/**
 * @author Josh Long
 */
public class UserProfileFragment extends SecuredCrmFragment {

    public UserProfileFragment(MainActivity mainActivity, String title) {
        super(mainActivity, title);
    }
    @Override
    protected void contributeToMenuWhenAuthenticated(Menu menu) {

        menu.add(getTitle() ).setTitle(getTitle() ).setOnMenuItemClickListener(
                new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        getMainActivity().show(UserProfileFragment.this);
                        return true;
                    }
                }
        );
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return new LinearLayout(getActivity());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DaggerInjectionUtils.inject(this);


    }

}
