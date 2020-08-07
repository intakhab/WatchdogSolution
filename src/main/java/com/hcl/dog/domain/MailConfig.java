package com.hcl.dog.domain;
/**
 * 
 * @author intakhabalam.s@hcl.com
 * Mail Confi
 */
public class MailConfig {
	
	private String host="";
	private int port;
	private String username="";
	private String password="";
	private String fromMail="";
	private boolean debugMail=false;

	//
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getFromMail() {
		return fromMail;
	}
	public void setFromMail(String fromMail) {
		this.fromMail = fromMail;
	}
	public boolean isDebugMail() {
		return debugMail;
	}
	public void setDebugMail(boolean debugMail) {
		this.debugMail = debugMail;
	}
}
