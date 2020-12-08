package com.hcl.dog.service;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.hcl.dog.common.AppUtil;
import com.hcl.dog.common.WatchDogException;
import com.hcl.dog.component.DataLoaderComponent;
import com.hcl.dog.dto.ErrorDto;
import com.hcl.dog.dto.ResponseDto;
/***
 * 
 * @author intakhabalam.s@hcl.com
 * File Dog class for watchdog  
 * Logic for automation.
 * @see PrepMailService {@link PrepMailService}
 * @see CommonService {@link CommonService}
 * @see XMLUtilService {@link XMLUtilService}
 * @see DataLoaderComponent {@link DataLoaderComponent}
 * @see Environment {@link Environment}
 *
 */
@Service
public class FileService {
	private Logger logger = LogManager.getLogger("file-serv");
	private String fileType;
	@Autowired
	private CommonService commonService;
	@Autowired
	private XMLUtilService xmlUtilService;
	@Autowired
	private PrepMailService prepMailService;
	@Autowired
	private DataLoaderComponent dataLoader;
	@Autowired
	private PlanIDService planIDService;
	private List<ErrorDto> errorList=new ArrayList<>(0);
	@Autowired
	private Environment env;
	/**
	 * Process the file
	 * @param inputFile  {@link File}
	 * @param count  {@link Integer}
	 * @throws WatchDogException {@link WatchDogException}
	 */
	public void processFileRun(File inputFile, int count) throws WatchDogException {
		synchronized (this) {
			resetValue();
			String fileName = inputFile.getName();
			boolean isValidFile = false;
			try {
				if (StringUtils.getFilenameExtension(fileName).
						equalsIgnoreCase(dataLoader.configDto.getFileExtension())) {

					logger.info("------------------------------------------------------------------------------------------");
					logger.info("Processing file name [ " + fileName + " ] - counter  [ " + count+ " ]");

					if (checkFileConvention(fileName)) {//
						isValidFile = true;
						logger.info("Found valid file, Start Processing...");
					}
					// IF valid file name found then calling command line batch
					if (isValidFile) {
						// we are checking so file having PlanIds exits are not 
						checkPlanIdsExistInSoFile(inputFile);
						
						ResponseDto responseDto=commonService.runCommandLineBatch(dataLoader.configDto.getBatchFilePath(),
								createCommandArgs(fileName),fileName);

						if (responseDto.isCommandRun()) {
							logger.info("File Command line run successfully...");
							checkResponseCode(inputFile);
							
						}else {
							logger.error("File  [ " + fileName + " ]  having issues {1}, Moving to failure directory");
							commonService.gotoArchiveFailureFolder(inputFile.getPath(), fileName,"F");
							commonService.addFilesForReports(fileName,AppUtil.FILE_INTERNAL_PROBLEM,"F");

						}
					} else {

						logger.warn("Please check file naming convention [ " + fileName + " ],"
								+ " It has not valid naming convention for processing {} ");
						logger.warn("Please check at Settings Tabs in WatchDog UI for naming convention {} ");
                        // If we do not want files not supported by WatchDog can archive to another folder  
						if (AppUtil.TRUE_STR.equalsIgnoreCase(dataLoader.configDto.getEnableArchiveOthersFile())) {
							commonService.gotoArchiveFailureFolder(inputFile.getPath(), fileName,"P");
						}
					}

				}

			} catch (WatchDogException e) {
				doCommanFailureWork(inputFile);
				throw new WatchDogException("Error {Dog-0001}: Exception at file run {} " + e.getMessage()) ;
			} 
		}
	}

