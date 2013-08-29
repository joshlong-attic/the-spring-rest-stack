package org.springframework.hateoas;


import java.io.Serializable;

public class Link implements Serializable {

	private String rel;
	private String href;

	public String getHref() {
		return href;
	}

	public String getRel() {
		return rel;
	}

}
