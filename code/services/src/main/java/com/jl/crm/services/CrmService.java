package com.jl.crm.services;

import org.springframework.http.MediaType;

import java.util.Collection;

/** @author Josh Long */
public interface CrmService {

	ProfilePhoto readUserProfilePhoto(long userId);

	void writeUserProfilePhoto(long userId, MediaType mediaType, byte[] bytesForProfilePhoto);

	User findById(long userId);

	User createUser(String username, String password, String firstName, String lastName);

	User removeUser(long userId);

	User updateUser(long userId, String username, String password, String firstName, String lastName);

	User findUserByUsername(String username);

	Customer removeAccount(long userId, long customerId);

	Customer addAccount(long userId, String firstName, String lastName);

	Collection<Customer> loadCustomerAccounts(long userId);

	Customer findCustomerById(long customerId);

	/** simple abstraction to hold information about the profile photo. */
	static class ProfilePhoto {
		private Long userId;
		private byte[] photo;
		private MediaType mediaType;

		public ProfilePhoto(long userId, byte[] data, MediaType mediaType) {
			this.mediaType = mediaType;
			this.photo = data;
			this.userId = userId;
		}

		public MediaType getMediaType() {
			return this.mediaType;
		}

		public byte[] getPhoto() {
			return this.photo;
		}

		public Long getUserId() {
			return this.userId;
		}
	}

}
