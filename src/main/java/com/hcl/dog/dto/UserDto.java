package com.hcl.dog.dto;

/**
 * 
 * @author intakhabalam.s@hcl.com
 *
 */
public class UserDto {
	
	private String id;
	private String username;
	private String userpass;
	private String email;
	private boolean active = true;
	private String createDate;
	private String sessionTime="";

	public UserDto() {
		
	}
	public UserDto(String username) {
		this.username=username;
	}

	public String getUsername() {
		return username;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getUserpass() {
		return userpass;
	}

	public void setUserpass(String userpass) {
		this.userpass = userpass;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCreateDate() {
		return createDate;
	}

	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}

	public String getSessionTime() {
		return sessionTime;
	}

	public void setSessionTime(String sessionTime) {
		this.sessionTime = sessionTime;
	}

}
