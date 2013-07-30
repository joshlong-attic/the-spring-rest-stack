package com.jl.crm.android.utils;

import android.app.*;
import com.jl.crm.android.Crm;
import com.jl.crm.android.activities.BaseActivity;

/**
 * Convenience methods to simplify using Dagger for dependency injection in Android {@link Fragment fragments} and
 * {@link Activity acitivities}.
 *
 * @author josh long
 */
public class DaggerInjectionUtils {

	public static void inject(BaseActivity ac) {
		doInjectionForTarget(ac, ac);
	}

	private static void doInjectionForTarget(BaseActivity baseActivity, Object target) {
		Application application = baseActivity.getApplication();
		if (application instanceof Crm){
			Crm crm = (Crm) application;
			crm.inject(target);
		}
	}


}
