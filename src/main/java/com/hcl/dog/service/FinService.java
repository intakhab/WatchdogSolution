package com.hcl.dog.service;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.DisallowConcurrentExecution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.hcl.dog.common.AppUtil;
import com.hcl.dog.common.WatchDogException;
import com.hcl.dog.component.DataLoaderComponent;
import com.hcl.dog.domain.GLTransaction;
import com.hcl.dog.domain.GLTransactions;
import com.hcl.dog.domain.ProcessGLTransactionCommit;
import com.hcl.dog.dto.APIDto;
import com.hcl.dog.dto.ResponseDto;

/***
 * 
 * @author intakhabalam.s@hcl.com
 * Financial batch optimization business logic for watchdog
 * codes work on the basis of instruction given from UI
 * @see Service
 * @see PrepMailService {@link PrepMailService}
 * @see CommonService {@link CommonService}
 * @see XMLUtilService {@link XMLUtilService}
 * @see DataLoaderComponent {@link DataLoaderComponent}
 * @see CounterService {@link CounterService}
 * @see Environment {@link Environment}
 */
@Service
@DisallowConcurrentExecution
public class FinService {
	private Logger logger = LogManager.getLogger("fin-serv");
	@Autowired
	private CommonService commonService;
	@Autowired 
	private XMLUtilService xmlUtilService;
	@Autowired
	private DataLoaderComponent dataLoader;
	@Autowired
	private CounterService counterService;
		
	private String fileType=AppUtil.EMPTY_STR;
    private final String GL_TRANSACTION_RETRIEVE="processGLTransactionRetrieve"; //API
    private final String GL_TRANSACTION_COMMIT = "processGLTransactionCommit";//API

	/***
	 * This is the entry point of fin services api processing,
	 * will take the input xml, group AP_TRANSACTION_RETRIEVE
	 * @param inputFile as file
	 * @param count {@link Integer}
	 * @param api as {@link APIDto}
	 * @param group {@link String}
	 * @param archive {@link Boolean}
	 * @throws WatchDogException {@link Exception}
	 * @return {@link Boolean}
	 */
	public boolean processFinRun(File inputFile, int count,APIDto api, 
			String group, boolean archive) throws WatchDogException {
		
		synchronized (this) {
			boolean success=false;
			String fileName = inputFile.getName();
			String apiName=null;
			String apiStr=null;
			try {
				if (checkFileConvention(fileName)) {
					logger.info(
							"------------------------------------------------------------------------------------------");
					logger.info("Fin Processing file name [ " + fileName + " ] - counter  [ " + count + " ]  -  [ "+group+" ]   ");
					logger.info("Fin found valid file, Start Processing...");
					apiName = api.getApiName();
					apiStr = api.getApiStrProcess();

					if (apiName != null && apiStr != null) {
						logger.info("Fin API type dyanmically search {} " + apiName + " = " + apiStr);
						
						logger.info("Modifying XML Tag before processing...");
						// Means XML Modified successfully
						boolean isModifiedXML = modifyInputApiXML(inputFile, apiName, apiStr);
						if (isModifiedXML) {
							success=invokeBatchResponse(inputFile,fileName,apiName,archive,group,api);
						}
					}
				}

			 } catch (Exception e) {
				 success=false;
				 commonService.addFilesForReports(fileName, "Exception Occured in Processing", "F");
				throw new WatchDogException("Error :  {Fin-0001} :  " + e.getMessage());

			 }finally{
				    logger.info("Refreshing xml tag after Processing {} ");
					boolean isRefresh=refreshXMLTagValues(inputFile, apiName, apiStr);
					logger.info("XML refreshed => "+isRefresh);

			 }
			
			return success;
		}
	}

