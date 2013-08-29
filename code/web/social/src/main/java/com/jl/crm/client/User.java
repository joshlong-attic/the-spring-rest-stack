package com.jl.crm.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;

import java.util.Date;

@JsonIgnoreProperties (ignoreUnknown = true)
public class User {
	private String selfLink ;
	private String firstName, lastName, username;
	private String profilePhotoMediaType;
	private boolean profilePhotoImported;
	private Date signupDate;

	User() {
	}

	@Override
	public String toString() {
		return "User{" + "firstName='" + firstName + '\'' + ", username='" + username
				+ '\'' + ", lastName='" + lastName + '\'' + '}';
	}

	public User( String firstName, String lastName, String username, String profilePhotoMediaType, boolean hasProfilePhoto, Date signupDate) {

		this.firstName = firstName;
		this.lastName = lastName;
		this.username = username;
		this.profilePhotoMediaType = profilePhotoMediaType;
		this.profilePhotoImported = hasProfilePhoto;
		this.signupDate = signupDate;
	}



	public Long getDatabaseId(){
		String href = this.selfLink ;
		return Long.parseLong(href.substring(href.lastIndexOf("/")+1) );
	}

	public void setId( String  l) {
		this.selfLink = l;
	}


	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public String getUsername() {
		return username;
	}

	public MediaType getProfilePhotoMediaType() {
		return StringUtils.hasText(profilePhotoMediaType) ? MediaType.parseMediaType(profilePhotoMediaType) : null;
	}

	public boolean isProfilePhotoImported() {
		return profilePhotoImported;
	}

	public Date getSignupDate() {
		return signupDate;
	}


}
