package com.jl.crm.android.activities;

import android.app.Activity;
import android.content.*;
import android.net.Uri;
import android.os.*;
import android.util.Log;
import android.view.Window;
import android.webkit.*;
import com.jl.crm.android.R;
import com.jl.crm.client.*;
import org.springframework.security.crypto.encrypt.AndroidEncryptors;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.sqlite.SQLiteConnectionRepository;
import org.springframework.social.connect.sqlite.support.SQLiteConnectionRepositoryHelper;
import org.springframework.social.connect.support.*;
import org.springframework.social.oauth2.*;
import org.springframework.util.StringUtils;

/**
 * this is designed to be the interface into the RESTful web service
 * <p/>
 * <p/>
 * TODO design this to be flexible enough to take the API type (T) (in the case of the CRM, this is {@code
 * CrmOperations}.)
 *
 * @author Josh Long
 */
public class CrmWebOAuthActivity extends Activity /* <T> */ {

	WebView webView;
	CrmOperations crmOperations;
	String loadingString = "Loading...";
	String applicationName = "crm";
	ConnectionFactoryRegistry connectionFactoryRegistry;
	SQLiteConnectionRepository connectionRepository;
	SQLiteConnectionRepositoryHelper repositoryHelper;
	OAuth2ConnectionFactory<CrmOperations> crmOperationsConnectionFactory;
	SharedPreferences twitterPreferences;
	Connection<CrmOperations> crmOperationsConnection;

	String oauthCallbackUrl() {
		return getString(R.string.oauth_access_token_callback_uri);
	}

	@Override
	public void onStart() {
		super.onStart();

		Uri uri = getIntent().getData();

		if (uri == null){
			String returnUrl = oauthCallbackUrl();
			this.doPreConnect(returnUrl);
		}
	}

	void doPostConnect(final String accessToken) {
		AsyncTask<Object, Object, Object> at = new AsyncTask<Object, Object, Object>() {
			@Override
			protected Object doInBackground(Object... params) {
				Log.d(CrmWebOAuthActivity.class.getName(), "access token received: '" + accessToken + "' ");
				crmOperationsConnection = crmOperationsConnectionFactory.createConnection(new AccessGrant(accessToken));
				crmOperations = crmOperationsConnection.getApi();
				return null;
			}
		};
		at.execute();
	}

	void doPreConnect(String returnUrl) {
		this.webView.clearView();

		OAuth2Operations oAuth2Operations = this.crmOperationsConnectionFactory.getOAuthOperations();
		if (oAuth2Operations instanceof OAuth2Template){
			OAuth2Template oAuth2Template = (OAuth2Template) oAuth2Operations;
			oAuth2Template.setUseParametersForClientAuthentication(false);
		}

		OAuth2Parameters oAuth2Parameters = new OAuth2Parameters();
		oAuth2Parameters.setScope("read,write");
		if (StringUtils.hasText(returnUrl)){
			oAuth2Parameters.setRedirectUri(returnUrl);
		}

		String authorizationUrl = oAuth2Operations.buildAuthenticateUrl(GrantType.IMPLICIT_GRANT, oAuth2Parameters);
		Log.d(CrmWebOAuthActivity.class.getName(), "Loading the authorization URL, '" + authorizationUrl + "'.");

		webView.loadUrl(authorizationUrl);
	}

	String fullUrl(String baseUrl, String end) {
		String base = !baseUrl.endsWith("/") ? baseUrl + "/" : baseUrl;
		String newEnd = end.startsWith("/") ? end.substring(1) : end;
		return base + newEnd;
	}

	CrmApiAdapter apiAdapter() {
		return new CrmApiAdapter();
	}

	CrmServiceProvider serviceProvider(String clientId, String clientSecret, String baseUrl, String authorizeUrl, String accessTokenUrl) {
		return new CrmServiceProvider(baseUrl, clientId, clientSecret, authorizeUrl, accessTokenUrl);
	}

	CrmConnectionFactory crmConnectionFactory(CrmServiceProvider crmServiceProvider, CrmApiAdapter crmApiAdapter) {
		return new CrmConnectionFactory(crmServiceProvider, crmApiAdapter);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		final Activity self = this;

		String baseUrl = getString(R.string.base_uri) ;
		String clientId =getString(R.string.client_id) ;
		String clientSecret = getString(R.string.client_secret ) ;
		String accessTokenUri = fullUrl(baseUrl,getString(R.string.token_uri) );
		String authorizeUri = fullUrl(baseUrl, getString(R.string.authorize_uri));

		// setup common infrastructure for Spring Social
		twitterPreferences = getSharedPreferences("CrmConnectPreferences", Context.MODE_PRIVATE);
		connectionFactoryRegistry = new ConnectionFactoryRegistry();
		crmOperationsConnectionFactory = crmConnectionFactory(serviceProvider(clientId, clientSecret, baseUrl, authorizeUri, accessTokenUri), apiAdapter());
		connectionFactoryRegistry.addConnectionFactory(crmOperationsConnectionFactory);
		repositoryHelper = new SQLiteConnectionRepositoryHelper(self);
		connectionRepository = new SQLiteConnectionRepository(repositoryHelper, connectionFactoryRegistry, AndroidEncryptors.text("password", "5c0744940b5c369b"));

		// setup UI
		getWindow().requestFeature(Window.FEATURE_PROGRESS);
		getWindow().setFeatureInt(Window.FEATURE_PROGRESS, Window.PROGRESS_VISIBILITY_ON);
		this.webView = new WebView(this);
		setContentView(this.webView);

		final Activity activity = this;

		webView.setWebViewClient(new WebViewClient() {

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				Log.d(CrmWebOAuthActivity.class.getName(), "shouldOverrideUrlLoading(): loading url in webview:" + url);

				if (url.startsWith(oauthCallbackUrl())){
					Uri uri = Uri.parse(url);
					String encodedFragment = uri.getEncodedFragment();
					String at = "access_token=";
					if (encodedFragment.contains(at)){
						doPostConnect(encodedFragment.substring((at.length())).split("&")[0]);
					}
				}
				else {
					view.loadUrl(url);
				}
				return true;
			}

			@Override
			public void onLoadResource(WebView view, String url) {
				Log.d(CrmWebOAuthActivity.class.getName(), "onLoadResource(): loading url in webview:" + url);
				super.onLoadResource(view, url);
			}

			@Override
			public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
				Log.d(CrmWebOAuthActivity.class.getName(), "shouldInterceptRequest(): loading url in webview:" + url);
				return super.shouldInterceptRequest(view, url);
			}
		});
		webView.setWebChromeClient(new WebChromeClient() {

			public void onProgressChanged(WebView view, int progress) {
				activity.setTitle(loadingString);
				activity.setProgress(progress * 100);
				if (progress == 100){
					activity.setTitle(applicationName);
				}
			}
		});
	}


}
