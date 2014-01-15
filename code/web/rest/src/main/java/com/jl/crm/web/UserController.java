package com.jl.crm.web;

import com.jl.crm.services.CrmService;
import com.jl.crm.services.Customer;
import com.jl.crm.services.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

/**
 * Handles {@link com.jl.crm.services.User} user entities.
 *
 * @author Josh Long
 */
@RestController
@RequestMapping(value = ApiUrls.ROOT_URL_USERS,
        produces = MediaType.APPLICATION_JSON_VALUE)
class UserController {

    CrmService crmService;

    @Autowired
    UserController(CrmService crmService) {
        this.crmService = crmService;
    }

    @RequestMapping(method = RequestMethod.DELETE, value = ApiUrls.URL_USERS_USER)
    User deleteUser(@PathVariable Long user) {
        return crmService.removeUser(user);
    }

    @RequestMapping(method = RequestMethod.GET, value = ApiUrls.URL_USERS_USER)
    User loadUser(@PathVariable Long user) {
        return crmService.findById(user);
    }

    @RequestMapping(method = RequestMethod.GET, value = ApiUrls.URL_USERS_USER_CUSTOMERS)
    CustomerList loadUserCustomers(@PathVariable Long user) {
        CustomerList customerResourceCollection = new CustomerList();
        customerResourceCollection.addAll(this.crmService.loadCustomerAccounts(user));
        return customerResourceCollection;
    }

    @RequestMapping(method = RequestMethod.GET, value = ApiUrls.URL_USERS_USER_CUSTOMERS_CUSTOMER)
    Customer loadSingleUserCustomer(@PathVariable Long user, @PathVariable Long customer) {
        return crmService.findCustomerById(customer);
    }

    /**
     * This is superior to using an {@link ArrayList} of {@link Customer} because it bakes
     * in the generic type information which would've otherwise been lost and helps
     * Jackson in the conversion at runtime.
     */
    static class CustomerList extends ArrayList<Customer> {

        private static final long serialVersionUID = 1L;

    }
}
