package com.jl.crm.web;

import com.jl.crm.services.CrmService;
import com.jl.crm.services.Customer;
import com.jl.crm.services.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.ExposesResourceFor;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    ResponseEntity<Resource<User>> deleteUser(@PathVariable Long user) {
        return new ResponseEntity<Resource<User>>(
                userResourceAssembler.toResource(crmService.removeUser(user)), HttpStatus.NOT_FOUND);
    }

    @RequestMapping(method = GET, value = "/{user}")
    ResponseEntity<Resource<User>> loadUser(@PathVariable Long user) {
        User discoveredUser = this.crmService.findById(user);
        if (null == discoveredUser) {
            return new ResponseEntity<Resource<User>>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<Resource<User>>(
                userResourceAssembler.toResource(discoveredUser), HttpStatus.OK);
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

    @RequestMapping(method = RequestMethod.POST, value = "/{user}/customers")
    ResponseEntity<Void> addCustomer(@PathVariable Long user, @RequestBody Customer c) {
        Customer customer = crmService.addCustomer(user, c.getFirstName(), c.getLastName());

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(linkTo(methodOn(getClass()).loadSingleUserCustomer(user, customer.getId())).toUri());

        return new ResponseEntity<Void>(httpHeaders, HttpStatus.CREATED);
    }
}