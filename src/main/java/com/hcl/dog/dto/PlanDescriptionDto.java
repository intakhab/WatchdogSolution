package com.hcl.dog.dto;

/**
 * 
 * @author intakhabalam.s@hcl.com
 *
 */
public class PlanDescriptionDto {
	public String systemPlanID = "";
	public String planDescription = "";
	public String logisticsGroupCode = "";
	public String divisionCode = "";
	public boolean isReadAuto = false;
	public String createdTime="";
	
	private PlanDescriptionDto() {

	}

	public static PlanDescriptionDto getInstance() {
		return new PlanDescriptionDto();
	}

	public void reset() {
		systemPlanID = "";
		planDescription = "";
		logisticsGroupCode = "";
		divisionCode = "";
		isReadAuto = false;
		createdTime="";
	}
}
