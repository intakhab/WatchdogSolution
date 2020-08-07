package com.hcl.dog.domain;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Plan")
@XmlAccessorType(XmlAccessType.FIELD)
public class SOPlan {
	@XmlElement
	private String DivisionCode;
	@XmlElement
	private String PlanDescription;
	@XmlElement
	private String SystemPlanID;
	@XmlElement
	private String LogisticsGroupCode;

	public String getDivisionCode() {
		return DivisionCode;
	}

	public void setDivisionCode(String DivisionCode) {
		this.DivisionCode = DivisionCode;
	}

	public String getPlanDescription() {
		return PlanDescription;
	}

	public void setPlanDescription(String PlanDescription) {
		this.PlanDescription = PlanDescription;
	}

	public String getSystemPlanID() {
		return SystemPlanID;
	}

	public void setSystemPlanID(String SystemPlanID) {
		this.SystemPlanID = SystemPlanID;
	}

	public String getLogisticsGroupCode() {
		return LogisticsGroupCode;
	}

	public void setLogisticsGroupCode(String LogisticsGroupCode) {
		this.LogisticsGroupCode = LogisticsGroupCode;
	}

}
