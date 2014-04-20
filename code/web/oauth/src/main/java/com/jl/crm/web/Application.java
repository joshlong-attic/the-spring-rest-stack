package com.jl.crm.web;

import com.jl.crm.services.CrmService;
import com.jl.crm.services.ServiceConfiguration;
import com.jl.crm.services.User;
import org.apache.coyote.http11.Http11NioProtocol;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.annotation.*;
import org.springframework.core.io.Resource;
import org.springframework.hateoas.config.EnableHypermediaSupport;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
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
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.MultipartConfigElement;
import java.util.Collection;

/**
 * Request OAuth authorization:
 * <code>curl -X POST -vu android-crm:123456 http://localhost:8080/oauth/token -H "Accept: application/json" -d "password=cowbell&username=joshlong&grant_type=password&scope=write&client_secret=123456&client_id=android-crm </code>
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
public class Application extends SpringBootServletInitializer {

    private static Class<Application> applicationClass = Application.class;

    public static void main(String[] args) {
        SpringApplication.run(applicationClass);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(applicationClass);
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

            if (container instanceof TomcatEmbeddedServletContainerFactory) {

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
            }
        };
    }

    @Bean
    MultipartConfigElement multipartConfigElement() {
        return new MultipartConfigElement("");
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }

    @Bean
    TextEncryptor textEncryptor() {
        return Encryptors.noOpText();
    }


    @Configuration
    @EnableWebSecurity
    static class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

        @Autowired
        private CrmService crmService;

        @Override
        protected void configure(AuthenticationManagerBuilder auth) throws Exception {
            auth.userDetailsService( userDetailsService() );
        }

        @Override
        protected UserDetailsService userDetailsService() {
            return (username) -> {
                User u = crmService.findUserByUsername(username);
                return new CrmUser(u.getId(), u.getUsername(), u.getPassword(), u.isEnabled(), u.isEnabled(), u.isEnabled(), u.isEnabled(), AuthorityUtils.createAuthorityList("USER", "write"));
            };
        }

        @Bean
        @Override
        public UserDetailsService userDetailsServiceBean() throws Exception {
            return super.userDetailsServiceBean();
        }

        @Bean
        @Override
        public AuthenticationManager authenticationManagerBean() throws Exception {
            return super.authenticationManagerBean();
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.csrf().requireCsrfProtectionMatcher (new AntPathRequestMatcher("/oauth/authorize")).disable() ;
            http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
            http.requestMatchers()
                    .and()
                    .authorizeRequests()
                    .antMatchers("/").permitAll()
                    .anyRequest().authenticated();
        }
    }

    @Configuration
    @EnableResourceServer
    @EnableAuthorizationServer
    static class OAuth2Configuration extends AuthorizationServerConfigurerAdapter {

        private final String applicationName = "crm";

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


class CrmUser extends org.springframework.security.core.userdetails.User {

    private long userId;

    public long getUserId() {
        return userId;
    }

    public CrmUser(long userId, String username, String password, boolean enabled, boolean accountNonExpired, boolean credentialsNonExpired, boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
        this.userId = userId;
    }
}