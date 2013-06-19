package com.jl.crm.web;

import com.jl.crm.services.ServiceConfiguration;
import org.springframework.context.annotation.*;
import org.springframework.core.Conventions;
import org.springframework.data.repository.support.DomainClassConverter;
import org.springframework.data.rest.webmvc.config.RepositoryRestMvcConfiguration;
import org.springframework.format.support.FormattingConversionService;
import org.springframework.hateoas.config.EnableHypermediaSupport;
import org.springframework.orm.jpa.support.OpenEntityManagerInViewFilter;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.vote.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.encrypt.*;
import org.springframework.security.crypto.password.*;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.client.ClientCredentialsTokenEndpointFilter;
import org.springframework.security.oauth2.provider.error.*;
import org.springframework.security.oauth2.provider.token.*;
import org.springframework.security.oauth2.provider.vote.ScopeVoter;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.web.filter.*;
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
 * This configuration class sets up Spring Data REST, Spring MVC, Spring Security and Spring Security OAuth, along with
 * importing all of our existing service implementations.
 *
 * @author Josh Long
 */
public class CrmWebApplicationInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {

	private int maxUploadSizeInMb = 5 * 1024 * 1024; // 5 MB
	private Map<Filter, String> servletFilterMapping = new LinkedHashMap<Filter, String>();

	public CrmWebApplicationInitializer() {
		super();

		// initialize what filters we want and what their registered names should be.
		Map<Filter, String> stringFilterMap = new LinkedHashMap<Filter, String>();
		stringFilterMap.put(new DelegatingFilterProxy(), "springSecurityFilterChain");
		Filter[] filters = new Filter[]{new HiddenHttpMethodFilter(), new MultipartFilter(), new OpenEntityManagerInViewFilter()};
		for (Filter f : filters) {
			stringFilterMap.put(f, Conventions.getVariableName(f));
		}
		this.servletFilterMapping = stringFilterMap;
	}

	@Override
	protected Class<?>[] getRootConfigClasses() {
		return new Class<?>[]{ServiceConfiguration.class, SecurityConfiguration.class};
	}

	@Override
	protected Class<?>[] getServletConfigClasses() {
		return new Class<?>[]{RepositoryRestMvcConfiguration.class, WebMvcConfiguration.class};
	}

	@Override
	protected void registerDispatcherServlet(ServletContext servletContext) {
		servletContext.addListener(new HttpSessionEventPublisher());
		super.registerDispatcherServlet(servletContext);
	}

	@Override
	protected String[] getServletMappings() {
		return new String[]{"/"};
	}

	@Override
	protected Filter[] getServletFilters() {
		Set<Filter> filters = servletFilterMapping.keySet();
		return filters.toArray(new Filter[filters.size()]);
	}

	@Override
	protected FilterRegistration.Dynamic registerServletFilter(ServletContext servletContext, Filter filter) {
		String filterName = this.servletFilterMapping.get(filter);
		FilterRegistration.Dynamic registration = servletContext.addFilter(filterName, filter);
		registration.setAsyncSupported(isAsyncSupported());
		registration.addMappingForServletNames(getDispatcherTypes(), false, getServletName());
		return registration;
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
@ImportResource ("classpath:/oauth-security.xml")
class SecurityConfiguration {
	private String applicationName = "crm";

	@Bean
	public OAuth2AuthenticationEntryPoint oauthAuthenticationEntryPoint() {
		OAuth2AuthenticationEntryPoint oAuth2AuthenticationEntryPoint = new OAuth2AuthenticationEntryPoint();
		oAuth2AuthenticationEntryPoint.setRealmName(this.applicationName);
		return oAuth2AuthenticationEntryPoint;
	}

	@Bean
	public UnanimousBased accessDecisionManager() {
		List<AccessDecisionVoter> decisionVoters = new ArrayList<AccessDecisionVoter>();
		decisionVoters.add(new ScopeVoter());
		decisionVoters.add(new RoleVoter());
		decisionVoters.add(new AuthenticatedVoter());
		return new UnanimousBased(decisionVoters);
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return NoOpPasswordEncoder.getInstance();
	}

	@Bean
	public TextEncryptor textEncryptor() {
		return Encryptors.noOpText();
	}

	@Bean
	public InMemoryTokenStore tokenStore() {
		return new InMemoryTokenStore();
	}

	@Bean
	public DefaultTokenServices tokenServices(InMemoryTokenStore tokenStore, ClientDetailsService jpaUserCredentialsService) {
		DefaultTokenServices defaultTokenServices = new DefaultTokenServices();
		defaultTokenServices.setTokenStore(tokenStore);
		defaultTokenServices.setSupportRefreshToken(true);
		defaultTokenServices.setClientDetailsService(jpaUserCredentialsService);
		return defaultTokenServices;
	}

	@Bean
	public OAuth2AccessDeniedHandler oauthAccessDeniedHandler() {
		return new OAuth2AccessDeniedHandler();
	}

	@Bean
	public ClientCredentialsTokenEndpointFilter clientCredentialsTokenEndpointFilter(AuthenticationManager authenticationManager, OAuth2AuthenticationEntryPoint entryPoint) {
		ClientCredentialsTokenEndpointFilter endpointFilter = new ClientCredentialsTokenEndpointFilter();
		endpointFilter.setAuthenticationManager(authenticationManager);
		endpointFilter.setAuthenticationEntryPoint(entryPoint);
		return endpointFilter;
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
