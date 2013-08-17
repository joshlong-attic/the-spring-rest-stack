package com.jl.crm.android;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import com.jl.crm.client.*;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.sqlite.SQLiteConnectionRepository;
import org.springframework.social.connect.sqlite.support.SQLiteConnectionRepositoryHelper;
import org.springframework.social.oauth2.*;
import org.springframework.util.*;

import java.util.List;

/**
 * We need a central place to lookup information about the currrent ocnnection to the CRM A lot of the stuff that's
 * happening in the {@link com.jl.crm.android.activities.AuthenticationActivity} can happen here instead. The nice part
 * about this is that we can then test for things like current authentication status in other Android componentns where
 * this class might be injected.
 *
 * @author Josh Long
 */
public class CrmConnectionState {

	private OAuth2Operations oAuth2Operations;
	private String oauthAccessTokenCallbackUri;
	private SQLiteConnectionRepository sqLiteConnectionRepository;
	private CrmConnectionFactory connectionFactory;
	private SQLiteConnectionRepositoryHelper repositoryHelper;

	private Runnable connectionNotEstablishedRunnable;
	private Runnable connectionEstablishedRunnable;

	private Activity activity;

	private AsyncTask<?, ?, Connection<CrmOperations>> asyncTaskToLoadCrmOperationsConnection = new AsyncTask<Object, Object, Connection<CrmOperations>>() {
		@Override
		protected Connection<CrmOperations> doInBackground(Object... params) {
			Connection<CrmOperations> connection = sqLiteConnectionRepository.findPrimaryConnection(CrmOperations.class);
			activity.runOnUiThread(connection != null ? connectionEstablishedRunnable : connectionNotEstablishedRunnable);
			return null;
		}
	};

	public CrmConnectionState(Activity context,
			                           CrmConnectionFactory connectionFactory,
			                           SQLiteConnectionRepositoryHelper repositoryHelper,
			                           SQLiteConnectionRepository sqLiteConnectionRepository,
			                           Runnable connectionEstablishedRunnable,
			                           Runnable connectionNotEstablishedRunnable,
			                           String oauthAccessTokenCallbackUri) {
		this.activity = context;
		this.repositoryHelper = repositoryHelper;
		this.sqLiteConnectionRepository = sqLiteConnectionRepository;
		this.connectionFactory = connectionFactory;
		this.oAuth2Operations = connectionFactory.getOAuthOperations();
		this.connectionEstablishedRunnable = connectionEstablishedRunnable;
		this.connectionNotEstablishedRunnable = connectionNotEstablishedRunnable;
		this.oauthAccessTokenCallbackUri = oauthAccessTokenCallbackUri;
	}

	public void start() {
		asyncTaskToLoadCrmOperationsConnection.execute(new Object[]{});
	}

	public void resetLocalConnections() {
		SQLiteDatabase sqLiteDatabase = null;
		try {
			sqLiteDatabase = repositoryHelper.getWritableDatabase();
			clearAllConnections();
		}
		finally {
			if (null != sqLiteDatabase){
				sqLiteDatabase.close();
			}
		}
	}

	private void clearAllConnections() {
		MultiValueMap<String, Connection<?>> mvMapOfConnections =
				  sqLiteConnectionRepository.findAllConnections();
		for (String k : mvMapOfConnections.keySet()) {
			List<Connection<?>> connectionList = mvMapOfConnections.get(k);
			for (Connection<?> c : connectionList) {
				sqLiteConnectionRepository.removeConnection(c.getKey());
			}
		}
	}

	public String buildAuthenticationUrl() {
		OAuth2Template oAuth2Template = (OAuth2Template) oAuth2Operations;
		oAuth2Template.setUseParametersForClientAuthentication(false);
		OAuth2Parameters oAuth2Parameters = new OAuth2Parameters();
		oAuth2Parameters.setScope("read,write");
		if (StringUtils.hasText(oauthAccessTokenCallbackUri)){
			oAuth2Parameters.setRedirectUri(oauthAccessTokenCallbackUri);
		}
		return oAuth2Operations.buildAuthenticateUrl(GrantType.IMPLICIT_GRANT, oAuth2Parameters);
	}

