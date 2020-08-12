package com.hcl.dog.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.hcl.dog.common.AppUtil;
import com.hcl.dog.common.WatchDogException;
import com.hcl.dog.component.DataLoaderComponent;
import com.hcl.dog.domain.PlanId;
import com.hcl.dog.domain.PlanIds;
import com.hcl.dog.domain.SOPlan;
import com.hcl.dog.domain.SoSystemPlanId;
import com.hcl.dog.dto.ErrorDto;
import com.hcl.dog.dto.PlanDescriptionDto;
import com.hcl.dog.dto.ResponseDto;
/**
 * 
 * @author intakhabalam.s@hcl.com
 * @see {@link Service}
 *
 */
@Service
public class PlanIDService {
	
	private Logger logger = LogManager.getLogger("planid-serv");
	@Autowired
	private CommonService commonService;
	@Autowired
	private XMLUtilService xmlUtilService;
	@Autowired
	private Environment env;
    @Autowired
	private DataLoaderComponent dataLoader;
    private final String SO_FILE="@so.xml";
	private PlanDescriptionDto planDesDto=PlanDescriptionDto.getInstance();
	
	@Autowired
	private PrepMailService prepMailService;

	/***
	 * This method will check the plan id from SO file. 
	 * Check into db if it does exist then not save otherwise save
	 * @param inputFile  {@link File}
	 * @throws WatchDogException {@link WatchDogException}
	 */
	public void modifyInputApiXML4PlanId(File inputFile) throws WatchDogException {
		planDesDto.reset();
		String planId = xmlUtilService.getValueFromXML(inputFile, AppUtil.TNS_SYSTEM_PLAN_ID);
		//boolean isPlanIdFound=false;
		if(planId==null) {
			planId = xmlUtilService.getValueFromXML(inputFile, AppUtil.SYSTEM_PLAN_ID);
		}
		if(planId==null) {
			logger.error("PlanId not found in this file => "+inputFile.getName());
			return;
		}
		planId=StringUtils.trimLeadingWhitespace(planId);
		
		logger.info("PlanId found [ "+planId+" ]  for file => "+inputFile.getName());
		//Before Saving PlanId, I need to get the description of plan id
		 boolean b=callPlanDescription(planId.trim());
		 if(!b) {
			  logger.info("PlanId will not go for auto optimization call, Pls check response file from output folder");
			  return;
		 }
		try {
			PlanIds planIds = xmlUtilService.convertXMLToObject(PlanIds.class,
					Paths.get(env.getProperty("db.planid")).toFile());
			if (planIds != null) {
				List<PlanId> planList = planIds.getPlanId();
				boolean isFound = false;
				if (planList != null && planList.size() > 0) {

					for (PlanId p : planList) {
						// Here saving the plan id input file name and condition
						if (planId.trim().equals(p.getId().trim())) {
							logger.info("PlanId already exist in DB {} ");
							isFound = true;
							break;
						}
					}
				}
				if (!isFound) {
					commonService.storeSystemId(planId, planDesDto, "0");// not found in list so inserting into DB. 0
																			// shows inserting first time
				}
			} else {
				commonService.storeSystemId(planId, planDesDto, "0"); // Assuming plansIds is null so inserting into DB
			}

		} catch (FileNotFoundException | JAXBException e) {
			logger.error("Error: {PlanIds-0001}  {modifyInputApiXML4PlanId} " + e.getMessage());
		}
		
	}
	
	/***
	 * @param planId
	 * @return {@link Boolean}
	 */
	public boolean callPlanDescription(String planId) {
		logger.info(" Trimmed PlanId   :: "+planId);
		File findEntityFile=Paths.get(dataLoader.configDto.getSoOrderInputFolderPath()
				.concat(File.separator).concat(env.getProperty("planid.entity.name")+SO_FILE)).toFile();
		logger.info(" Putting planId in this file [ " +findEntityFile.getName()+ " ]");
		try {
		   boolean isModified=xmlUtilService.modifyValuesInXML(findEntityFile, "Id", planId);
		   if(isModified) {
			  logger.info("Find Entity xml put with plan id::"+planId);
			  return invokeBatchResponse(findEntityFile,env.getProperty("planid.entity.name"));
		   }
		}catch (Exception e) {
            logger.error("Error {PlanIds-0002} :  {readSystemPlanIds} " + e.getMessage());
		}
		return false;
	}
	
	
	/***
	 * 
	 * @param inputFile {@link File}
	 * @param apiName {@link String}
	 * @return as boolean {@link Boolean}
	 * @throws WatchDogException {@link WatchDogException}
	 */
	private boolean invokeBatchResponse(File apiInputFile,
			String apiName) throws WatchDogException {
		boolean success=false;
		 logger.info("Invoking batch for file {} "+apiInputFile.getPath());
		 logger.info("With Api name {} "+apiName);
         String [] commandArgs= prepareCommandArgs(apiInputFile, apiName);
         String filePath=apiInputFile.getPath();
	   	ResponseDto responseDto = commonService.
				runCommandLineBatch(dataLoader.configDto.getBatchFilePath(),commandArgs[0],filePath);
		
		if (responseDto.isCommandRun()) {
			logger.info("PlanIDs [ "+apiName+ " ] Command line run successfully...");
			if (checkPlanIdResponseCode(apiInputFile,apiName,commandArgs[1])) {
				logger.info("PlanIDs [ "+apiName+ " ]  API completed sucessfully [ " + filePath + " ] ");
				success= true;

			}
		}
	
		return success;
	}
	
