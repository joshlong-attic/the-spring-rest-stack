package com.jl.crm.android.activities.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.actionbarsherlock.app.SherlockFragment;
import com.jl.crm.android.R;

/**
 * Simple fragment to display a pretty picture while the background processing is looking up
 * any current CRM connectivity.
 *
 * @author Josh Log
 */
public class WelcomeFragment extends SherlockFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ImageView imageView = new ImageView(getActivity());
        imageView.setImageResource(R.drawable.springsource_welcome_transparent_bg);
        imageView.setBackgroundColor(Color.parseColor("#69d54c"));//R.color.spring_welcome_bg_color);
        return imageView;
    }
}
