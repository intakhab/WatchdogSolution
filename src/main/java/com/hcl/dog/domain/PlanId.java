package com.hcl.dog.domain;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "systemplanid")
@XmlAccessorType(XmlAccessType.FIELD)
public class PlanId {
	@XmlAttribute
	private String id;

	@XmlElement(name = "PlanDescription")
	private String planDescription;
	
	@XmlElement(name = "LogisticsGroupCode")
	private String logisticsGroupCode;
	
	@XmlElement(name = "DivisionCode")
	private String divisionCode;
	
	@XmlElement(name = "CreatedDate")
	private String createdDate;

	@XmlElement(name = "IsRun")
	private String isRun;
	
	
	
	
	
	
	public String getPlanDescription() {
		return planDescription;
	}

	public void setPlanDescription(String planDescription) {
		this.planDescription = planDescription;
	}

	public String getLogisticsGroupCode() {
		return logisticsGroupCode;
	}

	public void setLogisticsGroupCode(String logisticsGroupCode) {
		this.logisticsGroupCode = logisticsGroupCode;
	}

	public String getDivisionCode() {
		return divisionCode;
	}

	public void setDivisionCode(String divisionCode) {
		this.divisionCode = divisionCode;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}

	public String getIsRun() {
		return isRun;
	}

	public void setIsRun(String isRun) {
		this.isRun = isRun;
	}

}

