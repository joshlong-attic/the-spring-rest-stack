package com.jl.crm.services;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Thrown when the system fails to load a valid {@link User} profile photo image.
 *
 * @author Josh Long
 */
@ResponseStatus (HttpStatus.NOT_FOUND)
public class UserProfilePhotoReadException extends UserException {

	private static final long serialVersionUID = 1L;

	public UserProfilePhotoReadException(User user, Throwable cause) {
		super(user, cause);
	}

	public UserProfilePhotoReadException(long userId, Throwable cause) {
		super(userId, cause);
	}

	public UserProfilePhotoReadException(User user) {
		super(user);
	}

	public UserProfilePhotoReadException(long userId) {
		super(userId);
	}

}
