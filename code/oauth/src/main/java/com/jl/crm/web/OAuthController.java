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

import static com.jl.crm.web.OAuthController.AUTHORIZATION_REQUEST_SESSION_ATTRIBUTE;

@Controller
@SessionAttributes (AUTHORIZATION_REQUEST_SESSION_ATTRIBUTE)
public class OAuthController {

	public static final String AUTHORIZATION_REQUEST_SESSION_ATTRIBUTE = "authorizationRequest";

	private ClientDetailsService clientDetailsService;

	@RequestMapping ("/oauth/confirm_access")
	public ModelAndView getAccessConfirmation(Map<String, Object> model, HttpServletRequest httpServletRequest) throws Exception {
		AuthorizationRequest clientAuth = (AuthorizationRequest) model.remove(AUTHORIZATION_REQUEST_SESSION_ATTRIBUTE);
		ClientDetails client = clientDetailsService.loadClientByClientId(clientAuth.getClientId());
		model.put("auth_request", clientAuth);
		model.put("client", client);

		HttpSession httpSession = httpServletRequest.getSession(false);
		if (httpSession != null){
			Object exception = httpSession.getAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
			if (exception != null && exception instanceof AuthenticationException){
				model.put("exception", exception);
			}
		}
		return new ModelAndView("access_confirmation", model);
	}

	@Inject
	public void setClientDetailsService(ClientDetailsService clientDetailsService) {
		this.clientDetailsService = clientDetailsService;
	}

}
