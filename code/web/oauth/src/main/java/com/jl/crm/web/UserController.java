package com.jl.crm.web;

import com.jl.crm.services.CrmService;
import com.jl.crm.services.Customer;
import com.jl.crm.services.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.ExposesResourceFor;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceAssembler;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;
import static org.springframework.web.bind.annotation.RequestMethod.*;

/**
 * Handles {@link com.jl.crm.services.User} user entities.
 *
 * @author Josh Long
 */
@RestController
@ExposesResourceFor(User.class)
@RequestMapping(value = "/users")
class UserController {

    private final CrmService crmService;
    private final ResourceAssembler<User, Resource<User>> userResourceAssembler;
    private final ResourceAssembler<Customer, Resource<Customer>> customerResourceAssembler;

    @Autowired
    UserController(CrmService crmService, ResourceAssembler<User, Resource<User>> userResourceAssembler, ResourceAssembler<Customer, Resource<Customer>> customerResourceAssembler) {
        this.crmService = crmService;
        this.userResourceAssembler = userResourceAssembler;
        this.customerResourceAssembler = customerResourceAssembler;
    }

    @RequestMapping(method = DELETE, value = "/{user}")
    ResponseEntity<Resource<User>> deleteUser(@PathVariable Long user) {
        return new ResponseEntity<>(userResourceAssembler.toResource(crmService.removeUser(user)), HttpStatus.NOT_FOUND);
    }

    @RequestMapping(method = GET, value = "/{user}")
    ResponseEntity<Resource<User>> loadUser(@PathVariable Long user) {
        return Optional.of(this.crmService.findById(user))
                .map(u -> new ResponseEntity<>(userResourceAssembler.toResource(u), HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @RequestMapping(method = GET, value = "/{user}/customers")
    Resources<Resource<Customer>> loadUserCustomers(@PathVariable Long user) {
        List<Resource<Customer>> customers = this.crmService.loadCustomerAccounts(user).parallelStream()
                .map(customerResourceAssembler::toResource)
                .collect(Collectors.toList());
        Resources<Resource<Customer>> customerResources = new Resources<Resource<Customer>>(customers);
        customerResources.add(linkTo(methodOn(UserController.class).loadUserCustomers(user)).withSelfRel());
        return customerResources;
    }

    @RequestMapping(method = GET, value = "/{user}/customers/{customer}")
    ResponseEntity<Resource<Customer>> loadSingleUserCustomer(@PathVariable Long user, @PathVariable Long customer) {
        return Optional.of(this.crmService.findCustomerById(customer))
                .map(u -> new ResponseEntity<>(customerResourceAssembler.toResource(u), HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @RequestMapping(method = POST, value = "/{user}/customers")
    ResponseEntity<Void> addCustomer(@PathVariable Long user, @RequestBody Customer c) {
        Customer customer = crmService.addCustomer(user, c.getFirstName(), c.getLastName());

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(linkTo(methodOn(getClass()).loadSingleUserCustomer(user, customer.getId())).toUri());

        return new ResponseEntity<>(httpHeaders, HttpStatus.CREATED);
    }
}