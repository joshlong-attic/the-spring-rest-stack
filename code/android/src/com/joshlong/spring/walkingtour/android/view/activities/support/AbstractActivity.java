package com.joshlong.spring.walkingtour.android.view.activities.support;


import android.app.Activity;
import android.os.Bundle;
import com.joshlong.spring.walkingtour.android.utils.DaggerInjectionUtils;
import com.squareup.otto.Bus;

import javax.inject.Inject;

/**
 * @author Josh Long
 */
public class AbstractActivity extends Activity   {

    @Inject Bus bus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DaggerInjectionUtils.inject(this);
    }

    @Override protected void onResume() {
        super.onResume();
        bus.register(this);
    }

    @Override protected void onPause() {
        super.onPause();
        bus.unregister(this);
    }

}
