package com.jl.crm.client;

import org.springframework.social.connect.support.OAuth2ConnectionFactory;

/** @author Josh Long */
public class CrmConnectionFactory extends OAuth2ConnectionFactory<CrmOperations> {
	public CrmConnectionFactory(CrmServiceProvider serviceProvider, CrmApiAdapter apiAdapter) {
		super("crm", serviceProvider, apiAdapter);
	}
}