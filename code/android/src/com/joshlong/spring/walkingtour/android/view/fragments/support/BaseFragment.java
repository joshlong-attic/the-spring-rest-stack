package com.joshlong.spring.walkingtour.android.view.fragments.support;

import android.app.Fragment;
import android.os.Bundle;
import com.joshlong.spring.walkingtour.android.utils.DaggerInjectionUtils;

/**
 * See <a href="http://developer.android.com/guide/components/fragments.html">the Google Android tutorial on Fragments</a> for more.
 *
 * @author Josh Long
 */
public class BaseFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DaggerInjectionUtils.inject(this);
    }
}
