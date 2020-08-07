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

import com.hcl.dog.common.AppUtil;
import com.hcl.dog.common.WatchDogException;
import com.hcl.dog.component.DataLoaderComponent;
import com.hcl.dog.domain.FBPay;
import com.hcl.dog.domain.FBPayFindEntity;
import com.hcl.dog.domain.FreightBillPayStatusData;
import com.hcl.dog.domain.FreightBills;
import com.hcl.dog.domain.FreightEntity;
import com.hcl.dog.dto.APIDto;
import com.hcl.dog.dto.ResponseDto;

/**
 * 
 * @author intakhabalam.s@hcl.com
 * FBPay Service Business logic for watchdog
 * @see Service
 * @see FBpayService {@link FBpayService}
 * @see CommonService {@link CommonService}
 * @see XMLUtilService {@link XMLUtilService}
 * @see DataLoaderComponent {@link DataLoaderComponent}
 * @see Environment {@link Environment}
   @see PrepMailService {@link PrepMailService}
 */
@Service
public class FBpayService {
	private Logger logger = LogManager.getLogger("fbpay-serv");
	@Autowired
	private CommonService commonService;

	@Autowired
	private XMLUtilService xmlUtilService;

	@Autowired
	private DataLoaderComponent dataLoader;

	@Autowired
	private PrepMailService prepMailService;
	
	private String FBPAY_FIND_ENTITY_FILE_NAME="findEntities@fbpay.xml";
	private String PROCESS_UPDATE_FBPAY_STATUS_API="processUpdateFBPayStatus";
	private String FIND_ENTITIES_API="findEntities";
	private String fileType;
	

