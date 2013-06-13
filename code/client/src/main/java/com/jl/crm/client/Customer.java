package com.jl.crm.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.hateoas.Link;

import java.util.Date;

/** client side representation of customer data from the REST service. */
@JsonIgnoreProperties (ignoreUnknown = true)
public class Customer {
	private Link selfLink;
	private String firstName, lastName;
	private Date signupDate;
	private User user;

	Customer() {
	}

	public Customer(User user, String firstName, String lastName) {
		this(user, null, firstName, lastName, null );
	}

	public Customer(User user, Link selfLink, String firstName, String lastName, Date signupDate) {
		this.selfLink = selfLink;
		this.user = user;
		this.signupDate = signupDate;
		this.firstName = firstName;
		this.lastName = lastName;
	}

	public Link getId() {
		return this.selfLink;
	}

	void setId(Link l) {
		this.selfLink = l;
	}

	public Long getDatabaseId() {
		if (null == this.selfLink){
			return null;
		}
		String href = this.selfLink.getHref();
		return Long.parseLong(href.substring(href.lastIndexOf("/") + 1));
	}

	public User getUser(){
		return this.user ;
	}
	public Date getSignupDate() {
		return this.signupDate;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}
}
