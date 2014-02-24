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

	private static final long serialVersionUID = 1L;

	public CustomerWriteException(Customer customer, Throwable cause) {
		super(customer, cause);
	}
}
