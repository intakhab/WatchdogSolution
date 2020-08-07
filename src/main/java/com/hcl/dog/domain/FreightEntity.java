package com.hcl.dog.domain;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Entity")
@XmlAccessorType(XmlAccessType.FIELD)
public class FreightEntity {
	
	@XmlElement
	private FreightBill FreightBill;

	public FreightBill getFreightBill() {
		return FreightBill;
	}

	public void setFreightBill(FreightBill FreightBill) {
		this.FreightBill = FreightBill;
	}
}
