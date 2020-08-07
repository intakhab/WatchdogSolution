package com.hcl.dog.domain;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
/**
 * @author intakhabalam.s@hcl.com
 */
@XmlRootElement(name = "FreightBillPayStatusData")
@XmlAccessorType(XmlAccessType.FIELD)
public class FreightBillPayStatusData {
	private String PaymentAmount;

	private String CheckNumber;

	private String SystemFreightBillID;

	private String PaymentStatusEnumVal;

	private String PaymentDate;
	
	public String getPaymentAmount() {
		return PaymentAmount;
	}

	public void setPaymentAmount(String PaymentAmount) {
		this.PaymentAmount = PaymentAmount;
	}

	public String getCheckNumber() {
		return CheckNumber;
	}

	public void setCheckNumber(String CheckNumber) {
		this.CheckNumber = CheckNumber;
	}

	public String getSystemFreightBillID() {
		return SystemFreightBillID;
	}

	public void setSystemFreightBillID(String SystemFreightBillID) {
		this.SystemFreightBillID = SystemFreightBillID;
	}

	public String getPaymentStatusEnumVal() {
		return PaymentStatusEnumVal;
	}

	public void setPaymentStatusEnumVal(String PaymentStatusEnumVal) {
		this.PaymentStatusEnumVal = PaymentStatusEnumVal;
	}

	public String getPaymentDate() {
		return PaymentDate;
	}

	public void setPaymentDate(String PaymentDate) {
		this.PaymentDate = PaymentDate;
	}
}
