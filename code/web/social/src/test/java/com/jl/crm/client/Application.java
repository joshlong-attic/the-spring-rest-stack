package com.jl.crm.client;

import com.google.common.collect.Sets;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.SystemUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.SecurityAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.*;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.social.connect.*;
import org.springframework.social.connect.jdbc.JdbcUsersConnectionRepository;
import org.springframework.social.connect.support.ConnectionFactoryRegistry;
import org.springframework.social.oauth2.AccessGrant;
import org.springframework.social.oauth2.OAuth2Parameters;
import org.springframework.social.oauth2.OAuth2Template;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.io.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Exercises the CRM OAuth 2 API with our Spring Social-powered client.
 */
@ComponentScan
@EnableAutoConfiguration(exclude = SecurityAutoConfiguration.class)
public class Application {

    public static void main(String args[]) throws Throwable {

        CrmClient.ClientCallback<Void, CrmOperations> crmOperationsClientCallback =
                new CrmClient.ClientCallback<Void, CrmOperations>() {
                    @Override
                    public Void executeWithClient(Connection<CrmOperations> connection) throws Exception {

                        Log logger = LogFactory.getLog(getClass());


                        CrmOperations customerServiceOperations = connection.getApi();
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
                        return null;
                    }
                };

        String username = "joshlong", password = "cowbell",  clientId = "android-crm" ,clientSecret = "123456" ;
        String [] scopes ="read,write".split(",");


        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put("sscrm.base-url", "http://localhost:8080/"); // NB: this must end with '/'.
        properties.put("sscrm.client-id", clientId);
        properties.put("sscrm.client-secret", clientSecret);
        properties.put("sscrm.authorize-url", "oauth/authorize");    // NB: these two paths are relative.
        properties.put("sscrm.access-token-url", "oauth/token");     //

        ApplicationContext configurableApplicationContext = new SpringApplicationBuilder()
                .properties(properties)
                .sources(Application.class)
                .run(args);

        CrmClient crmClient = configurableApplicationContext.getBean(CrmClient.class);
        crmClient.doWithClient( username,  password,  scopes, crmOperationsClientCallback);

    }

}

 @Component
class CrmClient {

    static interface ClientCallback<RETURNVALUE, CLIENT> {
        RETURNVALUE executeWithClient(Connection<CLIENT> CLIENT) throws Exception;
    }

    private CrmConnectionFactory connectionFactory;
    private UsersConnectionRepository usersConnectionRepository;
    private Connection<CrmOperations> connection;

    @Autowired
    public CrmClient(CrmConnectionFactory connectionFactory,
                     UsersConnectionRepository usersConnectionRepository) {
        this.connectionFactory = connectionFactory;
        this.usersConnectionRepository = usersConnectionRepository;
    }

    public <OUT> OUT doWithClient(String username, String pw, String[] scopes, ClientCallback<OUT, CrmOperations> callable) {
        try {
            this.obtainAccessToken(username, pw);
            return callable.executeWithClient(this.connection);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    protected void curl(String tokenUrl) {
        log("tokenUrl: " + tokenUrl);
    }

    protected void log(String msg, Object... pa) {
        System.out.println(String.format(msg, pa));
    }

    // curl -X POST -v -u android-crm:123456 http://localhost:8080/oauth/token -H "Accept: application/json" -d "password=cowbell&username=joshlong&grant_type=password&scope=read%2Cwrite&client_secret=123456&client_id=android-crm"
    protected void obtainAccessToken(String username, String password, String... scopes) throws Exception {

        OAuth2Template oAuth2Operations = connectionFactory.getOAuthOperations();
        oAuth2Operations.setUseParametersForClientAuthentication(false);

        OAuth2Parameters parameters = new OAuth2Parameters();
        parameters.setScope(StringUtils.join(scopes));

        AccessGrant accessGrant = oAuth2Operations.exchangeCredentialsForAccess(username, password, parameters);
        log(ToStringBuilder.reflectionToString(accessGrant));
        connection = connectionFactory.createConnection(accessGrant);
        UserProfile userProfile = connection.fetchUserProfile();
        String userId = userProfile.getUsername();
        Set<String> userIdSet = Sets.newHashSet(userId);
        String providerId = connectionFactory.getProviderId();
        ConnectionRepository connectionRepository = this.usersConnectionRepository.createConnectionRepository(userId);
        boolean firstConnection = usersConnectionRepository.findUserIdsConnectedTo(providerId, userIdSet).size() == 0; // if there are 0 connections
        if (firstConnection) {
            connectionRepository.addConnection(this.connection);
        }
    }
}

@Configuration
class SocialClientConfiguration {


    @Bean
    CrmServiceProvider crmServiceProvider(Environment e) {
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
    JdbcUsersConnectionRepository jdbcUsersConnectionRepository(DataSource dataSource, ConnectionFactoryLocator locator) {
        return new JdbcUsersConnectionRepository(dataSource, locator, Encryptors.noOpText());
    }

    @Bean
    CrmApiAdapter crmApiAdapter() {
        return new CrmApiAdapter();
    }

    @Bean
    CrmConnectionFactory crmConnectionFactory(CrmServiceProvider crmServiceProvider, CrmApiAdapter crmApiAdapter) {
        return new CrmConnectionFactory(crmServiceProvider, crmApiAdapter);
    }

    @Bean
    @Scope(proxyMode = ScopedProxyMode.INTERFACES)
    ConnectionFactoryLocator connectionFactoryLocator(CrmConnectionFactory crmConnectionFactory) {
        ConnectionFactoryRegistry registry = new ConnectionFactoryRegistry();
        registry.addConnectionFactory(crmConnectionFactory);
        return registry;
    }

    @Bean
    @Scope(proxyMode = ScopedProxyMode.INTERFACES)
    UsersConnectionRepository usersConnectionRepository(DataSource dataSource, ConnectionFactoryLocator connectionFactoryLocator) {
        return new JdbcUsersConnectionRepository(dataSource, connectionFactoryLocator, Encryptors.noOpText());
    }

    @Bean
    @Scope(value = "prototype", proxyMode = ScopedProxyMode.INTERFACES)
    CrmOperations customerServiceOperations(ConnectionRepository connectionRepository) {
        Connection<CrmOperations> customerServiceOperations = connectionRepository.findPrimaryConnection(CrmOperations.class);
        return customerServiceOperations != null ? customerServiceOperations.getApi() : null;
    }

}