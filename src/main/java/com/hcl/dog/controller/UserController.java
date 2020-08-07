package com.hcl.dog.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Paths;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.bind.JAXBException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.hcl.dog.common.AppUtil;
import com.hcl.dog.domain.User;
import com.hcl.dog.domain.Users;
import com.hcl.dog.dto.SystemUserDto;
import com.hcl.dog.dto.UserDto;
import com.hcl.dog.service.AlertService;
import com.hcl.dog.service.CommonService;
import com.hcl.dog.service.SettingsService;
import com.hcl.dog.service.XMLUtilService;

/***
 * @author intakhabalam.s@hcl.com
 * @see Controller Login/User Registration
 * @see AlertService {@link AlertService}
 * @see Environment {@link Environment}
 * @see XMLUtilService {@link XMLUtilService}
 * @see SettingsService {@link SettingsService}
 * @see CommonService {@link CommonService}
 */
@Controller
public class UserController {

	@Autowired
	private AlertService alertService;
	@Autowired
	private Environment env;

	@Autowired
	private SettingsService dogConfigService;

	@Autowired
	private XMLUtilService xmlUtilService;

	@Autowired
	private CommonService commonService;

	/**
	 * will do redirect to login pages
	 * @return {@link String}
	 */
	@RequestMapping("/login")
	public String login() {
		return "login";
	}

	/***
	 * This method will logged in with credentials
	 * @param session {@link HttpSession}
	 * @param userDto {@link UserDto}
	 * @param model {@link Model}
	 * @return {@link String}
	 */
	@RequestMapping(value = "/dologin", method = RequestMethod.POST)
	public String doLogin(HttpSession session, @ModelAttribute("user") UserDto userDto, Model model) {

		try {
			Users users = loadAllusers();
			boolean isFound = false;
			if (users == null) {
				// check default login for system only
				isFound = checkSystemAdmin(userDto);

			} else {
				// again check if system is logging or not
				isFound = checkSystemAdmin(userDto);

				if (!isFound) { // Means normal users are logging

					for (User u : users.getUsers()) {
						if (userDto.getEmail().trim().equals(u.getEmail().trim())
								&& userDto.getUserpass().trim().equals(u.getUserpass().trim())) {
							userDto.setUsername(u.getUsername());
							userDto.setUserpass(AppUtil.EMPTY_STR);
							userDto.setSessionTime(env.getProperty("session.timeout"));
							isFound = true;
						}

						if (isFound && !u.isActive()) {
							model.addAttribute("msg", alertService.error(
									"Your account is not active. Please contact to system admin for activation."));
							return "login";
						}
					}
				}
			}

			if (isFound) {
				// Set user dummy data
				session.setAttribute("user", userDto);
				int sessionTime = Integer.valueOf(env.getProperty("session.timeout"));
				session.setMaxInactiveInterval(sessionTime);

			} else {
				model.addAttribute("msg", alertService.error("Login failed. Try again."));
				return "login";
			}
		} catch (FileNotFoundException | JAXBException e) {
			return "redirect:/login?msg=System Error!!! Please contact to System Admin.";

		} //
		return "redirect:/status";
	}

	/****
	 * @param userDto {@link UserDto}
	 * @return {@link Boolean}
	 */
	private boolean checkSystemAdmin(UserDto userDto) {
		SystemUserDto system = new SystemUserDto();
		if (userDto.getEmail().equals(system.getEmail()) && userDto.getUserpass().equals(system.getUserpass())) {
			userDto.setUsername("System");
			userDto.setUserpass(AppUtil.EMPTY_STR);
			return true;
		} else {
			return false;
		}
	}

	/***
	 * This method will restore the configuration file when db would crashed.
	 * @param model {@link Model}
	 * @return {@link String}
	 */
	@RequestMapping("/restoreuser")
	public String restoreUsers(ModelMap model) {
		try {
			commonService.reloaUserbackup();

		} catch (Exception e) {
			return "redirect:/errorpage?msg=User file loading error " + e.getMessage();

		}
		return "redirect:/showusers?msg=User loaded successfully!!!";
	}

	/***
	 * Logout
	 * @param session {@link HttpSession} 
	 * @return {@link String}
	 */
	@RequestMapping("/logout")
	public String logout(HttpSession session) {
		session.setAttribute("user", null);
		session.invalidate();
		return "redirect:/login?msg=You are now signed out!!!";
	}

