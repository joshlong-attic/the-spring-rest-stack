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
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.*;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.*;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import javax.servlet.*;
import java.io.File;
import java.util.*;


/**
 * In conjunction with {@link CrmSecurityApplicationInitializer}, this
 * configuration class sets up Spring Data REST, Spring MVC, Spring Security and
 * Spring Security OAuth, along with importing all of our existing service
 * implementations.
 * 
 * @see CrmSecurityApplicationInitializer
 * 
 * @author Josh Long
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

	protected EnumSet<DispatcherType> getDispatcherTypes() {
		return isAsyncSupported() ?
				         EnumSet.of(DispatcherType.REQUEST, DispatcherType.FORWARD, DispatcherType.INCLUDE, DispatcherType.ASYNC) :
				         EnumSet.of(DispatcherType.REQUEST, DispatcherType.FORWARD, DispatcherType.INCLUDE);
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
	private String applicationName = "crm";
	
	@Autowired
	private UserDetailsService userDetailsService;

	@Override
	protected void registerAuthentication(AuthenticationManagerBuilder auth)
			throws Exception {
		auth
			.apply(new InMemoryClientDetailsServiceConfigurer())
				.withClient("android-crm")
					.resourceIds(applicationName)
					.scopes("read","write")
					.authorities("ROLE_USER")
					.secret("123456")
					.authorizedGrantTypes("authorization_code","implicit","password")
					.and()
				.and()
			.userDetailsService(userDetailsService);
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
			.authorizeUrls()
				.antMatchers("/favicon.ico").permitAll()
				.anyRequest().hasRole("USER")
				.and()
			.formLogin()
				.loginPage("/crm/signin.html")
				.defaultSuccessUrl("/crm/welcome.html")
				.failureUrl("/crm/signin.html?error=true")
				.permitAll()
				.and()
			.apply(new OAuth2ServerConfigurer())
				.resourceId(applicationName);
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
