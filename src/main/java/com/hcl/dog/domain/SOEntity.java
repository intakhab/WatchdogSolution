package com.hcl.dog.domain;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Entity")
@XmlAccessorType(XmlAccessType.FIELD)
public class SOEntity {
	
	@XmlElement
	private SOPlan Plan;

	public SOPlan getPlan() {
		return Plan;
	}

	public void setPlan(SOPlan Plan) {
		this.Plan = Plan;
	}
}
