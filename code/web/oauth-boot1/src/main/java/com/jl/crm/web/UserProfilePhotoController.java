package com.jl.crm.web;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Links;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.jl.crm.services.CrmService;
import com.jl.crm.services.ProfilePhoto;
import com.jl.crm.services.User;
import com.jl.crm.services.UserProfilePhotoReadException;
import com.jl.crm.services.UserProfilePhotoWriteException;

@Controller
@RequestMapping(value = "/users/{userId}/photo")
class UserProfilePhotoController {

	final CrmService crmService;
	final UserLinks userLinks;

	@Autowired
	UserProfilePhotoController(CrmService crmService, UserLinks userLinks) {
		this.crmService = crmService;
		this.userLinks = userLinks;
	}

	@RequestMapping(method = RequestMethod.POST)
	HttpEntity<Void> writeUserProfilePhoto(
			@PathVariable Long userId,
			@RequestParam MultipartFile file) throws Throwable {

		if (userId == null) {
			throw new UserProfilePhotoWriteException(
					null,
					new RuntimeException("you need to specify a valid user ID#"));
		}
		User user = this.crmService.findById(userId);

		byte[] bytesForProfilePhoto = FileCopyUtils.copyToByteArray(file
				.getInputStream());

		this.crmService.writeUserProfilePhoto(user.getId(),
				MediaType.parseMediaType(file.getContentType()),
				bytesForProfilePhoto);

		Link photoLink = this.userLinks.getPhotoLink(user);
		Link userLink = this.userLinks.getSelfLink(user);
		Links wrapperOfLinks = new Links(photoLink, userLink);

		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.add("Link", wrapperOfLinks.toString());
		httpHeaders.setLocation(URI.create(photoLink.getHref())); // "Location: /users/{userId}/photo"

		return new ResponseEntity<Void>(httpHeaders, HttpStatus.ACCEPTED);
	}

	@RequestMapping(method = RequestMethod.GET)
	HttpEntity<byte[]> loadUserProfilePhoto(@PathVariable Long userId)
			throws Throwable {

		User user = this.crmService.findById(userId);
		if (user == null) {
			throw new UserProfilePhotoReadException(-1, new RuntimeException(
					"couldn't find the user"));
		}

		ProfilePhoto profilePhoto = this.crmService.readUserProfilePhoto(user
				.getId());
		if (profilePhoto == null) {
			throw new UserProfilePhotoReadException(-1, new RuntimeException(
					"couldn't find the user photo"));
		}

		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(profilePhoto.getMediaType());
		return new ResponseEntity<byte[]>(profilePhoto.getPhoto(), httpHeaders,
				HttpStatus.OK);
	}

}
