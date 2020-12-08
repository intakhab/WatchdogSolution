package com.hcl.dog.dto;

/***
 * @author intakhabalam.s@hcl.com
 */
public class SettingsInfoDto {

	private String dogId = "";
	private String enableArchiveOthersFile = "";
	private String enableResponseCodeLog = "";
	private boolean enableMail = false;
	private String toWhomEmail = "";
	private String batchFilePath = "";
	private String inputFolderPath = "";
	private String outputFolderPath = "";
	private String archiveFolderPath = "";
	//
	private String optInputFolderPath = "";
	private String soaOutputFolderPath = "";
	private String optFileSupports = "";
	private String[] optSupportsAPI = new String[0];

	private String carrierCodeTag = "CarrierCode";
	private String dcCodeTag = "DistributionCenterCode";
	private String responeCodeTag = "CompletedSuccessfully";
	private String fileExtension = "";
	private String fileTypeSeparator = "";
	private String responseFilePrefix = "";
	private String fileSupports = "";
	private String[] supportsAPI = new String[0];
	private String dogType = "";
	private String failureFolderPath = "";
	private boolean flag = false;
	private boolean stopFileRun = false;
	private boolean stopBatchRun = false;
	private boolean stopNonEdiBatchRun = false;
	private boolean stopBulkBatchRun = false;
	private boolean stopFBPayRun =false;

	private String nonEdiCamFileSupports = "";
	private String nonEdiCamInputFolderPath = "";
	private String[] nonEdiCamSupportsAPI = new String[0];
	//
	private String nonEdiCamWineFileSupports = "";
	private String[] nonEdiCamWineSupportsAPI = new String[0];
	//
	private String limitFilesFolder = "100";
	private String[] systemPlanId = new String[0];
	private String systemPlanIdText = "";
	private String tabId = "filePathId1";
	private boolean cronHit = false;
	//
	private String soOrderInputFolderPath = "";
	private String[] soOrderSupportsAPI = new String[0];
	private boolean stopSoBatchRun = false;
	private boolean enableStartupEmail = false;
	private boolean enableShutdownEmail = false;

	private String fbPayFileSupports = "";
	private String fbPayInputFolderPath = "";
	private String[] fbPaySupportsAPI = new String[0];

	private String bulkInputFolderPath = "";
	private String bulkFileSupports = "";
	private String[] bulkSupportsAPI = new String[0];

	private String filePollingTime = "1";
	private String finCronTimeG1 = "00 00 21 * * *";
	private String finCronTimeG2 = "00 05 21 * * *";
	private String finCronTimeG3 = "00 20 21 * * *";
	private String finCronTimeG4 = "00 30 21 * * *";
	//
	private String soCronTimeG1 = "00 15 22 * * *";
	private String soCronTimeG2 = "00 45 22 * * *";
	// Put in Mis
	private String nonEdiPoollingTimeG1 = "30";
	private String nonEdiPoollingTimeW1 = "35";
	private String nonEdiPoollingTimeW2 = "40";

	private String bulkPoollingTimeG1 = "00 00 22 * * *";
	private String bulkPoollingTimeG2 = "00 30 22 * * *";

	private String host = "mail.cbrands.com";
	private int port = 25;
	private String mailUserName = "noreply@cbrands.com";
	private String mailPassword = "";
	private String fromMail = "noreply@cbrands.com";
	private boolean debugMail = false;
	private boolean autoPilot = false;
	private boolean autoReports=false;
	
	private String autoPilotCron = "00 00 05 * * *";// Five O'clock Morning default
	private String reportsCron = "00 00 18 * * *";// Six O'clock Morning default

	

	/********************************************************************************/
	public String getBulkFileSupports() {
		return bulkFileSupports;
	}

	public void setBulkFileSupports(String bulkFileSupports) {
		this.bulkFileSupports = bulkFileSupports;
	}

