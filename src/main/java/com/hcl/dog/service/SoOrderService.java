package com.hcl.dog.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Paths;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.DisallowConcurrentExecution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.hcl.dog.common.AppUtil;
import com.hcl.dog.common.WatchDogException;
import com.hcl.dog.component.DataLoaderComponent;
import com.hcl.dog.domain.PlanId;
import com.hcl.dog.domain.PlanIds;
import com.hcl.dog.dto.APIDto;
import com.hcl.dog.dto.ResponseDto;
/***
 * 
 * @author intakhabalam.s@hcl.com
 * This class belongs to SO order optimization
 * @see Service
 * @see <a>CommonService</a>
 * @see XMLUtilService
 * @see DataLoaderComponent
 * @see CounterService
 * @see Environment
 */
@Service
@DisallowConcurrentExecution
public class SoOrderService {
	
	private Logger logger = LogManager.getLogger("so-serv");

	@Autowired
	private CommonService commonService;
	@Autowired
	private XMLUtilService xmlUtilService;
	@Autowired
	private DataLoaderComponent dataLoader;
	@Autowired
	private Environment env;
	
	
    /**
     * 
     * @param inputFile as file
     * @param count as integer
     * @param api  as {@link APIDto}
     * @param group string
     * @return boolean as {@link Boolean}
     * @throws WatchDogException as {@link Exception}
     */
	
	public boolean processSoOrder(File inputFile, 
			int count, APIDto api,String group) throws WatchDogException {
		
		String fileName=inputFile.getName();
		boolean success=false;
		logger.info(
				"------------------------------------------------------------------------------------------");
		logger.info("SO Processing file name [ " + fileName + " ] - counter  [ " + count + " ]  - group [ "+group+ "]");

		try {  
				final String apiName=api.getApiName();
				final String apiStrProcess=api.getApiStrProcess();
				final File planIdFile=Paths.get(env.getProperty("db.planid")).toFile();
				
				try {
					PlanIds planIds = xmlUtilService.convertXMLToObject(PlanIds.class, planIdFile);
					if (planIds == null) {
						logger.info("PlanIds {null} SO Batch Optimization won't run {} ");
						api.setMailDescription("PlanIds {null}");
						
					}else {
                       if(planIds.getPlanId()!=null && planIds.getPlanId().size()>0) {
                    	   
							List<PlanId> planIdList=planIds.getPlanId();
							for(PlanId pd:planIdList) {
								
								success=
										modifyApiXMLAndInvokeBatch(inputFile, apiName, apiStrProcess, pd.getId(),api);
								
								if(success) {
									pd.setIsRun(String.valueOf(count));
								    xmlUtilService.editPlanIDXMLWithAttributeId(planIdFile, pd);
								}
							}
                       }else {
   						  api.setMailDescription("PlanIds {null} SO Batch Optimization won't run {} ");
   						  logger.info("PlanIds {0} SO Batch Optimization won't run {} ");

                       }

					}
				} catch (FileNotFoundException | JAXBException e) {
					logger.error("Error {SO-0004}:   Modifying XML Exeption {} " + e.getMessage());
				}

			
		} catch (WatchDogException e) {
			commonService.addFilesForReports(fileName, "Error during processing file", "F");
			throw new WatchDogException("Error {SO-0001}:  Exeption " + e.getMessage());
		}
		return success;
	}
	
	/***
	 * This method will check the response xml 
	 * in specific location which sending by TMS
	 * @param inputFile as File
	 * @param apiName as {@link String}
	 * @param responeFile as {@link String}
	 * @param api as {@link APIDto}
	 * @return as {@link Boolean}
	 * @throws WatchDogException as {@link Exception}
	 */
	private boolean checkResponseCode(File inputFile,String apiName,
			String responeFile,APIDto api) throws WatchDogException {

		logger.info("SO Scanning Response XML in directory  [ " + dataLoader.configDto.getOutputFolderPath() + " ]");
		String fileName = inputFile.getName();
		if (!commonService.checkFile(responeFile)) {
			logger.error("SO { :: "+AppUtil.RESPONSE_NOT_FOUND_FROM_TMS+" :: } ===>  for file [ " + responeFile + " ]");
			commonService.addFilesForReports(fileName,AppUtil.RESPONSE_NOT_FOUND_FROM_TMS,"F");
			api.setMailDescription(AppUtil.RESPONSE_NOT_FOUND_FROM_TMS);
			return false;
		}

		logger.info("SO { :: "+AppUtil.RESPONSE_FOUND_FROM_TMS +" :: } ===>  for file [ " + responeFile + " ]");

		try {
			File resFile = Paths.get(responeFile).toFile();
			String responseTagVal = xmlUtilService.getValueFromXML(resFile,
					dataLoader.configDto.getResponeCodeTag());
			// Print XML
			//final String contentXML= 
					xmlUtilService.printXML(resFile);
			if (AppUtil.FALSE_STR.equalsIgnoreCase(responseTagVal)) {
				   commonService.addFilesForReports(fileName,AppUtil.CHECK_RESP_XML,"F");
					api.setMailDescription(AppUtil.CHECK_RESP_XML);

				return false;
			} else {
				return true;
			}

		} catch (Exception e) {
			throw new WatchDogException("Error {SO-0002} :  File Exeption " + e.getMessage());
		}

	}
	
	
	/***
	 * This method will prepare command args for batch invoke
	 * @param apiFile as file
	 * @param fileName {@link String}
	 * @param apiName {@link String}
	 * @return {@link String[]}
	 */

