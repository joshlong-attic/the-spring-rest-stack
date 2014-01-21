package com.jl.crm.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class PageControllers {
	
	@RequestMapping("/login")
	String login() {
		return "login";
	}

	@RequestMapping("/")
	String index() {
		return "home";
	}

	@RequestMapping("/home")
	String home() {
		return "home";
	}

	@RequestMapping("/hello")
	String hello() {
		return "hello";
	}

}
