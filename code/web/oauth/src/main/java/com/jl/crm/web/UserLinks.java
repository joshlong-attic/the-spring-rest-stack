package com.jl.crm.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityLinks;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Component;

import com.jl.crm.services.User;

@Component
class UserLinks {

	String photo = "photo";
	String photoRel = "photo";
	String customers = "customers";
	String customersRel = "customers";
	EntityLinks entityLinks;

	@Autowired
	UserLinks(EntityLinks entityLinks) {
		this.entityLinks = entityLinks;
	}

	Link getSelfLink(User user) {
		return this.entityLinks.linkForSingleResource(User.class, user.getId())
				.withSelfRel();
	}

	Link getCustomersLink(User user) {
		return this.entityLinks.linkForSingleResource(User.class, user.getId())
				.slash(customers).withRel(customersRel);
	}

	Link getPhotoLink(User user) {
		return this.entityLinks.linkForSingleResource(User.class, user.getId())
				.slash(photo).withRel(photoRel);
	}

}
