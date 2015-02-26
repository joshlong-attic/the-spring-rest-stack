package com.jl.crm.services;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Repository for dealing with {@link Customer customer } records.
 *
 * @author Josh Long
 */
 public interface CustomerRepository extends PagingAndSortingRepository<Customer, Long> {


	List<Customer> findByUserId(@Param("userId") Long userId);

	@Query ("select c from Customer c where  c.user.id = :userId and (LOWER(concat(c.firstName, c.lastName)) LIKE :q   )")
	List<Customer> search(@Param("userId") Long userId,  @Param("q") String query);

}
