package com.jl.crm.web;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.provider.*;
import org.springframework.security.web.WebAttributes;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.inject.Inject;
import javax.servlet.http.*;
import java.util.Map;

 @Controller
@SessionAttributes("authorizationRequest")
public class OauthAccessConfirmationController {

	private ClientDetailsService clientDetailsService;

	@RequestMapping ("/oauth/confirm_access")
	public ModelAndView getAccessConfirmation(Map<String, Object> model, HttpServletRequest httpServletRequest) throws Exception {
		AuthorizationRequest clientAuth = (AuthorizationRequest) model.remove("authorizationRequest");
		ClientDetails client = clientDetailsService.loadClientByClientId(clientAuth.getClientId());
		model.put("auth_request", clientAuth);
		model.put("client", client);

		HttpSession httpSession = httpServletRequest.getSession(false);
		if (httpSession != null) {
			Object exception = httpSession.getAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
			if (exception != null && exception instanceof AuthenticationException) {
				model.put("exception", exception);
			}
		}
		return new ModelAndView("access_confirmation", model);
	}

	@RequestMapping("/oauth/error")
	public String handleError(Map<String, Object> model) throws Exception {
		// We can add more stuff to the model here for JSP rendering.  If the client was a machine then
		// the JSON will already have been rendered.
		model.put("message", "There was a problem with the OAuth2 protocol");
		return "oauth_error";
	}

	@Inject
	public void setClientDetailsService(ClientDetailsService clientDetailsService) {
		this.clientDetailsService = clientDetailsService;
	}
}
