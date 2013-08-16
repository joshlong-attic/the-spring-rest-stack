package com.jl.crm.android.activities;

import android.app.Activity;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.TextView;
import com.jl.crm.android.R;
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
	public boolean onOptionsItemSelected(MenuItem item) {
		return Commons.onOptionsItemSelected(this, item);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return Commons.onCreateOptionsMenu(this, menu);
	}

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
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Commons.onCreate(this, savedInstanceState);

		setContentView(R.layout.user_detail_activity);
	}
}
