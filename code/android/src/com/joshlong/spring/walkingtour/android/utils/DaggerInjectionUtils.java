package com.joshlong.spring.walkingtour.android.utils;

import android.app.*;
import com.joshlong.spring.walkingtour.android.Crm;

/**
 * @author josh long
 */
public class DaggerInjectionUtils {

    static private void doInjectionForTarget(Activity activity, Object target) {
        Application application = activity.getApplication();
        if (application instanceof Crm) {
            Crm crm = (Crm) application;
            crm.getObjectGraph().inject(target);
        }
    }

    static public void inject(Fragment fragment) {
        doInjectionForTarget(fragment.getActivity(), fragment);
    }

    static public void inject(Activity ac) {
        doInjectionForTarget(ac, ac);
    }
}
