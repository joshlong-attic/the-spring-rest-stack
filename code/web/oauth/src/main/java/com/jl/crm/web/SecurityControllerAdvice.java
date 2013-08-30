package com.jl.crm.web;

import com.jl.crm.services.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;

/**
 * Follows good advice demonstrated in <A href="http://stackoverflow.com/questions/17741787/injecting-custom-principal-to-controllers-by-spring-security/17751357#17751357">this
 * StackOverflow post</A>  with an epic explanation by <A href="https://twitter.com/rob_winch">Rob Winch</A>.
 * <p/>
 *
 * @author Josh Long
 */
@ControllerAdvice
public class SecurityControllerAdvice {

	private CrmService service;

	@Inject
	public SecurityControllerAdvice(CrmService service) {
		this.service = service;
	}

	@ModelAttribute
	public User currentUser(Authentication authentication) {
		if (null == authentication){
			return null;
		}
		return (User) authentication.getPrincipal();
	}
}
