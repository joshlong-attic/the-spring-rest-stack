package com.jl.crm.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceProcessor;
import org.springframework.stereotype.Component;

import com.jl.crm.services.Customer;

@Component
class CustomerResourceProcessor implements ResourceProcessor<Resource<Customer>> {

	CustomerLinks customerLinks;

	@Autowired
	CustomerResourceProcessor(CustomerLinks customerLinks) {
		this.customerLinks = customerLinks;
	}

	@Override
	public Resource<Customer> process(Resource<Customer> customerResource) {
		customerResource.add(customerLinks.getUserLink(customerResource
				.getContent()));
		return customerResource;
	}

}
