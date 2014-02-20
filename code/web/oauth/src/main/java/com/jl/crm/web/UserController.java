package com.jl.crm.web;

import com.jl.crm.services.CrmService;
import com.jl.crm.services.Customer;
import com.jl.crm.services.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.ExposesResourceFor;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collection;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;
import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * Handles {@link com.jl.crm.services.User} user entities.
 *
 * @author Josh Long
 */
@RestController
@ExposesResourceFor(User.class)
@RequestMapping(value = "/users")
class UserController {

    CrmService crmService;
    UserResourceAssembler userResourceAssembler;
    CustomerResourceAssembler customerResourceAssembler;

    @Autowired
    UserController(CrmService crmService,
                   UserResourceAssembler userResourceAssembler,
                   CustomerResourceAssembler customerResourceAssembler) {
        this.crmService = crmService;
        this.userResourceAssembler = userResourceAssembler;
        this.customerResourceAssembler = customerResourceAssembler;
    }

    @RequestMapping(method = DELETE, value = "/{user}")
    Resource<User> deleteUser(@PathVariable Long user) {
        return userResourceAssembler.toResource(crmService.removeUser(user));
    }

    @RequestMapping(method = GET, value = "/{user}")
    Resource<User> loadUser(@PathVariable Long user) {
        return this.userResourceAssembler.toResource(crmService.findById(user));
    }

    @RequestMapping(method = GET, value = "/{user}/customers")
    Resources<Resource<Customer>> loadUserCustomers(@PathVariable Long user) {
        Collection<Resource<Customer>> customerResourceCollection = new ArrayList<Resource<Customer>>();
        for (Customer c : this.crmService.loadCustomerAccounts(user)) {
            customerResourceCollection.add(customerResourceAssembler.toResource(c));
        }
        Resources<Resource<Customer>> customerResources = new Resources<Resource<Customer>>(customerResourceCollection);
        customerResources.add(linkTo(methodOn(UserController.class).loadUserCustomers(user)).withSelfRel());
        return customerResources;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{user}/customers/{customer}")
    Resource<Customer> loadSingleUserCustomer(@PathVariable Long user, @PathVariable Long customer) {
        return customerResourceAssembler.toResource(this.crmService.findCustomerById(customer));
    }
}
