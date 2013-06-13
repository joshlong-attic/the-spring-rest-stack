package com.jl.crm.services;


public class UserException extends RuntimeException {

	private static final String message =
			  "Could not find user profile photo for user # ";

	public UserException(User user, Throwable cause) {
		this(user == null ? -1 : user.getId(), cause);
	}

	public UserException(long userId, Throwable cause) {
		super(message + userId, cause);
	}

	public UserException(User user) {
		this(user.getId());
	}

	public UserException(long userId) {
		super(message + userId);
	}
}
