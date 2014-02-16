package com.jl.crm.web;
/*
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.provider.AuthorizationRequest;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.web.WebAttributes;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;

import static com.jl.crm.web.OAuthController.AUTHORIZATION_REQUEST_SESSION_ATTRIBUTE;

@Controller
@SessionAttributes(AUTHORIZATION_REQUEST_SESSION_ATTRIBUTE)
class OAuthController {

    public static final String AUTHORIZATION_REQUEST_SESSION_ATTRIBUTE = "authorizationRequest";

    ClientDetailsService clientDetailsService;

    @Autowired
    OAuthController(ClientDetailsService clientDetailsService) {
        this.clientDetailsService = clientDetailsService;
    }

    @RequestMapping("/oauth/confirm_access")
    ModelAndView getAccessConfirmation(Map<String, Object> model, HttpServletRequest httpServletRequest) throws Exception {
        AuthorizationRequest clientAuth = (AuthorizationRequest) model.remove(AUTHORIZATION_REQUEST_SESSION_ATTRIBUTE);
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

}
*/