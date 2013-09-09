package com.jl.crm.web;

import com.jl.crm.services.*;

import org.springframework.hateoas.*;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;

import java.util.*;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

/**
 * Handles {@link com.jl.crm.services.User} user entities.
 *
 * @author Josh Long
 */
@Controller
@ExposesResourceFor (User.class)
@RequestMapping (value = ApiUrls.ROOT_URL_USERS, produces = MediaType.APPLICATION_JSON_VALUE  )
class UserController {

	private CrmService crmService;
	private UserResourceAssembler userResourceAssembler;
	private CustomerResourceAssembler customerResourceAssembler;
        
        UserController(){}

	@Inject
	public UserController(CrmService crmService,
			UserResourceAssembler userResourceAssembler,
			CustomerResourceAssembler customerResourceAssembler) {
		this.crmService = crmService;
		this.userResourceAssembler = userResourceAssembler;
		this.customerResourceAssembler = customerResourceAssembler;
	}

	@RequestMapping (method = RequestMethod.DELETE, value = ApiUrls.URL_USERS_USER)
	HttpEntity<Resource<User>> deleteUser(@PathVariable Long user) {
		Resource<User> userResource = userResourceAssembler.toResource(crmService.removeUser(user));
		return new ResponseEntity<Resource<User>>(userResource, HttpStatus.OK);
	}

	@RequestMapping (method = RequestMethod.GET, value = ApiUrls.URL_USERS_USER)
	HttpEntity<Resource<User>> loadUser(@PathVariable Long user) {
		Resource<User> resource = this.userResourceAssembler.toResource(crmService.findById(user));
		return new ResponseEntity<Resource<User>>(resource, HttpStatus.OK);
	}

	@RequestMapping (method = RequestMethod.GET, value = ApiUrls.URL_USERS_USER_CUSTOMERS)
	HttpEntity<Resources<Resource<Customer>>> loadUserCustomers(@PathVariable Long user) {
		Collection<Resource<Customer>> customerResourceCollection = new ArrayList<Resource<Customer>>();
		for (Customer c : this.crmService.loadCustomerAccounts(user)) {
			customerResourceCollection.add(customerResourceAssembler.toResource(c));
		}
		Resources<Resource<Customer>> customerResources = new Resources<Resource<Customer>>(customerResourceCollection);
		customerResources.add(linkTo(methodOn(UserController.class).loadUserCustomers(user)).withSelfRel());
		return new ResponseEntity<Resources<Resource<Customer>>>(customerResources, HttpStatus.OK);
	}

	@RequestMapping (method = RequestMethod.GET, value = ApiUrls.URL_USERS_USER_CUSTOMERS_CUSTOMER)
	HttpEntity<Resource<Customer>> loadSingleUserCustomer(@PathVariable Long user, @PathVariable Long customer ) {
		Resource<Customer> customerResource = customerResourceAssembler.toResource( this.crmService.findCustomerById(customer ));
		return new ResponseEntity<Resource<Customer>>(customerResource, HttpStatus.OK);
	}
}
