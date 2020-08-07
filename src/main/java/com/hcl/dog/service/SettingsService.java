package com.hcl.dog.service;

import java.io.FileNotFoundException;
import java.nio.file.Paths;

import javax.xml.bind.JAXBException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.hcl.dog.common.AppUtil;
import com.hcl.dog.domain.CronConfig;
import com.hcl.dog.domain.MailConfig;
import com.hcl.dog.domain.SettingsInfo;
import com.hcl.dog.dto.SettingsInfoDto;
import com.hcl.dog.dto.UserDto;
/***
 * @author intakhabalam.s@hcl.com
   Database storage business logic class for watchdog  
 * @see Service
 * @see XMLUtilService {@link XMLUtilService}
 * @see CommonService {@link CommonService}
 * @see Environment {@link Environment}
 *
 */
@Service
public class SettingsService {

	private Logger logger = LogManager.getLogger("setting-serv");

	@Autowired
	private XMLUtilService xmlUtilService;
	@Autowired
	private CommonService commonService;
	@Autowired
	private Environment env;
	
	/**
	 * @param settingDto {@link SettingsInfoDto}
	 * @return link {@link SettingsInfo}
	 * @throws FileNotFoundException {@link FileNotFoundException}
	 * @throws JAXBException {@link JAXBException}
	 */
	public SettingsInfo saveSettingsInfo(SettingsInfoDto settingDto) throws
	FileNotFoundException, JAXBException {
		logger.info("Saving Setting Info");
		SettingsInfo config = convertDtoToObj(settingDto);
		String fileName = env.getProperty("db.location");
		commonService.backupConfigFile(fileName);

		return (SettingsInfo) xmlUtilService.convertObjectToXML(config, env.getProperty("db.location"));
	}

	
	
	
	/***
	 * This method will convert dto to obj
	 * @param settingInfoDto {@link SettingsInfoDto}
	 * @return {@link SettingsInfo}
	 */
	public SettingsInfo convertDtoToObj(SettingsInfoDto settingInfoDto) {
		logger.info("Converting Dto To Object");

		SettingsInfo settingInfo = new SettingsInfo();
		settingInfo.setDogId("1");
		settingInfo.setBatchFilePath(settingInfoDto.getBatchFilePath());
		settingInfo.setDogType("FD");
		settingInfo.setArchiveFolderPath(settingInfoDto.getArchiveFolderPath());
		settingInfo.setEnableArchiveOthersFile(Boolean.valueOf(settingInfoDto.getEnableArchiveOthersFile()));
		settingInfo.setEnableMail(settingInfoDto.getEnableMail());
		settingInfo.setEnableResponseCodeLog(Boolean.valueOf(settingInfoDto.getEnableResponseCodeLog()));
		settingInfo.setFileExtension(settingInfoDto.getFileExtension());
		settingInfo.setFileSupports(settingInfoDto.getFileSupports());
		settingInfo.setFileTypeSeparator(settingInfoDto.getFileTypeSeparator());
		settingInfo.setInputFolderPath(settingInfoDto.getInputFolderPath());
		settingInfo.setOutputFolderPath(settingInfoDto.getOutputFolderPath());
		settingInfo.setFailureFolderPath(settingInfoDto.getFailureFolderPath());
		settingInfo.setResponeCodeTag(settingInfoDto.getResponeCodeTag());
		settingInfo.setResponseFilePrefix(settingInfoDto.getResponseFilePrefix());
		settingInfo.setSupportsAPI(arrayModification(settingInfoDto.getSupportsAPI()));
		settingInfo.setToWhomEmail(settingInfoDto.getToWhomEmail());
		settingInfo.setFlag(true);
		settingInfo.setOptInputFolderPath(settingInfoDto.getOptInputFolderPath());
		settingInfo.setOptFileSupports(settingInfoDto.getOptFileSupports());
		settingInfo.setSoaOutputFolderPath(settingInfoDto.getSoaOutputFolderPath());
		settingInfo.setOptSupportsAPI(arrayModification(settingInfoDto.getOptSupportsAPI()));
		settingInfo.setNonEdiCamFileSupports(settingInfoDto.getNonEdiCamFileSupports());
		settingInfo.setNonEdiCamWineFileSupports(settingInfoDto.getNonEdiCamWineFileSupports());
		settingInfo.setNonEdiCamSupportsAPI(arrayModification(settingInfoDto.getNonEdiCamSupportsAPI()));
		settingInfo.setNonEdiCamWineSupportsAPI(arrayModification(settingInfoDto.getNonEdiCamWineSupportsAPI()));
		settingInfo.setNonEdiCamInputFolderPath(settingInfoDto.getNonEdiCamInputFolderPath());
		settingInfo.setStopFileRun(settingInfoDto.isStopFileRun());
		settingInfo.setStopBatchRun(settingInfoDto.isStopBatchRun());
		settingInfo.setStopNonEdiBatchRun(settingInfoDto.isStopNonEdiBatchRun());
		settingInfo.setLimitFilesFolder(settingInfoDto.getLimitFilesFolder());
		settingInfo.setSoOrderInputFolderPath(settingInfoDto.getSoOrderInputFolderPath());
		settingInfo.setSoOrderSupportsAPI(arrayModification(settingInfoDto.getSoOrderSupportsAPI()));
		settingInfo.setStopSoBatchRun(settingInfoDto.isStopSoBatchRun());
		settingInfo.setEnableStartupEmail(settingInfoDto.isEnableStartupEmail());
		settingInfo.setEnableShutdownEmail(settingInfoDto.isEnableShutdownEmail());
		settingInfo.setFbPayFileSupports(settingInfoDto.getFbPayFileSupports());
		settingInfo.setFbPayInputFolderPath(settingInfoDto.getFbPayInputFolderPath());
		settingInfo.setFbPaySupportsAPI(arrayModification(settingInfoDto.getFbPaySupportsAPI()));
		settingInfo.setStopBulkBatchRun(settingInfoDto.isStopBulkBatchRun());
		settingInfo.setBulkFileSupports(settingInfoDto.getBulkFileSupports());
		settingInfo.setBulkInputFolderPath(settingInfoDto.getBulkInputFolderPath());
		settingInfo.setBulkSupportsAPI(arrayModification(settingInfoDto.getBulkSupportsAPI()));
		//
		CronConfig cInfo=new CronConfig();
		cInfo.setFilePollingTime(settingInfoDto.getFilePollingTime());
		cInfo.setFinCronTimeG1(settingInfoDto.getFinCronTimeG1());
		cInfo.setFinCronTimeG2(settingInfoDto.getFinCronTimeG2());
		cInfo.setFinCronTimeG3(settingInfoDto.getFinCronTimeG3());
		cInfo.setFinCronTimeG4(settingInfoDto.getFinCronTimeG4());

		cInfo.setNonEdiPoollingTimeG1(settingInfoDto.getNonEdiPoollingTimeG1());
		cInfo.setNonEdiPoollingTimeW1(settingInfoDto.getNonEdiPoollingTimeW1());
		cInfo.setNonEdiPoollingTimeW2(settingInfoDto.getNonEdiPoollingTimeW2());
		
		cInfo.setSoCronTimeG1(settingInfoDto.getSoCronTimeG1());
		cInfo.setSoCronTimeG2(settingInfoDto.getSoCronTimeG2());
		
		cInfo.setBulkPoollingTimeG1(settingInfoDto.getBulkPoollingTimeG1());
		cInfo.setBulkPoollingTimeG2(settingInfoDto.getBulkPoollingTimeG2());
		//Mail
		MailConfig mc=new MailConfig();
		mc.setHost(settingInfoDto.getHost());
		mc.setPort(settingInfoDto.getPort());
		mc.setUsername(settingInfoDto.getMailUserName());
		mc.setPassword(settingInfoDto.getMailPassword());
		mc.setDebugMail(settingInfoDto.isDebugMail());
		mc.setFromMail(settingInfoDto.getFromMail());
		settingInfo.setXmailConfig(mc);
       //Cron
		settingInfo.setXcronConfig(cInfo);
		
		settingInfo.setAutoPilot(settingInfoDto.isAutoPilot());
		settingInfo.setAutoPilotCron(settingInfoDto.getAutoPilotCron());
	
		return settingInfo;
	}
	
	
	/***
	 * This method will convert Obj to Dto
	 * @param location {@link String}
	 * @return {@link SettingsInfoDto}
	 * @throws FileNotFoundException  {@link FileNotFoundException}
	 * @throws JAXBException  {@link JAXBException}
	 */
	public SettingsInfoDto convertObjToDto(String location) throws FileNotFoundException, JAXBException {
		logger.info("Converting Object To Dto");
			String dbLocation;
			if("1".equals(location)) {
				dbLocation=env.getProperty("db.location");
			}else if("3".equals(location)) {//backup
				dbLocation=env.getProperty("backup.dir")+"/"+"config.db";
			}
			else {
				dbLocation=env.getProperty("db.template");
			}
			SettingsInfo  wdInfo=xmlUtilService.
					convertXMLToObject(SettingsInfo.class, Paths.get(dbLocation).toFile());
			
			if(wdInfo==null) {
				return new SettingsInfoDto();
			}
		
		    SettingsInfoDto sInfoDto=new SettingsInfoDto();
			sInfoDto.setDogId(String.valueOf(wdInfo.getDogId()));
			sInfoDto.setBatchFilePath(wdInfo.getBatchFilePath());
			sInfoDto.setDogType(wdInfo.getDogType());
			sInfoDto.setArchiveFolderPath(wdInfo.getArchiveFolderPath());
			sInfoDto.setEnableArchiveOthersFile(String.valueOf(wdInfo.isEnableArchiveOthersFile()));
			sInfoDto.setEnableMail(wdInfo.isEnableMail());
			sInfoDto.setEnableResponseCodeLog(String.valueOf(wdInfo.isEnableResponseCodeLog()));
			sInfoDto.setFileExtension(wdInfo.getFileExtension());
			sInfoDto.setFileSupports(wdInfo.getFileSupports());
			sInfoDto.setFileTypeSeparator(wdInfo.getFileTypeSeparator());
			sInfoDto.setInputFolderPath(wdInfo.getInputFolderPath());
			sInfoDto.setOutputFolderPath(wdInfo.getOutputFolderPath());
			sInfoDto.setResponeCodeTag(wdInfo.getResponeCodeTag());
			sInfoDto.setResponseFilePrefix(wdInfo.getResponseFilePrefix());
			sInfoDto.setSupportsAPI(wdInfo.getSupportsAPI().split(AppUtil.COMMA_SEPERATOR));
			sInfoDto.setToWhomEmail(wdInfo.getToWhomEmail());
			sInfoDto.setFailureFolderPath(wdInfo.getFailureFolderPath());
			sInfoDto.setFlag(wdInfo.isFlag());
			sInfoDto.setOptInputFolderPath(wdInfo.getOptInputFolderPath());
			sInfoDto.setSoaOutputFolderPath(wdInfo.getSoaOutputFolderPath());
			sInfoDto.setNonEdiCamInputFolderPath(wdInfo.getNonEdiCamInputFolderPath());
			sInfoDto.setOptFileSupports(wdInfo.getOptFileSupports());
			sInfoDto.setSoaOutputFolderPath(wdInfo.getSoaOutputFolderPath());
			sInfoDto.setOptSupportsAPI(wdInfo.getOptSupportsAPI().split(AppUtil.COMMA_SEPERATOR));
			sInfoDto.setNonEdiCamInputFolderPath(wdInfo.getNonEdiCamInputFolderPath());
			sInfoDto.setNonEdiCamFileSupports(wdInfo.getNonEdiCamFileSupports());
			sInfoDto.setNonEdiCamSupportsAPI(wdInfo.getNonEdiCamSupportsAPI().split(AppUtil.COMMA_SEPERATOR));
			sInfoDto.setNonEdiCamWineFileSupports(wdInfo.getNonEdiCamWineFileSupports());
			sInfoDto.setNonEdiCamWineSupportsAPI(wdInfo.getNonEdiCamWineSupportsAPI().split(AppUtil.COMMA_SEPERATOR));
			sInfoDto.setStopFileRun(wdInfo.isStopFileRun());
			sInfoDto.setStopBatchRun(wdInfo.isStopBatchRun());
			sInfoDto.setStopNonEdiBatchRun(wdInfo.isStopNonEdiBatchRun());
			sInfoDto.setLimitFilesFolder(wdInfo.getLimitFilesFolder());
			sInfoDto.setStopSoBatchRun(wdInfo.isStopSoBatchRun());
			sInfoDto.setSoOrderInputFolderPath(wdInfo.getSoOrderInputFolderPath());
			sInfoDto.setSoOrderSupportsAPI(wdInfo.getSoOrderSupportsAPI().split(AppUtil.COMMA_SEPERATOR));
			sInfoDto.setStopBulkBatchRun(wdInfo.isStopBulkBatchRun());
			sInfoDto.setBulkFileSupports(wdInfo.getBulkFileSupports());
			sInfoDto.setBulkInputFolderPath(wdInfo.getBulkInputFolderPath());
			sInfoDto.setBulkSupportsAPI(wdInfo.getBulkSupportsAPI().split(AppUtil.COMMA_SEPERATOR));
			sInfoDto.setEnableStartupEmail(wdInfo.isEnableStartupEmail());
			sInfoDto.setEnableShutdownEmail(wdInfo.isEnableShutdownEmail());
			//
			sInfoDto.setFbPayFileSupports(wdInfo.getFbPayFileSupports());
			sInfoDto.setFbPayInputFolderPath(wdInfo.getFbPayInputFolderPath());
			sInfoDto.setFbPaySupportsAPI(wdInfo.getFbPaySupportsAPI().split(AppUtil.COMMA_SEPERATOR));
			
			//if(wdInfo.getXcronConfig()!=null) { //Revised logic for error prevention
				
				sInfoDto.setFilePollingTime(wdInfo.getXcronConfig().getFilePollingTime());
				
				sInfoDto.setFinCronTimeG1(wdInfo.getXcronConfig().getFinCronTimeG1().isEmpty()
						?sInfoDto.getFinCronTimeG1():wdInfo.getXcronConfig().getFinCronTimeG1());
				
				sInfoDto.setFinCronTimeG2(wdInfo.getXcronConfig().getFinCronTimeG2().isEmpty()
						?sInfoDto.getFinCronTimeG2():wdInfo.getXcronConfig().getFinCronTimeG2());
				
				sInfoDto.setFinCronTimeG3(wdInfo.getXcronConfig().getFinCronTimeG3().isEmpty()
						?sInfoDto.getFinCronTimeG3():wdInfo.getXcronConfig().getFinCronTimeG3());
				
				sInfoDto.setFinCronTimeG4(wdInfo.getXcronConfig().getFinCronTimeG4().isEmpty()
						?sInfoDto.getFinCronTimeG4():wdInfo.getXcronConfig().getFinCronTimeG4());

				sInfoDto.setNonEdiPoollingTimeG1(wdInfo.getXcronConfig().getNonEdiPoollingTimeG1().isEmpty()
						?sInfoDto.getNonEdiPoollingTimeG1():wdInfo.getXcronConfig().getNonEdiPoollingTimeG1());
				
				sInfoDto.setNonEdiPoollingTimeW1(wdInfo.getXcronConfig().getNonEdiPoollingTimeW1().isEmpty()
						?sInfoDto.getNonEdiPoollingTimeW1():wdInfo.getXcronConfig().getNonEdiPoollingTimeW1());
				
				sInfoDto.setNonEdiPoollingTimeW2(wdInfo.getXcronConfig().getNonEdiPoollingTimeW2().isEmpty()
						?sInfoDto.getNonEdiPoollingTimeW2():wdInfo.getXcronConfig().getNonEdiPoollingTimeW2());

				sInfoDto.setSoCronTimeG1(wdInfo.getXcronConfig().getSoCronTimeG1().isEmpty()
						?sInfoDto.getSoCronTimeG1():wdInfo.getXcronConfig().getSoCronTimeG1());
				
				sInfoDto.setSoCronTimeG2(wdInfo.getXcronConfig().getSoCronTimeG2().isEmpty()
						?sInfoDto.getSoCronTimeG2():wdInfo.getXcronConfig().getSoCronTimeG2());
				
				sInfoDto.setBulkPoollingTimeG1(wdInfo.getXcronConfig().getBulkPoollingTimeG1().isEmpty()
						?sInfoDto.getBulkPoollingTimeG1():wdInfo.getXcronConfig().getBulkPoollingTimeG1());
				
				sInfoDto.setBulkPoollingTimeG2(wdInfo.getXcronConfig().getBulkPoollingTimeG2().isEmpty()
						?sInfoDto.getBulkPoollingTimeG2():wdInfo.getXcronConfig().getBulkPoollingTimeG2());
			//}
			
			//if(wdInfo.getXmailConfig()!=null) { //Revised logic for error prevention
     		//Mail
				sInfoDto.setHost(wdInfo.getXmailConfig().getHost().isEmpty() ? sInfoDto.getHost()
						: wdInfo.getXmailConfig().getHost());
				sInfoDto.setPort(
						wdInfo.getXmailConfig().getPort() == 0 ? sInfoDto.getPort() : wdInfo.getXmailConfig().getPort());
				sInfoDto.setMailUserName(wdInfo.getXmailConfig().getUsername().isEmpty() ? sInfoDto.getMailUserName()
						: wdInfo.getXmailConfig().getUsername());
				sInfoDto.setMailPassword(wdInfo.getXmailConfig().getPassword().isEmpty() ? sInfoDto.getMailPassword()
						: wdInfo.getXmailConfig().getPassword());
				sInfoDto.setDebugMail(wdInfo.getXmailConfig().isDebugMail());
				sInfoDto.setFromMail(wdInfo.getXmailConfig().getFromMail().isEmpty() ? sInfoDto.getFromMail()
						: wdInfo.getXmailConfig().getFromMail());
	    	//}
			//Auto Pilot
			sInfoDto.setAutoPilot(wdInfo.isAutoPilot());
	        sInfoDto.setAutoPilotCron(wdInfo.getAutoPilotCron().isEmpty()?sInfoDto.getAutoPilotCron():wdInfo.getAutoPilotCron());

		return sInfoDto;
	}


	
	/***
	 * @param apiArrays
	 * @return {@link String}
	 */
	private String arrayModification(String[] apiArrays) {
		StringBuilder sb = new StringBuilder(AppUtil.EMPTY_STR);
		if (apiArrays.length != 0) {
			for (String s : apiArrays) {
				if (s != null && !s.isEmpty()) {
					sb.append(s).append(AppUtil.COMMA_SEPERATOR);
				}
			}
			if (sb.length() > 0) {
				sb.setLength(sb.length() - 1);
			}
		}
		return sb.toString();
	}

	/**
	 * This method will save user
	 * @param userDto {@link UserDto} 
	 * @return {@link UserDto}
	 * @throws FileNotFoundException  {@link FileNotFoundException}
	 * @throws JAXBException  {@link JAXBException}
	 */
	public UserDto saveUserInfo(UserDto userDto) throws FileNotFoundException, JAXBException {
		userDto.setCreateDate(AppUtil.currentTime());
		commonService.backupUserFile();
		commonService.createRegisterUsers(userDto);
		return userDto ;
	}
	
}

