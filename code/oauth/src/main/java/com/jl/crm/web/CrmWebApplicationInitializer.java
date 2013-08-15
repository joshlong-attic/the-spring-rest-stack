package com.jl.crm.web;

import com.jl.crm.services.ServiceConfiguration;
import org.springframework.context.annotation.*;
import org.springframework.core.annotation.Order;
import org.springframework.data.rest.webmvc.config.RepositoryRestMvcConfiguration;
import org.springframework.hateoas.config.EnableHypermediaSupport;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.encrypt.*;
import org.springframework.security.crypto.password.*;
import org.springframework.security.oauth2.config.annotation.authentication.configurers.InMemoryClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.OAuth2ServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.OAuth2ServerConfigurer;
import org.springframework.security.oauth2.provider.token.JdbcTokenStore;
import org.springframework.security.web.header.writers.frameoptions.XFrameOptionsHeaderWriter;
import org.springframework.security.web.header.writers.frameoptions.XFrameOptionsHeaderWriter.XFrameOptionsMode;
import org.springframework.security.web.util.MediaTypeRequestMatcher;
import org.springframework.web.accept.ContentNegotiationStrategy;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.*;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import javax.inject.Inject;
import javax.servlet.*;
import javax.sql.DataSource;
import java.io.File;
import java.util.Collections;


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
	@Inject private DataSource dataSource;
	@Inject private ContentNegotiationStrategy contentNegotiationStrategy;

	@Override
	protected void registerAuthentication(AuthenticationManagerBuilder auth) throws Exception {

		auth.apply(new InMemoryClientDetailsServiceConfigurer())
				  .withClient("android-crm")
				  .resourceIds(applicationName)
				  .scopes("read", "write")
				  .authorities("ROLE_USER")
				  .authorizedGrantTypes("authorization_code", "implicit", "password")
				  .secret("123456");

	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.requestMatcher(oauthRequestMatcher());

		http.authorizeRequests()
				  .anyRequest().authenticated();

		http.apply(new OAuth2ServerConfigurer())
				  .tokenStore(new JdbcTokenStore(this.dataSource))
				  .resourceId(applicationName);
	}

	@Bean
	public MediaTypeRequestMatcher oauthRequestMatcher() {
		MediaTypeRequestMatcher mediaTypeRequestMatcher = new MediaTypeRequestMatcher(contentNegotiationStrategy, MediaType.APPLICATION_JSON);
		mediaTypeRequestMatcher.setIgnoredMediaTypes(Collections.singleton(MediaType.ALL));
		return mediaTypeRequestMatcher;
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
class SecurityConfiguration extends WebSecurityConfigurerAdapter {

	@Inject private UserDetailsService userDetailsService;
	@Inject private DataSource dataSource;

	@Override
	protected void registerAuthentication(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailsService);
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.formLogin()
				  .loginPage("/crm/signin.html")
				  .loginProcessingUrl("/signin")
				  .defaultSuccessUrl("/crm/welcome.html")
				  .failureUrl("/crm/signin.html?error=true")
				  .usernameParameter("username")
				  .passwordParameter("password")
				  .permitAll();

		http.headers()
				  .addHeaderWriter(new XFrameOptionsHeaderWriter(XFrameOptionsMode.SAMEORIGIN))
				  .contentTypeOptions()
				  .xssProtection()
				  .cacheControl()
				  .httpStrictTransportSecurity();

		http.logout().logoutUrl("/signout").deleteCookies("JSESSIONID");


		// nb: the H2 administration console should *not* be left exposed.
		// comment out the mapping path below so that it requires an authentication to see it.
		String[] filesToLetThroughUnAuthorized =
				  {
							 H2EmbeddedDatbaseConsoleInitializer.H2_DATABASE_CONSOLE_MAPPING,
							 "/favicon.ico"
				  };

		http.authorizeRequests()
				  .antMatchers(filesToLetThroughUnAuthorized).permitAll()
				  .antMatchers("/users/*").denyAll()
				  .anyRequest().authenticated();

	}
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
