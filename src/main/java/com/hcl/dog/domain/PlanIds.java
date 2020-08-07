package com.hcl.dog.domain;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "PlanIds")
@XmlAccessorType(XmlAccessType.FIELD)
public class PlanIds {

	@XmlElement(name = "PlanId")
	private List<PlanId>  planId;

	public List<PlanId> getPlanId() {
		return planId;
	}

	public void setPlanId(List<PlanId> planId) {
		this.planId = planId;
	}
	
}
