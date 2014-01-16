package com.jl.crm.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class PageControllers {

	@RequestMapping ("/crm/welcome.html")
	public String welcome() {
		return "welcome";
	}

	@RequestMapping ("/crm/signin.html")
	public String signin() {
		return "signin";
	}

}
