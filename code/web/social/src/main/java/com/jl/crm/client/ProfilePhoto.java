package com.jl.crm.client;

import org.springframework.http.MediaType;

/**
 * Represents the payload and mime type of a profile photo.
 *
 * @author Josh Long
 */
public class ProfilePhoto {

	private final MediaType mediaType;

	private final byte[] bytes;

	public ProfilePhoto(byte[] data, MediaType mediaType) {
		this.bytes = data;
		this.mediaType = mediaType;
	}

	public MediaType getMediaType() {
		return this.mediaType;
	}

	public byte[] getBytes() {
		return this.bytes;
	}
}
