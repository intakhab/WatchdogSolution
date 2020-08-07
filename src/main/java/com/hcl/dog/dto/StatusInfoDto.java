package com.hcl.dog.dto;

import javax.xml.bind.annotation.XmlRootElement;

/***
 * 
 * @author intakhabalam.s
 *
 */
@XmlRootElement
public class StatusInfoDto {
	
	private String serverStatus="";
	private String hostAddress="";
	private String hostName="";
	private String cononicalHostName="";
	private String userName="";
	private String pid;
	private String versionId;
	private String port;
	private String startedTime;
	
	public String getServerStatus() {
		return serverStatus;
	}

	public void setServerStatus(String serverStatus) {
		this.serverStatus = serverStatus;
	}

	public String getHostName() {
		return hostName;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	public String getCononicalHostName() {
		return cononicalHostName;
	}

	public void setCononicalHostName(String cononicalHostName) {
		this.cononicalHostName = cononicalHostName;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getHostAddress() {
		return hostAddress;
	}

	public void setHostAddress(String hostAddress) {
		this.hostAddress = hostAddress;
	}

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public String getVersionId() {
		return versionId;
	}

	public void setVersionId(String versionId) {
		this.versionId = versionId;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getStartedTime() {
		return startedTime;
	}

	public void setStartedTime(String startedTime) {
		this.startedTime = startedTime;
	}

}
