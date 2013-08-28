package com.jl.crm.web;

import com.jl.crm.services.*;
import org.springframework.hateoas.*;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

/**
 * @author Josh Long
 */
@Component
class CustomerResourceAssembler implements ResourceAssembler<Customer, Resource<Customer>> {

	public static final String USER_REL = "user";
	private Class<UserController> controllerClass = com.jl.crm.web.UserController.class;

	@Override
	public Resource<Customer> toResource(Customer customer) {
		long userId = customer.getUser().getId();
		customer.setUser(null);
		Resource<Customer> customerResource = new Resource<Customer>(customer);
		Link selfLink = linkTo(methodOn(controllerClass).loadSingleUserCustomer(userId, customer.getId())).withSelfRel();
		Link userLink = linkTo(methodOn(controllerClass).loadUser( userId)).withRel(USER_REL);
		customerResource.add(selfLink);
		customerResource.add(userLink);
		return customerResource;
	}
}
