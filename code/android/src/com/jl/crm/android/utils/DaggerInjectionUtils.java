package com.jl.crm.android.utils;

import android.app.*;
import com.jl.crm.android.Crm;

/**
 * Convenience methods to simplify using Dagger for dependency injection in Android
 * {@link Fragment fragments} and {@link Activity acitivities}.
 *
 * @author josh long
 */
public class DaggerInjectionUtils {

	public static void inject(Fragment fragment) {
		doInjectionForTarget(fragment.getActivity(), fragment);
	}

	public static void inject(Activity ac) {
		doInjectionForTarget(ac, ac);
	}

	private static void doInjectionForTarget(Activity activity, Object target) {
		Application application = activity.getApplication();
		if (application instanceof Crm){
			Crm crm = (Crm) application;
			crm.getObjectGraph().inject(target);
		}
	}
}
