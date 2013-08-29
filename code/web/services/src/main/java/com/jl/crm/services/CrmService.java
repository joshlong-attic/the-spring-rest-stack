package com.jl.crm.services;

import org.springframework.http.MediaType;

import java.util.Collection;

/**
 * @author Josh Long
 */
public interface CrmService {

	Collection<Customer> search( long userId, String token) ;

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

}
