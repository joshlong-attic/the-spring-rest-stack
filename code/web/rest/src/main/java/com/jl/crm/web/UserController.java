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
import java.util.ArrayList;
import java.util.List;

/**
 * Handles {@link com.jl.crm.services.User} user entities.
 *
 * @author Josh Long
 */
@RestController
@RequestMapping(value = "/users")
class UserController {

    CrmService crmService;

    @Autowired
    UserController(CrmService crmService) {
        this.crmService = crmService;
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/{user}")
    ResponseEntity<User> deleteUser(@PathVariable Long user) {
        return new ResponseEntity<User>(crmService.removeUser(user), HttpStatus.NOT_FOUND);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{user}")
    User loadUser(@PathVariable Long user) {
        return this.crmService.findById(user);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{user}/customers")
    List<Customer> loadUserCustomers(@PathVariable Long user) {
        List<Customer> customerResourceCollection = new ArrayList<Customer>();
        customerResourceCollection.addAll(this.crmService.loadCustomerAccounts(user));
        return customerResourceCollection;
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

        return new ResponseEntity<Customer>(customer, httpHeaders, HttpStatus.CREATED);
    }


}