	private String[]  prepareCommandArgs(File apiFile,String fileName, String apiName) {

		// String
		String[] out_with_Args = new String[2];
		
		String outPutFolder = dataLoader.configDto.getOutputFolderPath() + File.separator + ""
				+ dataLoader.configDto.getResponseFilePrefix() 
				+ FilenameUtils.getBaseName(fileName) 
				+ AppUtil.UNDERSCORE_STR
				+ AppUtil.currentTime() 
				+ AppUtil.DOT_STR
				+ dataLoader.configDto.getFileExtension();

		StringBuilder sb = new StringBuilder(apiName).append(AppUtil.AND_STR).append(apiFile).append(AppUtil.AND_STR)
				.append(outPutFolder);
		
		out_with_Args[0]=sb.toString();
		out_with_Args[1]=outPutFolder;

		return out_with_Args;
	}


	/****
	 * This method will invoke command line invoke.
	 * @param apiFile as file
	 * @param fileName as filename
	 * @param apiName as apiname
	 * @param api {@link APIDto}
	 * @return {@link Boolean}
	 * @throws WatchDogException link {@link Exception}
	 */
	private boolean invokeBatch(File apiFile,String fileName,
			String apiName, APIDto api) throws WatchDogException  {
		boolean success=false;
		
         String [] commandArgs= prepareCommandArgs(apiFile,fileName, apiName);
            	
		ResponseDto responseDto = commonService.
				runCommandLineBatch(dataLoader.configDto.getBatchFilePath(),commandArgs[0],fileName);
		
		if (responseDto.isCommandRun()) {
			logger.info("SO Command line run successfully...");
			if (checkResponseCode(apiFile,apiName,commandArgs[1],api)) {
				logger.info("SO API File run sucessfully [ " + fileName + " ] ");
				api.setMailDescription(AppUtil.SUCCESS_MSG);
				success= true;
				commonService.addFilesForReports(fileName,"Passed","P");

			}
		}
		return success;
	}


     
    /**
     * This method will retrieve xml values put these values in another xml, parse api which coming form ui
     * @param inputFile as file
     * @param apiName {@link String}
     * @param apiStiring {@link String}
     * @param planId {@link String}
     * @param api {@link APIDto}
     * @return {@link Boolean} 
     * @throws WatchDogException @{@link Exception}
     */
	private boolean modifyApiXMLAndInvokeBatch(File inputFile, String apiName, String apiStiring,String planId,APIDto api) throws WatchDogException {
		
		
		logger.info("Plan Id found [ "+planId+" ]  for file => "+inputFile.getName());
		logger.info("Running SO Optimizer");
		boolean b = false;
		if (apiStiring.isEmpty()) {
			logger.info("SO API Args are Empty {} ");
			return true;
		}
		
	    String[] pileArrays = commonService.separateApiString(apiStiring);
		for (String s : pileArrays) {
			String tagName = s.split(AppUtil.EQUAL_STR)[0].trim();
			try {
				// if updated then find the XML
				b = xmlUtilService.modifyValuesInXML(inputFile, tagName, planId);
				if(b) {
					//then run the command line 
					//need command argument
					b= invokeBatch(inputFile,inputFile.getName(),apiName,api);
					
				}
			} catch (WatchDogException e) {
				logger.error("Error: {SO-0003} :  Modifying XML Exeption {} " + e.getMessage());
			}
		}
		return b;
	}


  
}
