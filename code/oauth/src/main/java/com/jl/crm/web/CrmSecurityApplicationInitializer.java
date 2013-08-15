/*
 * Copyright 2002-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jl.crm.web;

import org.springframework.orm.jpa.support.OpenEntityManagerInViewFilter;
import org.springframework.security.web.context.AbstractSecurityWebApplicationInitializer;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.HiddenHttpMethodFilter;
import org.springframework.web.multipart.support.MultipartFilter;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.support.AbstractDispatcherServletInitializer;

import javax.servlet.ServletContext;

/**
 * In conjunction with {@link CrmWebApplicationInitializer}, this configuration class sets
 * up Spring Security and Spring Security OAuth.
 *
 * @author Rob Winch
 * @see CrmWebApplicationInitializer
 */
public class CrmSecurityApplicationInitializer extends AbstractSecurityWebApplicationInitializer {

	/**
	 * Instruct Spring Security to use the {@link DispatcherServlet}'s
	 * {@link WebApplicationContext} to find the springSecurityFilterChain.
	 */
	@Override
	protected String getDispatcherWebApplicationContextSuffix() {
		return AbstractDispatcherServletInitializer.DEFAULT_SERVLET_NAME;
	}

	/**
	 * Insert the following filters before Spring Security. Be careful when inserting
	 * filters before Spring Security!
	 */
	@Override
	protected void afterSpringSecurityFilterChain(ServletContext servletContext) {
		insertFilters(servletContext, new HiddenHttpMethodFilter(), new MultipartFilter() , new OpenEntityManagerInViewFilter());
	}

	/**
	 * Register the {@link HttpSessionEventPublisher}
	 */
	@Override
	protected boolean enableHttpSessionEventPublisher() {
		return true;
	}
}
