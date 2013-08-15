package com.jl.crm.web;

import com.jl.crm.services.*;

import org.springframework.hateoas.*;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.inject.Inject;

@Component
public class CustomerLinks {

	static final String USER_REL = "user";

	private EntityLinks entityLinks;

	@Inject
	public CustomerLinks(EntityLinks entityLinks) {
		this.entityLinks = entityLinks;
	}

	public Link getUserLink(Customer customer) {
		return entityLinks.linkForSingleResource(User.class, customer.getUser().getId()).withRel(USER_REL);
	}

}
