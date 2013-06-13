package com.jl.crm.services;

import org.apache.commons.io.IOUtils;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.util.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.*;
import java.net.URI;
import java.util.Collections;

/**
 * simple client that posts files to the photo upload endpoint.
 *
 * @author Josh Long
 */
public class ProfilePhotoUploadClient {
	private URI baseUri;
	private RestTemplate restTemplate;

	public ProfilePhotoUploadClient(String baseUrl) throws Throwable {
		this(baseUrl, new RestTemplate());
	}

	public ProfilePhotoUploadClient(String baseUrl, RestTemplate restTemplate) throws Throwable {
		this.baseUri = new URI(baseUrl);
		this.restTemplate = restTemplate;
	}

	public static void main(String[] args) throws Throwable {

		File file = new File("/Users/jlong/Desktop/file.png");

		ProfilePhotoUploadClient profilePhotoUploadClient = new ProfilePhotoUploadClient("http://localhost:8080/");
		profilePhotoUploadClient.postProfilePhoto(21, file);

	}

	public URI postProfilePhoto(long userId, File file) throws IOException {
		FileInputStream fileInputStream = null;
		try {
			fileInputStream = new FileInputStream(file);
			byte bytesFromImage[] = IOUtils.toByteArray(fileInputStream);
			return this.postProfilePhoto(userId, file.getName(), bytesFromImage);
		}
		finally {
			IOUtils.closeQuietly(fileInputStream);
		}
	}

	public URI postProfilePhoto(long userId, final String fileName, byte[] bytesForImage) throws IOException {

		URI uri = UriComponentsBuilder.fromUri(this.baseUri)
				            .path("/users/{user}/photo")
				            .buildAndExpand(Collections.singletonMap("user", userId))
				            .toUri();

		ByteArrayResource byteArrayResource = new ByteArrayResource(bytesForImage) {
			@Override
			public String getFilename() {
				return fileName;
			}
		};

		MultiValueMap<String, Object> parts = new LinkedMultiValueMap<String, Object>();
		parts.set("file", byteArrayResource);

		return this.restTemplate.postForLocation(uri.toString(), parts);
	}
}
