package com.jl.crm.web;

import com.jl.crm.services.ServiceConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.data.rest.webmvc.config.RepositoryRestMvcConfiguration;
import org.springframework.hateoas.config.EnableHypermediaSupport;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.authentication.configurers.InMemoryClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.OAuth2ServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.OAuth2ServerConfigurer;
import org.springframework.security.oauth2.provider.token.JdbcTokenStore;
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher;
import org.springframework.security.web.util.matcher.NegatedRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.accept.ContentNegotiationStrategy;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import javax.inject.Inject;
import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletRegistration;
import javax.sql.DataSource;
import java.io.File;

/**
 * In conjunction with {@link CrmSecurityApplicationInitializer}, this configuration class sets up Spring Data REST, In
 * conjunction with {@link CrmWebApplicationInitializer}, this configuration class sets up Spring Data REST, Spring MVC,
 * Spring Security and Spring Security OAuth, along with importing all of our existing service implementations.
 *
 * @author Josh Long
 * @see CrmSecurityApplicationInitializer
 */
public class CrmWebApplicationInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {

    private int maxUploadSizeInMb = 5 * 1024 * 1024; // 5 MB

    @Override
    protected Class<?>[] getRootConfigClasses() {
        return new Class<?>[]{ServiceConfiguration.class};
    }

    @Override
    protected Class<?>[] getServletConfigClasses() {
        return new Class<?>[]{RepositoryRestMvcConfiguration.class, WebMvcConfiguration.class, SecurityConfiguration.class};
    }

    @Override
    protected String[] getServletMappings() {
        return new String[]{"/"};
    }

    @Override
    protected void customizeRegistration(ServletRegistration.Dynamic registration) {
        File uploadDirectory = ServiceConfiguration.CRM_STORAGE_UPLOADS_DIRECTORY;
        MultipartConfigElement multipartConfigElement = new MultipartConfigElement(uploadDirectory.getAbsolutePath(), maxUploadSizeInMb, maxUploadSizeInMb * 2, maxUploadSizeInMb / 2);
        registration.setMultipartConfig(multipartConfigElement);
    }

}

@Configuration
@EnableWebSecurity
@Order(1)
class OAuth2ServerConfiguration extends OAuth2ServerConfigurerAdapter {

    private final String applicationName = ServiceConfiguration.CRM_NAME;

    @Inject
    private DataSource dataSource;

    @Inject
    private UserDetailsService userDetailsService;

    @Inject
    private ContentNegotiationStrategy contentNegotiationStrategy;

    // @formatter:off
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .userDetailsService(userDetailsService)
                .and()
                .apply(new InMemoryClientDetailsServiceConfigurer())
                .withClient("android-crm")
                .resourceIds(applicationName)
                .scopes("read", "write")
                .authorities("ROLE_USER")
                .authorizedGrantTypes("authorization_code", "implicit", "password")
                .secret("123456")
                .and()
                .withClient("ios-crm")
                .resourceIds(applicationName)
                .scopes("read", "write")
                .authorities("ROLE_USER")
                .authorizedGrantTypes("password")
                .secret("123456");

    }
    // @formatter:on

    // @formatter:off
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .requestMatchers()
                .requestMatchers(oauthRequestMatcher())
                .and()
                .authorizeRequests()
                .antMatchers("/favicon.ico").permitAll()
                .anyRequest().authenticated()
                .and()
                .apply(new OAuth2ServerConfigurer())
                .tokenStore(new JdbcTokenStore(this.dataSource))
                .resourceId(applicationName);
    }
    // @formatter:on

    @Bean
    public RequestMatcher oauthRequestMatcher() {
      /*  MediaTypeRequestMatcher mediaTypeRequestMatcher =
                new MediaTypeRequestMatcher(contentNegotiationStrategy, MediaType.APPLICATION_JSON, new MediaType("image", "*"));
        Set<MediaType> mediaTypes = new HashSet<MediaType>();
        mediaTypes.add(MediaType.ALL);
        mediaTypes.add( new MediaType("image","webp"));
        mediaTypeRequestMatcher.setIgnoredMediaTypes( mediaTypes);
        return mediaTypeRequestMatcher;*/

        // oauth is complicated
        // how do i detect when it's a browser? when it's not?
        // if a native REST client makes a call and submits a Accept: */*
        MediaTypeRequestMatcher mediaTypeRequestMatcher = new MediaTypeRequestMatcher( this.contentNegotiationStrategy, MediaType.TEXT_HTML);
        return new NegatedRequestMatcher( mediaTypeRequestMatcher);

    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }

    @Bean
    public TextEncryptor textEncryptor() {
        return Encryptors.noOpText();
    }
}


@Configuration
@EnableWebSecurity
//@EnableGlobalMethodSecurity(prePostEnabled = true)
class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Inject
    private UserDetailsService userDetailsService;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService);
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    // @formatter:off
    @Override
    public void configure(WebSecurity web) throws Exception {
        web
                .ignoring()
                .antMatchers("/h2/**"); // h2 has its own security
    }
    // @formatter:on

    // @formatter:off
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // nb: the H2 administration console should *not* be left exposed.
        // comment out the mapping path below so that it requires an authentication to see it.
        String[] filesToLetThroughUnAuthorized =
                {
                        H2EmbeddedDatbaseConsoleInitializer.H2_DATABASE_CONSOLE_MAPPING,
                        "/favicon.ico"
                };

        http
                .authorizeRequests()
                .antMatchers(filesToLetThroughUnAuthorized).permitAll()
                // .antMatchers("/users/*").denyAll()
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .loginPage("/crm/signin.html")
                .defaultSuccessUrl("/crm/welcome.html")
                .usernameParameter("username")
                .passwordParameter("password")
                .permitAll()
                .and()
                .logout()
                .logoutUrl("/signout")
                .permitAll();
    }
    // @formatter:on
}

@Configuration
@ComponentScan
@EnableHypermediaSupport
@EnableWebMvc
class WebMvcConfiguration extends WebMvcConfigurationSupport {

    @Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
        configurer.enable();
    }

    @Bean
    public MultipartResolver multipartResolver() {
        return new StandardServletMultipartResolver();
    }

    /** This application renders Spring Security UI pages to support logging into, and out of, the application. */
    @Bean
    public ViewResolver internalResourceViewResolver() {
        InternalResourceViewResolver internalResourceViewResolver = new InternalResourceViewResolver();
        internalResourceViewResolver.setPrefix("/WEB-INF/crm/");
        internalResourceViewResolver.setSuffix(".jsp");
        return internalResourceViewResolver;
    }

}
