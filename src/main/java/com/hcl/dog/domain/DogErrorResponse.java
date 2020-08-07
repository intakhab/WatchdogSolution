package com.hcl.dog.domain;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "CISDocument")
@XmlAccessorType(XmlAccessType.FIELD)
public class DogErrorResponse {
	@XmlElement
	private DogResponseHeader ResponseHeader;

	public DogResponseHeader getResponseHeader() {
		return ResponseHeader;
	}

	public void setResponseHeader(DogResponseHeader responseHeader) {
		ResponseHeader = responseHeader;
	}
}

