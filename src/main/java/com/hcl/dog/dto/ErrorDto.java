package com.hcl.dog.dto;
/***
 * @author intakhabalam.s@hcl.com
 */
public class ErrorDto {
	/*<CISDocument>
	<ResponseHeader>
		<CompletedSuccessfully>false</CompletedSuccessfully>
		<Error>
			<Item>0</Item>
			<Severity>Error</Severity>
			<Code>20056</Code>
			<UserMessage>Unknow Error not found SO - 'USA'/'BC'.</UserMessage>
			<SystemMessage>Unknow Error not found SO - 'USA'/'BC'.</SystemMessage>
		</Error>
	</ResponseHeader>
    </CISDocument>*/
	private  String completedStatus;
	private String item;
	private String serverity;
	private String errorCode;
	private String userMessage;
	private String systemMessage;
	private String entityType;
	
	
	public String isCompletedStatus() {
		return completedStatus;
	}
	public void setCompletedStatus(String completedStatus) {
		this.completedStatus = completedStatus;
	}
	public String getItem() {
		return item;
	}
	public void setItem(String item) {
		this.item = item;
	}
	public String getServerity() {
		return serverity;
	}
	public void setServerity(String serverity) {
		this.serverity = serverity;
	}
	public String getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}
	public String getUserMessage() {
		return userMessage;
	}
	public void setUserMessage(String userMessage) {
		this.userMessage = userMessage;
	}
	public String getSystemMessage() {
		return systemMessage;
	}
	public void setSystemMessage(String systemMessage) {
		this.systemMessage = systemMessage;
	}
	public String getEntityType() {
		return entityType;
	}
	public void setEntityType(String entityType) {
		this.entityType = entityType;
	}
	
}
