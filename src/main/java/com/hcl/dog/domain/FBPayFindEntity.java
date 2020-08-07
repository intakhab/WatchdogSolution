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
public class FBPayFindEntity {
	
	@XmlElement
	private FBPayFindEntityApiHeader ApiHeader;
	@XmlElement
	private String EntityType;
	@XmlElement
	private Filter Filter;
	
	@XmlElement
	private Select Select;
	

	public Filter getFilter() {
		return Filter;
	}

	public void setFilter(Filter Filter) {
		this.Filter = Filter;
	}

	public Select getSelect() {
		return Select;
	}

	public void setSelect(Select Select) {
		this.Select = Select;
	}

	public String getEntityType() {
		return EntityType;
	}

	public void setEntityType(String EntityType) {
		this.EntityType = EntityType;
	}

	public FBPayFindEntityApiHeader getApiHeader() {
		return ApiHeader;
	}

	public void setApiHeader(FBPayFindEntityApiHeader apiHeader) {
		ApiHeader = apiHeader;
	}

}

@XmlRootElement(name = "ApiHeader")
@XmlAccessorType(XmlAccessType.FIELD)
class FBPayFindEntityApiHeader {
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

