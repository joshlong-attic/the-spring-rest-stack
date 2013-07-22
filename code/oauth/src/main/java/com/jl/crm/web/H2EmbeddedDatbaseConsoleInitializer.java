package com.jl.crm.web;


import org.h2.server.web.WebServlet;
import org.springframework.web.WebApplicationInitializer;

import javax.servlet.*;

/**
 * loads the <A href="http://127.0.0.1:8080/h2/">web-based H2 database console</A>.
 * <p/>
 * To access the database for this application, use the JDBC URI {@code jdbc:h2:mem:crm}.
 *
 * @author Josh Long
 */
public class H2EmbeddedDatbaseConsoleInitializer implements WebApplicationInitializer {
	/**
	 * We use this variable both in installing the H2 database administration console, as well as
	 * as excluding this from Spring Security's protection.
	 */
	public static final String H2_DATABASE_CONSOLE_MAPPING = "/h2/*";

	@Override
	public void onStartup(ServletContext servletContext) throws ServletException {
		WebServlet webServlet = new WebServlet();

		ServletRegistration.Dynamic dynamic = servletContext.addServlet("h2", webServlet);
		dynamic.setInitParameter("trace", "true");
		dynamic.setAsyncSupported(true);
		dynamic.addMapping(H2_DATABASE_CONSOLE_MAPPING);
		dynamic.setLoadOnStartup(1);
	}

}
