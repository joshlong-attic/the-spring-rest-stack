package com.jl.crm.android.widget;

import android.content.Context;
import android.net.Uri;
import android.util.AttributeSet;
import android.webkit.CookieManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import org.springframework.util.Assert;

/**
 * This simplifies connecting to our OAuth secured service.
 * <p/>
 * It should be possible to extract out a generic Spring Social powered {@code WebView}.
 *
 * @author Josh Long
 */
@SuppressWarnings("unchecked")
public class CrmOAuthFlowWebView extends WebView {

    /**
     * the URI to which the client will redirect with the {@code accessToken } in tow.
     */
    private String redirectUri;
    private String authenticateUri;
    private String redirectUriAccessTokenFragmentParameter = "access_token";
    private AccessTokenReceivedListener accessTokenReceivedListener;

    private WebViewClient webViewClient = new WebViewClient() {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url.startsWith((redirectUri))) {
                Uri uri = Uri.parse(url);
                String encodedFragment = uri.getEncodedFragment();
                String at = redirectUriAccessTokenFragmentParameter + "=";
                if (encodedFragment.contains(at)) {
                    String accessToken = (encodedFragment.substring((at.length())).split("&")[0]);
                    accessTokenReceivedListener.accessTokenReceived(accessToken);
                    return true;
                }
            }
            return false;
        }
    };

    public CrmOAuthFlowWebView(Context context, String authenticateUri, String redirectUri, AccessTokenReceivedListener accessTokenReceivedListener) {
        super(context);
        setup(authenticateUri, redirectUri, accessTokenReceivedListener);
        afterPropertiesSet();
    }

    public CrmOAuthFlowWebView(Context context, AttributeSet attrs, String authenticateUri, String redirectUri, AccessTokenReceivedListener accessTokenReceivedListener) {
        super(context, attrs);
        setup(authenticateUri, redirectUri, accessTokenReceivedListener);
        afterPropertiesSet();
    }

    public CrmOAuthFlowWebView(Context context, AttributeSet attrs, int defStyle, String authenticateUri, String redirectUri, AccessTokenReceivedListener accessTokenReceivedListener) {
        super(context, attrs, defStyle);
        setup(authenticateUri, redirectUri, accessTokenReceivedListener);
        afterPropertiesSet();
    }

    public CrmOAuthFlowWebView(Context context, AttributeSet attrs, int defStyle, boolean privateBrowsing, String authenticateUri, String redirectUri, AccessTokenReceivedListener accessTokenReceivedListener) {
        super(context, attrs, defStyle, privateBrowsing);
        setup(authenticateUri, redirectUri, accessTokenReceivedListener);
        afterPropertiesSet();
    }

    protected void setup(String authenticateUri, String redirectUri, AccessTokenReceivedListener accessTokenReceivedListener) {
        setAuthenticateUri(authenticateUri);
        setAccessTokenReceivedListener(accessTokenReceivedListener);
        setRedirectUri(redirectUri);
    }

    public void setAuthenticateUri(String authenticateUri) {
        this.authenticateUri = authenticateUri;
    }

    public void setRedirectUri(String redirectUri) {
        this.redirectUri = redirectUri;
    }

    public void setAccessTokenReceivedListener(AccessTokenReceivedListener atl) {
        this.accessTokenReceivedListener = atl;
    }

    protected void afterPropertiesSet() {
        setWebViewClient(this.webViewClient);
        Assert.notNull(this.redirectUri);
        Assert.notNull(this.authenticateUri);

    }

    public void noAccessToken() {
        WebSettings mWebSettings = getSettings();
        mWebSettings.setSavePassword(false);
        mWebSettings.setSaveFormData(false);

        CookieManager.getInstance().removeSessionCookie();

/*
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(false);
        cookieManager.removeAllCookie();*/

        // clearView();


        loadUrl(this.authenticateUri);
    }

    public void setRedirectUriAccessTokenFragmentParameter(String redirectUriAccessTokenFragmentParameter) {
        this.redirectUriAccessTokenFragmentParameter = redirectUriAccessTokenFragmentParameter;
    }

    /**
     * When the {@code WebView webView} receives the appropriate {@code access_token} request parameter, it calls the
     * configured {@link AccessTokenReceivedListener#accessTokenReceived(String)} accessTokenReceivedListener callback
     * method}.
     */
    public static interface AccessTokenReceivedListener {
        void accessTokenReceived(String at);
    }
}
