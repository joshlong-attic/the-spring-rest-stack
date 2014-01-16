package com.jl.crm.web;

import com.jl.crm.services.CrmService;
import com.jl.crm.services.ServiceConfiguration;
import com.jl.crm.services.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.rest.webmvc.config.RepositoryRestMvcConfiguration;
import org.springframework.hateoas.hal.DefaultCurieProvider;
//import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
//import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.builders.WebSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.authority.AuthorityUtils;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.util.UriTemplate;

import javax.servlet.MultipartConfigElement;
import java.util.ArrayList;
import java.util.Collection;

@ComponentScan
@EnableAutoConfiguration
public class Application extends SpringBootServletInitializer {
    private static Class<Application> applicationClass = Application.class;

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(applicationClass);
    }

    public static void main(String[] args) {
        SpringApplication.run(applicationClass);
    }
}

@Configuration
@Import({ServiceConfiguration.class, RepositoryRestMvcConfiguration.class})
@EnableWebMvc
class WebMvcConfiguration extends WebMvcConfigurerAdapter {

    String curieNamespace = "crm";

    @Bean
    MultipartConfigElement multipartConfigElement() {
        return new MultipartConfigElement("");
    }

    @Bean
    MultipartResolver multipartResolver() {
        return new StandardServletMultipartResolver();
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/crm/signin.html").setViewName("crm/signin");
        registry.addViewController("/").setViewName("crm/home");
    }

    @Bean
    DefaultCurieProvider defaultCurieProvider() {
        return new DefaultCurieProvider(curieNamespace, new UriTemplate(
                "http://localhost:8080/rels/{rel}"));
    }
}
/**



 @Configuration
 @EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
 @EnableWebSecurity
 class WebMvcSecurityConfiguration extends WebSecurityConfigurerAdapter {
 @Override
 public void configure(WebSecurity web) throws Exception {
 web.ignoring().antMatchers("/resources*//**");
 }

 @Autowired
 CrmService crmService;

 @Override
 protected void configure(AuthenticationManagerBuilder auth) throws Exception {

 CrmUserDetailsService crmUserDetailsService = new CrmUserDetailsService(this.crmService);

 auth.userDetailsService(crmUserDetailsService);

 }

 @Override
 protected void configure(HttpSecurity http) throws Exception {

 // let pass
 String[] filesToLetPass = {"/favicon.ico"};
 http.authorizeRequests()
 .antMatchers(filesToLetPass).permitAll()
 .anyRequest().authenticated();

 http.formLogin()
 .loginPage("/crm/signin.html")
 .defaultSuccessUrl("/crm/welcome.html")
 .usernameParameter("username")
 .passwordParameter("password")
 .permitAll();

 http.logout().logoutUrl("/signout").permitAll();
 }
 }

 class CrmUserDetailsService implements UserDetailsService {

 CrmService crmService;

 CrmUserDetailsService(CrmService crmService) {
 this.crmService = crmService;
 }

 @Override
 public UserDetails loadUserByUsername(String username)
 throws UsernameNotFoundException {
 com.jl.crm.services.User user = crmService.findUserByUsername(username);
 return new CrmUserDetails(user);
 }

 @SuppressWarnings("serial")
 public static class CrmUserDetails extends User implements UserDetails {

 public static final String SCOPE_READ = "read";
 public static final String SCOPE_WRITE = "write";
 public static final String ROLE_USER = "ROLE_USER";
 private Collection<GrantedAuthority> grantedAuthorities = new ArrayList<GrantedAuthority>();

 public CrmUserDetails(com.jl.crm.services.User user) {
 super(user);
 Assert.notNull(user, "the provided user reference can't be null");
 this.grantedAuthorities = AuthorityUtils.createAuthorityList(
 ROLE_USER, SCOPE_READ, SCOPE_WRITE);
 }

 @Override
 public Collection<? extends GrantedAuthority> getAuthorities() {
 return this.grantedAuthorities;
 }

 @Override
 public boolean isAccountNonExpired() {
 return isEnabled();
 }

 @Override
 public boolean isAccountNonLocked() {
 return isEnabled();
 }

 @Override
 public boolean isCredentialsNonExpired() {
 return isEnabled();
 }
 }
 }


 */