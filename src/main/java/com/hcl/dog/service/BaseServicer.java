package com.hcl.dog.service;

import javax.servlet.http.HttpSession;

import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import com.hcl.dog.dto.UserDto;
import com.hcl.dog.filter.WebFilter;
/***
 * @author intakhabalam.s@hcl.com
 * @see ApplicationRunner
 * @see Component
 * @see Repository
 * @see Service
 * @see Controller
 * @see WebFilter
 */

@Service
public class BaseServicer {
	
  /**
   * 
   * @param session {@link HttpSession}
   * @return {@link UserDto}
   */
	public UserDto getCurrentUserInfo( HttpSession session){
	   UserDto user = (UserDto)session.getAttribute("user");
	   return user;
   }

}

