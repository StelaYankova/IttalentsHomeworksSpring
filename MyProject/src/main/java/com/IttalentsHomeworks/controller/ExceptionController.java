package com.IttalentsHomeworks.controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;

@Controller
@ControllerAdvice
public class ExceptionController {
	
	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	public String handleNoGET(Exception ex) {
	    return "redirect:/404";
	} 
	@ExceptionHandler(NoHandlerFoundException.class)
	public String handle404(Exception ex) {
	    return "redirect:/404";
	}
	
	@RequestMapping(value = {"/404"}, method = RequestMethod.GET)
	public String NotFoudPage() {
	    return "pageNotFound";
	}
	
	@ExceptionHandler(Exception.class)
	public String exception(Exception e) {
		ModelAndView mav = new ModelAndView("exception");
		mav.addObject("name", e.getClass().getSimpleName());
		mav.addObject("message", e.getMessage());
		e.printStackTrace(System.err);
		return "exception";
	}
	
}