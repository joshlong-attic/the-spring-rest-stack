package com.jl.crm.android.activities;

import android.app.*;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import com.jl.crm.android.*;
import com.jl.crm.android.fragments.*;
import com.jl.crm.android.utils.DaggerInjectionUtils;
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

	String tag = CrmActivity.class.getName();

	CrmConnectionState crmConnectionState;

	AuthenticationFragment authenticationFragment;

	Runnable connectionEstablishedRunnable = new Runnable() {
		@Override
		public void run() {
			getActionBar().show();
			Log.d((tag), "connection established!");

			FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
			fragmentTransaction.replace(rootViewId, customerSearchFragment);
			fragmentTransaction.commit();
		}
	};

	Runnable connectionNotEstablishedRunnable = new Runnable() {
		@Override
		public void run() {
			Log.d(tag, "a connection either exists and is invalid or it doesn't exist." +
			           " To reset everything, we're removing existing (stale) connection information");
			crmConnectionState.resetLocalConnections();
			Log.d(tag, "loading the authentication fragment.");
			getFragmentManager().beginTransaction().add(rootViewId, authenticationFragment).commit(); // no need to signal noAccessToken because, if its not visible, it's not required
		}
	};

	int rootViewId = R.id.crm_activity_linear_layout;

	CustomerSearchFragment customerSearchFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		DaggerInjectionUtils.inject(this);

		Window window = getWindow();
		window.requestFeature(Window.FEATURE_PROGRESS);
		window.setFeatureInt(Window.FEATURE_PROGRESS, Window.PROGRESS_VISIBILITY_ON);

		setContentView(R.layout.crm_activity);

		this.crmConnectionState = new CrmConnectionState(this,
				                                                  connectionFactory,
				                                                  repositoryHelper,
				                                                  sqLiteConnectionRepository,
				                                                  this.connectionEstablishedRunnable,
				                                                  this.connectionNotEstablishedRunnable,
				                                                  getString(R.string.oauth_access_token_callback_uri));
		this.authenticationFragment = new AuthenticationFragment(this.crmConnectionState, this.connectionEstablishedRunnable);
		this.authenticationFragment.setArguments(getIntent().getExtras());

		this.customerSearchFragment = new CustomerSearchFragment();
		this.customerSearchFragment.setArguments(getIntent().getExtras());

	}

	@Override
	protected void onStart() {
		super.onStart();
		this.crmConnectionState.start();
	}
}