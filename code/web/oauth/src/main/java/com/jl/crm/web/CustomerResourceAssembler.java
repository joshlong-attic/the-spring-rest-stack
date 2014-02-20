package com.jl.crm.web;

import com.jl.crm.services.Customer;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

/**
 * @author Josh Long
 */
@Component
class CustomerResourceAssembler implements ResourceAssembler<Customer, Resource<Customer>> {

    String usersRel = "user";

    Class<UserController> controllerClass = UserController.class;

    @Override
    public Resource<Customer> toResource(Customer customer) {
        long userId = customer.getUser().getId();
        customer.setUser(null);
        Resource<Customer> customerResource = new Resource<Customer>(customer);
        Link selfLink = linkTo(methodOn(controllerClass).loadSingleUserCustomer(userId, customer.getId())).withSelfRel();
        Link userLink = linkTo(methodOn(controllerClass).loadUser(userId)).withRel(usersRel);
        customerResource.add(selfLink);
        customerResource.add(userLink);
        return customerResource;
    }
}
