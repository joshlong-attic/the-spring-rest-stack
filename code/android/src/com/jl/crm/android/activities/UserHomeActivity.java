package com.jl.crm.android.activities;

import android.app.Activity;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.TextView;
import com.jl.crm.android.R;
import com.jl.crm.android.utils.DaggerInjectionUtils;
import com.jl.crm.client.*;

import javax.inject.Inject;

/**
 * A simple {@code Activity} to demonstrate that we can work with a refreshed {@link com.jl.crm.client.CrmOperations}
 * instance with impunity anywhere in the application once they've gone through the initial {@link
 * AuthenticationActivity OAuth flow}.
 *
 * @author Josh Long
 */
public class UserHomeActivity extends Activity {

	@Inject LocationManager locationManager;
	@Inject CrmOperations crmOperations;
	@Inject LayoutInflater layoutInflater;
	User user;

	@Override
	protected void onStart() {
		super.onStart();
		user = crmOperations.currentUser();

		Log.d(UserHomeActivity.class.getName(), "currently connected user: " + user.toString());

		TextView fn = (TextView) findViewById(R.id.firstName);
		fn.setText(user.getFirstName());

		TextView ln = (TextView) findViewById(R.id.lastName);
		ln.setText(user.getLastName());
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		DaggerInjectionUtils.inject(this);

		setContentView(R.layout.user_detail_activity);


	}
}