	/***
	 * This method will check the response xml 
	 * in specific location which sending by TMS
	 * @param inputFile {@link File}
	 * @param apiName {@link String}
	 * @param responeFile {@link String}
	 * @return boolean {@link Boolean}
	 * @throws WatchDogException {@link WatchDogException}
	 */
	private boolean checkPlanIdResponseCode(File apiInputFile,String apiName,
			String responeFile) throws WatchDogException {

		logger.info("PlanIDs Scanning Response XML in directory  [ " + dataLoader.configDto.getOutputFolderPath() + " ]");
		if (!commonService.checkFile(responeFile)) {
			logger.error("PlanIDs { :: "+AppUtil.RESPONSE_NOT_FOUND_FROM_TMS+" :: } ===>  for file [ " + responeFile + " ]");
			return false;
		}
		logger.info("PlanIDs { :: "+AppUtil.RESPONSE_FOUND_FROM_TMS+" :: } ===>  for file [ " + responeFile + " ]");
		boolean b=false;
		try {
			File resFile = Paths.get(responeFile).toFile();
			String responseTagVal = xmlUtilService.getValueFromXML(resFile,
					dataLoader.configDto.getResponeCodeTag());
			// Print XML
			xmlUtilService.printXML(resFile);
			if (AppUtil.TRUE_STR.equalsIgnoreCase(responseTagVal)) {
				b=readSystemPlanIds(responeFile);
			}else {
				//New mailing change
				List<ErrorDto> errorList=new ArrayList<>(0);
				ErrorDto edto=commonService.buildErrorMsg(apiName,resFile);
				errorList.add(edto);
				// Added for plan id error
				prepMailService.sendXMail(apiInputFile, errorList);// Added on 10-08-2020

			}
		} catch (Exception e) {
			throw new WatchDogException("Error: {PlanIDs-0003} :   {checkResponseCode} " + e.getMessage());
		}
		return b;
        
	}
	
	
	/**
	 * 
	 * @param responeFile {@link String}
	 * @return boolean  {@link Boolean}
	 */
	private boolean readSystemPlanIds(String responeFile) {

		try {
			SoSystemPlanId sysPlanIds=(SoSystemPlanId)	xmlUtilService
					.convertXMLToObject(SoSystemPlanId.class,Paths.get(responeFile).toFile());
			if(sysPlanIds!=null && sysPlanIds.getEntity()!=null) {
				SOPlan  soPlan=sysPlanIds.getEntity().getPlan();
				
				logger.info("SystemPlanID :: "+soPlan.getSystemPlanID());
				logger.info("PlanDescription :: "+soPlan.getPlanDescription());
				logger.info("LogisticsGroupCode :: "+soPlan.getLogisticsGroupCode());
				logger.info("DivisionCode :: "+soPlan.getDivisionCode());
				//
				String desc=soPlan.getPlanDescription().toLowerCase().trim();
				
				if(desc.startsWith("auto")) {
					planDesDto.isReadAuto=true;
					planDesDto.divisionCode=soPlan.getDivisionCode();
					planDesDto.logisticsGroupCode=soPlan.getLogisticsGroupCode();
					planDesDto.systemPlanID=soPlan.getSystemPlanID();
					planDesDto.planDescription=soPlan.getPlanDescription();
					return true;
				}
			}else{
				logger.info("Please check the output plan id does not found");
			}
			
		} catch (Exception e) {
            logger.error("Error: {PlanId-0004} :  {readSystemPlanIds} " + e.getMessage());
            
		}

		return false;
	}
	
	/***
	 * This will prepare command which will invoke with command line
	 * @param inputFile {@link File}
	 * @param apiName {@link String}
	 * @return String array
	 */

	private String[]  prepareCommandArgs(File inputFile, String apiName) {
        String fileName=inputFile.getName();
		String [] out_with_Args=new String[2];
		
		String inputFolderFileName = dataLoader.configDto.getSoOrderInputFolderPath()
						.concat(File.separator).concat(fileName);
		
		String outPutFolder = dataLoader.configDto.getOutputFolderPath() 
				+ File.separator 
				+ dataLoader.configDto.getResponseFilePrefix()
				+ FilenameUtils.getBaseName(fileName)
				+ AppUtil.UNDERSCORE_STR
				+ AppUtil.currentTime()
				+ AppUtil.DOT_STR
				+ dataLoader.configDto.getFileExtension();

		StringBuilder sb = new StringBuilder(apiName)
				.append(AppUtil.AND_STR)
				.append(inputFolderFileName)
				.append(AppUtil.AND_STR)
				.append(outPutFolder);
		
		out_with_Args[0]=sb.toString();
		out_with_Args[1]=outPutFolder;

		return out_with_Args;
	}
}

