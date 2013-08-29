package com.jl.crm.services;

import org.springframework.http.MediaType;

/**
 * Simple abstraction to hold information about the profile photo.
 */
public class ProfilePhoto {

	private Long userId;
	private byte[] photo;
	private MediaType mediaType;

	public ProfilePhoto(long userId, byte[] data, MediaType mediaType) {
		this.mediaType = mediaType;
		this.photo = data;
		this.userId = userId;
	}

	public MediaType getMediaType() {
		return this.mediaType;
	}

	public byte[] getPhoto() {
		return this.photo;
	}

	public Long getUserId() {
		return this.userId;
	}

}
