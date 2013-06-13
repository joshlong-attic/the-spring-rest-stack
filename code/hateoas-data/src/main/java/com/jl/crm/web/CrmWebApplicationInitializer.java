package com.jl.crm.web;

import com.jl.crm.services.ServiceConfiguration;
import org.springframework.context.annotation.*;
import org.springframework.core.Conventions;
import org.springframework.data.repository.support.DomainClassConverter;
import org.springframework.data.rest.webmvc.config.RepositoryRestMvcConfiguration;
import org.springframework.format.support.FormattingConversionService;
import org.springframework.hateoas.config.*;
import org.springframework.orm.jpa.support.OpenEntityManagerInViewFilter;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.*;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.filter.HiddenHttpMethodFilter;
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
				  = buildWebApplicationContext(servletContext, ServiceConfiguration.class, RepositoryRestMvcConfiguration.class, WebMvcConfiguration.class);

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
@EnableEntityLinks
@ComponentScan
@EnableWebMvc
@EnableHypermediaSupport
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

	@Bean
	public ViewResolver internalResourceViewResolver() {
		InternalResourceViewResolver internalResourceViewResolver = new InternalResourceViewResolver();
		internalResourceViewResolver.setPrefix("/WEB-INF/crm/");
		internalResourceViewResolver.setSuffix(".jsp");
		return internalResourceViewResolver;
	}
}
