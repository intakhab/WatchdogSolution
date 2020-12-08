package com.hcl.dog.domain;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * This Class is main configuration container.
 * 
 * @author intakhabalam.s@hcl.com
 *
 */
@XmlRootElement(name = "DogInfoConfiguration")
@XmlAccessorType(XmlAccessType.FIELD)
public class SettingsInfo {

	private String dogId = "";
	private String batchFilePath = "";
	private String inputFolderPath = "";
	private String outputFolderPath = "";
	private String failureFolderPath = "";
	private String archiveFolderPath = "";
	private String carrierCodeTag = "CarrierCode";
	private String responeCodeTag = "CompletedSuccessfully";
	private String fileExtension = "xml";
	private String fileTypeSeparator = "@";
	private String responseFilePrefix = "Out_";
	private String fileSupports = "";
	private String supportsAPI = "";
	private String dogType = "FD";
	private String optInputFolderPath = "";
	private String soaOutputFolderPath = "";
	private String optFileSupports = "";
	private String optSupportsAPI = "";
	private String nonEdiCamFileSupports = "";
	private String nonEdiCamInputFolderPath = "";
	private String nonEdiCamSupportsAPI = "";
	private String nonEdiCamWineFileSupports = "";
	private String nonEdiCamWineSupportsAPI = "";
	private String toWhomEmail = "";
	private boolean enableArchiveOthersFile = false;
	private boolean enableResponseCodeLog = false;
	private boolean enableMail = false;
	private boolean flag = false;
	private boolean stopFileRun = false;
	private boolean stopBatchRun = false;
	private boolean stopNonEdiBatchRun = false;
	private boolean stopFBPayRun = false;
	private String limitFilesFolder = "100";
	private boolean stopSoBatchRun = false;
	private String soOrderInputFolderPath = "";
	private String soOrderSupportsAPI = "";
	private boolean enableStartupEmail = false;
	private boolean enableShutdownEmail = false;
	private String fbPayFileSupports = "";
	private String fbPayInputFolderPath = "";
	private String fbPaySupportsAPI = "";
	private boolean stopBulkBatchRun = false;
	private String bulkSupportsAPI = "";
	private String bulkFileSupports = "";
	private String bulkInputFolderPath = "";
	private CronConfig xcronConfig = new CronConfig();
	private MailConfig xmailConfig = new MailConfig();
	private boolean autoPilot = false;
	private String autoPilotCron = "";

	private boolean autoReports = false;
	private String reportsCron = "";

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
	public String getDogType() {
		return dogType;
	}

	public String getSoOrderInputFolderPath() {
		return soOrderInputFolderPath;
	}

	public void setSoOrderInputFolderPath(String soOrderInputFolderPath) {
		this.soOrderInputFolderPath = soOrderInputFolderPath;
	}

	public String getSoOrderSupportsAPI() {
		return soOrderSupportsAPI;
	}

	public void setSoOrderSupportsAPI(String soOrderSupportsAPI) {
		this.soOrderSupportsAPI = soOrderSupportsAPI;
	}

	public void setDogType(String dogType) {
		this.dogType = dogType;
	}

	public boolean isEnableArchiveOthersFile() {
		return enableArchiveOthersFile;
	}

	public void setEnableArchiveOthersFile(boolean enableArchiveOthersFile) {
		this.enableArchiveOthersFile = enableArchiveOthersFile;
	}

	public boolean isEnableResponseCodeLog() {
		return enableResponseCodeLog;
	}

	public void setEnableResponseCodeLog(boolean enableResponseCodeLog) {
		this.enableResponseCodeLog = enableResponseCodeLog;
	}

	public boolean isEnableMail() {

		return enableMail;
	}

