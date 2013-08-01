package com.jl.crm.android.activities;

import android.os.Bundle;
import com.actionbarsherlock.app.SherlockActivity;
import com.jl.crm.android.utils.DaggerInjectionUtils;

/**
 * Handles injection for subclasses.
 *
 * @author Josh Long
 */
public class BaseActivity extends SherlockActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		DaggerInjectionUtils.inject(this);
	}
}
