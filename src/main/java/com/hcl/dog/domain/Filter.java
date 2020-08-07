package com.hcl.dog.domain;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Filter")
@XmlAccessorType(XmlAccessType.FIELD)
public class Filter {
	
	@XmlElement
	private String Name="FreightBillNumber";
	
	@XmlElement
	private String Op="In";
	
	@XmlElement
	private String[] Value;

	public String getName() {
		return Name;
	}

	public void setName(String Name) {
		this.Name = Name;
	}

	public String[] getValue() {
		return Value;
	}

	public void setValue(String[] Value) {
		this.Value = Value;
	}

	public String getOp() {
		return Op;
	}

	public void setOp(String Op) {
		this.Op = Op;
	}
}
