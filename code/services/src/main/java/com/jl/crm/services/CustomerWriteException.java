package com.jl.crm.services;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Thrown when there is trouble persisting the {@link Customer customer}.
 *
 * @author Josh Long
 */
@ResponseStatus (HttpStatus.METHOD_NOT_ALLOWED)
public class CustomerWriteException extends CustomerException {
	public CustomerWriteException(Customer customer, Throwable cause) {
		super(customer, cause);
	}

	public CustomerWriteException(long id, Throwable cause) {
		super(id, cause);
	}

	public CustomerWriteException(Customer user) {
		super(user);
	}

	public CustomerWriteException(long id) {
		super(id);
	}
}