	/***
	 * This method will check the response xml 
	 * in specific location which sending by TMS
	 * @param inputFile as file
	 * @param apiName {@link String}
	 * @param responeFile {@link String}
	 * @param archive {@link Boolean}
	 * @param group {@link String}
	 * @param api {@link APIDto}
	 * @return {@link Boolean}
	 * @throws WatchDogException as exception
	 */
	private boolean checkResponseCode(File inputFile,String apiName,
			String responeFile,boolean archive,String group,APIDto api) throws WatchDogException {

		logger.info("Fin Scanning Response XML in directory  [ " + dataLoader.configDto.getOutputFolderPath() + " ]");
		String fileName = inputFile.getName();
		if (!commonService.checkFile(responeFile)) {
			logger.error("Fin { :: "+AppUtil.RESPONSE_NOT_FOUND_FROM_TMS+" :: } ===>  for file [ " + responeFile + " ]");
			commonService.addFilesForReports(fileName,AppUtil.RESPONSE_NOT_FOUND_FROM_TMS,"F");
            api.setMailDescription(AppUtil.RESPONSE_NOT_FOUND_FROM_TMS);
			return false;
		}

		logger.info("Fin { :: "+AppUtil.RESPONSE_FOUND_FROM_TMS+" :: } ===>  for file [ " + responeFile + " ]");

		try {
			File resFile = Paths.get(responeFile).toFile();
			String responseTagVal = xmlUtilService.getValueFromXML(resFile,
					dataLoader.configDto.getResponeCodeTag());
			// Print XML
			//@SuppressWarnings("unused")
			//final String contentXML= 
			xmlUtilService.printXML(resFile);
			
			if (AppUtil.FALSE_STR.equalsIgnoreCase(responseTagVal)) {
			       api.setMailDescription(AppUtil.CHECK_RESP_XML);
				   commonService.addFilesForReports(fileName,AppUtil.CHECK_RESP_XML,"F");
				return false;
			} else {
				if(GL_TRANSACTION_RETRIEVE.equals(apiName) && archive) {
	        	    	   //Before Archive I am Committing all SystemGLTransactionID//
					  commitAndSoa(responeFile,resFile.getName(),api);
				}
				return true;
			}

		} catch (Exception e) {
			throw new WatchDogException("Error {Fin-0002} :  File Exeption " + e.getMessage());
		}

	}
	
	/***
	 * 
	 * @param responeFile as string
	 * @param fname as string
	 * @param api as APIDto
	 */
	private void commitAndSoa(String responeFile, String fname, APIDto api) {
		 //Before Archive I am Committing all SystemGLTransactionID//
	       if(commtGLTransaction(responeFile,api)!=null) {
			    logger.info("Sending file to SOA directory { "+fname+" } ");
	            commonService.gotSOAFolder(responeFile, fname);
	       }else {
			   logger.info("GLTranactionCommit didn't get the SystemGLTransactionID value!!!");
	       }
		
	}

	/***
	 * This method will Commit The GL Trasaction
	 * @param responseFile {@link String}
	 * @param api as APIDto
	 * @return {@link String}
	 */

	private String commtGLTransaction(String responseFile,APIDto api) {
		
        logger.info("Starting processing for GLTransactionCommit with API { "+GL_TRANSACTION_COMMIT+" }");
		boolean success=false;
		File file = Paths.get(responseFile).toFile();
		try {
			String toBeNewXMLName = 
					dataLoader.configDto.getOutputFolderPath()
					.concat(File.separator)
					.concat(dataLoader.configDto.getResponseFilePrefix())
					.concat(GL_TRANSACTION_COMMIT)
					.concat(dataLoader.configDto.getFileTypeSeparator()).concat(fileType)
					+AppUtil.UNDERSCORE_STR+AppUtil.currentTime()
					+AppUtil.DOT_STR+dataLoader.configDto.getFileExtension();
			// Input Directory for Fin
            logger.info("File for { "+GL_TRANSACTION_COMMIT+" } processing file [ "+toBeNewXMLName  +" ]");
            File xmlFile=Paths.get(toBeNewXMLName).toFile();
			String xmlFileName=xmlFile.getName();
			
			GLTransactions gs = xmlUtilService.convertXMLToObject(GLTransactions.class, file);
			GLTransaction[] glArrays = gs.getGLTransaction();
			ProcessGLTransactionCommit prCommit = new ProcessGLTransactionCommit();
            if(glArrays!=null && glArrays.length>0) {
            	logger.info("SystemGLTransactionID  size {{ "+glArrays.length+ "}}");

				String[] val = new String[glArrays.length];
				int i = 0;
				for (GLTransaction gl : glArrays) {
					val[i] = gl.getSystemGLTransactionID();// SystemGLTransactionID
					i++;
	
				}
				prCommit.setSystemTransactionID(val);
            }else {
            	logger.info("SystemGLTransactionID size {{0}}");
            	return null;
            }

			String xmlStr = xmlUtilService.convertObjectToXML(prCommit);
			//logger.info("Dynamically creating GLTransactionCommit file \n "+xmlStr);
			success= xmlUtilService.writeXMLString(xmlStr, toBeNewXMLName);
			logger.info("GLTransactionCommit File has created here [ "+toBeNewXMLName+" ]");
			// Again Invoking the Command Line for CommitGLTransaction
			logger.info("Invoking the Command Line Batch for GLTransactionCommit {} ");
			try {
				Thread.sleep(1000); // 1 Second
			} catch (Exception e) {
			}
			success=invokeBatchResponse(xmlFile,xmlFileName,
					GL_TRANSACTION_COMMIT,false,"commit",api);//Group is commit 
			
            logger.info("GLTransactionCommit run successfully { "+success  +" }");

            return xmlFileName;
		} catch (Exception e) {
			logger.error("Error { Fin-0003 } GLTransactionCommit :: " + e.getMessage());
		}

		return null;
	}
	
	