   /***
    * New Features added 
    * @param inputFile
    * @return
    */
	private boolean checkPlanIdsExistInSoFile(File inputFile) {
		 if(fileType.equalsIgnoreCase("so")) {
			 try {
				 logger.info("Checking file contains planid or not {} ");
				 String planId = xmlUtilService.getValueFromXML(inputFile, AppUtil.TNS_SYSTEM_PLAN_ID);
					 
				 if(planId==null) {
					planId = xmlUtilService.getValueFromXML(inputFile, AppUtil.SYSTEM_PLAN_ID);
				 }
				 
				 if(planId==null) {
					 //shoot mail plan Id is null so chaining it it default planId
					   logger.info("Modifying PlanID as default PlanID  ");
					    boolean isChange=modifyPlanId(inputFile);
					    if(isChange) {
					    	
					    	String shipmentNo=xmlUtilService.getValueFromXML(inputFile, AppUtil.TNS_SHIPMENT_NO);
					    	 if(shipmentNo==null) {
					    		shipmentNo = xmlUtilService.getValueFromXML(inputFile, AppUtil.SHIPMENT_NO);
							 }
					    	
						     prepMailService.sendMail(inputFile, shipmentNo, "4");// Sending Mail
					    }

				 }
				 
			} catch (Exception e) {
				logger.error("File  [ " + inputFile.getName() + " ] does not contain PlanIDs, modified SystemPlanID to default PlanID {}");

			}
		 }
		return false;
	}


	private void resetValue() {
		errorList.clear();
	}


	/***
	 * @param fileName {@link String}
	 * @return string {@link String}
	 */
	private String createCommandArgs(String fileName) {

		String apiName=fileName.split(dataLoader.configDto.getFileTypeSeparator())[0];
		String inputFolderFileName=dataLoader.configDto.getInputFolderPath()+File.separator+fileName;
		String outPutFolder=dataLoader.configDto.getOutputFolderPath()+
				File.separator+""+dataLoader.configDto.getResponseFilePrefix()+fileName;

		StringBuilder sb=new StringBuilder(apiName).append(AppUtil.AND_STR).
				append(inputFolderFileName).append(AppUtil.AND_STR).append(outPutFolder);
		return sb.toString();
	}
  
