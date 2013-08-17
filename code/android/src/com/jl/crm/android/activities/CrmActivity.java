package com.jl.crm.android.activities;

import android.app.*;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import com.jl.crm.android.*;
import com.jl.crm.android.fragments.*;
import com.jl.crm.client.CrmConnectionFactory;
import org.springframework.social.connect.sqlite.SQLiteConnectionRepository;
import org.springframework.social.connect.sqlite.support.SQLiteConnectionRepositoryHelper;

import javax.inject.Inject;

/**
 * Lets users search for customer records.
 *
 * @author Josh Long
 */
public class CrmActivity extends Activity {

	@Inject SQLiteConnectionRepository sqLiteConnectionRepository;
	@Inject SQLiteConnectionRepositoryHelper repositoryHelper;
	@Inject CrmConnectionFactory connectionFactory;
	CrmConnectionState crmConnectionState;
	AuthenticationFragment authenticationFragment;
	Runnable connectionEstablishedRunnable = new Runnable() {

		@Override
		public void run() {
			getActionBar().show();
			Log.d(CrmActivity.class.getName(), "connection established!");

			FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
			fragmentTransaction.replace(rootViewId, customerSearchFragment);
			fragmentTransaction.commit();


		}
	};
	Runnable connectionNotEstablishedRunnable = new Runnable() {
		@Override
		public void run() {
			getFragmentManager().beginTransaction().add(rootViewId, authenticationFragment).commit(); // no need to signal noAccessToken because, if its not visible, it's not required
		}
	};
	int rootViewId = R.id.crm_activity_linear_layout;
	CustomerSearchFragment customerSearchFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Commons.onCreate(this, savedInstanceState);

		setContentView(R.layout.crm_activity);

		String uri = getString(R.string.oauth_access_token_callback_uri);
		this.crmConnectionState = new CrmConnectionState(this,
				                                                  connectionFactory,
				                                                  repositoryHelper,
				                                                  sqLiteConnectionRepository,
				                                                  this.connectionEstablishedRunnable,
				                                                  this.connectionNotEstablishedRunnable,
				                                                  uri);
		this.authenticationFragment = new AuthenticationFragment(this.crmConnectionState, this.connectionEstablishedRunnable);
		this.authenticationFragment.setArguments(getIntent().getExtras());

		this.customerSearchFragment = new CustomerSearchFragment();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return Commons.onCreateOptionsMenu(this, menu);
	}

	@Override
	protected void onStart() {
		super.onStart();
		this.crmConnectionState.start();
	}
}