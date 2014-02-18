package com.jl.crm.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.jl.crm.client.meta.Resource;

import java.util.Date;

/**
 * Client side representation of customer data from the REST service.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Customer extends Resource {

    private String firstName;
    private String lastName;
    private Date signupDate;
    private User user;
    private Long id;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Date getSignupDate() {
        return signupDate;
    }

    public void setSignupDate(Date signupDate) {
        this.signupDate = signupDate;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    Customer() {
    }

    public Customer(User user, Long id, String firstName, String lastName, Date signupDate) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.signupDate = signupDate;
        this.user = user;
        this.id = id;
    }
    /*Customer() {
    }

	public Customer(User user, String firstName, String lastName) {
		this(user, null, firstName, lastName, null);
	}

	public Customer(User user, Link selfLink, String firstName, String lastName, Date signupDate) {
		this.selfLink = selfLink;
		this.user = user;
		this.signupDate = signupDate;
		this.firstName = firstName;
		this.lastName = lastName;
	}

	@Override
	public String toString() {
		return "Customer{" + "firstName='" + firstName + '\'' + ", lastName='" + lastName
				+ '\'' + ", signupDate=" + signupDate + '}';
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

	public User getUser() {
		return this.user;
	}

	public Date getSignupDate() {
		return this.signupDate;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}*/
}