	/***
	 * 
	 * @param inputFile {@link File}
	 * @param fbPayInputFile {@link File}
	 * @param fileType {@link String}
	 * @param count {@link Integer}
	 * @return {@link Boolean}
	 * @throws WatchDogException {@link WatchDogException}
	 */
	public boolean processFBPay(File inputFile, File fbPayInputFile, String fileType, int count)
			throws WatchDogException {
		
		boolean success=false;
		synchronized (this) {
			String fbFileName = fbPayInputFile.getName();
			this.fileType=fileType;
			logger.info("------------------------------------------------------------------------------------------");
			logger.info("FBPay Processing file name [ " + fbFileName + " ] - counter  [ " + count + " ]");
			
			try {
				
				List<APIDto> apisList=getAPI();
				String[] fbNumber=new String[0];
				FBPay fbPayObj=null;
				for(APIDto apiDto:apisList) {
				    String apiName=apiDto.getApiName();
				    String apiStr=apiDto.getApiStrProcess();
				    // Separated API and Separated.
				    String[] pileArrays = commonService.separateApiString(apiStr);
					for(String ps:pileArrays) {
						String tags [] = ps.split(AppUtil.EQUAL_STR);
						//String fbn=tags[0];
						String action=tags[1];
						if(PROCESS_UPDATE_FBPAY_STATUS_API.equals(apiName) && action.equals("READ")) {
							apiDto.setMessageHeader("FBPay-"+apiName);
							apiDto.setGroupType("FBPay");
							apiDto.setFileName(fbPayInputFile.getName());
							//
							fbPayObj = (FBPay)xmlUtilService.convertXMLToObject(FBPay.class, fbPayInputFile);
							if(fbPayObj==null) {
						    	apiDto.setStatus(false);
						    	apiDto.setMailDescription(AppUtil.CHECK_RESP_XML);
								return false;
							}else {
						    	apiDto.setStatus(true);
						    	apiDto.setMailDescription(AppUtil.SUCCESS_MSG);

							}
							
							FreightBillPayStatusData[] fbData = fbPayObj.getFreightBillPayStatusData();
							if(fbData!=null && fbData.length>0) {
							     fbNumber=new String[fbData.length];
							     int index=0;
							     for(FreightBillPayStatusData fb:fbData) {
							    	 fbNumber[index]=fb.getSystemFreightBillID();
							    	 index++;
								
							     }
							}
							
						}if(FIND_ENTITIES_API.equals(apiName) && action.equals("PUT")) {
							
							apiDto.setMessageHeader("FBPay-"+apiName);
							apiDto.setGroupType("FBPay");

							//
							File findEntityFile=Paths.get(dataLoader.configDto.getFbPayInputFolderPath()
									+File.separator+FBPAY_FIND_ENTITY_FILE_NAME).toFile();
							
							FBPayFindEntity fbPayFindEntity=(FBPayFindEntity) xmlUtilService.convertXMLToObject(FBPayFindEntity.class, findEntityFile);
							
							if(fbNumber!=null && fbNumber.length>0) {
							  fbPayFindEntity.getFilter().setValue(fbNumber);
							}
							
							String xmlStr = xmlUtilService.convertObjectToXML(fbPayFindEntity);// Converting object into xml
			               
							// now persist the xml in disk with specific name
							String toToBeFindEntityName=dataLoader.configDto.getOutputFolderPath()
									+File.separator
									+FIND_ENTITIES_API
									+dataLoader.configDto.getFileTypeSeparator()
									+this.fileType
									+AppUtil.UNDERSCORE_STR
									+AppUtil.currentTime()
									+AppUtil.DOT_STR
									+dataLoader.configDto.getFileExtension();
							
							success= xmlUtilService.writeXMLString(xmlStr, toToBeFindEntityName);
							
							if(success) {
								//invoke the batch command//
							    File fbPayFile=Paths.get(toToBeFindEntityName).toFile();
						    	apiDto.setFileName(fbPayFile.getName());
						    	
								logger.info("Invoking the Command Line Batch for findEntites FBPay {} ");
							    success=invokeBatchResponse(inputFile,fbPayFile,FIND_ENTITIES_API,fbPayObj);
							    if(success) {
							    	apiDto.setStatus(true);
							    	apiDto.setMailDescription(AppUtil.SUCCESS_MSG);
							    }else {
							    	 apiDto.setStatus(false);
							    	 apiDto.setMailDescription(AppUtil.CHECK_RESP_XML);
							    }
							}
						}
						
					}
					
				}
				
				prepMailService.prepareAndSendAPIsMails(apisList);//send API
			} catch (Exception e) {
				throw new WatchDogException("Error: {FBPay-00010} Exception at file run {} " + e.getMessage()) ;
			}
			
		}
      return success;
	}
	
	
	/***
	 * 
	 * @param inputFile {@link File}
	 * @param apiInputFile {@link File}
	 * @param apiName {@link String}
	 * @param fbPay {@link FBPay}
	 * @return as boolean {@link Boolean}
	 * @throws WatchDogException {@link WatchDogException}
	 */
	private boolean invokeBatchResponse(File inputFile,File apiInputFile,
			String apiName,FBPay fbPay) throws WatchDogException {
		
         boolean success=false;
         String [] commandArgs= prepareCommandArgs(apiInputFile, apiName);
         String filePath=apiInputFile.getPath();
            	
		ResponseDto responseDto = commonService.
				runCommandLineBatch(dataLoader.configDto.getBatchFilePath(),commandArgs[0],filePath);
		
		if (responseDto.isCommandRun()) {
			logger.info("FBPay Command line run successfully...");
			if (checkResponseCode(inputFile,apiInputFile,apiName,commandArgs[1],fbPay)) {
				logger.info("FBPay API completed sucessfully [ " + filePath + " ] ");
			   // api.setMailDescription(commonService.TMS_ERROR);

				success= true;
				commonService.addFilesForReports(filePath,"Passed","P");

			}
		}
	
		return success;
	}

	
	/***
	 * This will prepare command which will invoke with command line
	 * @param inputFile {@link File}
	 * @param apiName {@link String}
	 * @return String array
	 */

	private String[]  prepareCommandArgs(File inputFile, String apiName) {

		// String
		String[] out_with_Args = new String[2];
		String outPutFolder = dataLoader.configDto.getOutputFolderPath() + File.separator + ""
				+ dataLoader.configDto.getResponseFilePrefix() + inputFile.getName();

		StringBuilder sb = new StringBuilder(apiName).append(AppUtil.AND_STR)
				.append(inputFile.getPath()).append(AppUtil.AND_STR)
				.append(outPutFolder);
		
		out_with_Args[0]=sb.toString();
		out_with_Args[1]=outPutFolder;

		return out_with_Args;
	}
	/***
	 * This method will check the response xml 
	 * in specific location which sending by TMS
	 * @param inputFile {@link File}
	 * @param apiInputFile {@link File}
	 * @param apiName {@link String}
	 * @param responeFile {@link String}
	 * @param fbPay {@link FBPay}
	 * @return boolean {@link Boolean}
	 * @throws WatchDogException {@link WatchDogException}
	 */
	private boolean checkResponseCode(File inputFile,File apiInputFile,String apiName,
			String responeFile,FBPay fbPay) throws WatchDogException {

		logger.info("FBPay Scanning Response XML in directory  [ " + dataLoader.configDto.getOutputFolderPath() + " ]");
		String fileName = apiInputFile.getName();
		if (!commonService.checkFile(responeFile)) {
			logger.error("FBPay { :: "+AppUtil.RESPONSE_NOT_FOUND_FROM_TMS+" :: } ===>  for file [ " + responeFile + " ]");
			commonService.addFilesForReports(fileName,AppUtil.RESPONSE_NOT_FOUND_FROM_TMS,"F");
			return false;
		}

		logger.info("FBPay { :: "+AppUtil.RESPONSE_FOUND_FROM_TMS+" :: } ===>  for file [ " + responeFile + " ]");

		try {
			File resFile = Paths.get(responeFile).toFile();
			String responseTagVal = xmlUtilService.getValueFromXML(resFile,
					dataLoader.configDto.getResponeCodeTag());
			// Print XML
			//final String contentXML= 
			xmlUtilService.printXML(resFile);
			if (AppUtil.FALSE_STR.equalsIgnoreCase(responseTagVal)) {
				   commonService.addFilesForReports(fileName,AppUtil.CHECK_RESP_XML,"F");
				return false;
			} else {
				boolean b= readFBBillGoToInput(inputFile,responeFile,resFile.getName(),fbPay);
				if(b)
				return true;
				
				return false;
			}

		} catch (Exception e) {
			throw new WatchDogException("Error {FBPay-0002} :   {checkResponseCode} " + e.getMessage());
		}
        
	}
	
