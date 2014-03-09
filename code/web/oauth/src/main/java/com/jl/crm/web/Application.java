package com.jl.crm.web;

import com.jl.crm.services.ServiceConfiguration;
import org.apache.catalina.connector.Connector;
import org.apache.coyote.http11.Http11NioProtocol;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.context.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.annotation.*;
import org.springframework.core.io.Resource;
import org.springframework.hateoas.config.EnableHypermediaSupport;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.OAuth2ResourceServerConfigurer;

import javax.servlet.MultipartConfigElement;
import java.io.IOException;


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
    @Profile("production")
    @Bean
    public EmbeddedServletContainerCustomizer containerCustomizer(
            @Value("${keystore.file}") final Resource keystoreFile,
            @Value("${keystore.alias}") final String keystoreAlias,
            @Value("${keystore.type}") final String keystoreType,
            @Value("${keystore.pass}") final String keystorePass,
            @Value("${tls.port}") final int tlsPort
    ) {
        return new EmbeddedServletContainerCustomizer() {


            @Override
            public void customize(ConfigurableEmbeddedServletContainer factory) {
                if (factory instanceof TomcatEmbeddedServletContainerFactory) {
                    TomcatEmbeddedServletContainerFactory containerFactory = (TomcatEmbeddedServletContainerFactory) factory;
                    containerFactory.addConnectorCustomizers(new TomcatConnectorCustomizer() {

                        @Override
                        public void customize(Connector connector) {

                            connector.setPort(tlsPort);
                            connector.setSecure(true);
                            connector.setScheme("https");
                            connector.setAttribute("keyAlias", "tomcat");
                            connector.setAttribute("keystorePass", "password");
                            String absoluteKeystoreFile;
                            try {
                                absoluteKeystoreFile = keystoreFile.getFile().getAbsolutePath();
                                connector.setAttribute("keystoreFile", absoluteKeystoreFile);
                            } catch (IOException e) {
                                throw new IllegalStateException("Cannot load keystore", e);
                            }
                            connector.setAttribute("clientAuth", "false");
                            connector.setAttribute("sslProtocol", "TLS");
                            connector.setAttribute("SSLEnabled", true);

                            Http11NioProtocol proto = (Http11NioProtocol) connector.getProtocolHandler();
                            proto.setSSLEnabled(true);
                            // proto.setClientAuth();
                            // uncomment this to require the
                            // client to authenticate. Then, you can use X509 support in Spring Security
                            proto.setKeystoreFile(absoluteKeystoreFile);
                            proto.setKeystorePass(keystorePass);
                            proto.setKeystoreType(keystoreType);
                            proto.setKeyAlias(keystoreAlias);
                        }
                    });

                }
            }
        };
    }

    @Bean
    MultipartConfigElement multipartConfigElement() {
        return new MultipartConfigElement("");
    }

}

/**
 * <p/>
 * Request OAuth authorization:
 * {@literal curl -X POST -vu android-crm:123456 http://localhost:8080/oauth/token -H "Accept: application/json" -d "password=cowbell&username=joshlong&grant_type=client_credentials&scope=read write&client_secret=123456&client_id=android-crm"}.
 *
 * Use the {@code access_token} returned in the previous request to make the authorized
 * request to the protected endpoint: {@literal curl http://localhost:8080/users/5 -H "Authorization: Bearer <INSERT TOKEN>"}
 *
 */


@Configuration
@EnableResourceServer
class ResourceServer extends ResourceServerConfigurerAdapter {

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.antMatcher("/**").authorizeRequests().anyRequest().authenticated();
    }

    @Override
    public void configure(OAuth2ResourceServerConfigurer resources) throws Exception {
        resources.resourceId("crm");
    }

}

@Configuration
@EnableAuthorizationServer
class OAuth2Config extends AuthorizationServerConfigurerAdapter {

    // config information
    final String[] scopes = "read,write".split(",");
    final String secret = "123456";
    final String authorizedGrantTypes = "password";
    final String[] authorities = {"ROLE_USER"};

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        // @formatter:off

        clients.inMemory()
                .withClient( "android-crm")
                .authorities(authorities)
                .scopes(scopes)
                .secret(secret)
                .authorizedGrantTypes( "password","client_credentials")
                .resourceIds("crm")
        ;




        // @formatter:on
    }

}