	/**
	 * This will registered user
	 * @param session {@link HttpSession}
	 * @param model {@link Model}
	 * @return {@link String}
	 */
	@RequestMapping("/reguser")
	public String userRegis(HttpSession session, ModelMap model) {
		model.addAttribute("user", new UserDto());
		return "reguser";
	}

	/***
	 * This will edit user
	 * 
	 * @param request {@link HttpServletRequest}
	 * @param userDto {@link UserDto}
	 * @param model {@link ModelMap}
	 * @return {@link String}
	 */

	@RequestMapping("/edituser")
	public String edutUser(HttpServletRequest request, @ModelAttribute("user") UserDto userDto, ModelMap model) {
		String userId = request.getParameter("id");
		boolean isFound = false;
		try {
			Users users = loadAllusers();// loads all user
			for (User u : users.getUsers()) {

				if (u.getId().equals(userId)) {
					userDto.setActive(u.isActive());
					userDto.setCreateDate(u.getCreatedate());
					userDto.setEmail(u.getEmail());
					// userDto.setSessionTime(env.getProperty("session.timeout"));
					userDto.setId(u.getId());
					userDto.setUsername(u.getUsername());
					userDto.setUserpass(u.getUserpass());
					model.addAttribute("user", userDto);
					isFound = true;
					break;
				}
			}

		} catch (FileNotFoundException | JAXBException e) {
			return "redirect:/errorpage?msg=Users not found!" + e.getMessage();

		}
		if (isFound) {
			return "edituser";
		}
		return "showusers";
	}

	/**
	 * This will save user
	 * @param user {@link UserDto}
	 * @param result {@link BindingResult} 
	 * @param model {@link Model} 
	 * @return {@link String} 
	 */

	@RequestMapping("/saveuser")
	public String saveUser(@ModelAttribute("user") UserDto user, BindingResult result, Model model) {
		try {
			user = dogConfigService.saveUserInfo(user);
			model.addAttribute("msg", alertService.sucess("User saved successfully"));
			model.addAttribute("user", user);
		} catch (FileNotFoundException | JAXBException e) {
			return "redirect:/errorpage?msg=User saving problem!" + e.getMessage();

		}

		return "redirect:/showusers?msg=User saved successfully!!!";
	}

	/***
	 * This will update user
	 * 
	 * @param  user {@link User}
	 * @param result {@link BindingResult}
	 * @param model link {@link Model}
	 * @return {@link String}
	 */
	@RequestMapping("/updateuser")
	public String updateUser(@ModelAttribute("user") User user, BindingResult result, Model model) {
		try {
			commonService.backupUserFile();
			xmlUtilService.editUserXMLWithAttributeId(Paths.get(env.getProperty("db.user")).toFile(), user);
		} catch (Exception e) {
			return "redirect:/errorpage?msg=User updating problem " + e.getMessage();
		}

		return "redirect:/showusers?msg=User updated successfully!!!";
	}

	/**
	 * Check duplicate email in system 
	 * @param req {@link HttpServletRequest}
	 * @return {@link Boolean}
	 */
	@RequestMapping("/emailcheck")
	@ResponseBody
	public boolean checkEmail(HttpServletRequest req) {

		String s = req.getParameter("email");
		boolean isFoundEmail = false;
		try {
			Users user = loadAllusers();// loads all user
			Iterator<User> it = user.getUsers().iterator();
			while (it.hasNext()) {
				User u = (User) it.next();
				if (u.getEmail().equals(s)) {
					isFoundEmail = true;
					break;
				}
			}
		} catch (FileNotFoundException | JAXBException e) {
			isFoundEmail = false;
		}
		return isFoundEmail;
	}

	/***
	 * @throws FileNotFoundException as {@link FileNotFoundException}
	 * @throws JAXBException as {@link JAXBException}
	 * @return as {@link Users}
	 */
	private Users loadAllusers() throws FileNotFoundException, JAXBException {
		File userFile = Paths.get(env.getProperty("db.user")).toFile();
		if (!userFile.exists()) {
			return null;
		}
		return xmlUtilService.convertXMLToObject(Users.class, userFile);
	}

	@RequestMapping("/showusers")
	public ModelAndView showUsers(ModelMap model) {
		ModelAndView mav = new ModelAndView();
		try {
			Users users = loadAllusers();
			if (users == null) {
				// Users not found, empty user
				model.addAttribute("users", new Users());
			} else {
				model.addAttribute("users", users.getUsers());
			}
			mav.setViewName("showusers");
			return mav;

		} catch (FileNotFoundException | JAXBException e) {
		}
		return null;

	}

}

