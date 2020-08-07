package com.hcl.dog.domain;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
/**
 * 
 * @author intakhabalam.s@hcl.com
 *
 */
@XmlRootElement(name = "FreightBill")
@XmlAccessorType(XmlAccessType.FIELD)
public class FreightBill {
	private String FreightBillID;

	public String getFreightBillID() {
		return FreightBillID;
	}

	public void setFreightBillID(String FreightBillID) {
		this.FreightBillID = FreightBillID;
	}
}