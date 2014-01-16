package com.jl.crm.web;

import com.jl.crm.services.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
//import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.List;

/*
 * Convenience REST endpoint to answer the question:
 * <EM>who's currently signed in for this session?</EM>. We look up the
 * currently installed Spring Security {@link Authentication} and then adapt it
 * to a {@link User}.
 * 
 * @author Josh Long

@Controller
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
class CurrentUserController {

	private UserLinks userLinks;

	@Autowired
	CurrentUserController(UserLinks userLinks) {
		this.userLinks = userLinks;
	}

	@RequestMapping(value = "/user", method = RequestMethod.GET)
	public HttpEntity<Resource<User>> currentUser(@CurrentUser User self) {
		List<Link> linkList = new ArrayList<Link>();
		linkList.add(this.userLinks.getSelfLink(self));
		linkList.add(this.userLinks.getPhotoLink(self));
		linkList.add(this.userLinks.getCustomersLink(self));
		UserResource userResource = new UserResource(self, linkList);
		return new ResponseEntity<Resource<User>>(userResource, HttpStatus.OK);
	}

	private static class UserResource extends Resource<User> {
		public UserResource(User content, Iterable<Link> links) {
			super(content, links);
		}
	}

}
 */