	public String[] getNonEdiCamSupportsAPI() {
		return nonEdiCamSupportsAPI;
	}

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

	public String getFbPayFileSupports() {
		return fbPayFileSupports;
	}

	public void setFbPayFileSupports(String fbPayFileSupports) {
		this.fbPayFileSupports = fbPayFileSupports;
	}

	public String getFbPayInputFolderPath() {
		return fbPayInputFolderPath;
	}

	public void setFbPayInputFolderPath(String fbPayInputFolderPath) {
		this.fbPayInputFolderPath = fbPayInputFolderPath;
	}

	public String[] getFbPaySupportsAPI() {
		return fbPaySupportsAPI;
	}

	public void setFbPaySupportsAPI(String[] fbPaySupportsAPI) {
		this.fbPaySupportsAPI = fbPaySupportsAPI;
	}

	public String getSoOrderInputFolderPath() {
		return soOrderInputFolderPath;
	}

	public void setSoOrderInputFolderPath(String soOrderInputFolderPath) {
		this.soOrderInputFolderPath = soOrderInputFolderPath;
	}

	public String[] getSoOrderSupportsAPI() {
		return soOrderSupportsAPI;
	}

	public void setSoOrderSupportsAPI(String[] soOrderSupportsAPI) {
		this.soOrderSupportsAPI = soOrderSupportsAPI;
	}

	public void setNonEdiCamSupportsAPI(String[] nonEdiCamSupportsAPI) {
		this.nonEdiCamSupportsAPI = nonEdiCamSupportsAPI;
	}

	public String getOptInputFolderPath() {
		return optInputFolderPath;
	}

	public boolean isStopFileRun() {
		return stopFileRun;
	}

	public void setStopFileRun(boolean stopFileRun) {
		this.stopFileRun = stopFileRun;
	}

	public boolean isStopBatchRun() {
		return stopBatchRun;
	}

	public void setStopBatchRun(boolean stopBatchRun) {
		this.stopBatchRun = stopBatchRun;
	}

	public void setOptInputFolderPath(String optInputFolderPath) {
		this.optInputFolderPath = optInputFolderPath;
	}

	public String getOptFileSupports() {
		return optFileSupports;
	}

	public void setOptFileSupports(String optFileSupports) {
		this.optFileSupports = optFileSupports;
	}

	public String[] getOptSupportsAPI() {
		return optSupportsAPI;
	}

	public void setOptSupportsAPI(String[] optSupportsAPI) {
		this.optSupportsAPI = optSupportsAPI;
	}

	public String getDogId() {
		return dogId;
	}

	public void setDogId(String dogId) {
		this.dogId = dogId;
	}

	public String getEnableArchiveOthersFile() {
		return enableArchiveOthersFile;
	}

	public void setEnableArchiveOthersFile(String enableArchiveOthersFile) {
		this.enableArchiveOthersFile = enableArchiveOthersFile;
	}

	public String getEnableResponseCodeLog() {
		return enableResponseCodeLog;
	}

	public void setEnableResponseCodeLog(String enableResponseCodeLog) {
		this.enableResponseCodeLog = enableResponseCodeLog;
	}

	public boolean getEnableMail() {
		return enableMail;
	}

	public void setEnableMail(boolean enableMail) {
		this.enableMail = enableMail;
	}

	public String getToWhomEmail() {
		return toWhomEmail;
	}

	public void setToWhomEmail(String toWhomEmail) {
		this.toWhomEmail = toWhomEmail;
	}

	public String getBatchFilePath() {
		return batchFilePath;
	}

	public void setBatchFilePath(String batchFilePath) {
		this.batchFilePath = batchFilePath;
	}

	public String getInputFolderPath() {
		return inputFolderPath;
	}

	public void setInputFolderPath(String inputFolderPath) {
		this.inputFolderPath = inputFolderPath;
	}

	public String getOutputFolderPath() {
		return outputFolderPath;
	}

