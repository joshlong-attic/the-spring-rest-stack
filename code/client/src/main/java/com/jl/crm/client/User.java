package com.jl.crm.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.hateoas.Link;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;

import java.util.Date;

@JsonIgnoreProperties (ignoreUnknown = true)
public class User {
	private Link selfLink ;
	private String firstName, lastName, username;
	private String profilePhotoMediaType;
	private boolean profilePhotoImported;
	private Date signupDate;

	User() {
	}


	public User( Link selfLink, String firstName, String lastName, String username, String profilePhotoMediaType, boolean hasProfilePhoto, Date signupDate) {
		this.selfLink = selfLink;
	}

	public User( String firstName, String lastName, String username, String profilePhotoMediaType, boolean hasProfilePhoto, Date signupDate) {

		this.firstName = firstName;
		this.lastName = lastName;
		this.username = username;
		this.profilePhotoMediaType = profilePhotoMediaType;
		this.profilePhotoImported = hasProfilePhoto;
		this.signupDate = signupDate;
	}



	public Link getId() {
		return this.selfLink;
	}
	public Long getDatabaseId(){
		String href = this.selfLink.getHref();
		return Long.parseLong(href.substring(href.lastIndexOf("/")+1) );
	}
	void setId(Link l) {
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
