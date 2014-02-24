package com.jl.crm.services;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 *
 * Base services for persisting {@link User} users
 *
 */
 public interface UserRepository extends PagingAndSortingRepository<User, Long> {

	User findByUsername(@Param("username") String username);

	List<User> findUsersByFirstNameOrLastNameOrUsername(
			@Param("firstName") String firstName,
            @Param("lastName") String lastName,
			@Param("username") String username);

}
