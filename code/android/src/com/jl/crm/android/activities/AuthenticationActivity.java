package com.jl.crm.android.activities;

import android.content.Intent;
import android.os.*;
import android.view.Window;
import com.jl.crm.android.R;
import com.jl.crm.android.widget.OAuth2ImplicitFlowWebView;
import com.jl.crm.client.*;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.sqlite.SQLiteConnectionRepository;
import org.springframework.social.oauth2.*;
import org.springframework.util.*;

import javax.inject.Inject;
import java.util.List;

/**
 * this is designed to be the interface into the RESTful web service
 * <p/>
 * <p/>
 * TODO design this to be flexible enough to take the API type (T) (in the case of the CRM, this is {@code
 * CrmOperations}.)
 *
 * @author Josh Long
 */
public class AuthenticationActivity extends BaseActivity {

	@Inject SQLiteConnectionRepository sqLiteConnectionRepository;
	@Inject CrmConnectionFactory connectionFactory;
	OAuth2ImplicitFlowWebView webView;
	OAuth2ImplicitFlowWebView.AccessTokenReceivedListener accessTokenReceivedListener = new OAuth2ImplicitFlowWebView.AccessTokenReceivedListener() {

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
	private Runnable connectionEstablishedRunnable = new Runnable() {
		@Override
		public void run() {
			connectionEstablished();
		}
	};
	private AsyncTask<?, ?, Connection<CrmOperations>> asyncTaskToLoadCrmOperationsConnection =
			  new AsyncTask<Object, Object, Connection<CrmOperations>>() {
				  @Override
				  protected Connection<CrmOperations> doInBackground(Object... params) {

					  clearAllConnections();

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
			  };

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
		Intent intent = new Intent(AuthenticationActivity.this, CustomerSearchActivity.class);
		startActivity(intent);
	}

	@Override
	public void onStart() {
		super.onStart();
		asyncTaskToLoadCrmOperationsConnection.execute(new Object[]{});
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Window w = this.getWindow();
		w.requestFeature(Window.FEATURE_PROGRESS);
		w.setFeatureInt(Window.FEATURE_PROGRESS, Window.PROGRESS_VISIBILITY_ON);

		this.webView = webView();

		setContentView(this.webView);
	}

	protected OAuth2ImplicitFlowWebView webView() {
		String authenticateUri = buildAuthenticationUrl();
		String returnUri = getString(R.string.oauth_access_token_callback_uri);
		return new OAuth2ImplicitFlowWebView(this, authenticateUri, returnUri, this.accessTokenReceivedListener);
	}

	protected String buildAuthenticationUrl() {
		OAuth2Operations oAuth2Operations = this.connectionFactory.getOAuthOperations();
		if (oAuth2Operations instanceof OAuth2Template){
			OAuth2Template oAuth2Template = (OAuth2Template) oAuth2Operations;
			oAuth2Template.setUseParametersForClientAuthentication(false);
		}
		String returnUri = getString(R.string.oauth_access_token_callback_uri);
		OAuth2Parameters oAuth2Parameters = new OAuth2Parameters();
		oAuth2Parameters.setScope("read,write");
		if (StringUtils.hasText(returnUri)){
			oAuth2Parameters.setRedirectUri(returnUri);
		}
		return oAuth2Operations.buildAuthenticateUrl(GrantType.IMPLICIT_GRANT, oAuth2Parameters);
	}

}
