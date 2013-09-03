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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;

        User user = (User) o;

        if (profilePhotoImported != user.profilePhotoImported) return false;
        if (!firstName.equals(user.firstName)) return false;
        if (!lastName.equals(user.lastName)) return false;
        if (profilePhotoMediaType != null ? !profilePhotoMediaType.equals(user.profilePhotoMediaType) : user.profilePhotoMediaType != null)
            return false;
        if (selfLink != null ? !selfLink.equals(user.selfLink) : user.selfLink != null) return false;
        if (signupDate != null ? !signupDate.equals(user.signupDate) : user.signupDate != null) return false;
        if (!username.equals(user.username)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = selfLink != null ? selfLink.hashCode() : 0;
        result = 31 * result + firstName.hashCode();
        result = 31 * result + lastName.hashCode();
        result = 31 * result + username.hashCode();
        result = 31 * result + (profilePhotoMediaType != null ? profilePhotoMediaType.hashCode() : 0);
        result = 31 * result + (profilePhotoImported ? 1 : 0);
        result = 31 * result + (signupDate != null ? signupDate.hashCode() : 0);
        return result;
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
