package com.jl.crm.web;

import javax.servlet.MultipartConfigElement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.SpringBootWebSecurityConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.rest.webmvc.config.RepositoryRestMvcConfiguration;
import org.springframework.hateoas.config.EnableHypermediaSupport;
import org.springframework.hateoas.hal.DefaultCurieProvider;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.authentication.configurers.InMemoryClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.authentication.configurers.InMemoryClientDetailsServiceConfigurer.ClientBuilder;
import org.springframework.security.oauth2.config.annotation.web.configuration.OAuth2ServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.OAuth2ServerConfigurer;
import org.springframework.security.oauth2.provider.token.InMemoryTokenStore;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.jl.crm.services.CrmService;
import com.jl.crm.services.ServiceConfiguration;

@ComponentScan
@EnableAutoConfiguration(exclude = {SpringBootWebSecurityConfiguration.class})
public class Application   {

    public static final String APPLICATION_NAME = "crm";

    private static Class<Application> applicationClass = Application.class;
 
    public static void main(String[] args) {
        SpringApplication.run(applicationClass);
    }
}

@Configuration
@Import({ServiceConfiguration.class, RepositoryRestMvcConfiguration.class})
@EnableWebMvc
@EnableHypermediaSupport(type = EnableHypermediaSupport.HypermediaType.HAL)
class WebMvcConfiguration {

    String curieNamespace = com.jl.crm.web.Application.APPLICATION_NAME;

    @Bean
    MultipartConfigElement multipartConfigElement() {
        return new MultipartConfigElement("");
    }

    @Bean
    MultipartResolver multipartResolver() {
        return new StandardServletMultipartResolver();
    }

    @Bean
    DefaultCurieProvider defaultCurieProvider() {
        org.springframework.hateoas.UriTemplate template = new org.springframework.hateoas.UriTemplate(
                "http://localhost:8080/rels/{rel}");
        return new DefaultCurieProvider(curieNamespace, template);
    }
}
/** 
 * <code>
 * curl -X POST -vu android-crm:123456 http://localhost:8080/oauth/token -H "Accept: application/json" -d "password=cowbell&username=joshlong&grant_type=password&scope=read%2Cwrite&client_secret=123456&client_id=android-crm
 * </code>
 */
@Configuration
@EnableWebSecurity
class WebSecurityConfig extends OAuth2ServerConfigurerAdapter {

    private final String applicationName = com.jl.crm.web.Application.APPLICATION_NAME;

    @Autowired
    private CrmService crmService;
	
    // @formatter:off
    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.csrf().disable();
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http.requestMatchers()
                .and()
                .authorizeRequests()
                .antMatchers("/").permitAll()
                .anyRequest().authenticated()
                .and()
                .apply(new OAuth2ServerConfigurer())
                .tokenStore(new InMemoryTokenStore())
                .resourceId(applicationName);

    }
    // @formatter:on
 
    
    @Override
    protected void configure(AuthenticationManagerBuilder authManagerBuilder)
            throws Exception {
        
    	final String scopes[] ="read,write".split(",");
    	final String secret = "123456" ; 
    	final String  authorizedGrantTypes = "password"; 
    	final String authorities  = "ROLE_USER";
    	
    	authManagerBuilder
    	.userDetailsService( new CrmUserDetailsService( this.crmService))
    		.and()
		.apply(new InMemoryClientDetailsServiceConfigurer())
			.withClient("android-crm")
			.resourceIds(applicationName)
			.scopes(scopes)
			.authorities(authorities)
			.authorizedGrantTypes( authorizedGrantTypes)
			.secret(secret)
		.and()
			.withClient("ios-crm")
			.resourceIds(applicationName)
			.scopes(scopes)
			.authorities(authorities)
			.authorizedGrantTypes(authorizedGrantTypes)
			.secret(secret);

        }
        // @formatter:on

        
         

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

    @Bean
    PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }

    @Bean
    TextEncryptor textEncryptor() {
        return Encryptors.noOpText();
    }
}

/*
 * 
 * @Configuration
 * 
 * @EnableWebMvcSecurity class MvcSecurityConfiguration extends
 * WebSecurityConfigurerAdapter {
 * 
 * @Autowired CrmService crmService;
 * 
 * String applicationName = "crm";
 * 
 * @Override
 * 
 * @Bean protected UserDetailsService userDetailsService() { return new
 * CrmUserDetailsService(this.crmService); }
 * 
 * @Override
 * 
 * @Bean public AuthenticationManager authenticationManagerBean() throws
 * Exception { return super.authenticationManagerBean(); }
 * 
 * @Bean PasswordEncoder passwordEncoder() { return
 * NoOpPasswordEncoder.getInstance(); }
 * 
 * @Bean TextEncryptor textEncryptor() { return Encryptors.noOpText(); }
 * 
 * @Override protected void configure(AuthenticationManagerBuilder auth) throws
 * Exception { auth.userDetailsService(new
 * CrmUserDetailsService(this.crmService)); }
 * 
 * // @formatter:off
 * 
 * @Override protected void configure(HttpSecurity http) throws Exception {
 * http.authorizeRequests() .antMatchers("/**", "/favicon.ico",
 * "/webjars/**").permitAll() .anyRequest().authenticated().and().formLogin()
 * .defaultSuccessUrl("/home").failureUrl("/login")
 * .loginPage("/login").permitAll().and()
 * 
 * .logout().permitAll().and().csrf().disable();
 * 
 * } // @formatter:on
 * 
 * }
 * 
 * @Configuration
 * 
 * @EnableWebSecurity
 * 
 * @Order(1) class OAuth2ServerConfiguration extends
 * OAuth2ServerConfigurerAdapter {
 * 
 * private final String applicationName = ServiceConfiguration.CRM_NAME;
 * 
 * @Autowired private DataSource dataSource;
 * 
 * @Autowired private UserDetailsService userDetailsService;
 * 
 * @Autowired private ContentNegotiationStrategy contentNegotiationStrategy;
 * 
 * // @formatter:off
 * 
 * @Override protected void configure(AuthenticationManagerBuilder auth) throws
 * Exception { auth.userDetailsService(userDetailsService) .and() .apply(new
 * InMemoryClientDetailsServiceConfigurer()) .withClient("android-crm")
 * .resourceIds(applicationName) .scopes("read", "write")
 * .authorities("ROLE_USER") .authorizedGrantTypes("authorization_code",
 * "implicit", "password").secret("123456").and()
 * .withClient("ios-crm").resourceIds(applicationName) .scopes("read",
 * "write").authorities("ROLE_USER")
 * .authorizedGrantTypes("password").secret("123456");
 * 
 * }
 * 
 * // @formatter:on
 * 
 * // @formatter:off
 * 
 * @Override protected void configure(HttpSecurity http) throws Exception {
 * http.apply(new OAuth2ServerConfigurer()) .tokenStore(new
 * JdbcTokenStore(this.dataSource))
 * .resourceId(applicationName).and().requestMatchers()
 * .requestMatchers(oauthRequestMatcher()).and()
 * .authorizeRequests().anyRequest().permitAll(); }
 * 
 * // @formatter:on
 * 
 * @Bean RequestMatcher oauthRequestMatcher() { MediaTypeRequestMatcher
 * mediaTypeRequestMatcher = new MediaTypeRequestMatcher(
 * this.contentNegotiationStrategy, MediaType.TEXT_HTML); return new
 * NegatedRequestMatcher(mediaTypeRequestMatcher); }
 * 
 * }
 */