	/**
	 * 
	 * @param inputFile
	 * @param responeFile
	 * @param name
	 * @param fbPay
	 * @return boolean
	 */
	private boolean readFBBillGoToInput(File inputFile,String responeFile, String name,FBPay fbPay) {

		try {
			
			String filureFolder=dataLoader.configDto.getFailureFolderPath().concat(File.separator).concat(inputFile.getName());
			
			String currentFileLocation=dataLoader.configDto.getFbPayInputFolderPath().concat(File.separator).concat(inputFile.getName());

			FreightBills fb=(FreightBills)	xmlUtilService.convertXMLToObject(FreightBills.class,Paths.get(responeFile).toFile());
			
			FreightEntity [] feArrays=fb.getEntity();
			
			FreightBillPayStatusData[]  fbPayArrays=fbPay.getFreightBillPayStatusData();
			
			if((feArrays!=null && fbPayArrays!=null) 
					&& (feArrays.length>0 && fbPayArrays.length>0)) {
				
				if(feArrays.length==fbPayArrays.length) {
					
					for(int x=0;x<feArrays.length;x++) {
					   String fBillId=	feArrays[x].getFreightBill().getFreightBillID();
					   fbPayArrays[x].setSystemFreightBillID(fBillId);
					}
					
				}
			}else {
	            logger.warn("Freight Entity[] NULL found in Response XML [" +responeFile +"], File is moving to Failure Folder ");
	            commonService.moveReplaceFile(currentFileLocation, filureFolder);
				return false;
			}
			
			String xmlStr = xmlUtilService.convertObjectToXML(fbPay);// Converting object into xml
			String [] fbreak=inputFile.getName().split(dataLoader.configDto.getFileTypeSeparator());
            String extens=fbreak[1].toLowerCase();
            extens=extens.replaceAll(fileType, dataLoader.configDto.getFbPayFileSupports().split(AppUtil.COMMA_SEPERATOR)[0]);
            
            String newFileName=fbreak[0].concat(dataLoader.configDto.getFileTypeSeparator()).concat(extens);
            
			String toBeFBPayfileLocation=dataLoader.configDto.getFbPayInputFolderPath().concat(File.separator).concat(newFileName);
			
			String destLocation=dataLoader.configDto.getInputFolderPath().concat(File.separator).concat(newFileName);
			
			boolean success= xmlUtilService.writeXMLString(xmlStr, toBeFBPayfileLocation);

			if(success) {
				commonService.moveReplaceFile(toBeFBPayfileLocation, destLocation);
				String archFolder=dataLoader.configDto.getArchiveFolderPath().
						concat(File.separator).concat(inputFile.getName());
				
				commonService.moveReplaceFile(currentFileLocation, archFolder);
				return true;
			}else {
				commonService.moveReplaceFile(currentFileLocation, filureFolder);
			}
			
		} catch (Exception e) {
            logger.error("Error {FBPay-0003} :  {readFBBillGoToInput} " + e.getMessage());
		}

		return false;
	}


	/***
	 * 
	 * @return list {@link APIDto}
	 */
	public List<APIDto>   getAPI() {
		List<APIDto> apisList=new ArrayList<APIDto>(0);
		String []  apis=dataLoader.configDto.getFbPaySupportsAPI();
		for(String api:apis){
			APIDto adto=new APIDto();
			final String [] apistr=api.split(AppUtil.FILE_SEPARTOR);
			adto.setApiName(apistr[0]);
			adto.setApiStrProcess(apistr[1]);
			apisList.add(adto);
		}
		return apisList;
	}

}
