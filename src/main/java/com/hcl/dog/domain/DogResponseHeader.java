package com.hcl.dog.domain;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "ResponseHeader")
@XmlAccessorType(XmlAccessType.FIELD)
public class DogResponseHeader {

	@XmlElement
	private String CompletedSuccessfully;
	@XmlElement
	private DogError Error;

	public String getCompletedSuccessfully() {
		return CompletedSuccessfully;
	}

	public void setCompletedSuccessfully(String completedSuccessfully) {
		CompletedSuccessfully = completedSuccessfully;
	}

	public DogError getError() {
		return Error;
	}

	public void setError(DogError error) {
		Error = error;
	}

}