	/**
	 * This method will check response code
	 * @param inputFile  {@link File}
	 * @throws WatchDogException {@link WatchDogException}
	 */
	private void checkResponseCode(File inputFile) throws WatchDogException {

		logger.info("Scanning Response XML in directory  [ " + dataLoader.configDto.getOutputFolderPath() + " ]");
		String fileName = inputFile.getName();
		String filePath=inputFile.getPath();
		String resFileNameStartWith = dataLoader.configDto.getResponseFilePrefix().concat(fileName);

		String responeFile = dataLoader.configDto.getOutputFolderPath().concat(File.separator).concat(resFileNameStartWith);

		if (!commonService.checkFile(responeFile)) {
			logger.error("[ FD :: "+AppUtil.RESPONSE_NOT_FOUND_FROM_TMS+" :: ] ===> for file [ " + fileName + " ]");
			prepMailService.sendMail(inputFile, AppUtil.EMPTY_STR, "1");// Sending Mail
			commonService.gotoArchiveFailureFolder(filePath, fileName,"F");
			commonService.addFilesForReports(fileName,AppUtil.RESPONSE_NOT_FOUND_FROM_TMS,"F");
			logger.info(AppUtil.FILE_PROCESS_MSG);
			return;
		}

		logger.info("[ FD :: "+AppUtil.RESPONSE_FOUND_FROM_TMS+" :: ] ===>  for file name [ " + resFileNameStartWith + " ]");
		logger.info("Response XML file location [ " + responeFile + " ]");

		try {
			File responseFile = Paths.get(responeFile).toFile();

			String responseTagVal = xmlUtilService.getValueFromXML(responseFile, dataLoader.configDto.getResponeCodeTag());
			// Print XML //final String contentXML=
			xmlUtilService.printXML(responseFile);
			
			if (AppUtil.FALSE_STR.equalsIgnoreCase(responseTagVal)) {
				
				boolean isPlanError=errorService(inputFile,responseFile,"addEntity");// WIll check   error in output file for plan description
				if(isPlanError) {
					
				   boolean isModified=modifyPlanId(inputFile);//here modifying plan id as default

				   if(isModified) {
					   logger.info("Plan Id modified as "+env.getProperty("plan.not.found.code")+" in current file and it will reprocess again!!!");
					   return;
				   }

				}
				String changeEntityType = getEntityfromFileType(fileType.toLowerCase());

				// In case of EDI it will throw error. so put the check here..
				logger.info("File Type :: [ " + fileType.toLowerCase() + " ] ===> Entity Type :: [  " + changeEntityType
						+ " ]");

				if (null != changeEntityType) {

					String baseLastName = StringUtils.split(fileName, dataLoader.configDto.getFileTypeSeparator())[1];
					String changeEntityFileName = changeEntityType.concat(dataLoader.configDto.getFileTypeSeparator())
							.concat(baseLastName);

					String currentFileName = 
							dataLoader.configDto.getInputFolderPath().concat(File.separator).concat(changeEntityFileName);
					
					File newFile=Paths.get(currentFileName).toFile();
					if (inputFile.renameTo(newFile)) {

						logger.info("Current Input file name [ " +fileName+  "] ===>Renaming to  [ " + changeEntityFileName + " ]");
						logger.info("New Modified File location [ " + currentFileName + " ]");

						ResponseDto responseDto=commonService.runCommandLineBatch(dataLoader.configDto.getBatchFilePath(),
								createCommandArgs(newFile.getName()),currentFileName);

						if (responseDto.isCommandRun()) {
							checkResponseCodeForUpdate(Paths.get(currentFileName).toFile());

						}else {
							logger.error("File  [ " + changeEntityFileName + " ]  having issues {2}, Moving to failure directory {}");
							commonService.gotoArchiveFailureFolder(currentFileName, changeEntityFileName,"F");
							commonService.addFilesForReports(changeEntityFileName,AppUtil.FILE_INTERNAL_PROBLEM,"F");

						}

					}
				}else {
					
					//prepMailService.sendMail(inputFile,contentXML,"2");
					prepMailService.sendXMail(inputFile, errorList);
					// Go To Failure Folder as this file has not configure properly, 
					//will have to check File Support(s) and Change Entity Name Tag in Configuration
					logger.error("File  [ " + fileName + " ]  having issue [ :: changeEntity=NULL :: ], Moving to failure directory {}");
					doCommanFailureWork(inputFile);

				}
			}else{
				getPlanIDInSOFile(inputFile);
			}
			logger.info(AppUtil.FILE_PROCESS_MSG);
		} catch (Exception e) {
			throw new WatchDogException("Error {Dog-0002} :  File Exeption " + e.getMessage());
		}

	}

   /**
    * 
    * @param inputFile 
    * @return {@link Boolean}
    */
	private boolean modifyPlanId(File inputFile) {
		String planNotFoundCode=checkPanNotFoundCode(inputFile);//Suspicious code has been written need to check
		if(planNotFoundCode!=null &&  env.getProperty("plan.not.found.code").equals(planNotFoundCode)) {
			 logger.info("PlanId already modified in current file with current code-"+ env.getProperty("plan.not.found.code"));
			 return false;
		}
		 logger.info("PlanId modifying in current file");
		 boolean isModified=xmlUtilService.modifyValuesInXML(inputFile, AppUtil.SYSTEM_PLAN_ID, env.getProperty("plan.not.found.code"));
		   if(!isModified) {
			   isModified=xmlUtilService.modifyValuesInXML(inputFile, AppUtil.TNS_SYSTEM_PLAN_ID, env.getProperty("plan.not.found.code"));
		   }
		return isModified;
	}
	
	/***
	 * This method will check plan not found code is already exist or not
	 * @param inputFile input file
	 * @return String value 999
	 */
	private String checkPanNotFoundCode(File inputFile) {
		 String sysemPlanId = xmlUtilService.getValueFromXML(inputFile,AppUtil.SYSTEM_PLAN_ID);
		 if(null==sysemPlanId) {
			 sysemPlanId=xmlUtilService.getValueFromXML(inputFile,AppUtil.TNS_SYSTEM_PLAN_ID);
		 }
		
		return sysemPlanId;
	}


