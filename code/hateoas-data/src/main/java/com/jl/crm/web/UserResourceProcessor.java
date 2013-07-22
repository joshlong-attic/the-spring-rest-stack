package com.jl.crm.web;


import com.jl.crm.services.User;
import org.springframework.hateoas.*;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

@Component
public class UserResourceProcessor implements ResourceProcessor<Resource<User>> {
	private UserLinks userLinks;

	@Inject
	public void setUserLinks(UserLinks ul) {
		this.userLinks = ul;
	}

	@Override
	public Resource<User> process(Resource<User> ur) {
		User user = ur.getContent();
		ur.add(userLinks.getPhotoLink(user));
		ur.add(userLinks.getCustomersLink(user));
		return ur;
	}
}
