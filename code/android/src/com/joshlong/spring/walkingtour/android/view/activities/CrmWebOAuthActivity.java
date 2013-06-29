package com.joshlong.spring.walkingtour.android.view.activities;

import com.joshlong.spring.walkingtour.android.view.activities.support.AbstractWebViewActivity;

/**
 * this is designed to be the interface into the RESTful web service
 *
 * @author Josh Long
 */
public class CrmWebOAuthActivity  extends AbstractWebViewActivity {
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
