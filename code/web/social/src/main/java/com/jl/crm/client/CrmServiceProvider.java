package com.jl.crm.client;


import org.springframework.social.oauth2.AbstractOAuth2ServiceProvider;
import org.springframework.social.oauth2.AccessGrant;
import org.springframework.social.oauth2.OAuth2Template;
import org.springframework.util.MultiValueMap;

/**
 * @author Josh Long
 */
public class CrmServiceProvider extends AbstractOAuth2ServiceProvider<CrmOperations> {
    private String baseUrl;

    public CrmServiceProvider(
            String baseUrl,
            String clientId,
            String consumerSecret,
            String authorizeUrl,
            String accessTokenUrl) {
        super(new CrmOAuth2Template(clientId, consumerSecret, authorizeUrl, accessTokenUrl));
        this.baseUrl = safeBaseUrl(baseUrl);
    }

    protected String safeBaseUrl(String baseUrl) {
        if (baseUrl.endsWith("/")) {
            return "" + baseUrl.subSequence(0, baseUrl.lastIndexOf("/"));
        }
        return baseUrl;
    }

    @Override
    public CrmOperations getApi(String accessToken) {
        return new CrmTemplate(accessToken, baseUrl);
    }
}

class CrmOAuth2Template extends OAuth2Template {
    public CrmOAuth2Template(String clientId, String clientSecret, String authorizeUrl, String accessTokenUrl) {
        super(clientId, clientSecret, authorizeUrl, accessTokenUrl);
        setUseParametersForClientAuthentication(true);
    }

    @Override
    public AccessGrant exchangeCredentialsForAccess(String username, String password, MultiValueMap<String, String> additionalParameters) {
        additionalParameters.remove("grant_type");
       // additionalParameters.add("grant_type", "client_credentials");
        additionalParameters.add("grant_type", "password");
        additionalParameters.remove("scope");
     //   additionalParameters.add("scope", "read");
        additionalParameters.add("scope", "write");
        return super.exchangeCredentialsForAccess(username, password, additionalParameters);
    }
}
