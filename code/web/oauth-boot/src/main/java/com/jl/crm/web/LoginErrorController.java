package com.jl.crm.web;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
class LoginErrorController {

	@RequestMapping("/login-error")
	String error(Model model, HttpServletRequest request) {

		// request state
		for (String k : Collections.list(request.getAttributeNames()))
			System.out.println(k + "=" + request.getAttribute(k));

		// session state 
		HttpSession session = request.getSession(false);
		if (null != session) {
			for (String k : Collections.list(session.getAttributeNames()))
				System.out.println(k + "=" + session.getAttribute(k));
		}

		// model state
		Map<String, Object> vals = model.asMap();
		for (String k : vals.keySet())
			System.out.println(k + "=" + vals.get(k));

		return "login";
	}
}