	/***
	 * This method will check success of so file.
	 * @param file {@link File}
	 */
	private void getPlanIDInSOFile(File file) {
		// Run SO Optimizer will call from 2 place
		 if(fileType.equalsIgnoreCase("so")) {
			 try {
				 logger.error("[ :: SO :: ] type file found, processing so file");
				 planIDService.modifyInputApiXML4PlanId(file);
				
			} catch (WatchDogException e) {
				logger.error("File  [ " + file.getName() + " ] having issue, not modified/saved SystemPlanID {}");

			}
		 }
		commonService.gotoArchiveFailureFolder(file.getPath(), file.getName(),"P");
		commonService.addFilesForReports(file.getName(),"Passed","P");
	}
	
	


	/***
	 * @param file {@link File}
	 */
	private void doCommanFailureWork(File file) {
		commonService.gotoArchiveFailureFolder(file.getPath(), file.getName(),"F");
		commonService.addFilesForReports(file.getName(),"Exception occured in processing","F");
	}

	/**
	 * If String will come like cam then changeArray cam=changeEntity
	 * will get the values changeEntity
	 * @param ct {@link String}
	 * @return {@link String} 
	 */
	private String getEntityfromFileType(String ct) {
		try {
			String[] changeArray = 	dataLoader.configDto.getSupportsAPI();

			for(String s:changeArray) {
				if(s.isEmpty()) {
					logger.warn("ChangeEntity does not found for [ "+ct+" ] ");
					continue;
				}
				String etype="";
				try {
					etype=StringUtils.trimWhitespace(s).split(ct.concat(AppUtil.EQUAL_STR))[1]; //cam=changeEntity =>cam=
				}catch (Exception e) {
				}
				if(etype!=null && !etype.isEmpty()) {
					logger.info("ChangeEntity file type dyanmically search {} "+etype);
					return etype.trim();
				}
			}
		}catch (Exception e) {
			logger.error("Error {Dog:0003} ChangeEntity does not found, "
					+ "check in Settings Tabs Configuration { "+ct+" }",e.getMessage());
		}
		return null;
	}



	/****
	 * This method will run in cases false assume going for update
	 * @param inputFile  {@link File}
	 * @throws WatchDogException {@link WatchDogException}
	 */
	private void checkResponseCodeForUpdate(File inputFile) throws WatchDogException {

		logger.info("Reprocessing for ChangeEntity.");

		String fileName = inputFile.getName();
		String filePath=inputFile.getPath();
		try {
			logger.info("Scanning Response XML in directory  [ " + dataLoader.configDto.getOutputFolderPath() + " ]");
			String resFileNameStartWith = dataLoader.configDto.getResponseFilePrefix().concat(fileName);

			String responeFile = dataLoader.configDto.getOutputFolderPath().concat(File.separator).concat(resFileNameStartWith);

			if (!commonService.checkFile(responeFile)) {
				    logger.error("[ FD :: "+AppUtil.RESPONSE_NOT_FOUND_FROM_TMS+" :: ] ===> for file [ " + fileName + " ]");
					prepMailService.sendMail(inputFile, AppUtil.EMPTY_STR, "1");// Sending Mail in case of update//
					commonService.gotoArchiveFailureFolder(filePath, fileName,"F");
					commonService.addFilesForReports(fileName,AppUtil.RESPONSE_NOT_FOUND_FROM_TMS,"F");
				return;
			}

			logger.info("[ FD :: "+AppUtil.RESPONSE_FOUND_FROM_TMS+" :: ] ===>  for file [ " + resFileNameStartWith + " ]");

			File responseFile = Paths.get(responeFile).toFile();
			
			String responseTagVal = xmlUtilService.
					getValueFromXML(responseFile, dataLoader.configDto.getResponeCodeTag());
			// Print XML
			//final String contentXML=
			xmlUtilService.printXML(responseFile);
			//Sending Email.
			if (AppUtil.FALSE_STR.equalsIgnoreCase(responseTagVal)) {
				    errorService(inputFile,responseFile,"changeEntity");// Check error
					//prepMailService.sendMail(inputFile,contentXML,"2");
					prepMailService.sendXMail(inputFile, errorList);// Added on 11-12-2018
					commonService.gotoArchiveFailureFolder(filePath, fileName,"F");
					commonService.addFilesForReports(fileName,AppUtil.CHECK_RESP_XML,"F");

			}else{
				//Here SO OPT will run
				getPlanIDInSOFile(inputFile);
			}

		} catch (Exception e) {
			throw new WatchDogException("Error {Dog-0004} :  File Exeption " + e.getMessage());
		}
		logger.info("Reprocessing finished ");

	}
	
