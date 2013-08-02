package com.jl.crm.client;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.SystemUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.logging.*;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.*;
import org.springframework.core.env.*;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.embedded.*;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.social.connect.*;
import org.springframework.social.connect.jdbc.JdbcUsersConnectionRepository;
import org.springframework.social.connect.support.ConnectionFactoryRegistry;
import org.springframework.social.oauth2.*;
import org.springframework.util.StringUtils;

import javax.inject.Inject;
import javax.sql.DataSource;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.URI;
import java.util.*;

/**
 * Exercises the OAuth 2 API with our Spring Social-powered client.
 *
 * @author Josh Long
 */
public class ClientExample implements InitializingBean {
	private static Log logger = LogFactory.getLog(ClientExample.class.getName());
	private CrmConnectionFactory crmConnectionFactory;
	private Environment environment;
	private Connection<CrmOperations> connection;

	public static void main(String args[]) throws Throwable {
		AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
		Map<String, Object> properties = new HashMap<String, Object>();
		properties.put("sscrm.base-url", "http://localhost:8080/"); // NB: this must end with '/'.
		properties.put("sscrm.client-id", "android-crm");
		properties.put("sscrm.client-secret", "123456");
		properties.put("sscrm.authorize-url", "oauth/authorize");    // NB: these two paths are relative.
		properties.put("sscrm.access-token-url", "oauth/token");
		MapPropertySource mapPropertySource = new MapPropertySource("oauth", properties);
		applicationContext.getEnvironment().getPropertySources().addLast(mapPropertySource);
		applicationContext.register(SocialClientConfiguration.class);
		applicationContext.refresh();
		ClientExample clientExample = applicationContext.getBean(ClientExample.class);
		clientExample.workWithClient();
	}

	public void workWithClient() throws Throwable { /* obtain the current user profile */
		UserProfile userProfile = connection.fetchUserProfile();
		logger.info("obtained connection: " + userProfile.getUsername() + ".");
		CrmOperations customerServiceOperations = connection.getApi(); /* obtain our own User */
		User self = customerServiceOperations.currentUser();
		logger.info(ToStringBuilder.reflectionToString(self)); /* obtain the current customer */
		Customer customer = customerServiceOperations.createCustomer("Nic", "Cage", new java.util.Date());
		logger.info(customer.toString()); /* loading the photo */
		ProfilePhoto profilePhoto = customerServiceOperations.getUserProfilePhoto();
		logger.info("profile photo mime type: " + profilePhoto.getMediaType().toString());
		File photoOutputFile = new File(new File(SystemUtils.getUserHome(), "Desktop"), "profile.jpg");
		InputStream byteArrayInputStream = new ByteArrayInputStream(profilePhoto.getBytes());
		OutputStream outputStream = new FileOutputStream(photoOutputFile);
		IOUtils.copy(byteArrayInputStream, outputStream);
	}

	@Inject
	public void setEnvironment(Environment e) {
		this.environment = e;
	}

	@Inject
	public void setCrmConnectionFactory(CrmConnectionFactory crmConnectionFactory) {
		this.crmConnectionFactory = crmConnectionFactory;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		String returnToUrl = environment.getProperty("sscrm.base-url") + "/";
		OAuth2Operations oAuth2Operations = crmConnectionFactory.getOAuthOperations();
		if (oAuth2Operations instanceof OAuth2Template){
			((OAuth2Template) oAuth2Operations).setUseParametersForClientAuthentication(false);
		}
		OAuth2Parameters parameters = new OAuth2Parameters();
		if (StringUtils.hasText(returnToUrl)){
			parameters.setRedirectUri(returnToUrl);
		}
		parameters.setScope("read,write");
		String authorizationUrl = oAuth2Operations.buildAuthenticateUrl(GrantType.IMPLICIT_GRANT, parameters);
		Desktop.getDesktop().browse(new URI(authorizationUrl));
		String i = JOptionPane.showInputDialog(null, "What's the 'access_token'?");
		String accessToken = i != null && !i.trim().equals("") ? i.trim() : null;
		connection = crmConnectionFactory.createConnection(new AccessGrant(accessToken));
	}
}

@Configuration
class SocialClientConfiguration {
	public static final String CRM_SOCIAL_NAME = "crm-social";
	private Log log = LogFactory.getLog(getClass());

	@Bean
	public ClientExample clientExample() {
		return new ClientExample();
	}

