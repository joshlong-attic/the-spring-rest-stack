package com.jl.crm.client;

import com.google.common.collect.Sets;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.SystemUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.*;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseFactoryBean;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.social.connect.*;
import org.springframework.social.connect.jdbc.JdbcUsersConnectionRepository;
import org.springframework.social.connect.support.ConnectionFactoryRegistry;
import org.springframework.social.oauth2.AccessGrant;
import org.springframework.social.oauth2.GrantType;
import org.springframework.social.oauth2.OAuth2Parameters;
import org.springframework.social.oauth2.OAuth2Template;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.URI;
import java.sql.Driver;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Exercises the CRM OAuth 2 API with our Spring Social-powered client.
 *
 * @author Josh Long
 */
public class ClientExample implements InitializingBean {
    private static Log logger = LogFactory.getLog(ClientExample.class.getName());
    private CrmConnectionFactory crmConnectionFactory;
    private Environment environment;
    private UsersConnectionRepository usersConnectionRepository;
    private Connection<CrmOperations> connection;

    public ClientExample(CrmConnectionFactory crmConnectionFactory, Environment environment, UsersConnectionRepository usersConnectionRepository) {
        this.crmConnectionFactory = crmConnectionFactory;
        this.environment = environment;
        this.usersConnectionRepository = usersConnectionRepository;
    }

    public static void main(String args[]) throws Throwable {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put("sscrm.base-url", "http://localhost:8080/"); // NB: this must end with '/'.
        properties.put("sscrm.client-id", "android-crm");
        properties.put("sscrm.client-secret", "123456");
        properties.put("sscrm.authorize-url", "oauth/authorize");    // NB: these two paths are relative.
        properties.put("sscrm.access-token-url", "oauth/token");     //
        MapPropertySource mapPropertySource = new MapPropertySource("oauth", properties);
        applicationContext.getEnvironment().getPropertySources().addLast(mapPropertySource);
        applicationContext.register(SocialClientConfiguration.class);
        applicationContext.refresh();
        ClientExample clientExample = applicationContext.getBean(ClientExample.class);
        clientExample.workWithClient();
    }

    public void workWithClient() throws Throwable {
        // return our API-specific binding
        CrmOperations customerServiceOperations = connection.getApi(); /* obtain our own User */

        // obtain the current user profile from the Spring Social API
        UserProfile userProfile = connection.fetchUserProfile();
        logger.info("obtained connection: " + userProfile.getUsername() + ".");


        // fetch the current (CRM-specific) user profile identity
        User self = customerServiceOperations.currentUser();
        logger.info(ToStringBuilder.reflectionToString(self)); /* obtain the current customer */

        // add a customer record under the user
        Customer customer = customerServiceOperations.createCustomer("Nic", "Cage", new java.util.Date());
        logger.info(customer.toString()); /* loading the photo */

        // check to see what the profile photo is right now.
        ProfilePhoto profilePhoto = customerServiceOperations.getUserProfilePhoto();
        logger.info("profile photo mime type: " + profilePhoto.getMediaType().toString());

        // save the current profile photo to the desktop
        File photoOutputFile = new File(new File(SystemUtils.getUserHome(), "Desktop"), "profile.jpg");
        InputStream byteArrayInputStream = new ByteArrayInputStream(profilePhoto.getBytes());
        OutputStream outputStream = new FileOutputStream(photoOutputFile);
        IOUtils.copy(byteArrayInputStream, outputStream);

        // run a query against the current customer records
        String query = "josh";
        Collection<Customer> customerCollection = customerServiceOperations.search(query);
        for (Customer c : customerCollection) {
            logger.debug("searched for '" + query + "', found: " + c.toString());
        }

        // let's finally update the profile photo
        ClassPathResource classPathResource = new ClassPathResource("/s2-logo.jpg");
        InputStream readEmAll = classPathResource.getInputStream();
        byte[] profilePhotoBytes = IOUtils.toByteArray(readEmAll);
        customerServiceOperations.setProfilePhoto(profilePhotoBytes, MediaType.IMAGE_JPEG);
    }

