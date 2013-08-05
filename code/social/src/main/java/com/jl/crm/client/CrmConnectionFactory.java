package com.jl.crm.client;

import org.springframework.social.connect.*;
import org.springframework.social.connect.support.OAuth2ConnectionFactory;
import org.springframework.social.oauth2.*;

/**
 * In practice, this should <EM>always</EM> be configured using a {@link CrmServiceProvider  } and  a {@link CrmApiAdapter}.
 *
 * @author Josh Long
 **/
public class CrmConnectionFactory extends OAuth2ConnectionFactory<CrmOperations> {
	public CrmConnectionFactory(CrmServiceProvider serviceProvider, CrmApiAdapter apiAdapter) {
		super("crm", serviceProvider, apiAdapter);
	}

	public CrmConnectionFactory( OAuth2ServiceProvider<CrmOperations> serviceProvider, ApiAdapter<CrmOperations> apiAdapter) {
		super("crm", serviceProvider, apiAdapter);
	}


	@Override
	protected String extractProviderUserId(AccessGrant accessGrant) {
		CrmOperations api = ((CrmServiceProvider)getServiceProvider()).getApi(accessGrant.getAccessToken());
		UserProfile userProfile = getApiAdapter().fetchUserProfile(api);
		return userProfile.getUsername();
	}
}