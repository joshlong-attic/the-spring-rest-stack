package com.jl.crm.client;


import org.springframework.social.oauth2.*;

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
		super(new OAuth2Template(clientId, consumerSecret, authorizeUrl, accessTokenUrl));
		this.baseUrl = safeBaseUrl(baseUrl);
	}

	protected String safeBaseUrl(String baseUrl) {
		if (baseUrl.endsWith("/")){
			return "" + baseUrl.subSequence(0, baseUrl.lastIndexOf("/")  );
		}
		return baseUrl;
	}

	@Override
	public CrmOperations getApi(String accessToken) {
		return new CrmTemplate(accessToken, baseUrl);
	}
}
