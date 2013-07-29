package com.jl.crm.android.activities;

import android.app.Activity;
import android.os.Bundle;
import com.jl.crm.android.utils.DaggerInjectionUtils;

/**
 * Handles injection for subclasses.
 *
 * @author Josh Long
 */
public class BaseActivity
 extends Activity
{

	@Override protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		DaggerInjectionUtils.inject(this);
	}



}
