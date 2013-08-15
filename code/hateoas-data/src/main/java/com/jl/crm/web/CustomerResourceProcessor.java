package com.jl.crm.web;

import com.jl.crm.services.Customer;
import org.springframework.hateoas.*;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

@Component
public class CustomerResourceProcessor implements ResourceProcessor<Resource<Customer>> {
	private CustomerLinks customerLinks;

	@Inject
	public void setCustomerLinks(CustomerLinks customerLinks) {
		this.customerLinks = customerLinks;
	}

	@Override
	public Resource<Customer> process(Resource<Customer> customerResource) {
		customerResource.add(customerLinks.getUserLink(customerResource.getContent()));
		return customerResource;
	}
}
