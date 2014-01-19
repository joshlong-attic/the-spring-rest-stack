package com.jl.crm.web;

import com.jl.crm.services.CrmService;
import com.jl.crm.services.ServiceConfiguration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.Order;
import org.springframework.data.rest.webmvc.config.RepositoryRestMvcConfiguration;
import org.springframework.hateoas.hal.DefaultCurieProvider;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.SecurityConfigurer;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.config.annotation.authentication.configurers.InMemoryClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.OAuth2ServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.OAuth2ServerConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeTokenGranter;
import org.springframework.security.oauth2.provider.endpoint.AuthorizationEndpoint;
import org.springframework.security.oauth2.provider.endpoint.FrameworkEndpointHandlerMapping;
import org.springframework.security.oauth2.provider.endpoint.TokenEndpoint;
import org.springframework.security.oauth2.provider.endpoint.WhitelabelApprovalEndpoint;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.security.oauth2.provider.token.ConsumerTokenServices;
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher;
import org.springframework.security.web.util.matcher.NegatedRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.accept.ContentNegotiationStrategy;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.util.UriTemplate;

import javax.servlet.MultipartConfigElement;

@ComponentScan
@EnableAutoConfiguration
public class Application extends SpringBootServletInitializer {
	private static Class<Application> applicationClass = Application.class;

	@Override
	protected SpringApplicationBuilder configure(
			SpringApplicationBuilder application) {
		return application.sources(applicationClass);
	}

	public static void main(String[] args) {
		SpringApplication.run(applicationClass);
	}
}

@Configuration
@Import({ ServiceConfiguration.class, RepositoryRestMvcConfiguration.class })
@EnableWebMvc
class WebMvcConfiguration extends WebMvcConfigurerAdapter {

	String curieNamespace = "crm";

	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
		registry.addViewController("/home").setViewName("home");
		registry.addViewController("/hello").setViewName("hello");
		registry.addViewController("/login").setViewName("login");
	}

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
		return new DefaultCurieProvider(curieNamespace, new UriTemplate(
				"http://localhost:8080/rels/{rel}"));
	}
}

@Configuration
@EnableWebSecurity
class OAuth2Configuration extends OAuth2ServerConfigurerAdapter {

	@Autowired
	CrmService crmService;

	@Autowired
	ContentNegotiationStrategy contentNegotiationStrategy;

	String applicationName = "crm";

	@Override
	@Bean
	protected UserDetailsService userDetailsService() {
		return new CrmUserDetailsService(this.crmService);
	}

	@Override
	@Bean
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth)
			throws Exception {
		auth.userDetailsService(new CrmUserDetailsService(this.crmService));

		InMemoryClientDetailsServiceConfigurer clientDetailsServiceConfigurer = auth
				.apply(new InMemoryClientDetailsServiceConfigurer());

		String userRole = "ROLE_USER";
		String[] scopes = { "read", "write" };

		String authorityPassword = "password", authorityAuthorizationCode = "authorization_code", authorityImplicit = "implicit";

		String secret = "123456";

		// android
		clientDetailsServiceConfigurer
				.withClient("android-crm")
				.resourceIds(applicationName)
				.scopes(scopes)
				.authorities(userRole)
				.secret(secret)
				.authorizedGrantTypes(authorityAuthorizationCode,
						authorityImplicit, authorityPassword);

		// ios
		clientDetailsServiceConfigurer.withClient("ios-crm")
				.resourceIds(applicationName).scopes(scopes)
				.authorities(userRole)
				.authorizedGrantTypes(authorityPassword).secret(secret);
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {

		ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry authorizedRequests = http.authorizeRequests();

		authorizedRequests.antMatchers("/favicon.ico").permitAll();

		authorizedRequests.anyRequest().authenticated();

		http.formLogin()			
			.defaultSuccessUrl("/hello")
			.loginPage("/login")
			.permitAll();

		http.csrf().disable();

		http.logout().permitAll();

		//http.requestMatchers().requestMatchers(oauthRequestMatcher());

		http.apply(new OAuth2ServerConfigurer()).resourceId(applicationName);
	}

	@Bean
	public RequestMatcher oauthRequestMatcher() {
		MediaTypeRequestMatcher mediaTypeRequestMatcher = new MediaTypeRequestMatcher(
				contentNegotiationStrategy, MediaType.TEXT_HTML);
		return new NegatedRequestMatcher(mediaTypeRequestMatcher);
	}

}
