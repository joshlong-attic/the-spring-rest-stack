package com.joshlong.spring.walkingtour.android.view.activities;

import android.app.Activity;
import android.content.*;
import android.net.Uri;
import android.os.*;
import android.util.Log;
import android.view.Window;
import android.webkit.*;
import com.jl.crm.client.*;
import com.joshlong.spring.walkingtour.android.R;
import org.springframework.security.crypto.encrypt.AndroidEncryptors;
import org.springframework.social.connect.*;
import org.springframework.social.connect.sqlite.SQLiteConnectionRepository;
import org.springframework.social.connect.sqlite.support.SQLiteConnectionRepositoryHelper;
import org.springframework.social.connect.support.*;
import org.springframework.social.oauth2.*;
import org.springframework.util.StringUtils;

/**
 * this is designed to be the interface into the RESTful web service
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
	Activity activity = this;
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
//		else {
//			String accessToken = uri.getQueryParameter("access_token");
//			if (accessToken != null){
//				this.doPostConnect(accessToken);
//			}
//		}
	}

	void doPostConnect(Uri uriWithAccessToken) {
		this.doPostConnect(uriWithAccessToken.getQueryParameter("access_token"));
	}

	void doPostConnect(final String accessToken) {
		AsyncTask<Object,Object,Object> at  = new AsyncTask<Object, Object, Object>() {
			@Override
			protected Object doInBackground(Object... params) {
				Log.d(CrmWebOAuthActivity.class.getName(), "access token received: '" + accessToken + "' ");
				crmOperationsConnection = crmOperationsConnectionFactory.createConnection(new AccessGrant(accessToken));
				crmOperations = crmOperationsConnection.getApi();
				return null ;
			}
		} ;
		at.execute();



		/*String returnToUrl = environment.getProperty("sscrm.base-url") + "/";

		if (oAuth2Operations instanceof OAuth2Template){
			((OAuth2Template) oAuth2Operations).setUseParametersForClientAuthentication(false);
		}
		OAuth2Parameters parameters = new OAuth2Parameters();
		if (StringUtils.hasText(returnToUrl)){
			parameters.setRedirectUri(returnToUrl);
		}
		parameters.setScope("read,write");
		String authorizationUrl = oAuth2Operations.buildAuthenticateUrl(GrantType.IMPLICIT_GRANT, parameters);
		Desktop.getDesktop().browse(new URI(authorizationUrl));
		String i = JOptionPane.showInputDialog(null, "What's the 'access_token'?");
		String accessToken = i != null && !i.trim().equals("") ? i.trim() : null;
		connection = crmConnectionFactory.createConnection(new AccessGrant(accessToken));
	}
*/


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

	ApiAdapter<CrmOperations> apiAdapter() {
		return new CrmApiAdapter();
	}

	OAuth2ServiceProvider<CrmOperations> serviceProvider(String clientId, String clientSecret, String baseUrl, String authorizeUrl, String accessTokenUrl) {
		return new CrmServiceProvider(baseUrl, clientId, clientSecret, authorizeUrl, accessTokenUrl);
	}

	OAuth2ConnectionFactory<CrmOperations> crmConnectionFactory(OAuth2ServiceProvider<CrmOperations> crmServiceProvider, ApiAdapter<CrmOperations> crmApiAdapter) {
		return new CrmConnectionFactory(crmServiceProvider, crmApiAdapter);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		final Activity self = this;

		String baseUrl = "http://10.0.2.2:8080/";
		String clientId = "android-crm";
		String clientSecret = "123456";
		String accessTokenUrl = fullUrl(baseUrl, "/oauth/token");
		String authorizeUrl = fullUrl(baseUrl, "/oauth/authorize");

		// setup common infrastructure for Spring Social
		twitterPreferences = getSharedPreferences("TwitterConnectPreferences", Context.MODE_PRIVATE);
		connectionFactoryRegistry = new ConnectionFactoryRegistry();
		OAuth2ServiceProvider<CrmOperations> serviceProvider = serviceProvider(clientId, clientSecret, baseUrl, authorizeUrl, accessTokenUrl);
		ApiAdapter<CrmOperations> apiAdapter = apiAdapter();
		crmOperationsConnectionFactory = crmConnectionFactory(serviceProvider, apiAdapter);
		connectionFactoryRegistry.addConnectionFactory(crmOperationsConnectionFactory);
		repositoryHelper = new SQLiteConnectionRepositoryHelper(self);
		connectionRepository = new SQLiteConnectionRepository(repositoryHelper, connectionFactoryRegistry, AndroidEncryptors.text("password", "5c0744940b5c369b"));


		// setup UI
		getWindow().requestFeature(Window.FEATURE_PROGRESS);
		getWindow().setFeatureInt(Window.FEATURE_PROGRESS, Window.PROGRESS_VISIBILITY_ON);
		this.webView = new WebView(this);
		setContentView(this.webView);


		webView.setWebViewClient(new WebViewClient() {

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				Log.d(CrmWebOAuthActivity.class.getName(), "shouldOverrideUrlLoading(): loading url in webview:" + url);

				if (url.startsWith(oauthCallbackUrl())){
					Uri uri = Uri.parse(url);
					String encodedFragment = uri.getEncodedFragment();
					String at = "access_token=";
					if (encodedFragment.contains(at)){
						doPostConnect(
								               encodedFragment.substring((at.length())).split("&")[0]
						);
					}
					//	doPostConnect(  encodedFragment.sp );
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


/*
    @SuppressWarnings("unused")
    private static final String TAG = CrmWebOAuthActivity.class.getSimpleName();

    private static final String REQUEST_TOKEN_KEY = "request_token";

    private static final String REQUEST_TOKEN_SECRET_KEY = "request_token_secret";

    private  ConnectionRepository connectionRepository;

    private TwitterConnectionFactory connectionFactory;

    private SharedPreferences twitterPreferences;

    // ***************************************
    // Activity methods
    // ***************************************
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.connectionRepository = getApplicationContext().getConnectionRepository();
        this.connectionFactory = getApplicationContext().getTwitterConnectionFactory();
        this.twitterPreferences = getSharedPreferences("TwitterConnectPreferences", Context.MODE_PRIVATE);
    }

    @Override
    public void onStart() {
        super.onStart();
        Uri uri = getIntent().getData();
        if (uri != null) {
            String oauthVerifier = uri.getQueryParameter("oauth_verifier");

            if (oauthVerifier != null) {
                getWebView().clearView();
                new TwitterPostConnectTask().execute(oauthVerifier);
            }
        } else {
            new TwitterPreConnectTask().execute();
        }
    }

    // ***************************************
    // Private methods
    // ***************************************
    private String getOAuthCallbackUrl() {
        return getString(R.string.twitter_oauth_callback_url);
    }

    private void displayTwitterAuthorization(OAuthToken requestToken) {
        // save for later use
        saveRequestToken(requestToken);

        // Generate the Twitter authorization URL to be used in the browser or web view
        String authUrl = this.connectionFactory.getOAuthOperations().buildAuthorizeUrl(requestToken.getValue(),
                OAuth1Parameters.NONE);

        // display the twitter authorization screen
        getWebView().loadUrl(authUrl);
    }

    private void displayTwitterOptions() {
        Intent intent = new Intent();
        intent.setClass(this, TwitterActivity.class);
        startActivity(intent);
        finish();
    }

    private void saveRequestToken(OAuthToken requestToken) {
        SharedPreferences.Editor editor = this.twitterPreferences.edit();
        editor.putString(REQUEST_TOKEN_KEY, requestToken.getValue());
        editor.putString(REQUEST_TOKEN_SECRET_KEY, requestToken.getSecret());
        editor.commit();
    }

    private OAuthToken retrieveRequestToken() {
        String token = this.twitterPreferences.getString(REQUEST_TOKEN_KEY, null);
        String secret = this.twitterPreferences.getString(REQUEST_TOKEN_SECRET_KEY, null);
        return new OAuthToken(token, secret);
    }

    private void deleteRequestToken() {
        this.twitterPreferences.edit().clear().commit();
    }

    // ***************************************
    // Private classes
    // ***************************************
    private class TwitterPreConnectTask extends AsyncTask<Void, Void, OAuthToken> {

        @Override
        protected void onPreExecute() {
            showProgressDialog("Initializing OAuth Connection...");
        }

        @Override
        protected OAuthToken doInBackground(Void... params) {
            // Fetch a one time use Request Token from Twitter
            return connectionFactory.getOAuthOperations().fetchRequestToken(getOAuthCallbackUrl(), null);
        }

        @Override
        protected void onPostExecute(OAuthToken requestToken) {
            dismissProgressDialog();
            displayTwitterAuthorization(requestToken);
        }

    }

    private class TwitterPostConnectTask extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            showProgressDialog("Finalizing OAuth Connection...");
        }

        @Override
        protected Void doInBackground(String... params) {
            if (params.length <= 0) {
                return null;
            }

            final String verifier = params[0];

            OAuthToken requestToken = retrieveRequestToken();

            // Authorize the Request Token
            AuthorizedRequestToken authorizedRequestToken = new AuthorizedRequestToken(requestToken, verifier);

            // Exchange the Authorized Request Token for the Access Token
            OAuthToken accessToken = connectionFactory.getOAuthOperations().exchangeForAccessToken(
                    authorizedRequestToken, null);

            deleteRequestToken();

            // Persist the connection and Access Token to the repository
            Connection<Twitter> connection = connectionFactory.createConnection(accessToken);

            try {
                connectionRepository.addConnection(connection);
            } catch (DuplicateConnectionException e) {
                // connection already exists in repository!
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            dismissProgressDialog();
            displayTwitterOptions();
        }

    }*/

}
