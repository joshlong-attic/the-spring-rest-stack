package com.jl.crm.web;

import com.jl.crm.services.CrmService;
import com.jl.crm.services.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Links;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceAssembler;
import org.springframework.http.*;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
@RequestMapping(value = "/users/{user}/photo")
class UserProfilePhotoController {

    private final CrmService crmService;
    private final ResourceAssembler<User, Resource<User>> userResourceAssembler;

    @Autowired
    UserProfilePhotoController(CrmService crmService, ResourceAssembler<User, Resource<User>> userResourceAssembler) {
        this.crmService = crmService;
        this.userResourceAssembler = userResourceAssembler;
    }

    @RequestMapping(method = POST)
    HttpEntity<Void> writeUserProfilePhoto(@PathVariable Long user, @RequestParam MultipartFile file) throws Throwable {
        byte bytesForProfilePhoto[] = FileCopyUtils.copyToByteArray(file.getInputStream());
        this.crmService.writeUserProfilePhoto(user, MediaType.parseMediaType(file.getContentType()), bytesForProfilePhoto);


        Resource<User> userResource = this.userResourceAssembler.toResource(crmService.findById(user));
        List<Link> linkCollection = userResource.getLinks();
        Links wrapperOfLinks = new Links(linkCollection);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Link", wrapperOfLinks.toString());  // we can't encode the links in the body of the response, so we put them in the "Links:" header.
        httpHeaders.setLocation(URI.create(userResource.getLink("photo").getHref())); // "Location: /users/{userId}/photo"

        return new ResponseEntity<>(httpHeaders, HttpStatus.ACCEPTED);
    }

    @RequestMapping(method = RequestMethod.GET)
    HttpEntity<byte[]> loadUserProfilePhoto(@PathVariable Long user) throws Exception{
        return Optional.of(this.crmService.readUserProfilePhoto(user))
                .map(profilePhoto -> {
                    HttpHeaders httpHeaders = new HttpHeaders();
                    httpHeaders.setContentType(profilePhoto.getMediaType());
                    return new ResponseEntity<>(profilePhoto.getPhoto(), httpHeaders, HttpStatus.OK);
                })
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));

    }
}
