package com.jl.crm.android.activities;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.*;
import android.util.Log;
import android.view.Window;
import android.webkit.*;
import com.jl.crm.android.R;
import com.jl.crm.client.*;
import org.springframework.social.oauth2.*;
import org.springframework.util.StringUtils;

import javax.inject.*;

/**
 * this is designed to be the interface into the RESTful web service
 * <p/>
 * <p/>
 * TODO design this to be flexible enough to take the API type (T) (in the case of the CRM, this is {@code
 * CrmOperations}.)
 *
 * @author Josh Long
 */
public class CrmWebOAuthActivity extends BaseActivity {


	@Inject
	CrmConnectionFactory connectionFactory;
	@Inject
	SharedPreferences preferences;
	WebView webView;
	WebChromeClient webChromeClient = new WebChromeClient() {
		public void onProgressChanged(WebView view, int progress) {
			setTitle(loadingString());
			setProgress(progress * 100);
			if (progress == 100){
				setTitle(applicationName());
			}
		}
	};
	WebViewClient webViewClient = new WebViewClient() {

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			Log.d(CrmWebOAuthActivity.class.getName(), "shouldOverrideUrlLoading(): loading url in webview:" + url);
			if (url.startsWith(oauthCallbackUrl())){
				Uri uri = Uri.parse(url);
				String encodedFragment = uri.getEncodedFragment();
				String at = "access_token=";
				if (encodedFragment.contains(at)){
					String accessToken = (encodedFragment.substring((at.length())).split("&")[0]);
					CrmOperations crmOperations = crmOperations(accessToken, connectionFactory, preferences);
					doWithCrmOperations(crmOperations);
				}
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
	};

	@Inject
	Provider<CrmOperations>  operationsProvider ;

	CrmOperations crmOperations(final String at, final CrmConnectionFactory connectionFactory, final SharedPreferences preferences) {
		AsyncTask<?, ?, CrmOperations> crmOperationsAsyncTask = new AsyncTask<Object, Object, CrmOperations>() {
			private final String accessTokenPreferenceName = "accessToken";

			@Override
			protected CrmOperations doInBackground(Object... params) {


				return operationsProvider.get();

				/*final String accessToken = preferences.getString(this.accessTokenPreferenceName, at);
				AccessGrant accessGrant = new AccessGrant(accessToken);
				return connectionFactory.createConnection(accessGrant).getApi();*/
			}
		};
		SharedPreferences.Editor editor = preferences.edit();
		editor.putString("accessToken", at);
		editor.commit();
		try {
			return crmOperationsAsyncTask.execute().get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	protected void doWithCrmOperations(final CrmOperations crmOperations) {
		AsyncTask<?, ?, ?> asyncTask = new AsyncTask<Object, Object, Object>() {
			@Override
			protected Object doInBackground(Object... params) {
				User currentSignedInUser = crmOperations.currentUser();
				Log.d(CrmWebOAuthActivity.class.getName(), currentSignedInUser.toString());

				return null;
			}
		};
		try {
			asyncTask.execute().get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}


	}

	protected String oauthCallbackUrl() {
		return getString(R.string.oauth_access_token_callback_uri);
	}

	@Override
	public void onStart() {
		super.onStart();

		Uri uri = getIntent().getData();

		if (uri == null){
			String returnUrl = oauthCallbackUrl();
			String authorizationUrl = buildAuthenticationUrl(returnUrl);

			this.webView.clearView();
			this.webView.loadUrl(authorizationUrl);

		}
	}

	protected String buildAuthenticationUrl(String returnUrl) {
		OAuth2Operations oAuth2Operations = this.connectionFactory.getOAuthOperations();
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
		Log.d(CrmWebOAuthActivity.class.getName(), "buildAuthenticationUrl: '" + authorizationUrl + "'. returnUrl: '" + returnUrl + "'");
		return authorizationUrl;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.webView = webView();
		getWindow().requestFeature(Window.FEATURE_PROGRESS);
		getWindow().setFeatureInt(Window.FEATURE_PROGRESS, Window.PROGRESS_VISIBILITY_ON);

		setContentView(this.webView);
	}

	protected String loadingString() {
		return getString(R.string.loading_string);
	}

	protected String applicationName() {
		return getString(R.string.app_name);
	}

	protected WebView webView() {
		WebView webView = new WebView(this);
		webView.setWebViewClient(this.webViewClient);
		webView.setWebChromeClient(this.webChromeClient);
		return webView;
	}

}
