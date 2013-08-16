package com.jl.crm.services;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Thrown when the system is inable to - for whatever reason - write the user profile photo.
 *
 * @author Josh Long
 */
@ResponseStatus (HttpStatus.METHOD_NOT_ALLOWED)
public class UserProfilePhotoWriteException extends UserException {

	private static final long serialVersionUID = 1L;

	public UserProfilePhotoWriteException(User user, Throwable cause) {
		super(user, cause);
	}

	public UserProfilePhotoWriteException(long userId, Throwable cause) {
		super(userId, cause);
	}

	public UserProfilePhotoWriteException(User user) {
		super(user);
	}

	public UserProfilePhotoWriteException(long userId) {
		super(userId);
	}

}
