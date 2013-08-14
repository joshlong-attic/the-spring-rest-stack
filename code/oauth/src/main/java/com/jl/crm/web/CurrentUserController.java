package com.jl.crm.web;

import com.jl.crm.services.*;
import com.jl.crm.services.security.CrmUserDetailsService;
import org.springframework.hateoas.*;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.*;

/**
 * Convenience REST endpoint to answer the question: <EM>who's currently signed in for this session?</EM>.
 * We look up the currently installed Spring Security {@link Authentication} and then adapt it to a
 * {@link User}.
 *
 * @author Josh Long
 */
@Controller
public class CurrentUserController {

	private CrmService crmService;
	private UserLinks userLinks;

	@RequestMapping (value = "/user", method = RequestMethod.GET)
	public HttpEntity<Resource<User>> currentUser(Authentication auth) {
		CrmUserDetailsService.CrmUserDetails crmUserDetails = (CrmUserDetailsService.CrmUserDetails) auth.getPrincipal();
		long userId = crmUserDetails.getUser().getId();
		User self = this.crmService.findById(userId);
		List<Link> linkList = new ArrayList<Link>();
		linkList.add( this.userLinks.getSelfLink(self));
		linkList.add(this.userLinks.getPhotoLink( self));
		linkList.add(this.userLinks.getCustomersLink(self));
		UserResource userResource = new UserResource(self, linkList);
		return new ResponseEntity<Resource<User>>(userResource , HttpStatus.OK);
	}

	@Inject
	public void setUserLinks(UserLinks userLinks) {
		this.userLinks = userLinks;
	}

	@Inject
	public void setCrmService(CrmService crmService) {
		this.crmService = crmService;
	}

	static class UserResource extends Resource<User> {
		public UserResource(User content, Iterable<Link> links) {
			super(content, links);
		}
	}
}
