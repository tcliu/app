package org.codelab.app.web.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class DefaultController {
	
	@RequestMapping("/hello")
	public ModelAndView hello() {
		return new ModelAndView("/WEB-INF/views/hello.html");
	}
	
	@RequestMapping("/admin")
	public ModelAndView admin() {
		return new ModelAndView("/WEB-INF/views/admin.html");
	}

}
