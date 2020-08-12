package com.hcl.dog.domain;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Error")
@XmlAccessorType(XmlAccessType.FIELD)
public class DogError {
	
	@XmlElement
	private String Severity="";
	@XmlElement
	private String Item="";
	@XmlElement
	private String UserMessage="";
	@XmlElement
	private String Code="";
	@XmlElement
	private String SystemMessage="";
	
	
	public String getSeverity() {
		return Severity;
	}

	public void setSeverity(String Severity) {
		this.Severity = Severity;
	}

	public String getItem() {
		return Item;
	}

	public void setItem(String Item) {
		this.Item = Item;
	}

	public String getUserMessage() {
		return UserMessage;
	}

	public void setUserMessage(String UserMessage) {
		this.UserMessage = UserMessage;
	}

	public String getCode() {
		return Code;
	}

	public void setCode(String Code) {
		this.Code = Code;
	}

	public String getSystemMessage() {
		return SystemMessage;
	}

	public void setSystemMessage(String SystemMessage) {
		this.SystemMessage = SystemMessage;
	}

}
