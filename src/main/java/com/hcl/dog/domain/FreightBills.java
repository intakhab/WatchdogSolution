package com.hcl.dog.domain;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "CISDocument")
@XmlAccessorType(XmlAccessType.FIELD)
public class FreightBills {
	@XmlElement
	private ResponseHeader ResponseHeader;
	
	@XmlElement
	private FreightApiHeader ApiHeader;

	@XmlElement
	private FreightEntity[] Entity;

	public ResponseHeader getResponseHeader() {
		return ResponseHeader;
	}

	public void setResponseHeader(ResponseHeader responseHeader) {
		ResponseHeader = responseHeader;
	}

	public FreightApiHeader getApiHeader() {
		return ApiHeader;
	}

	public void setApiHeader(FreightApiHeader apiHeader) {
		ApiHeader = apiHeader;
	}

	public FreightEntity[] getEntity() {
		return Entity;
	}

	public void setEntity(FreightEntity[] entity) {
		Entity = entity;
	}
	
	

}
@XmlRootElement(name = "ResponseHeader")
@XmlAccessorType(XmlAccessType.FIELD)
class ResponseHeader {
	
	@XmlElement
	private String CompletedSuccessfully;

	public String getCompletedSuccessfully() {
		return CompletedSuccessfully;
	}

	public void setCompletedSuccessfully(String CompletedSuccessfully) {
		this.CompletedSuccessfully = CompletedSuccessfully;
	}

}
@XmlRootElement(name = "FreightApiHeader")
@XmlAccessorType(XmlAccessType.FIELD)
class FreightApiHeader {
	
	@XmlElement
	private String OperationName;

	public String getOperationName() {
		return OperationName;
	}

	public void setOperationName(String OperationName) {
		this.OperationName = OperationName;
	}

}