	/***
	 * @param responseFile
	 * @param entityType
	 * @throws WatchDogException 
	 */
	private boolean errorService(File inputFile,File responseFile,String entityType) throws WatchDogException {
		
		ErrorDto edto=null;
		try {
			edto=commonService.buildErrorMsg(entityType,responseFile);
			errorList.add(edto);
			
		} catch (Exception e) {
			logger.error("Error in errorService{} ",e);

		}
		// SO File PlanID not found logic written here....
		if(fileType.equalsIgnoreCase("so")) {
		      return checkPlanDoesNotExist(inputFile,edto);// check here plan error code 
		}else {
			return false;
		}
	}

	/***
	 * 
	 * @param inputFile
	 * @param edto
	 * @return {@link Boolean} 
	 * @throws WatchDogException {@link WatchDogException}
	 */
	private boolean checkPlanDoesNotExist(File inputFile, ErrorDto edto) throws WatchDogException {
		
		   String[] planArrays = env.getProperty("plan.error.code").split(",");//More than error code put at prop file
		   
			if (planArrays != null && planArrays.length != 0) {
				for (String planErrorCode : planArrays) {
					logger.info("prop file code ::  " + planErrorCode);
					logger.info("xml error code ::  " + edto.getErrorCode());
					logger.info("xml system message msg ::  " + edto.getSystemMessage());
					logger.info("xml user message msg ::  " + edto.getUserMessage());
					if (planErrorCode.trim().equals(edto.getErrorCode().trim())) {
						logger.info("Error code mathced {}");
						return true;
					}else {
						logger.error("Error code doesn't mathced, Please check prop file {}");
                        return false;
					}
				}

		}

		return false;
	}
	
	/**
	 * This method will check file convention '@' file separator 
	 * @param fileName  {@link String}
	 * @return link {@link Boolean}}
	 */

	public boolean checkFileConvention(String fileName) {
		fileType = "NOT VALID";
		final String fname = fileName;
		try {
			String fileEndWith = dataLoader.configDto.getFileSupports().concat(AppUtil.COMMA_SEPERATOR)
					.concat(dataLoader.configDto.getOptFileSupports());

			if (fileEndWith.isEmpty() && fileEndWith.length() == 0) {
				throw new WatchDogException("File naming convention {empty}") ;
			}
			String fileNameAfterFileSepartor = fname.split(dataLoader.configDto.getFileTypeSeparator())[1].split(AppUtil.DOT_STR_COMP)[0];

			List<String> fileDcOrCam = commonService.split(fileEndWith.trim(), AppUtil.COMMA_SEPERATOR);
			for (String fn : fileDcOrCam) {
				String noSpaceStr = fn.replaceAll(AppUtil.WHITE_SPACE, AppUtil.EMPTY_STR);
				if (fileNameAfterFileSepartor.equalsIgnoreCase(noSpaceStr)
						|| StringUtils.startsWithIgnoreCase(fileNameAfterFileSepartor, noSpaceStr)) {
					fileType = fn;
					logger.info("[ :: " + fileType + " ::] type file found");
					return true;
				}
			}

		} catch (Exception e) {
			logger.error("Error {Dog-0004}  File Convention : " + (fileName) + "  " + e.getMessage());
			return false;
		}
		return false;

	}
}