	/***
	 * 
	 * @param inputFile {@link File}
	 * @param fileName {@link String}
	 * @param apiName {@link String} 
	 * @param archive {@link Boolean}
	 * @param group {@link String}
	 * @param api {@link APIDto}
	 * @return {@link Boolean}
	 * @throws WatchDogException as exception
	 */
	private boolean invokeBatchResponse(File inputFile,String fileName,
			String apiName,boolean archive,String group,APIDto api) throws WatchDogException {
		
         boolean success=false;
         String [] commandArgs= prepareCommandArgs(fileName, apiName,group);
            	
		ResponseDto responseDto = commonService.runCommandLineBatch(dataLoader.configDto.getBatchFilePath(),commandArgs[0],fileName);
		if (responseDto.isCommandRun()) {
			logger.info("Fin Command line run successfully...");
			if (checkResponseCode(inputFile,apiName,commandArgs[1],archive,group,api)) {
				logger.info("Fin API File completed sucessfully [ " + fileName + " ] ");
	            api.setMailDescription(AppUtil.SUCCESS_MSG);
				success= true;
				commonService.addFilesForReports(fileName,"Passed","P");

			}
		}
	
		return success;
	}

	/**
	 * if sting will come like cam then changeArray=cam=changeEntity will get the
	 * values changeEntity
	 * @param inputFile {@link File}
	 * @param apiName {@link String}
	 * @param apiStiring {@link String}
	 * @return {@link Boolean}
	 * @throws WatchDogException as exception
	 */
	private boolean modifyInputApiXML(File inputFile, String apiName, String appStr)  {
	
		boolean b = false;
		String appString=AppUtil.checkAndRemoveCurlyStr(appStr);
		if (appString.isEmpty()) {
			logger.info("API Args are Empty {} ");
			return true;
		}
		String[] pipeSeparator = appString.split("\\|");
		List<String> tagsList=new ArrayList<>();
		int count=0;
		for (String s : pipeSeparator) {
			String tagName = s.split(AppUtil.EQUAL_STR)[0].trim();
			String tagValue = s.split(AppUtil.EQUAL_STR)[1].trim();
			//First check the no of Tags 
			if("BLANK".equalsIgnoreCase(tagValue)) {
				tagValue="";
			}
			if ("AUTO_NUM".equalsIgnoreCase(tagValue)) {
				tagValue = counterService.getCounter();
			}
			
			int noOfTags=commonService.countXMLTag(inputFile, tagName);
			if(noOfTags==2) {
				tagsList.add(tagValue);
				count++;
				if(count<2)
				continue;
			}
			try {
				
				switch(noOfTags) {
				
				case 1:
					String xmlValuesFromTag = xmlUtilService.getValueFromXML(inputFile, tagName);
					logger.info("Current Tag value :: " + xmlValuesFromTag);
					// if updated then find the XML
					b = xmlUtilService.modifyValuesInXML(inputFile, tagName, tagValue);
				break;
				case 2:
				    b = xmlUtilService.modifyValuesInXMLTags(inputFile, tagName, tagsList);
                  break;
				case 0://means tag not found
				case -1:///means Exception found
				    b=false;
				    break;
					
				}

			} catch (Exception e) {
				b = false;
				logger.error("Error {Fin-0003} :  Modifying XML Exeption {} " + e.getMessage());


			}
		}
		return b;
	}
	
	/***
	 * 
	 * @param inputFile {@link File}
	 * @param apiName {@link String}
	 * @param apiStiring {@link String}
	 * @return {@link Boolean}
	 * @throws WatchDogException as exception
	 */
	
