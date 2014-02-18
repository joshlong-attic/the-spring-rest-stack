package com.jl.crm.web;

import com.jl.crm.services.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.bind.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
class CurrentUserController {

    private UserLinks userLinks;

    @Autowired
    public CurrentUserController(UserLinks userLinks) {
        this.userLinks = userLinks;
    }

    @RequestMapping(value = "/user", method = RequestMethod.GET)
    HttpEntity<Resource<User>> currentUser(@AuthenticationPrincipal User self) {
        List<Link> linkList = new ArrayList<Link>();
        linkList.add(this.userLinks.getSelfLink(self));
        linkList.add(this.userLinks.getPhotoLink(self));
        linkList.add(this.userLinks.getCustomersLink(self));
        Resource<User> userResource = new Resource<User>(self, linkList);
        return new ResponseEntity<Resource<User>>(userResource, HttpStatus.OK);
    }

}

/*
    private static class UserResource extends Resource<User> {
        public UserResource(User content, Iterable<Link> links) {
            super(content, links);
        }
    }
*/
