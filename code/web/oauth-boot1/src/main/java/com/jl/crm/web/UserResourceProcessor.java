package com.jl.crm.web;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceProcessor;
import org.springframework.stereotype.Component;

import com.jl.crm.services.User;

@Component
class UserResourceProcessor implements ResourceProcessor<Resource<User>> {

    UserLinks userLinks;

    @Autowired
    UserResourceProcessor(UserLinks userLinks) {
        this.userLinks = userLinks;
    }

    @Override
    public Resource<User> process(Resource<User> ur) {
        User user = ur.getContent();
        ur.add(userLinks.getPhotoLink(user));
        ur.add(userLinks.getCustomersLink(user));
        return ur;
    }

}
