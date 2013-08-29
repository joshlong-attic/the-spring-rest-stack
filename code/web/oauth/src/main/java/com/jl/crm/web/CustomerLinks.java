package com.jl.crm.web;

import com.jl.crm.services.*;
import org.springframework.hateoas.*;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

@Component
public class CustomerLinks {

	static final String USER_REL = "user";

	private EntityLinks entityLinks;

	@Inject
	CustomerLinks(EntityLinks entityLinks) {
		this.entityLinks = entityLinks;
	}

	Link getUserLink(Customer customer) {
		return entityLinks.linkForSingleResource(User.class, customer.getUser().getId()).withRel(USER_REL);
	}

}
