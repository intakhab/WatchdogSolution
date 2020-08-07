package com.hcl.dog.domain;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "CronInfo")
public class CronConfig {
	private String filePollingTime="1";
	//
	private String finCronTimeG1="";
	private String finCronTimeG2="";
	private String finCronTimeG3="";
	private String finCronTimeG4="";
   //
	private String soCronTimeG1="";
	private String soCronTimeG2="";
    //
	private String nonEdiPoollingTimeG1="";
	private String nonEdiPoollingTimeW1="";
	private String nonEdiPoollingTimeW2="";
	//
	private String bulkPoollingTimeG1="";
	private String bulkPoollingTimeG2="";

	public String getFilePollingTime() {
		return filePollingTime;
	}
	public void setFilePollingTime(String filePollingTime) {
		this.filePollingTime = filePollingTime;
	}
	public String getFinCronTimeG1() {
		return finCronTimeG1;
	}
	public void setFinCronTimeG1(String finCronTimeG1) {
		this.finCronTimeG1 = finCronTimeG1;
	}
	public String getFinCronTimeG2() {
		return finCronTimeG2;
	}
	public void setFinCronTimeG2(String finCronTimeG2) {
		this.finCronTimeG2 = finCronTimeG2;
	}
	public String getFinCronTimeG3() {
		return finCronTimeG3;
	}
	public void setFinCronTimeG3(String finCronTimeG3) {
		this.finCronTimeG3 = finCronTimeG3;
	}
	public String getFinCronTimeG4() {
		return finCronTimeG4;
	}
	public void setFinCronTimeG4(String finCronTimeG4) {
		this.finCronTimeG4 = finCronTimeG4;
	}
	public String getSoCronTimeG1() {
		return soCronTimeG1;
	}
	public void setSoCronTimeG1(String soCronTimeG1) {
		this.soCronTimeG1 = soCronTimeG1;
	}
	public String getSoCronTimeG2() {
		return soCronTimeG2;
	}
	public void setSoCronTimeG2(String soCronTimeG2) {
		this.soCronTimeG2 = soCronTimeG2;
	}
	public String getNonEdiPoollingTimeG1() {
		return nonEdiPoollingTimeG1;
	}
	public void setNonEdiPoollingTimeG1(String nonEdiPoollingTimeG1) {
		this.nonEdiPoollingTimeG1 = nonEdiPoollingTimeG1;
	}
	public String getNonEdiPoollingTimeW1() {
		return nonEdiPoollingTimeW1;
	}
	public void setNonEdiPoollingTimeW1(String nonEdiPoollingTimeW1) {
		this.nonEdiPoollingTimeW1 = nonEdiPoollingTimeW1;
	}
	public String getNonEdiPoollingTimeW2() {
		return nonEdiPoollingTimeW2;
	}
	public void setNonEdiPoollingTimeW2(String nonEdiPoollingTimeW2) {
		this.nonEdiPoollingTimeW2 = nonEdiPoollingTimeW2;
	}
	public String getBulkPoollingTimeG1() {
		return bulkPoollingTimeG1;
	}
	public void setBulkPoollingTimeG1(String bulkPoollingTimeG1) {
		this.bulkPoollingTimeG1 = bulkPoollingTimeG1;
	}
	public String getBulkPoollingTimeG2() {
		return bulkPoollingTimeG2;
	}
	public void setBulkPoollingTimeG2(String bulkPoollingTimeG2) {
		this.bulkPoollingTimeG2 = bulkPoollingTimeG2;
	}

}
