package com.jl.crm.web;

import com.jl.crm.services.Customer;
import com.jl.crm.services.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityLinks;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Component;


@Component
class CustomerLinks {

    String usersRel = "user";
    EntityLinks entityLinks;

    @Autowired
    CustomerLinks(EntityLinks entityLinks) {
        this.entityLinks = entityLinks;
    }

    Link getUserLink(Customer customer) {
        return entityLinks.linkForSingleResource(User.class, customer.getUser().getId()).withRel(usersRel);
    }

}
