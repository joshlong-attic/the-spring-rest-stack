package com.jl.crm.services;

/**
 * @author Josh Long
 */
public class CustomerException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public CustomerException(Customer customer, Throwable cause) {
		this(customer.getId(), cause);
	}

	public CustomerException(long id, Throwable cause) {
		super("Could not update customer # " + id, cause);
	}

	public CustomerException(Customer user) {
		this(user.getId());
	}

	public CustomerException(long id) {
		super("Could not update customer # " + id);
	}

}