	public boolean refreshXMLTagValues(File inputFile, String apiName, 
			String appStr)  {
		
		boolean b = false;
		
		String apiString=AppUtil.checkAndRemoveCurlyStr(appStr);

		if (apiString.isEmpty()) {
			logger.info("API Args are Empty {} ");
			return true;
		}
		String[] pipeSeparator = apiString.split("\\|");
		List<String> tagsList=new ArrayList<>();
		int count=0;
		for (String s : pipeSeparator) {
			String tagName = s.split(AppUtil.EQUAL_STR)[0];
			String tagValue = AppUtil.EMPTY_STR;
			//First check the no of Tags 
			int noOfTags=commonService.countXMLTag(inputFile,  tagName);
			if(noOfTags==2) {
				tagsList.add(AppUtil.EMPTY_STR); //Sending Blank Values as we need fresh XML
				count++;
				if(count<2)
				continue;
			}
			try {
				
				switch(noOfTags) {
				case 1:
					b = xmlUtilService.modifyValuesInXML(inputFile, tagName, tagValue);
				break;
				case 2:
				    b = xmlUtilService.modifyValuesInXMLTags(inputFile, tagName, tagsList);
                  break;
				case 0://means tag not found
				case -1:///means Exception found
				    b=false;
				    break;
				}

			} catch (Exception e) {
				b = false;
				logger.error("Error {Fin-0005} :  XML Exeption {} " + e.getMessage());

			}
		}
		return b;
	}
	
	
	/***
	 * 
	 * @param fileName
	 * @return
	 */

	private String[]  prepareCommandArgs(String fileName, String apiName,String group) {

		// String
		String[] out_with_Args = new String[2];
		String inputFolderFileName = dataLoader.configDto.getOptInputFolderPath() + File.separator + fileName;
		String outPutFolder = dataLoader.configDto.getOutputFolderPath() + File.separator + ""
				+ dataLoader.configDto.getResponseFilePrefix()
				+ FilenameUtils.getBaseName(fileName) 
				+ AppUtil.UNDERSCORE_STR
				+ AppUtil.currentTime() +AppUtil.DOT_STR+dataLoader.configDto.getFileExtension();

		if ("commit".equals(group)) {// For Commit API I have created files in output directory
			inputFolderFileName = dataLoader.configDto.getOutputFolderPath() + File.separator + fileName;
			outPutFolder = inputFolderFileName;// for Commit API response is generating by codes itself thats why output
												// file and input file would be same
		}

		StringBuilder sb = new StringBuilder(apiName).append(AppUtil.AND_STR)
				.append(inputFolderFileName).append(AppUtil.AND_STR)
				.append(outPutFolder);
		
		out_with_Args[0]=sb.toString();
		out_with_Args[1]=outPutFolder;

		return out_with_Args;
	}

	/***
	 * This method will check the file convention for further process
	 * 
	 * @param fileName
	 * @return
	 */
	private boolean checkFileConvention(String fileName) {
		if (!StringUtils.getFilenameExtension(fileName).
				equalsIgnoreCase(dataLoader.configDto.getFileExtension())) {
			return false;
		}
		fileType = AppUtil.EMPTY_STR;
		final String fname = fileName;
		try {
			String fileEndWith = dataLoader.configDto.getOptFileSupports();
			if (fileEndWith.isEmpty() && fileEndWith.length() == 0) {
				throw new WatchDogException(
						"OPT File naming convention {empty}. Check properties file for your reference.");
			}
			String fileNameAfterFileSepartor = fname.split(dataLoader.configDto.getFileTypeSeparator())[1]
					.split(AppUtil.DOT_STR_COMP)[0];// .xml

			List<String> fileDcOrCam = commonService.split(fileEndWith.trim(), AppUtil.COMMA_SEPERATOR);
			for (String fn : fileDcOrCam) {
				String noSpaceStr = fn.replaceAll(AppUtil.WHITE_SPACE, AppUtil.EMPTY_STR);
				if (fileNameAfterFileSepartor.equalsIgnoreCase(noSpaceStr)
						|| StringUtils.startsWithIgnoreCase(fileNameAfterFileSepartor, noSpaceStr)) {
					fileType = fn;
					logger.info("[ :: " + fileType + " ::] type file found.");
					return true;
				}
			}

		} catch (Exception e) {
			logger.error("Error {Fin-0006} : File Convention : " + (fileName) + "  " + e.getMessage());
			return false;
		}
		return false;
	}
	
}
