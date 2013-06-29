package com.jl.crm.client;

import org.apache.commons.logging.*;
import org.springframework.social.connect.*;

/***
 * @author Josh Long
 */
public class CrmApiAdapter implements ApiAdapter<CrmOperations> {
	private Log log = LogFactory.getLog(getClass());

	@Override
	public boolean test(CrmOperations customerServiceOperations) {
		return (null != customerServiceOperations.currentUser());
	}

	@Override
	public void setConnectionValues(CrmOperations customerServiceOperations, ConnectionValues values) {
		User profile = customerServiceOperations.currentUser();
		values.setProviderUserId(Long.toString(profile.getDatabaseId()));
		values.setDisplayName(profile.getUsername());
	}

	@Override
	public  UserProfile fetchUserProfile(CrmOperations customerServiceOperations) {
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
		log.info(String.format("calling updateStatus(CustomerServiceOperations customerServiceOperations, " +
		                       "String message) with the status '%s', but this method is a no-op!", message));
	}
}
