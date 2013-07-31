package com.jl.crm.android.activities;

import android.content.Context;
import android.net.Uri;
import android.util.AttributeSet;
import android.webkit.*;
import org.springframework.util.Assert;

/** @author Josh Long */
public class Oauth2ImplicitFlowWebView extends WebView {

	/** the URI to which the client will redirect with the {@code accessToken } in tow. */
	private String redirectUri;
	private String authenticateUri;
	private String redirectUriAccessTokenFragmentParameter = "access_token";
	private WebViewClient webViewClient = new WebViewClient() {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			if (url.startsWith((redirectUri))){
				Uri uri = Uri.parse(url);
				String encodedFragment = uri.getEncodedFragment();
				String at = redirectUriAccessTokenFragmentParameter + "=";
				if (encodedFragment.contains(at)){
					String accessToken = (encodedFragment.substring((at.length())).split("&")[0]);
					accessTokenReceivedListener.accessTokenReceived(accessToken);
					return true;
				}
			}
			return false;
		}
	};

	private AccessTokenReceivedListener accessTokenReceivedListener;

	public Oauth2ImplicitFlowWebView(Context context, String authenticateUri, String redirectUri, AccessTokenReceivedListener accessTokenReceivedListener) {
		super(context);
		setup(authenticateUri, redirectUri, accessTokenReceivedListener);
		afterPropertiesSet();
	}

	public Oauth2ImplicitFlowWebView(Context context, AttributeSet attrs, String authenticateUri, String redirectUri, AccessTokenReceivedListener accessTokenReceivedListener) {
		super(context, attrs);
		setup(authenticateUri, redirectUri, accessTokenReceivedListener);
		afterPropertiesSet();
	}

	public Oauth2ImplicitFlowWebView(Context context, AttributeSet attrs, int defStyle, String authenticateUri, String redirectUri, AccessTokenReceivedListener accessTokenReceivedListener) {
		super(context, attrs, defStyle);
		setup(authenticateUri, redirectUri, accessTokenReceivedListener);
		afterPropertiesSet();
	}

	public Oauth2ImplicitFlowWebView(Context context, AttributeSet attrs, int defStyle, boolean privateBrowsing, String authenticateUri, String redirectUri, AccessTokenReceivedListener accessTokenReceivedListener) {
		super(context, attrs, defStyle, privateBrowsing);
		setup(authenticateUri, redirectUri, accessTokenReceivedListener);
		afterPropertiesSet();
	}

	public void noAccessToken() {
		clearView();
		this.loadUrl(this.authenticateUri);
	}

	public void setAuthenticateUri(String authenticateUri) {
		this.authenticateUri = authenticateUri;
	}

	protected void setup(String authenticateUri, String redirectUri, AccessTokenReceivedListener accessTokenReceivedListener) {
		setAuthenticateUri(authenticateUri);
		setAccessTokenReceivedListener(accessTokenReceivedListener);
		setRedirectUri(redirectUri);
	}

	public void setRedirectUriAccessTokenFragmentParameter(String redirectUriAccessTokenFragmentParameter) {
		this.redirectUriAccessTokenFragmentParameter = redirectUriAccessTokenFragmentParameter;
	}

	public void setRedirectUri(String redirectUri) {
		this.redirectUri = redirectUri;
	}

	protected void afterPropertiesSet() {
		setWebViewClient(this.webViewClient);
		Assert.notNull(this.redirectUri);
		Assert.notNull(this.authenticateUri);

	}

	public void setAccessTokenReceivedListener(AccessTokenReceivedListener atl) {
		this.accessTokenReceivedListener = atl;
	}

	public static interface AccessTokenReceivedListener {
		void accessTokenReceived(String at);
	}
}

/*

	@Inject
	CrmConnectionFactory connectionFactory;
	@Inject
	SharedPreferences preferences;
	WebView webView;

	protected void continueWithAccessToken(String accessToken) {
		saveAccessToken(accessToken);
		Intent intent = new Intent(CrmWebOAuthActivity.this, UserWelcomeActivity.class);
		startActivity(intent);
	}

	protected String accessTokenPersistenceKey() {
		return "accessToken";
	}

	protected void saveAccessToken(final String at) {
		if (StringUtils.hasText(at)){
			SharedPreferences.Editor editor = preferences.edit();
			editor.putString(this.accessTokenPersistenceKey(), at);
			editor.commit();
		}
	}

	protected String readAccessToken() {
		return this.preferences.getString(this.accessTokenPersistenceKey(), null);
	}

	protected String oauthCallbackUrl() {
		return getString(R.string.oauth_access_token_callback_uri);
	}

	protected void noAccessToken() {
		String returnUrl = oauthCallbackUrl();
		String authorizationUrl = buildAuthenticationUrl(returnUrl);
		this.webView.clearView();
		this.webView.loadUrl(authorizationUrl);
	}

	protected void resetAccessToken() {
		this.preferences.edit().clear().commit();
	}

	@Override
	public void onStart() {
		super.onStart();

		//resetAccessToken();
		// two flows;
		// - they dont have an access token, in which case we should send them to the authentication flow
		// - they do have an access token, in which case we should attempt to connect with the stored accessToken (assuming it's not expired)



		String accessToken = this.readAccessToken();

		if (StringUtils.hasText(accessToken)){
			this.continueWithAccessToken(accessToken);
		}
		else {
			Uri uri = getIntent().getData();
			if (uri == null){
				this.noAccessToken();
			}
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
		Window w = this.getWindow();
		w.requestFeature(Window.FEATURE_PROGRESS);
		w.setFeatureInt(Window.FEATURE_PROGRESS, Window.PROGRESS_VISIBILITY_ON);

		this.webView = webView();

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
		webView.getSettings().setAllowContentAccess(true);
		webView.setWebViewClient(this.webViewClient);
		webView.setWebChromeClient(this.webChromeClient);
		return webView;
	}
*/