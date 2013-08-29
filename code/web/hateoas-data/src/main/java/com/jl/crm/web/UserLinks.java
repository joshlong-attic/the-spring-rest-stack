package com.jl.crm.web;


import com.jl.crm.services.User;
import org.springframework.hateoas.*;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

@Component
public class UserLinks {

	private static final String PHOTO = "photo";
	private static final String PHOTO_REL = "photo";
	private static final String CUSTOMER = "customers";
	private static final String CUSTOMER_REL = "customers";

	private final EntityLinks entityLinks;

	@Inject
	UserLinks(EntityLinks entityLinks) {
		this.entityLinks = entityLinks;
	}

	Link getSelfLink(User user) {
		return this.entityLinks.linkForSingleResource(User.class, user.getId()).withSelfRel();
	}

	Link getCustomersLink(User user) {
		return this.entityLinks.linkForSingleResource(User.class, user.getId()).slash(CUSTOMER).withRel(CUSTOMER_REL);
	}

	Link getPhotoLink(User user) {
		return this.entityLinks.linkForSingleResource(User.class, user.getId()).slash(PHOTO).withRel(PHOTO_REL);
	}

}
