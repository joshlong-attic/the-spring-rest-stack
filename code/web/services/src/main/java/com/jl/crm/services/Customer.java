package com.jl.crm.services;

import com.fasterxml.jackson.annotation.*;
import org.apache.commons.lang.builder.*;
import org.springframework.hateoas.Identifiable;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Represents a customer record.
 *
 * @author Josh Long
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY,isGetterVisibility = JsonAutoDetect.Visibility.ANY,getterVisibility = JsonAutoDetect.Visibility.ANY)
@Entity (name = "Customer")
@Table (name = "customer")
public class Customer implements Identifiable<Long>, Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue (strategy = GenerationType.SEQUENCE)
	@Column (name = "id", unique = true, nullable = false )
	private Long id;

	@JsonIgnore
	@ManyToOne (fetch = FetchType.LAZY)
	@JoinColumn (nullable = true, name = "customer_user_id_fkey") // TODO fixme
	private User user;

	@Column (name = "signup_date")
	private Date signupDate;

	@Column (name = "first_name")
	private String firstName;

	@Column (name = "last_name")
	private String lastName;

	@JsonProperty
	Long getDatabaseId() {
		return this.id;
	}

	public Customer() {
	}

	public Customer(Customer c) {
		this(new User(c.user), c);
	}

	public Customer(User user, Customer c) {
		this.firstName = c.firstName;
		this.lastName = c.lastName;
		this.signupDate = c.signupDate;
		this.user = user;
		this.id = c.id;
	}

	public Customer(User user, String firstName, String lastName, Date signupDate) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.signupDate = signupDate;
		this.user = user;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(55, 113).append(this.id).append(this.firstName).append(
				this.lastName).toHashCode();
	}

	@Override
	public boolean equals(Object o) {
		Customer other = (Customer) o;
		return new EqualsBuilder().append(other.firstName, this.firstName).append(
				other.lastName, this.lastName).append(other.id, this.id).isEquals();
	}

	@Override
	public Long getId() {
		return this.id;
	}

	public void setDatabaseId(Long id) {
		this.id = id;
	}

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

}
