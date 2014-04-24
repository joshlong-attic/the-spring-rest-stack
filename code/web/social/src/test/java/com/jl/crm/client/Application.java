package com.jl.crm.client;

import com.google.common.collect.Sets;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.SecurityAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.*;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.social.connect.*;
import org.springframework.social.connect.jdbc.JdbcUsersConnectionRepository;
import org.springframework.social.connect.support.ConnectionFactoryRegistry;
import org.springframework.social.oauth2.AccessGrant;
import org.springframework.social.oauth2.OAuth2Parameters;
import org.springframework.social.oauth2.OAuth2Template;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Exercises the CRM OAuth 2 API with our Spring Social-powered client.
 */
@ComponentScan
@EnableAutoConfiguration(exclude = SecurityAutoConfiguration.class)
public class Application {

    public static void log(String msg, Object... parms) {
        System.out.println(String.format(msg, parms));
    }

    public static void handle(Connection<CrmOperations> clientConnection) {
        CrmOperations customerServiceOperations = clientConnection.getApi();

        // obtain the current user profile from the Spring Social API
        UserProfile userProfile = clientConnection.fetchUserProfile();
        log("obtained connection: " + userProfile.getUsername() + ".");

        // fetch the current (CRM-specific) user profile identity
        User self = customerServiceOperations.user(5L);
        log(ToStringBuilder.reflectionToString(self));
    }

    public static void main(String args[]) throws Throwable {

        String username = "joshlong",
                password = "cowbell",
                clientId = "android-crm",
                clientSecret = "123456";
        String[] scopes = "read,write".split(",");


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
        crmClient.doWithClient(username, password, scopes, new CrmClient.ClientCallback<Void, CrmOperations>() {
            @Override
            public Void executeWithClient(Connection<CrmOperations> clientConnection) throws Exception {
                handle(clientConnection);
                return null;
            }
        });

    }

}

@Component
class CrmClient {

    static interface ClientCallback<RETURNVALUE, CLIENT> {
        RETURNVALUE executeWithClient(Connection<CLIENT> clientConnection) throws Exception;
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

            OAuth2Template oAuth2Operations = connectionFactory.getOAuthOperations();
            oAuth2Operations.setUseParametersForClientAuthentication(false);

            OAuth2Parameters parameters = new OAuth2Parameters();
            parameters.setScope(StringUtils.join(scopes));

            AccessGrant accessGrant = oAuth2Operations.exchangeCredentialsForAccess(username, pw, parameters);
            connection = connectionFactory.createConnection(accessGrant);
            UserProfile userProfile = connection.fetchUserProfile();
            String userId = userProfile.getUsername();
            Set<String> userIdSet = Sets.newHashSet(userId);
            String providerId = connectionFactory.getProviderId();
            ConnectionRepository connectionRepository = this.usersConnectionRepository.createConnectionRepository(userId);
            boolean firstConnection = usersConnectionRepository.findUserIdsConnectedTo(providerId, userIdSet).size() == 0; // if there are 0 connections
            if (firstConnection)
                connectionRepository.addConnection(this.connection);

            return callable.executeWithClient(this.connection);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


}

@Configuration
class CrmClientConfiguration {


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