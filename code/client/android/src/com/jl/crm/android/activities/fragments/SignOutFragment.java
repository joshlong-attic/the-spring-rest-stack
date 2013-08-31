package com.jl.crm.android.activities.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.jl.crm.android.activities.MainActivity;

/**
 * Renders a link to sign all the other fragments out
 */
public class SignOutFragment extends SecuredCrmFragment {
    public SignOutFragment(MainActivity mainActivity, String title) {
        super(mainActivity, title);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return null;
    }

}
