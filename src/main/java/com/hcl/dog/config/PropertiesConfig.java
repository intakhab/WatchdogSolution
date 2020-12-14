package com.hcl.dog.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import com.hcl.dog.common.AppUtil;
/***
 * 
 * @author intakhabalam.s@hcl.com
 * @see {@link Configuration}
 * @see {@link PropertySource}
 */
@Configuration
@PropertySource("classpath:application.properties")
public class PropertiesConfig {
	
	@Value("${initial.polling.delay}")
	public String initialPollingDelay;
	
	public String ipAddress=AppUtil.EMPTY_STR;
	
	//public String versionId="16082018-V-0.7";
	//public String versionId="04092018-V-1.1";//Login Major change
	//public String versionId="07092018-V-1.2";//SO Revamp
	//public String versionId="11092018-V-1.3"; //SO
	//public String versionId="14092018-V-1.4"; //FBPay
	//public String versionId="27092018-V-2.1";// Cron/Mail/AutoStart changes
	//public String versionId="10102018-V-2.2"; //Doc added/Documentation
	//public String versionId="01112018-V-2.3"; //BLK Implemented
	//public String versionId="11122018-V-2.5"; //SO Implemented// Mail Fixed
	//public String versionId="18122018-V-2.6";// Plan not found implemented
	//public String versionId="20122018-V-2.7";// Blk G2 implemented
	//public String versionId="09012019-V-2.8";// Plan not found modified
	//public String versionId="10022019-V-2.9";// Plan not found modified bugs fixed
	//public String versionId="13022019-V-3.0";// Plan not found modified bugs fixed logger change
	
	//public String versionId="06032019-V-3.1";// PlanID Empty-mail shoot
	
	//public String versionId="10082020-V-3.3";// PlanID Failure mail shoot
	
	//public String versionId="07092020-V-3.4";// Wine Job is closed from setting NonEdiComponent check
	
	// public String versionId="04122020-V-4.0";// Major changes, new functionality added, reporting and autopilot will take backup
	
	 	public String versionId="10122020-V-4.1";// Temp Folder search added
}
