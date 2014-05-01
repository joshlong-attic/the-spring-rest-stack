package com.jl.crm.web;

import com.jl.crm.services.CrmService;
import com.jl.crm.services.Customer;
import com.jl.crm.services.ServiceConfiguration;
import com.jl.crm.services.User;
import org.apache.coyote.http11.Http11NioProtocol;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.context.annotation.*;
import org.springframework.core.io.Resource;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.ResourceAssembler;
import org.springframework.hateoas.config.EnableHypermediaSupport;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configurers.GlobalAuthenticationConfigurerAdapter;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;

import javax.servlet.MultipartConfigElement;
import java.util.ArrayList;
import java.util.Collection;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

/**
 * Request OAuth authorization:
 * <code>
 * curl -X POST -vu android-crm:123456 http://localhost:8080/oauth/token -H "Accept: application/json" -d "password=cowbell&username=joshlong&grant_type=password&scope=write&client_secret=123456&client_id=android-crm"
 * </code>
 * <p/>
 * Use the access_token returned in the previous request to make the authorized request to the protected endpoint:
 * <p/>
 * <code>curl http://localhost:8080/users/5 -H "Authorization: Bearer <INSERT TOKEN>"</code>
 *
 * @author Josh Long
 */
@ComponentScan
@Import(ServiceConfiguration.class)
@EnableHypermediaSupport(type = EnableHypermediaSupport.HypermediaType.HAL)
@EnableAutoConfiguration
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class);
    }


    // you can run this with SSL/TLS. For example, build the application (`mvn clean install`) in the `oauth` directory, then run:
    //   java -Dspring.profiles.active=production -Dkeystore.file=file:///`pwd`/src/main/resources/keystore.p12 -jar target/oauth-1.0.0.BUILD-SNAPSHOT.jar
    @Bean
    @Profile("production")
    EmbeddedServletContainerCustomizer containerCustomizer(
            @Value("${keystore.file}") Resource keystoreFile,
            @Value("${keystore.pass}") String keystorePass) throws Exception {

        String absoluteKeystoreFile = keystoreFile.getFile().getAbsolutePath();

        return (ConfigurableEmbeddedServletContainer container) -> {
            TomcatEmbeddedServletContainerFactory tomcat = (TomcatEmbeddedServletContainerFactory) container;
            tomcat.addConnectorCustomizers(
                    (connector) -> {
                        connector.setPort(8443);
                        connector.setSecure(true);
                        connector.setScheme("https");

                        Http11NioProtocol proto = (Http11NioProtocol) connector.getProtocolHandler();
                        proto.setSSLEnabled(true);
                        proto.setKeystoreFile(absoluteKeystoreFile);
                        proto.setKeystorePass(keystorePass);
                        proto.setKeystoreType("PKCS12");
                        proto.setKeyAlias("tomcat");
                    }
            );

        };
    }

    @Bean
    MultipartConfigElement multipartConfigElement() {
        return new MultipartConfigElement("");
    }

    @Bean
    ResourceAssembler<User, org.springframework.hateoas.Resource<User>> userResourceAssembler() {
        return (u) -> {
            try {
                String customersRel = "customers", photoRel = "photo";
                User user = new User(u);
                user.setPassword(null);
                long userId = user.getId();
                Collection<Link> links = new ArrayList<>();
                links.add(linkTo(methodOn(UserController.class).loadUser(userId)).withSelfRel());
                links.add(linkTo(methodOn(UserController.class).loadUserCustomers(userId)).withRel(customersRel));
                links.add(linkTo(methodOn(UserProfilePhotoController.class).loadUserProfilePhoto(user.getId())).withRel(photoRel));
                return new org.springframework.hateoas.Resource<>(user, links);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
    }

    @Bean
    ResourceAssembler<Customer, org.springframework.hateoas.Resource<Customer>> customerResourceResourceAssembler() {
        return (customer) -> {
            String usersRel = "user";
            Class<UserController> controllerClass = UserController.class;
            Long userId = customer.getUser().getId();
            customer.setUser(null);
            org.springframework.hateoas.Resource<Customer> customerResource = new org.springframework.hateoas.Resource<>(customer);
            customerResource.add(linkTo(methodOn(controllerClass).loadSingleUserCustomer(userId, customer.getId())).withSelfRel());
            customerResource.add(linkTo(methodOn(controllerClass).loadUser(userId)).withRel(usersRel));
            return customerResource;
        };
    }

    @Configuration
    static class WebSecurityConfiguration extends GlobalAuthenticationConfigurerAdapter {

        @Autowired
        private CrmService crmService;

        @Override
        public void init(AuthenticationManagerBuilder auth) throws Exception {
            auth.userDetailsService(userDetailsService());
        }

        protected UserDetailsService userDetailsService() {
            return (username) -> {
                User u = crmService.findUserByUsername(username);
                return new org.springframework.security.core.userdetails.User(
                        u.getUsername(), u.getPassword(), u.isEnabled(),
                        u.isEnabled(), u.isEnabled(), u.isEnabled(),
                        AuthorityUtils.createAuthorityList("USER", "write"));
            };
        }

    }


    @Configuration
    @EnableResourceServer
    @EnableAuthorizationServer
    static class OAuth2Configuration extends AuthorizationServerConfigurerAdapter {

        private final String applicationName = "crm";

        /**
         * This is required for password grants, which we specify below as one of the  {@literal authorizedGrantTypes()}.
         */
        @Autowired
        private AuthenticationManager authenticationManager;

        @Override
        public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
            endpoints.authenticationManager(authenticationManager);
        }

        @Override
        public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
            clients.inMemory()
                    .withClient("android-crm")
                    .authorizedGrantTypes("password", "authorization_code", "refresh_token")
                    .authorities("ROLE_USER")
                    .scopes("write")
                    .resourceIds(applicationName)
                    .secret("123456");
        }
    }
}
