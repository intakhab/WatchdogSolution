package com.hcl.dog.domain;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Select")
@XmlAccessorType(XmlAccessType.FIELD)
public class Select {

	@XmlElement
	private String Name = "FreightBillID";

	public String getName() {
		return Name;
	}

	public void setName(String Name) {
		this.Name = Name;
	}
}