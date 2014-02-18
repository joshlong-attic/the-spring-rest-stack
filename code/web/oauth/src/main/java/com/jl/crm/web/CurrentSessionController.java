package com.jl.crm.web;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/session")
class CurrentSessionController {



    @RequestMapping( method = RequestMethod.GET)
    HttpEntity<Map<String,Object>> currentUser() {
        Map<String,Object> v = new HashMap<String, Object>() ;

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();
        Assert.isInstanceOf(CrmUserDetails.class, principal);
        CrmUserDetails crmUserDetails = (CrmUserDetails) principal;
        v.put("userId", crmUserDetails.getId());

        // todo put other useful data like when the session started etc


        return new ResponseEntity<Map<String,Object>>( v, HttpStatus.OK);
    }
}