	public void setEnableMail(boolean enableMail) {
		this.enableMail = enableMail;
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

	public String getResponeCodeTag() {
		return responeCodeTag;
	}

	public void setResponeCodeTag(String responeCodeTag) {
		this.responeCodeTag = responeCodeTag;
	}

	public String getFileExtension() {
		return fileExtension;
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

	public String getDogId() {
		return dogId;
	}

	public void setDogId(String dogId) {
		this.dogId = dogId;
	}

	public String getToWhomEmail() {
		return toWhomEmail;
	}

	public void setToWhomEmail(String toWhomEmail) {
		this.toWhomEmail = toWhomEmail;
	}

	public String getSupportsAPI() {
		return supportsAPI;
	}

	public void setSupportsAPI(String supportsAPI) {
		this.supportsAPI = supportsAPI;
	}

	public String getFailureFolderPath() {
		return failureFolderPath;
	}

	public void setFailureFolderPath(String failureFolderPath) {
		this.failureFolderPath = failureFolderPath;
	}

	public boolean isFlag() {
		return flag;
	}

	public void setFlag(boolean flag) {
		this.flag = flag;
	}

	public String getCarrierCodeTag() {
		return carrierCodeTag;
	}

	public void setCarrierCodeTag(String carrierCodeTag) {
		this.carrierCodeTag = carrierCodeTag;
	}

	public String getOptInputFolderPath() {
		return optInputFolderPath;
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

	public String getOptSupportsAPI() {
		return optSupportsAPI;
	}

	public void setOptSupportsAPI(String optSupportsAPI) {
		this.optSupportsAPI = optSupportsAPI;
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

	public String getLimitFilesFolder() {
		return limitFilesFolder;
	}

	public void setLimitFilesFolder(String limitFilesFolder) {
		this.limitFilesFolder = limitFilesFolder;
	}

	public String getNonEdiCamSupportsAPI() {
		return nonEdiCamSupportsAPI;
	}

	public void setNonEdiCamSupportsAPI(String nonEdiCamSupportsAPI) {
		this.nonEdiCamSupportsAPI = nonEdiCamSupportsAPI;
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

	public String getNonEdiCamWineSupportsAPI() {
		return nonEdiCamWineSupportsAPI;
	}

	public void setNonEdiCamWineSupportsAPI(String nonEdiCamWineSupportsAPI) {
		this.nonEdiCamWineSupportsAPI = nonEdiCamWineSupportsAPI;
	}

	public boolean isStopSoBatchRun() {
		return stopSoBatchRun;
	}

	public void setStopSoBatchRun(boolean stopSoBatchRun) {
		this.stopSoBatchRun = stopSoBatchRun;
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

	public String getFbPaySupportsAPI() {
		return fbPaySupportsAPI;
	}

	public void setFbPaySupportsAPI(String fbPaySupportsAPI) {
		this.fbPaySupportsAPI = fbPaySupportsAPI;
	}

	public CronConfig getXcronConfig() {
		return xcronConfig;
	}

	public void setXcronConfig(CronConfig xcronConfig) {
		this.xcronConfig = xcronConfig;
	}

	public MailConfig getXmailConfig() {
		return xmailConfig;
	}

	public void setXmailConfig(MailConfig xmailConfig) {
		this.xmailConfig = xmailConfig;
	}

	public boolean isAutoPilot() {
		return autoPilot;
	}

	public void setAutoPilot(boolean autoPilot) {
		this.autoPilot = autoPilot;
	}

	public String getAutoPilotCron() {
		return autoPilotCron;
	}

	public void setAutoPilotCron(String autoPilotCron) {
		this.autoPilotCron = autoPilotCron;
	}

	public String getBulkSupportsAPI() {
		return bulkSupportsAPI;
	}

	public void setBulkSupportsAPI(String bulkSupportsAPI) {
		this.bulkSupportsAPI = bulkSupportsAPI;
	}

	public String getBulkFileSupports() {
		return bulkFileSupports;
	}

	public void setBulkFileSupports(String bulkFileSupports) {
		this.bulkFileSupports = bulkFileSupports;
	}

	public String getBulkInputFolderPath() {
		return bulkInputFolderPath;
	}

	public void setBulkInputFolderPath(String bulkInputFolderPath) {
		this.bulkInputFolderPath = bulkInputFolderPath;
	}

	public boolean isStopBulkBatchRun() {
		return stopBulkBatchRun;
	}

	public void setStopBulkBatchRun(boolean stopBulkBatchRun) {
		this.stopBulkBatchRun = stopBulkBatchRun;
	}

	public boolean isStopFBPayRun() {
		return stopFBPayRun;
	}

	public void setStopFBPayRun(boolean stopFBPayRun) {
		this.stopFBPayRun = stopFBPayRun;
	}

}
