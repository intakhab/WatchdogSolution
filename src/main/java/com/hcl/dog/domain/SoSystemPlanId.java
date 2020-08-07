package com.hcl.dog.domain;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "CISDocument")
@XmlAccessorType(XmlAccessType.FIELD)
public class SoSystemPlanId {
	@XmlElement
	private ResponseHeader ResponseHeader;
	
	@XmlElement
	private SOApiHeader ApiHeader;
	
	@XmlElement
	private SOEntity Entity;

	public ResponseHeader getResponseHeader() {
		return ResponseHeader;
	}

	public void setResponseHeader(ResponseHeader ResponseHeader) {
		this.ResponseHeader = ResponseHeader;
	}

	public SOApiHeader getApiHeader() {
		return ApiHeader;
	}

	public void setApiHeader(SOApiHeader ApiHeader) {
		this.ApiHeader = ApiHeader;
	}

	public SOEntity getEntity() {
		return Entity;
	}

	public void setEntity(SOEntity Entity) {
		this.Entity = Entity;
	}

}
@XmlRootElement(name = "ApiHeader")
@XmlAccessorType(XmlAccessType.FIELD)
class SOApiHeader {
	private String OperationName;

	public String getOperationName() {
		return OperationName;
	}

	public void setOperationName(String OperationName) {
		this.OperationName = OperationName;
	}

}