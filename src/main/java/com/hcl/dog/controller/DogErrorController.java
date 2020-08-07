package com.hcl.dog.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
/***
 * 
 * @author intakhabalam.s@hcl.com
 * @see Controller {@link Controller}
 * @see ErrorController {@link ErrorController}
 *
 */
@Controller
public class DogErrorController implements ErrorController{
 
	
    @RequestMapping("/errorpage")
    public ModelAndView renderErrorPage(HttpServletRequest httpRequest) {
        ModelAndView errorPage = new ModelAndView("errorpage");
        String errorMsg = httpRequest.getParameter("msg");
        errorPage.addObject("errorMsg", errorMsg);
        return errorPage;
    }
     
 
    @Override
    public String getErrorPath() {
        return "/errorpage";
    }
}
