package com.jl.crm.client;

import org.springframework.social.connect.*;
import org.springframework.web.client.HttpStatusCodeException;

/** @author Josh Long */
public class CrmApiAdapter implements ApiAdapter<CrmOperations> {

	@Override
	public boolean test(CrmOperations customerServiceOperations) {
		boolean everythingAlright = false;
		try {
			everythingAlright = (null != customerServiceOperations.currentUser());
		}
		catch (HttpStatusCodeException e) {
			// ignore since we're just trying to test for connectivity.
			System.err.println( "HttpStatusCodeException : something very wrong happened here. " + e);
		}
		catch ( Error r) {
			 System.err.println( "Error : Something very wrong happened here. " + r );
		}
		return everythingAlright;
	}

	@Override
	public void setConnectionValues(CrmOperations customerServiceOperations, ConnectionValues values) {
		User profile = customerServiceOperations.currentUser();
		values.setProviderUserId(Long.toString(profile.getId()));
		values.setDisplayName(profile.getUsername());
	}

	@Override
	public UserProfile fetchUserProfile(CrmOperations customerServiceOperations) {
		User user = customerServiceOperations.currentUser();
		String name = user.getFirstName() + ' ' + user.getLastName();
		return new UserProfileBuilder()
				         .setName(name)
				         .setUsername(user.getUsername())
				         .setFirstName(user.getFirstName())
				         .setLastName(user.getLastName())
				         .build();
	}

	@Override
	public void updateStatus(CrmOperations customerServiceOperations, String message) {
		System.out.println(String.format("calling updateStatus(CustomerServiceOperations customerServiceOperations, " +
		                                 "String message) with the status '%s', but this method is a no-op!", message));
	}
}
