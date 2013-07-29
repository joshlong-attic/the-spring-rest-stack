package com.jl.crm.android.activities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.TextView;
import com.jl.crm.android.R;
import com.jl.crm.client.*;

import javax.inject.Inject;

/**
 * A simple {@code Activity} to demonstrate that we can work with a refreshed {@link com.jl.crm.client.CrmOperations}
 * instance with impunity anywhere in the application once they've gone through the initial {@link CrmWebOAuthActivity
 * OAuth flow}.
 *
 * @author Josh Long
 */
public class UserWelcomeActivity
		  extends BaseActivity {

	User user;

	@Inject CrmOperations crmOperations;

	@Inject LayoutInflater layoutInflater;

	@Override
	protected void onStart() {
		super.onStart();
		user = crmOperations.currentUser();
		TextView fn = (TextView) findViewById(R.id.first_name);
		fn.setText(user.getFirstName());

		TextView ln = (TextView) findViewById(R.id.last_name);
		ln.setText(user.getLastName());


	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.user_detail_activity);


	}
}