	public void setOutputFolderPath(String outputFolderPath) {
		this.outputFolderPath = outputFolderPath;
	}

	public String getArchiveFolderPath() {
		return archiveFolderPath;
	}

	public void setArchiveFolderPath(String archiveFolderPath) {
		this.archiveFolderPath = archiveFolderPath;
	}

	public String getCarrierCodeTag() {
		return carrierCodeTag;
	}

	public void setCarrierCodeTag(String carrierCodeTag) {
		this.carrierCodeTag = carrierCodeTag;
	}

	public String getDcCodeTag() {
		return dcCodeTag;
	}

	public void setDcCodeTag(String dcCodeTag) {
		this.dcCodeTag = dcCodeTag;
	}

	public String getResponeCodeTag() {
		return responeCodeTag;
	}

	public void setResponeCodeTag(String responeCodeTag) {
		this.responeCodeTag = responeCodeTag;
	}

	public String getFileExtension() {
		return fileExtension;
	}

	public String getFailureFolderPath() {
		return failureFolderPath;
	}

	public void setFailureFolderPath(String failureFolderPath) {
		this.failureFolderPath = failureFolderPath;
	}

	public boolean getFlag() {
		return flag;
	}

	public void setFlag(boolean flag) {
		this.flag = flag;
	}

	public void setFileExtension(String fileExtension) {
		this.fileExtension = fileExtension;
	}

	public String getFileTypeSeparator() {
		return fileTypeSeparator;
	}

	public void setFileTypeSeparator(String fileTypeSeparator) {
		this.fileTypeSeparator = fileTypeSeparator;
	}

	public String getResponseFilePrefix() {
		return responseFilePrefix;
	}

	public void setResponseFilePrefix(String responseFilePrefix) {
		this.responseFilePrefix = responseFilePrefix;
	}

	public String getFileSupports() {
		return fileSupports;
	}

	public void setFileSupports(String fileSupports) {
		this.fileSupports = fileSupports;
	}

	public String[] getSupportsAPI() {
		return supportsAPI;
	}

	public void setSupportsAPI(String[] supportsAPI) {
		this.supportsAPI = supportsAPI;
	}

	public String getDogType() {
		return dogType;
	}

	public void setDogType(String dogType) {
		this.dogType = dogType;
	}

	public String getLimitFilesFolder() {
		return limitFilesFolder;
	}

	public void setLimitFilesFolder(String limitFilesFolder) {
		this.limitFilesFolder = limitFilesFolder;
	}

	public String[] getSystemPlanId() {
		return systemPlanId;
	}

	public void setSystemPlanId(String[] systemPlanId) {
		this.systemPlanId = systemPlanId;
	}

	public String getSystemPlanIdText() {
		return systemPlanIdText;
	}

	public void setSystemPlanIdText(String systemPlanIdText) {
		this.systemPlanIdText = systemPlanIdText;
	}

	public String getNonEdiCamFileSupports() {
		return nonEdiCamFileSupports;
	}

	public void setNonEdiCamFileSupports(String nonEdiCamFileSupports) {
		this.nonEdiCamFileSupports = nonEdiCamFileSupports;
	}

	public boolean isStopNonEdiBatchRun() {
		return stopNonEdiBatchRun;
	}

	public void setStopNonEdiBatchRun(boolean stopNonEdiBatchRun) {
		this.stopNonEdiBatchRun = stopNonEdiBatchRun;
	}

	public String getSoaOutputFolderPath() {
		return soaOutputFolderPath;
	}

	public void setSoaOutputFolderPath(String soaOutputFolderPath) {
		this.soaOutputFolderPath = soaOutputFolderPath;
	}

	public String getNonEdiCamInputFolderPath() {
		return nonEdiCamInputFolderPath;
	}

