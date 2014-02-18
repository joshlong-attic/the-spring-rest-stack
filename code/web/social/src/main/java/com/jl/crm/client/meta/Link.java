package com.jl.crm.client.meta;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * describes a link to a URL and relationship.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Link {
    @Override
    public String toString() {
        return "Link{" +
                "rel='" + rel + '\'' +
                ", href='" + href + '\'' +
                '}';
    }

    public Link() {
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public String getRel() {
        return rel;
    }

    public void setRel(String rel) {
        this.rel = rel;
    }

    public Link(String rel, String href) {

        this.href = href;
        this.rel = rel;
    }

    private String href, rel;
}
