package com.hcl.dog.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
/**
 * 
 * @author intakhabalam.s@hcl.com
 * @see {@link ControllerAdvice}
 * @see {@link ExceptionHandler}
 *
 */
@ControllerAdvice
public class GlobalExceptionHandler {
	/**
	 * 
	 * @param request as {@link HttpServletRequest}
	 * @param ex as {@link Exception}
	 * @return {@link String}
	 */
	@ExceptionHandler(Exception.class)
	public String handleSQLException(HttpServletRequest request, Exception ex){
		request.setAttribute("param1", ex.getLocalizedMessage() +"{ }  "+" "+ex.getStackTrace()[0]);
		return "rediret:/errorpage";
	}
	/**
	 * 
	 * @param request {@link HttpServletRequest}
	 * @param ex {@link Exception}
	 * @return {@link String}
	 */
    @ResponseStatus(value=HttpStatus.NOT_FOUND, reason="Exception occured")
	@ExceptionHandler(IOException.class)
	public String handleIOException(HttpServletRequest request, Exception ex){
    	request.setAttribute("param1", ex.getLocalizedMessage() +"{ }  "+" "+ex.getStackTrace()[0]);
		return "rediret:/errorpage";
	}
}
