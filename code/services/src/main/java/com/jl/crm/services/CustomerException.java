package com.jl.crm.services;

/** @author Josh Long */
public class CustomerException extends RuntimeException {
	private static final String message =
			"could not update customer # ";

	public CustomerException(Customer customer, Throwable cause) {
		this(customer.getId(), cause);
	}

	public CustomerException(long id, Throwable cause) {
		super(message + id, cause);
	}

	public CustomerException(Customer user) {
		this(user.getId());
	}

	public CustomerException(long id) {
		super(message + id);
	}
}
