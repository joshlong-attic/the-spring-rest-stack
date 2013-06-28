package com.jl.crm.web;

import com.jl.crm.services.ServiceConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.data.repository.support.DomainClassConverter;
import org.springframework.data.rest.webmvc.config.RepositoryRestMvcConfiguration;
import org.springframework.format.support.FormattingConversionService;
import org.springframework.hateoas.config.EnableHypermediaSupport;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.encrypt.*;
import org.springframework.security.crypto.password.*;
import org.springframework.security.oauth2.config.annotation.authentication.configurers.InMemoryClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.OAuth2ServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.OAuth2ServerConfigurer;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationProcessingFilter;
import org.springframework.security.oauth2.provider.error.OAuth2AuthenticationEntryPoint;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.*;
import org.springframework.security.web.util.RequestMatcher;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.*;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.LinkedHashMap;


/**
 * In conjunction with {@link CrmSecurityApplicationInitializer}, this configuration class sets up Spring Data REST,
 * Spring MVC, Spring Security and Spring Security OAuth, along with importing all of our existing service
 * implementations.
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
class SecurityConfiguration extends OAuth2ServerConfigurerAdapter {

	/** Request matcher that checks whether a request is to be handled by the Spring Security OAuth machinery. */

	private String signInUrl = "/signin" ;
	private String signoutUrl =  "/signout";
	private String applicationName = "crm";
	private String signInFormUrl = "/" +applicationName + "" + signInUrl + ".html"; // => /crm/signin.html

	@Autowired
	private UserDetailsService userDetailsService;

	/**
	 * We want to do the right thing when a request comes in for '/': if it's a browser, send the client to the
	 * '/crm/signin.html' page. If it's a client, send the client to the Oauth entry point.
	 */
	@Bean
	public AuthenticationEntryPoint overridingAuthenticationEntryPoint() throws Exception {
		RequestMatcher checkIfItsAnOAuthRequest = new RequestMatcher() {

			/* this happens to be thread safe in our use case, no guarantees for other methods */
			private final LocalOAuth2AuthenticationProcessingFilter localOAuth2AuthenticationProcessingFilter =
					  new LocalOAuth2AuthenticationProcessingFilter();

			@Override
			public boolean matches(HttpServletRequest request) {
				return localOAuth2AuthenticationProcessingFilter.isOAuthRequest(request);
			}

			/* we need to subclass the OAuth2AuthenticationProcessingFilter because we need access to protected methods. */
			final class LocalOAuth2AuthenticationProcessingFilter extends OAuth2AuthenticationProcessingFilter {
				/* we only need to answer this one question, using methods that are protected */
				public boolean isOAuthRequest(HttpServletRequest request) {
					return parseHeaderToken(request) != null
					       || parseToken(request) != null;
				}
			}
		};

		LinkedHashMap<RequestMatcher, AuthenticationEntryPoint> linkedHashMap = new LinkedHashMap<>();
		linkedHashMap.put(checkIfItsAnOAuthRequest, new OAuth2AuthenticationEntryPoint());

		LoginUrlAuthenticationEntryPoint signInFormAuthenticationEntryPoint = new LoginUrlAuthenticationEntryPoint(this.signInFormUrl);
		signInFormAuthenticationEntryPoint.afterPropertiesSet();

		DelegatingAuthenticationEntryPoint delegatingAuthenticationEntryPoint = new DelegatingAuthenticationEntryPoint(linkedHashMap);
		delegatingAuthenticationEntryPoint.setDefaultEntryPoint(signInFormAuthenticationEntryPoint);

		return delegatingAuthenticationEntryPoint;
	}

	@Override
	protected void registerAuthentication(AuthenticationManagerBuilder auth)
			  throws Exception {
		auth
				  .apply(new InMemoryClientDetailsServiceConfigurer())
				  .withClient("android-crm")
				  .resourceIds(applicationName)
				  .scopes("read", "write")
				  .authorities("ROLE_USER")
				  .secret("123456")
				  .authorizedGrantTypes("authorization_code", "implicit", "password")
				  .and()
				  .and()
				  .userDetailsService(userDetailsService);
	}


	@Override
	protected void configure(HttpSecurity http) throws Exception {
			http
				  .authorizeUrls().antMatchers("/favicon.ico").permitAll().anyRequest().hasRole("USER")
				  .and().exceptionHandling().authenticationEntryPoint(overridingAuthenticationEntryPoint())
				  .and().formLogin().loginUrl(this.signInUrl).loginPage(this.signInFormUrl).defaultSuccessUrl("/").failureUrl(this.signInFormUrl + "?error=true").permitAll()
				  .and().logout().logoutUrl( this.signoutUrl ).logoutSuccessUrl( this.signInFormUrl).permitAll()
				  .and().apply(new OAuth2ServerConfigurer()).resourceId(this.applicationName)
			;
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
@ComponentScan
@EnableHypermediaSupport
@EnableWebMvc
class WebMvcConfiguration extends WebMvcConfigurationSupport {
	@Bean
	public DomainClassConverter<?> domainClassConverter() {
		return new DomainClassConverter<FormattingConversionService>(mvcConversionService());
	}

	@Override
	public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
		configurer.enable();
	}

	@Bean
	public MultipartResolver multipartResolver() {
		return new StandardServletMultipartResolver();
	}

	// setup support for rendering .jsp pages
	@Bean
	public ViewResolver internalResourceViewResolver() {
		InternalResourceViewResolver internalResourceViewResolver = new InternalResourceViewResolver();
		internalResourceViewResolver.setPrefix("/WEB-INF/crm/");
		internalResourceViewResolver.setSuffix(".jsp");
		return internalResourceViewResolver;
	}


}
