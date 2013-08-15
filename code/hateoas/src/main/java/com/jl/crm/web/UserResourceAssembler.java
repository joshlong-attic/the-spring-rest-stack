package com.jl.crm.web;

import com.jl.crm.services.User;
import org.springframework.hateoas.*;
import org.springframework.stereotype.Component;

import java.util.*;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

/**
 * handles the user information.
 *
 * @author Josh Long
 */
@Component
class UserResourceAssembler implements ResourceAssembler<User, Resource<User>> {

	public static final String CUSTOMERS_REL = "customers";

	public static final String PHOTO_REL = "photo";

	@Override
	public Resource<User> toResource(User  u ) {
		try {

			User user =new User( u) ;
			user.setPassword(null );

			long userId = user.getId();
			Resource<User> userResource = new Resource<User>(user);
			Collection<Link> links = new ArrayList<Link>();
			links.add(linkTo(methodOn(UserController.class).loadUser(userId)).withSelfRel());
			links.add(linkTo(methodOn(UserController.class).loadUserCustomers( userId)).withRel(CUSTOMERS_REL));
			links.add(linkTo(methodOn(UserProfilePhotoController.class).loadUserProfilePhoto(user.getId() )).withRel(PHOTO_REL));
			for (Link l : links) {
				userResource.add(l);
			}
			return userResource;

		} catch (Exception throwable) {
			throw new RuntimeException(throwable);
		}

	}
}
