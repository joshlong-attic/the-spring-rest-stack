package com.jl.crm.client;

import org.springframework.social.connect.ApiAdapter;
import org.springframework.social.connect.support.OAuth2ConnectionFactory;
import org.springframework.social.oauth2.OAuth2ServiceProvider;

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
}