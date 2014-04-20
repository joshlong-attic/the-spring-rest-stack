package com.jl.crm.web;

import com.jl.crm.services.CrmService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/session")
class CurrentSessionController {

    final CrmService crmService ;

    @Autowired
    CurrentSessionController(CrmService crmService) {
        this.crmService = crmService;
    }

    @RequestMapping(method = RequestMethod.GET)
    HttpEntity<Map<String, Object>> currentUser() {
        Map<String, Object> v = new HashMap<String, Object>();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();
        UserDetails crmUserDetails = (UserDetails) principal ;
        v.put("userId", crmService.findUserByUsername( crmUserDetails.getUsername()).getId() ) ;
        return new ResponseEntity<>(v, HttpStatus.OK);
    }
}