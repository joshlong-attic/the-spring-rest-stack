package com.jl.crm.client.meta;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;
import java.util.Map;

/**
 * client-side mirror to the Spring HATEOAS Resource type so
 * I don't need to link to all of Spring HATEOAS.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class Resource {

    @JsonProperty("_links")
    private Map<String, Object> links = new HashMap<String, Object>();

    public Resource() {
    }

    public Map<String, Object> getLinks() {
        return links;
    }

    public void setLinks(Map<String, Object> links) {
        this.links = links;
    }
}
