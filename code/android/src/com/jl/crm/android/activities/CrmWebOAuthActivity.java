package com.jl.crm.android.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import com.jl.crm.android.*;
import com.jl.crm.client.CrmConnectionFactory;
import org.springframework.social.oauth2.*;
import org.springframework.util.StringUtils;

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
public class CrmWebOAuthActivity extends BaseActivity {

	@Inject
	AccessTokenClient accessTokenClient;
	@Inject
	CrmConnectionFactory connectionFactory;
	Oauth2ImplicitFlowWebView webView;
	Oauth2ImplicitFlowWebView.AccessTokenReceivedListener accessTokenReceivedListener = new Oauth2ImplicitFlowWebView.AccessTokenReceivedListener() {
		@Override
		public void accessTokenReceived(String at) {
			Intent intent = new Intent(CrmWebOAuthActivity.this, UserWelcomeActivity.class);
			startActivity(intent);
		}
	};


	@Override
	public void onStart() {
		super.onStart();

		//resetAccessToken();


		String accessToken = this.accessTokenClient.readAccessTokenKey();
		if (StringUtils.hasText(accessToken)){
			this.accessTokenReceivedListener.accessTokenReceived(accessToken);    // skip the form all together
		}
		else {
			Uri uri = getIntent().getData();
			if (uri == null){
				webView.noAccessToken();
			}
		}
	}

	protected String buildAuthenticationUrl( ) {
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

		String authorizationUrl = oAuth2Operations.buildAuthenticateUrl(GrantType.IMPLICIT_GRANT, oAuth2Parameters);
		Log.d(CrmWebOAuthActivity.class.getName(), "buildAuthenticationUrl: '" + authorizationUrl + "'. returnUrl: '" + returnUri + "'");
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

	protected Oauth2ImplicitFlowWebView webView() {
		String authenticateUri = buildAuthenticationUrl( );
		String returnUri = getString(R.string.oauth_access_token_callback_uri);
		return new Oauth2ImplicitFlowWebView(this, authenticateUri, returnUri, this.accessTokenReceivedListener);
	}

}
