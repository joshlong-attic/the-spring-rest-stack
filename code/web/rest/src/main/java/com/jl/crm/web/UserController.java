package com.jl.crm.web;

import com.jl.crm.services.CrmService;
import com.jl.crm.services.Customer;
import com.jl.crm.services.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Collection;
import java.util.Optional;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * Handles {@link com.jl.crm.services.User} user entities.
 *
 * @author Josh Long
 */
@RestController
@RequestMapping(value = "/users")
class UserController {

    private final CrmService crmService;

    @Autowired
    UserController(CrmService crmService) {
        this.crmService = crmService;
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/{user}")
    ResponseEntity<User> deleteUser(@PathVariable Long user) {
        return new ResponseEntity<>(crmService.removeUser(user), HttpStatus.NOT_FOUND);
    }

    @RequestMapping(method = GET, value = "/{user}")
    ResponseEntity<User> loadUser(@PathVariable Long user) {
        return Optional.of(this.crmService.findById(user))
                .map(u -> new ResponseEntity<>(u, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{user}/customers")
    Collection<Customer> loadUserCustomers(@PathVariable Long user) {
        return this.crmService.loadCustomerAccounts(user);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{user}/customers/{customer}")
    Customer loadSingleUserCustomer(@PathVariable Long user,
                                    @PathVariable Long customer) {
        return crmService.findCustomerById(customer);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/{user}/customers")
    ResponseEntity<Customer> addCustomer(@PathVariable Long user, @RequestBody Customer c) {
        Customer customer = crmService.addCustomer(user, c.getFirstName(), c.getLastName());

        URI uriOfNewResource = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/users/{user}/customers/{customer}")
                .buildAndExpand(user, customer.getId())
                .toUri();

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(uriOfNewResource);

        return new ResponseEntity<>(customer, httpHeaders, HttpStatus.CREATED);
    }


}
