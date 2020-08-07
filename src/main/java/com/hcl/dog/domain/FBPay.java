package com.hcl.dog.domain;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
/**
 * @author intakhabalam.s@hcl.com
 */
@XmlRootElement(name = "CISDocument")
@XmlAccessorType(XmlAccessType.FIELD)
public class FBPay {
	private FBPayApiHeader ApiHeader;
	
	private FreightBillPayStatusData[] FreightBillPayStatusData;
	
	public FreightBillPayStatusData[] getFreightBillPayStatusData() {
		return FreightBillPayStatusData;
	}

	public void setFreightBillPayStatusData(FreightBillPayStatusData[] FreightBillPayStatusData) {
		this.FreightBillPayStatusData = FreightBillPayStatusData;
	}

	public FBPayApiHeader getApiHeader() {
		return ApiHeader;
	}

	public void setApiHeader(FBPayApiHeader apiHeader) {
		ApiHeader = apiHeader;
	}

}
@XmlRootElement(name = "ApiHeader")
@XmlAccessorType (XmlAccessType.FIELD)
class FBPayApiHeader {
	@XmlElement
	private String OperationName;
	@XmlElement
	private String ClientID;

	public String getOperationName() {
		return OperationName;
	}

	public void setOperationName(String OperationName) {
		this.OperationName = OperationName;
	}

	public String getClientID() {
		return ClientID;
	}

	public void setClientID(String ClientID) {
		this.ClientID = ClientID;
	}
}