	@Bean
	public DataSource dataSource() {

		ClassPathResource classPathResource = new ClassPathResource("/crm-social-schema-h2.sql");

		ResourceDatabasePopulator resourceDatabasePopulator = new ResourceDatabasePopulator();
		resourceDatabasePopulator.addScript(classPathResource);

		EmbeddedDatabaseFactoryBean embeddedDatabaseFactoryBean = new EmbeddedDatabaseFactoryBean();
		embeddedDatabaseFactoryBean.setDatabasePopulator(resourceDatabasePopulator);
		embeddedDatabaseFactoryBean.setDatabaseName(CRM_SOCIAL_NAME);
		embeddedDatabaseFactoryBean.setDatabaseType(EmbeddedDatabaseType.H2);
		embeddedDatabaseFactoryBean.afterPropertiesSet();
		return embeddedDatabaseFactoryBean.getObject();

	}

	@Bean
	public CrmServiceProvider crmServiceProvider(Environment e) {
		final String propertyNameRoot = "sscrm";
		String clientId = e.getProperty(propertyNameRoot + ".client-id"), clientSecret = e.getProperty(propertyNameRoot + ".client-secret");
		String baseUrl = e.getProperty(propertyNameRoot + ".base-url"), authorizeUrl = e.getProperty(propertyNameRoot + ".authorize-url"), accessTokenUrl = e.getProperty(propertyNameRoot + ".access-token-url");
		return this.createCrmServiceProvider(clientId, clientSecret, baseUrl, authorizeUrl, accessTokenUrl);
	}

	private CrmServiceProvider createCrmServiceProvider(String clientId, String clientSecret, String baseUrl, String authorizeUrl, String accessTokenUrl) {
		log.debug(String.format("baseUrl=%s, clientSecret=%s, consumerSecret=%s, authorizeUrl=%s, accessTokenUrl=%s", baseUrl, clientId, clientSecret, authorizeUrl, accessTokenUrl));
		final String http = "http://";
		assert baseUrl != null && baseUrl.length() > 0 : "the baseUrl can't be null!";
		if (!authorizeUrl.toLowerCase().startsWith(http)){
			authorizeUrl = baseUrl + authorizeUrl;
		}
		if (!accessTokenUrl.toLowerCase().startsWith(http)){
			accessTokenUrl = baseUrl + accessTokenUrl;
		}
		return new CrmServiceProvider(baseUrl, clientId, clientSecret, authorizeUrl, accessTokenUrl);
	}

	@Bean
	public JdbcUsersConnectionRepository jdbcUsersConnectionRepository(DataSource dataSource, ConnectionFactoryLocator locator) {
		return new JdbcUsersConnectionRepository(dataSource, locator, Encryptors.noOpText());
	}

	@Bean
	public CrmApiAdapter crmApiAdapter() {
		return new CrmApiAdapter();
	}

	@Bean
	public CrmConnectionFactory crmConnectionFactory(CrmServiceProvider crmServiceProvider, CrmApiAdapter crmApiAdapter) {
		return new CrmConnectionFactory(crmServiceProvider, crmApiAdapter);
	}

	@Bean
	@Scope (proxyMode = ScopedProxyMode.INTERFACES)
	public ConnectionFactoryLocator connectionFactoryLocator(CrmConnectionFactory crmConnectionFactory) {
		ConnectionFactoryRegistry registry = new ConnectionFactoryRegistry();
		registry.addConnectionFactory(crmConnectionFactory);
		return registry;
	}

	@Bean
	@Scope (proxyMode = ScopedProxyMode.INTERFACES)
	public UsersConnectionRepository usersConnectionRepository(DataSource dataSource, ConnectionFactoryLocator connectionFactoryLocator) {
		return new JdbcUsersConnectionRepository(dataSource, connectionFactoryLocator, Encryptors.noOpText());
	}

	@Bean
	@Scope (value = "prototype", proxyMode = ScopedProxyMode.INTERFACES)
	public ConnectionRepository connectionRepository(UsersConnectionRepository usersConnectionRepository) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null){
			throw new IllegalStateException("Unable to get a " + ConnectionRepository.class.getName() + ": no user signed in via Spring Security. Please fix this!");
		}
		return usersConnectionRepository.createConnectionRepository(authentication.getName());
	}

	@Bean
	@Scope (value = "request", proxyMode = ScopedProxyMode.INTERFACES)
	public CrmOperations customerServiceOperations(ConnectionRepository connectionRepository) {
		Connection<CrmOperations> customerServiceOperations = connectionRepository.findPrimaryConnection(CrmOperations.class);
		return customerServiceOperations != null ? customerServiceOperations.getApi() : null;
	}
}