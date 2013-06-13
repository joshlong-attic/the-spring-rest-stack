package com.jl.crm.web;

import com.jl.crm.services.ServiceConfiguration;
import org.springframework.context.annotation.*;
import org.springframework.core.Conventions;
import org.springframework.data.repository.support.DomainClassConverter;
import org.springframework.data.rest.webmvc.config.RepositoryRestMvcConfiguration;
import org.springframework.format.support.FormattingConversionService;
import org.springframework.hateoas.config.EnableEntityLinks;
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
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.*;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.filter.*;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.*;
import org.springframework.web.servlet.*;
import org.springframework.web.servlet.config.annotation.*;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import javax.servlet.*;
import java.io.File;
import java.util.*;


@SuppressWarnings ("unused")
public class CrmWebApplicationInitializer implements WebApplicationInitializer {

	private final String patternAll = "/";
	private final String springServletName = "spring";
	private int maxUploadSizeInMb = 5 * 1024 * 1024; // 5 MB

	@Override
	public void onStartup(ServletContext servletContext) throws ServletException {
		Map<String, Filter> filterMap = new LinkedHashMap<String, Filter>();
		filterMap.put("springSecurityFilterChain", new DelegatingFilterProxy());

		// register the rest
		List<? extends Filter> filters = Arrays.asList(new HiddenHttpMethodFilter(), new MultipartFilter(), new OpenEntityManagerInViewFilter());
		for (Filter f : filters) {
			filterMap.put(Conventions.getVariableName(f), f);
		}
		for (String fn : filterMap.keySet()) {
			registerFilter(servletContext, fn, filterMap.get(fn));
		}


		servletContext.addListener(new HttpSessionEventPublisher());

		WebApplicationContext webApplicationContext
				  = buildWebApplicationContext(servletContext, ServiceConfiguration.class, RepositoryRestMvcConfiguration.class, SecurityConfiguration.class, WebMvcConfiguration.class);

		servletContext.addListener(new ContextLoaderListener(webApplicationContext));

		DispatcherServlet dispatcherServlet = new DispatcherServlet();
		dispatcherServlet.setContextClass(AnnotationConfigWebApplicationContext.class);
		ServletRegistration.Dynamic spring = servletContext.addServlet(this.springServletName, dispatcherServlet);
		spring.addMapping(patternAll);
		spring.setAsyncSupported(true);
		customizeRegistration(spring);
	}

	protected void customizeRegistration(ServletRegistration.Dynamic registration) {
		File uploadDirectory = ServiceConfiguration.CRM_STORAGE_UPLOADS_DIRECTORY;
		MultipartConfigElement multipartConfigElement = new MultipartConfigElement(uploadDirectory.getAbsolutePath(), maxUploadSizeInMb, maxUploadSizeInMb * 2, maxUploadSizeInMb / 2);
		registration.setMultipartConfig(multipartConfigElement);
	}

	protected void registerFilter(ServletContext servletContext, String name, Filter filter) {
		FilterRegistration.Dynamic filterRegistration = servletContext.addFilter(name, filter);
		filterRegistration.addMappingForUrlPatterns(null, true, this.patternAll);
		filterRegistration.addMappingForServletNames(null, true, this.springServletName);
		filterRegistration.setAsyncSupported(true);
	}

	protected WebApplicationContext buildWebApplicationContext(ServletContext servletContext, Class... configClasses) {
		AnnotationConfigWebApplicationContext ac = new AnnotationConfigWebApplicationContext();
		ac.setServletContext(servletContext);
		ac.register(configClasses);
		ac.refresh();
		return ac;
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
@EnableEntityLinks
@ComponentScan
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
