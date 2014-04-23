package com.jl.crm.web;

import com.jl.crm.services.CrmService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Collections;
import java.util.Optional;

@RestController
@RequestMapping(value = "/users/{user}/photo")
class UserProfilePhotoController {

    private final CrmService crmService;

    @Autowired
    UserProfilePhotoController(CrmService crmService) {
        this.crmService = crmService;
    }

    @RequestMapping(method = RequestMethod.POST)
    HttpEntity<Void> writeUserProfilePhoto(@PathVariable Long user,
                                           @RequestParam MultipartFile file) throws Throwable {
        byte bytesForProfilePhoto[] = FileCopyUtils.copyToByteArray(file.getInputStream());
        this.crmService.writeUserProfilePhoto(user, MediaType.parseMediaType(file.getContentType()), bytesForProfilePhoto);
        HttpHeaders httpHeaders = new HttpHeaders();
        URI uriOfPhoto = ServletUriComponentsBuilder.fromCurrentContextPath()
                .pathSegment(("/users" + "/{user}" + "/photo").substring(1))
                .buildAndExpand(Collections.singletonMap("user", user))
                .toUri();
        httpHeaders.setLocation(uriOfPhoto);
        return new ResponseEntity<>(httpHeaders, HttpStatus.CREATED);
    }

    @RequestMapping(method = RequestMethod.GET)
    HttpEntity<byte[]> loadUserProfilePhoto(@PathVariable Long user) throws Exception {
        return Optional.of(this.crmService.readUserProfilePhoto(user))
                .map(profilePhoto -> {
                    HttpHeaders httpHeaders = new HttpHeaders();
                    httpHeaders.setContentType(profilePhoto.getMediaType());
                    return new ResponseEntity<>(profilePhoto.getPhoto(), httpHeaders, HttpStatus.OK);
                })
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}
