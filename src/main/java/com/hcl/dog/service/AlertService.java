package com.hcl.dog.service;

import org.springframework.stereotype.Service;

/**
 * @author intakhabalam.s@hcl.com
 * Utility service for Alert
 * @see {@link Service}
 */
@Service
public class AlertService {
	
	/**
	 * @param msg {@link String}
	 * @return {@link String}
	 */
	public String sucess(String msg) {
		return "<div id='alertId' class='alert alert-success' data-dismiss='alert' aria-label='Close'"
				+ " role='alert'><i class='fa fa-check-circle-o' aria-hidden='true' style='font-size:30px;color:green'></i>&nbsp;&nbsp;" + msg + "<span style='float:right;cursor: pointer;'>&times;</span></div>";
	}

	/**
	 * @param msg {@link String}
	 * @return {@link String}
	 */
	public String error(String msg) {
		return "<div id='alertId' class='alert alert-danger' data-dismiss='alert' aria-label='Close'"
				+ " role='alert'><i class='fa fa-exclamation-triangle' aria-hidden='true' style='font-size:30px;color:red'></i>&nbsp;&nbsp;" + msg + "<span style='float:right;cursor: pointer;'>&times;</span></div>";
		
	}

	/***
	 * @param msg {@link String}
	 * @return {@link String}
	 */
	public String primary(String msg) {
		return "<div id='alertId' class='alert alert-primary' data-dismiss='alert' aria-label='Close'"
				+ " role='alert'>" + msg + "<span style='float:right;cursor: pointer;'>&times;</span></div>";
	}

}