	public void setNonEdiCamInputFolderPath(String nonEdiCamInputFolderPath) {
		this.nonEdiCamInputFolderPath = nonEdiCamInputFolderPath;
	}

	public String getNonEdiCamWineFileSupports() {
		return nonEdiCamWineFileSupports;
	}

	public void setNonEdiCamWineFileSupports(String nonEdiCamWineFileSupports) {
		this.nonEdiCamWineFileSupports = nonEdiCamWineFileSupports;
	}

	public String[] getNonEdiCamWineSupportsAPI() {
		return nonEdiCamWineSupportsAPI;
	}

	public void setNonEdiCamWineSupportsAPI(String[] nonEdiCamWineSupportsAPI) {
		this.nonEdiCamWineSupportsAPI = nonEdiCamWineSupportsAPI;
	}

	public String getTabId() {
		return tabId;
	}

	public void setTabId(String tabId) {
		this.tabId = tabId;
	}

	public boolean isStopSoBatchRun() {
		return stopSoBatchRun;
	}

	public boolean isEnableStartupEmail() {
		return enableStartupEmail;
	}

	public void setEnableStartupEmail(boolean enableStartupEmail) {
		this.enableStartupEmail = enableStartupEmail;
	}

	public boolean isEnableShutdownEmail() {
		return enableShutdownEmail;
	}

	public void setEnableShutdownEmail(boolean enableShutdownEmail) {
		this.enableShutdownEmail = enableShutdownEmail;
	}

	public void setStopSoBatchRun(boolean stopSoBatchRun) {
		this.stopSoBatchRun = stopSoBatchRun;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getMailUserName() {
		return mailUserName;
	}

	public void setMailUserName(String mailUserName) {
		this.mailUserName = mailUserName;
	}

	public String getMailPassword() {
		return mailPassword;
	}

	public void setMailPassword(String mailPassword) {
		this.mailPassword = mailPassword;
	}

	public String getFromMail() {
		return fromMail;
	}

	public void setFromMail(String fromMail) {
		this.fromMail = fromMail;
	}

	public boolean isDebugMail() {
		return debugMail;
	}

	public void setDebugMail(boolean debugMail) {
		this.debugMail = debugMail;
	}

	public boolean isCronHit() {
		return cronHit;
	}

	public void setCronHit(boolean cronHit) {
		this.cronHit = cronHit;
	}

	public String getAutoPilotCron() {
		return autoPilotCron;
	}

	public void setAutoPilotCron(String autoPilotCron) {
		this.autoPilotCron = autoPilotCron;
	}

	public boolean isAutoPilot() {
		return autoPilot;
	}

	public void setAutoPilot(boolean autoPilot) {
		this.autoPilot = autoPilot;
	}

	public String getBulkInputFolderPath() {
		return bulkInputFolderPath;
	}

	public void setBulkInputFolderPath(String bulkInputFolderPath) {
		this.bulkInputFolderPath = bulkInputFolderPath;
	}

	public String[] getBulkSupportsAPI() {
		return bulkSupportsAPI;
	}

	public void setBulkSupportsAPI(String[] bulkSupportsAPI) {
		this.bulkSupportsAPI = bulkSupportsAPI;
	}

	public boolean isStopBulkBatchRun() {
		return stopBulkBatchRun;
	}

	public void setStopBulkBatchRun(boolean stopBulkBatchRun) {
		this.stopBulkBatchRun = stopBulkBatchRun;
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

	public boolean isAutoReports() {
		return autoReports;
	}

	public void setAutoReports(boolean autoReports) {
		this.autoReports = autoReports;
	}

	public String getReportsCron() {
		return reportsCron;
	}

	public void setReportsCron(String reportsCron) {
		this.reportsCron = reportsCron;
	}

	public boolean isStopFBPayRun() {
		return stopFBPayRun;
	}

	public void setStopFBPayRun(boolean stopFBPayRun) {
		this.stopFBPayRun = stopFBPayRun;
	}

}
