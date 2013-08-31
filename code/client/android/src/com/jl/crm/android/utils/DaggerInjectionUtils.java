package com.jl.crm.android.utils;

import android.app.*;
import android.content.*;
import android.support.v4.app.Fragment;
import com.jl.crm.android.Crm;


public class DaggerInjectionUtils {

    public static void inject(Fragment fragment) {
        Crm crm = forApplication(fragment.getActivity().getApplication());
        crm.inject(fragment);
    }

    public static void inject(Activity baseActivity) {
        Crm crm = forApplication(baseActivity.getApplication());
        crm.inject(baseActivity);
    }

    private static Crm forApplication(Application application) {
        if (application instanceof Crm){
            return (Crm) application;
        }
        return null;
    }

    //todo
    private void inject(ContentProvider contentProvider) {
        Context ctx = contentProvider.getContext();
    }


}
