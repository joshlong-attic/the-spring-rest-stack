package com.jl.crm.android.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.*;
import android.view.*;
import com.jl.crm.android.*;
import com.jl.crm.android.widget.CrmOAuthFlowWebView;
import com.jl.crm.client.*;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.sqlite.SQLiteConnectionRepository;
import org.springframework.social.connect.sqlite.support.SQLiteConnectionRepositoryHelper;

import javax.inject.Inject;

/**
 * this is designed to be the interface into the RESTful web service
 * <p/>
 * <p/>
 * TODO design this to be flexible enough to take the API type (T) (in the case of the CRM, this is {@code
 * CrmOperations}.)
 *
 * @author Josh Long
 */
public class AuthenticationActivity extends Activity {

	@Inject SQLiteConnectionRepository sqLiteConnectionRepository;
	@Inject SQLiteConnectionRepositoryHelper repositoryHelper;
	@Inject CrmConnectionFactory connectionFactory;
	private CrmOAuthFlowWebView webView;
	private CrmOAuthFlowWebView.AccessTokenReceivedListener accessTokenReceivedListener =
			  new CrmOAuthFlowWebView.AccessTokenReceivedListener() {
				  @Override
				  public void accessTokenReceived(final String accessToken) {
					  try {
						  AsyncTask<?, ?, Connection<CrmOperations>> asyncTask = new AsyncTask<Object, Object, Connection<CrmOperations>>() {
							  @Override
							  protected Connection<CrmOperations> doInBackground(Object... params) {
								  Connection<CrmOperations> crmOperationsConnection = crmConnectionState.installAccessToken(accessToken);
								  runOnUiThread(connectionEstablishedRunnable);
								  return crmOperationsConnection;
							  }
						  };

						  asyncTask.execute(new Object[0]);
					  }
					  catch (Exception e) {
						  throw new RuntimeException(e);
					  }

				  }
			  };
	private Runnable connectionEstablishedRunnable = new Runnable() {
		@Override
		public void run() {
			Intent intent = new Intent(AuthenticationActivity.this, CustomerSearchActivity.class);
			startActivity(intent);
		}
	};
	private Runnable connectionNotEstablishedRunnable = new Runnable() {
		@Override
		public void run() {
			webView.noAccessToken();
		}
	};
	private CrmConnectionState crmConnectionState;
	private boolean debug = false;

	@Override
	public void onStart() {
		super.onStart();
		crmConnectionState.start();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return Commons.onOptionsItemSelected(this, item);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return Commons.onCreateOptionsMenu(this, menu);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Commons.onCreate(this, savedInstanceState);

		getActionBar().hide();

		this.crmConnectionState = new CrmConnectionState(
				                                                  this,
				                                                  this.connectionFactory,
				                                                  this.repositoryHelper,
				                                                  this.sqLiteConnectionRepository,
				                                                  this.connectionEstablishedRunnable,
				                                                  this.connectionNotEstablishedRunnable,
				                                                  getString(R.string.oauth_access_token_callback_uri));
		if (debug){    // todo make this a thing thats checked against some setting in
			// todo local storage so we know if the application has been used and authenticated before.
			crmConnectionState.resetLocalConnections();
		}

		this.webView = webView();
		this.setContentView(this.webView);
	}

	protected CrmOAuthFlowWebView webView() {
		String authenticateUri = crmConnectionState.buildAuthenticationUrl();
		String returnUri = getString(R.string.oauth_access_token_callback_uri);
		return new CrmOAuthFlowWebView(this, authenticateUri, returnUri, this.accessTokenReceivedListener);
	}

}
