package com.jl.crm.web;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.oauth2.provider.*;
import org.springframework.security.oauth2.provider.client.ClientDetailsUserDetailsService;

import javax.sql.DataSource;

/**
 * Configures a Spring Security OAuth {@link JdbcClientDetailsService}. Depends on the SQL schema defined in {@code
 * crm-schema-(h2|postgresql).sql}.
 *
 * @author Josh Long
 */
public class JdbcClientDetailsServiceConfigurer extends SecurityConfigurerAdapter<AuthenticationManager, AuthenticationManagerBuilder> {

	private DataSource dataSource;

	public JdbcClientDetailsServiceConfigurer(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	@Override
	public void init(AuthenticationManagerBuilder builder) throws Exception {
		JdbcClientDetailsService clientDetailsService = new JdbcClientDetailsService(dataSource);

		ClientDetailsUserDetailsService userDetailsService = new ClientDetailsUserDetailsService(clientDetailsService);

		builder.userDetailsService(userDetailsService);
		builder.setSharedObject(ClientDetailsService.class, clientDetailsService);
	}

}
