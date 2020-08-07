package com.hcl.dog.service;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.hcl.dog.common.WatchDogException;
import com.hcl.dog.component.DataLoaderComponent;
import com.hcl.dog.dto.APIDto;
import com.hcl.dog.dto.ResponseDto;
import com.hcl.dog.service.CommonService;
import com.hcl.dog.service.CounterService;
import com.hcl.dog.service.XMLUtilService;

/***
 * 
 * @author intakhabalam.s@hcl.com
 *
 */
@Service
public class FinancialTestBatchService {

	private Logger logger = LogManager.getLogger("Dog-2");
	
	@Autowired
	private CommonService commonService;
	@Autowired
	private XMLUtilService xmlUtilService;
	@Autowired
	private DataLoaderComponent dataLoaderComponent;
	
	private String fileType;
	@Autowired
	private CounterService counterService;
	
    private final String API_NAME_FOR_ARCHIVE="processGLTransactionRetrieve";
    
	/***
	 * 
	 * @param f
	 * @param count
	 * @throws WatchDogException
	 */
	public boolean processFinRun(File inputFile, int count,APIDto api, String group, boolean archive) throws WatchDogException {
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
						// Means XML Modified successfully
						boolean isModifiedXML = modifyInputApiXML(inputFile, apiName, apiStr);
						if (isModifiedXML) {
							ResponseDto responseDto = commonService.runCommandLineBatch(
									dataLoaderComponent.configDto.getBatchFilePath(), prepareCommandArgs(fileName, apiName),
									fileName);
							if (responseDto.isCommandRun()) {
								logger.info("Fin Batch Run Successfully...");
								if (checkResponseCode(inputFile,apiName,archive)) {
									logger.info("Fin File completed sucessfully [ " + fileName + " ] ");
									success= true;
									commonService.addFilesForReports(fileName,"Passed","P");

								}
							}
						}
					}
				}else {
					if ("true".equalsIgnoreCase(dataLoaderComponent.configDto.getEnableArchiveOthersFile())) {
						 commonService.gotoArchiveFailureFolder(inputFile.getPath(), fileName,"P");
					}
				}

			 } catch (Exception e) {
				commonService.addFilesForReports(fileName,"Exception during processing","F");
				success=false;
				throw new WatchDogException("Error :  {processOPTRun} :  " + e.getMessage());

			 }finally {
				 refreshXMLTagValues(inputFile, apiName, apiStr);
			 }
			
			return success;
		}
	}

	/***
	 * 
	 * @param inputFile
	 * @return
	 * @throws WatchDogException
	 */
	private boolean checkResponseCode(File inputFile,String apiName,boolean archive) throws WatchDogException {

		logger.info("OPT Scanning Response XML in directory  [ " + dataLoaderComponent.configDto.getOutputFolderPath() + " ]");
		String fileName = inputFile.getName();
		//String filePath = inputFile.getPath();
		String resFileNameStartWith = dataLoaderComponent.configDto.getResponseFilePrefix().concat(fileName);

		String responeFile = dataLoaderComponent.configDto.getOutputFolderPath().concat(File.separator)
				.concat(resFileNameStartWith);

		if (!checkResponseCodeFile(responeFile, inputFile)) {
			logger.error("Fin { :: Not found Response XML :: } ===>  for file [ " + fileName + " ]");
			commonService.addFilesForReports(fileName,"Not found Response XML","F");

			return false;
		}

		logger.info("Fin { :: Found Response XML :: } ===>  for file [ " + resFileNameStartWith + " ]");
		logger.info("Response XML file location [ " + responeFile + " ]");

		try {
			File responseFile = Paths.get(responeFile).toFile();
			String responseTagVal = xmlUtilService.getValueFromXML(responseFile,
					dataLoaderComponent.configDto.getResponeCodeTag());
			// Print XML
			 xmlUtilService.printXML(responseFile);
			if ("false".equalsIgnoreCase(responseTagVal)) {
				   commonService.addFilesForReports(fileName,"Check Response XML","F");

				return false;
			} else {
					if(API_NAME_FOR_ARCHIVE.equals(apiName) && archive) {
				        commonService.gotoArchiveFailureFolder(responeFile, resFileNameStartWith, "P");
					}
				return true;
			}

		} catch (Exception e) {
			throw new WatchDogException("Error {000777} :  File Exeption " + e.getMessage());
		}

	}

	/**
	 * if sting will come like cam then changeArray=cam=changeEntity will get the
	 * values changeEntity
	 * 
	 * @param ct
	 * @return
	 * @throws WatchDogException
	 */
	private boolean modifyInputApiXML(File inputFile, String apiName, String apiStiring) throws WatchDogException {
	
		boolean b = false;
		if (apiStiring.contains("{") || apiStiring.contains("}")) {
			apiStiring = apiStiring.replaceAll("\\{", "").replaceAll("\\}", "");
		}
		if (apiStiring.isEmpty()) {
			logger.info("API Args are Empty {} ");
			return true;
		}
		String[] pipeSeparator = apiStiring.split("\\|");
		List<String> tagsList=new ArrayList<>();
		int count=0;
		for (String s : pipeSeparator) {
			String tagName = s.split("=")[0];
			String tagValue = s.split("=")[1];
			//First check the no of Tags 
			int noOfTags=commonService.countXMLTag(inputFile, tagName);
			if(noOfTags==2) {
				tagsList.add(tagValue);
				count++;
				if(count<2)
				continue;
			}
			if (tagValue.equals("?")) {
				tagValue = counterService.getCounter();
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
				throw new WatchDogException("Error {00054} :  Modifying XML Exeption {} " + e.getMessage());

			}
		}
		return b;
	}
	
	/***
	 * 
	 * @param inputFile
	 * @param apiName
	 * @param apiStiring
	 * @return
	 * @throws WatchDogException
	 */
	
	private boolean refreshXMLTagValues(File inputFile, String apiName, String apiStiring) throws WatchDogException {
		
		boolean b = false;
		if (apiStiring.contains("{") || apiStiring.contains("}")) {
			apiStiring = apiStiring.replaceAll("\\{", "").replaceAll("\\}", "");
		}
		if (apiStiring.isEmpty()) {
			logger.info("API Args are Empty {} ");
			return true;
		}
		String[] pipeSeparator = apiStiring.split("\\|");
		List<String> tagsList=new ArrayList<>();
		int count=0;
		for (String s : pipeSeparator) {
			String tagName = s.split("=")[0];
			String tagValue = "";
			//First check the no of Tags 
			int noOfTags=commonService.countXMLTag(inputFile, tagValue);
			if(noOfTags==2) {
				tagsList.add(""); //Sending Blank Values as we need fresh XML
				count++;
				if(count<2)
				continue;
			}
			try {
				
				switch(noOfTags) {
				
				case 1:
					b = xmlUtilService.modifyValuesInXML(inputFile, tagName, "");
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
				throw new WatchDogException("Error {00055} :  Refreshing XML Exeption {} " + e.getMessage());

			}
		}
		return b;
	}
	
	
	/***
	 * 
	 * @param fileName
	 * @return
	 */

	private String prepareCommandArgs(String fileName, String apiName) {

		// String
		// apiName=fileName.split(dataLoader.configDto.getFileTypeSeparator())[0];
		String inputFolderFileName = dataLoaderComponent.configDto.getOptInputFolderPath() + File.separator + fileName;
		String outPutFolder = dataLoaderComponent.configDto.getOutputFolderPath() + File.separator + ""
				+ dataLoaderComponent.configDto.getResponseFilePrefix() + fileName;

		StringBuilder sb = new StringBuilder(apiName).append("&").append(inputFolderFileName).append("&")
				.append(outPutFolder);
		return sb.toString();
	}

	/***
	 * This method will check the file convention for further process
	 * 
	 * @param fileName
	 * @return
	 */
	private boolean checkFileConvention(String fileName) {

		if (!StringUtils.getFilenameExtension(fileName).equals(dataLoaderComponent.configDto.getFileExtension())) {

			return false;
		}
		fileType = "NOT VALID";
		final String fname = fileName;
		try {
			String fileEndWith = dataLoaderComponent.configDto.getOptFileSupports();
			if (fileEndWith.isEmpty() && fileEndWith.length() == 0) {
				throw new WatchDogException(
						"OPT File naming convention {empty}. Check properties file for your reference.");
			}
			String fileNameAfterFileSepartor = fname.split(dataLoaderComponent.configDto.getFileTypeSeparator())[1]
					.split("\\.")[0];// .xml

			List<String> fileDcOrCam = commonService.split(fileEndWith.trim(), ",");
			for (String fn : fileDcOrCam) {
				String noSpaceStr = fn.replaceAll("\\s", "");
				if (fileNameAfterFileSepartor.equalsIgnoreCase(noSpaceStr)
						|| StringUtils.startsWithIgnoreCase(fileNameAfterFileSepartor, noSpaceStr)) {
					fileType = fn;
					logger.info("[ :: " + fileType + " ::] type file found.");
					return true;
				}
			}

		} catch (Exception e) {
			logger.error("Error {00099} : File Convention : " + (fileName) + "  " + e.getMessage());
			return false;
		}
		return false;
	}

	

	/**
	 * 
	 * @param responeFile
	 * @param inputFile
	 * @return
	 */
	private boolean checkResponseCodeFile(String responeFile, File inputFile) {
		File resFile = Paths.get(responeFile).toFile();
		if (!resFile.exists()) {
			logger.info("Response XML file [ " + resFile.getName() + " ] not found in directory [ "
					+ dataLoaderComponent.configDto.getOutputFolderPath() + " ]");

			return false;

		}
		return true;
	}
}
