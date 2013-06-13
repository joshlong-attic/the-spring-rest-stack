package com.jl.crm.services;

import org.springframework.data.domain.*;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.repository.annotation.RestResource;

import java.util.List;

/**
 * a repository for dealing with {@link Customer customer } records.
 *
 * @author Josh Long
 */
@RestResource (path = "customers", rel = "customers")
public interface CustomerRepository extends PagingAndSortingRepository<Customer, Long> {
	Page<Customer> findByUserId(Long id, Pageable pageable);

	List<Customer> findByUserId(Long id);
}
