package com.jl.crm.android;


import android.app.Application;
import dagger.ObjectGraph;

import java.util.*;

/**
 * the current Android Application instance.
 *
 * @author Josh Long
 */
public class Crm extends Application {
	private ObjectGraph objectGraph;

	@Override
	public void onCreate() {
		super.onCreate();
		Object[] modules = getModules().toArray();
		objectGraph = ObjectGraph.create(modules);
	}

	protected List<Object> getModules() {
		return Arrays.<Object>asList(new CrmModule(this));
	}

	public void inject(Object crmComponent) {
		this.objectGraph.inject(crmComponent);
	}

}