	public Connection<CrmOperations> installAccessToken(String accessToken) {
		AccessGrant accessGrant = new AccessGrant(accessToken);
		Connection<CrmOperations> crmOperationsConnection = connectionFactory.createConnection(accessGrant);
		sqLiteConnectionRepository.addConnection(crmOperationsConnection);
		return crmOperationsConnection;
	}

/*
	 SQLiteConnectionRepository sqLiteConnectionRepository;
	 SQLiteConnectionRepositoryHelper repositoryHelper;
	  CrmConnectionFactory connectionFactory;
//	CrmOAuthFlowWebView webView;



	CrmOAuthFlowWebView.AccessTokenReceivedListener accessTokenReceivedListener = new CrmOAuthFlowWebView.AccessTokenReceivedListener() {

		@Override
		public void accessTokenReceived(final String accessToken) {

			AsyncTask<?, ?, Connection<CrmOperations>> asyncTask = new AsyncTask<Object, Object, Connection<CrmOperations>>() {
				@Override
				protected Connection<CrmOperations> doInBackground(Object... params) {
					AccessGrant accessGrant = new AccessGrant(accessToken);
					Connection<CrmOperations> crmOperationsConnection = connectionFactory.createConnection(accessGrant);
					sqLiteConnectionRepository.addConnection(crmOperationsConnection);
					runOnUiThread(connectionEstablishedRunnable);
					return crmOperationsConnection;
				}
			};
			try {
				asyncTask.execute(new Object[0]);
			}
			catch (Exception e) {
				throw new RuntimeException(e);
			}

		}
	};
	Runnable connectionEstablishedRunnable = new Runnable() {
		@Override
		public void run() {
			connectionEstablished();
		}
	};
	AsyncTask<?, ?, Connection<CrmOperations>> asyncTaskToLoadCrmOperationsConnection =
			  new AsyncTask<Object, Object, Connection<CrmOperations>>() {
				  @Override
				  protected Connection<CrmOperations> doInBackground(Object... params) {

					  Connection<CrmOperations> connection = sqLiteConnectionRepository.findPrimaryConnection(CrmOperations.class);
					  if (connection != null){
						  runOnUiThread(connectionEstablishedRunnable);
					  }
					  else {
						  runOnUiThread(new Runnable() {
							  @Override
							  public void run() {
								  webView.noAccessToken();
							  }
						  });
					  }
					  return null;
				  }
			  };*/
	/*private boolean debug = true;

	@Override
	public void onStart() {
		super.onStart();
		asyncTaskToLoadCrmOperationsConnection.execute(new Object[]{});
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

		 // we don't want the ActionBar on this page, obviously,
		 // you shouldn't be able to view data if you're not authenticated.
		getActionBar().hide();

		if (debug){
			SQLiteDatabase sqLiteDatabase = null;
			try {
				sqLiteDatabase = repositoryHelper.getWritableDatabase();
				clearAllConnections();
			}
			finally {
				if (null != sqLiteDatabase){
					sqLiteDatabase.close();
				}
			}
		}
		this.webView = webView();
		setContentView(this.webView);
	}

	protected void clearAllConnections() {

		MultiValueMap<String, Connection<?>> mvMapOfConnections = sqLiteConnectionRepository.findAllConnections();
		for (String k : mvMapOfConnections.keySet()) {
			List<Connection<?>> connectionList = mvMapOfConnections.get(k);
			for (Connection<?> c : connectionList) {
				sqLiteConnectionRepository.removeConnection(c.getKey());
			}
		}
	}

	protected void connectionEstablished() {
		Intent intent = new Intent(this, CustomerSearchActivity.class);
		startActivity(intent);
	}

	protected CrmOAuthFlowWebView webView() {
		String authenticateUri = buildAuthenticationUrl();
		String returnUri = getString(R.string.oauth_access_token_callback_uri);
		return new CrmOAuthFlowWebView(this, authenticateUri, returnUri, this.accessTokenReceivedListener);
	}

	protected String buildAuthenticationUrl() {
		OAuth2Operations oAuth2Operations = this.connectionFactory.getOAuthOperations();
		OAuth2Template oAuth2Template = (OAuth2Template) oAuth2Operations;
		oAuth2Template.setUseParametersForClientAuthentication(false);
		String returnUri = getString(R.string.oauth_access_token_callback_uri);
		OAuth2Parameters oAuth2Parameters = new OAuth2Parameters();
		oAuth2Parameters.setScope("read,write");
		if (StringUtils.hasText(returnUri)){
			oAuth2Parameters.setRedirectUri(returnUri);
		}
		return oAuth2Operations.buildAuthenticateUrl(GrantType.IMPLICIT_GRANT, oAuth2Parameters);
	}
*/
}