    @Override
    public void afterPropertiesSet() throws Exception {

        // build up the OAuth2Parameters
        String returnToUrl = environment.getProperty("sscrm.base-url") + "crm/welcome.html";
        OAuth2Template oAuth2Operations = crmConnectionFactory.getOAuthOperations();
        oAuth2Operations.setUseParametersForClientAuthentication(false);

        OAuth2Parameters parameters = new OAuth2Parameters();
        parameters.setScope("read,write");
        if (StringUtils.hasText(returnToUrl)) {
            parameters.setRedirectUri(returnToUrl);
        }

        // figure out what the OAuth "authorize" endpoint should be and open it with
        // the system's default HTTP browser
        String authorizationUrl = oAuth2Operations.buildAuthenticateUrl(GrantType.IMPLICIT_GRANT, parameters);
        Desktop.getDesktop().browse(new URI(authorizationUrl));

        // the authorizationUrl above will have at a minimum, prompted an authenticated user to approve
        // certain permissions. After the approval, it will return with an `access_token` parameter
        // we must provide that `access_token` here.
        String i = JOptionPane.showInputDialog(null, "What's the 'access_token'?");
        String accessToken = i != null && !i.trim().equals("") ? i.trim() : null;


        // we have a live connection
        AccessGrant accessGrant = new AccessGrant(accessToken);
        connection = crmConnectionFactory.createConnection(accessGrant);

        UserProfile userProfile = connection.fetchUserProfile();

        String userId = userProfile.getUsername();
        Set<String> userIdSet = Sets.newHashSet(userId);
        String providerId = crmConnectionFactory.getProviderId();

        // find out whether we've already connected before.

        ConnectionRepository connectionRepository = this.usersConnectionRepository.createConnectionRepository(userId);
        boolean hasThisUserConnectedToThisServiceProviderBefore = usersConnectionRepository.findUserIdsConnectedTo(providerId, userIdSet).size() == 0;
        if (hasThisUserConnectedToThisServiceProviderBefore) {
            // If not, save this connection information
            // in theory we could look this up simply by persisting the userId in some sort of client-side storage
            connectionRepository.addConnection(this.connection);
        }
    }

}

@ComponentScan
@PropertySource("classpath:config.properties")
@Configuration
class SocialClientConfiguration {

    public static final String CRM_SOCIAL_NAME = "crm-social";

    @Bean
    public ClientExample clientExample(CrmConnectionFactory crmConnectionFactory, Environment environment, UsersConnectionRepository usersConnectionRepository) {
        return new ClientExample(crmConnectionFactory, environment, usersConnectionRepository);
    }

    @Bean
    public CrmServiceProvider crmServiceProvider(Environment e) {
        final String propertyNameRoot = "sscrm";
        String clientId = e.getProperty(propertyNameRoot + ".client-id");
        String clientSecret = e.getProperty(propertyNameRoot + ".client-secret");
        String baseUrl = e.getProperty(propertyNameRoot + ".base-url");
        String authorizeUrl = e.getProperty(propertyNameRoot + ".authorize-url");
        String accessTokenUrl = e.getProperty(propertyNameRoot + ".access-token-url");
        final String http = "http://";
        assert baseUrl != null && baseUrl.length() > 0 : "the baseUrl can't be null!";
        if (!authorizeUrl.toLowerCase().startsWith(http)) {
            authorizeUrl = baseUrl + authorizeUrl;
        }
        if (!accessTokenUrl.toLowerCase().startsWith(http)) {
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
    @Scope(proxyMode = ScopedProxyMode.INTERFACES)
    public ConnectionFactoryLocator connectionFactoryLocator(CrmConnectionFactory crmConnectionFactory) {
        ConnectionFactoryRegistry registry = new ConnectionFactoryRegistry();
        registry.addConnectionFactory(crmConnectionFactory);
        return registry;
    }

    @Bean
    @Scope(proxyMode = ScopedProxyMode.INTERFACES)
    public UsersConnectionRepository usersConnectionRepository(DataSource dataSource, ConnectionFactoryLocator connectionFactoryLocator) {
        return new JdbcUsersConnectionRepository(dataSource, connectionFactoryLocator, Encryptors.noOpText());
    }

    @Bean
    @Scope(value = "prototype", proxyMode = ScopedProxyMode.INTERFACES)
    public CrmOperations customerServiceOperations(ConnectionRepository connectionRepository) {
        Connection<CrmOperations> customerServiceOperations = connectionRepository.findPrimaryConnection(CrmOperations.class);
        return customerServiceOperations != null ? customerServiceOperations.getApi() : null;
    }

}

@Configuration
@Profile({"default", "test"})
class EmbeddedDataSourceConfiguration {
    @Bean
    public DataSource dataSource() {

        ClassPathResource classPathResource = new ClassPathResource("/crm-social-schema-h2.sql");

        ResourceDatabasePopulator resourceDatabasePopulator = new ResourceDatabasePopulator();
        resourceDatabasePopulator.addScript(classPathResource);

        EmbeddedDatabaseFactoryBean embeddedDatabaseFactoryBean = new EmbeddedDatabaseFactoryBean();
        embeddedDatabaseFactoryBean.setDatabasePopulator(resourceDatabasePopulator);
        embeddedDatabaseFactoryBean.setDatabaseName(SocialClientConfiguration.CRM_SOCIAL_NAME);
        embeddedDatabaseFactoryBean.setDatabaseType(EmbeddedDatabaseType.H2);
        embeddedDatabaseFactoryBean.afterPropertiesSet();
        return embeddedDatabaseFactoryBean.getObject();

    }

}

@Configuration
@Profile({"production"})
class ProductionDataSourceConfiguration {

    @Bean
    public DataSource dataSource(Environment env) {
        SimpleDriverDataSource dataSource = new SimpleDriverDataSource();
        dataSource.setDriverClass(env.getPropertyAsClass("dataSource.driverClass", Driver.class));
        dataSource.setUrl(env.getProperty("dataSource.url").trim());
        dataSource.setUsername(env.getProperty("dataSource.user").trim());
        dataSource.setPassword(env.getProperty("dataSource.password").trim());
        return dataSource;
    }
}

