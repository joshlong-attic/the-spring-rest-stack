package com.jl.crm.web;

class CrmWebApplicationInitializer {}

/**
 * In conjunction with {@link CrmSecurityApplicationInitializer}, this configuration class sets up Spring Data REST, In
 * conjunction with {@link CrmWebApplicationInitializer}, this configuration class sets up Spring Data REST, Spring MVC,
 * Spring Security and Spring Security OAuth, along with importing all of our existing service implementations.
 *
 * @author Josh Long
 * @see CrmSecurityApplicationInitializer
 */
//public class CrmWebApplicationInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {
//
//    private int maxUploadSizeInMb = 5 * 1024 * 1024; // 5 MB
//
//    @Override
//    protected Class<?>[] getRootConfigClasses() {
//        return new Class<?>[]{ServiceConfiguration.class};
//    }
//
//    @Override
//    protected Class<?>[] getServletConfigClasses() {
//        return new Class<?>[]{RepositoryRestMvcConfiguration.class, WebMvcConfiguration.class, SecurityConfiguration.class};
//    }
//
//    @Override
//    protected String[] getServletMappings() {
//        return new String[]{"/"};
//    }
//
//    @Override
//    protected void customizeRegistration(ServletRegistration.Dynamic registration) {
//        File uploadDirectory = ServiceConfiguration.CRM_STORAGE_UPLOADS_DIRECTORY;
//        MultipartConfigElement multipartConfigElement = new MultipartConfigElement(uploadDirectory.getAbsolutePath(), maxUploadSizeInMb, maxUploadSizeInMb * 2, maxUploadSizeInMb / 2);
//        registration.setMultipartConfig(multipartConfigElement);
//    }
//
//}
//  
