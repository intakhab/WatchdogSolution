package com.hcl.dog.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.xml.bind.JAXBException;

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
import com.hcl.dog.domain.Entity;
import com.hcl.dog.domain.LoadsEntity;
import com.hcl.dog.dto.APIDto;
import com.hcl.dog.dto.ResponseDto;
/**
 * @author intakhabalam.s@hcl.com
 * NonEdi Service Business logic for watchdog 
 * NonEdi/Wine Services
 * @see Service
 * @see DisallowConcurrentExecution {@link DisallowConcurrentExecution}
 * @see PrepMailService {@link PrepMailService}
 * @see CommonService {@link CommonService}
 * @see XMLUtilService {@link XMLUtilService}
 * @see DataLoaderComponent  {@link DataLoaderComponent}
 * @see CounterService {@link CounterService}
 * @see Environment {@link Environment}
 */
@Service
@DisallowConcurrentExecution
public class NonEdiService  {
  
	private Logger logger = LogManager.getLogger("nonedi-serv");
	
	@Autowired
	private CommonService commonService;
	@Autowired 
	private XMLUtilService xmlUtilService;
	@Autowired
	private DataLoaderComponent dataLoader;
	private String fileType;
    private final String SYSTEM_LOAD_ID = "SystemLoadID";//TAG name in XML
    private final String CARRIER_CODE = "CarrierCode"; //TAG name in XML
	/***
	 * This is the entry point of nonedi services. will take input xml/file and start processing 
	 * as per the given instruction from UI
	 * @param apiDto {@link APIDto}
	 * @param count {@link String}
	 * @param group {@link String}
	 * @throws WatchDogException as exception
	 * @return {@link Boolean}
	 */
	public boolean processNonEdiRun(APIDto apiDto, int count, String group) throws WatchDogException {

		synchronized (this) {
			
			boolean success = false;
			String fileName = apiDto.getFileName();
			try {
				if (checkFileConvention(fileName)) {// ok
					logger.info(
							"------------------------------------------------------------------------------------------");
					logger.info("NonEdi Processing file name [ " + fileName + " ] - counter  [ " + count + " ]  - group [ "+group+ "]");

					String[] apis = apiDto.getApiName().split(AppUtil.UNDERSCORE_STR);
					
					List<String> pipeSeparator = getTagsAndValueApi(apiDto.getApiStrProcess());
					if (pipeSeparator.size() == 0) {
						throw new WatchDogException("Missing APIs Arguments { Check Config UI } ");
					}

					String[] readput = pipeSeparator.get(0).split(AppUtil.EQUAL_STR)[1].split(AppUtil.UNDERSCORE_STR);
					
					for (String rp : readput) {
						
						if ("READ".equalsIgnoreCase(rp)) {
							
							logger.info("READ : APIs  { " + Arrays.toString(apis) + "} Calling  [ " + apis[0]
									+ "]");
							String[] command = prepareCommandArgs(apiDto.getFileName(), apis[0].trim());// findEntity=apis[0]
							
							commonService.runCommandLineBatch(dataLoader.configDto.getBatchFilePath(), command[0],
									command[1]);
							logger.info("NonEdi Command line run successfully and checking response xml in output folder.....");
							if (checkResponseCode(apiDto, apiDto.getFile(), command[1])) {// Bogous as need that //Every time hitting API and  checking response
                           logger.info("Group ["+group+"]  calling {} for READ ");
					    	switch (pipeSeparator.size()) {

								case 1:
									logger.info("Case 1 Invoking for READ Starting...");
									apiDto = getSystemLoadAndCarrier(apiDto);
									List<String> systemLoadList = apiDto.getSystemLoadID();
									int size = systemLoadList.size();
									logger.info("Total SystemLoad Founds [ " + size + " ]");
									apiDto.setSystemLoadID(systemLoadList);
									if (size > 0) {
										success = true;
										logger.info("Loaded all SystemLoads from XML in case 1");
									} else {
										logger.info("[SystemLoads] size :: { 0 } :: ");
									    commonService.addFilesForReports(fileName,"SystemLoads size 0","P");
									    apiDto.setMailDescription("SystemLoads size 0");
									    success=false;

									}
									logger.info("Case 1 Invoking for READ  Finsihed...");

									break;
								case 2:
									logger.info("Case 2 Invoking for READ Starting...");
									apiDto = getSystemLoadAndCarrier(apiDto);
									int carrierSize = apiDto.getCarrierCodes().size();
									int loadSize = apiDto.getSystemLoadID().size();
									//
									logger.info("Total SystemLoad Founds [ " + loadSize + " ]");
									logger.info("Total CarrierCode Founds [ " + carrierSize + " ]");
									if ((loadSize > 0 && carrierSize > 0) && loadSize == carrierSize) {
										success = true;
										logger.info("Loaded all SystemLoads and CarrierCodes from XML valus in case 2");
									} else {
										logger.info("[SystemLoads] and [CarrierCodes size :: { 0 } :: ");
									    commonService.addFilesForReports(fileName,"SystemLoads & CarrierCodes size 0","P");
									    apiDto.setMailDescription("SystemLoads & CarrierCodes size 0");

									    success=false;
									}
									logger.info("Case 2 Invoking for READ  Finsihed...");
									break;
								}

							}
						}if ("PUT".equalsIgnoreCase(rp) && success) {

								logger.info("PUT : APIs  { " + Arrays.toString(apis) + "} Calling  [ " + apis[1] + "]");
								String fileSupports="";
								/*****************************/
								if("G1".equalsIgnoreCase(group)){
									 fileSupports=dataLoader.configDto.getNonEdiCamFileSupports();
								}else {
									 fileSupports=dataLoader.configDto.getNonEdiCamWineFileSupports();
								}
								/****/
								String fname = apis[1] + dataLoader.configDto.getFileTypeSeparator()
										+ fileSupports + AppUtil.DOT_STR+ dataLoader.configDto.getFileExtension();

								logger.info("File name for XML Modification {} : " + fname);
								
								File sysCamFile = Paths.get(dataLoader.configDto.getNonEdiCamInputFolderPath() 
										+ File.separator + fname).toFile();

		                         logger.info("Group ["+group+"]  calling {} for PUT ");

								switch (pipeSeparator.size()) {

									case 1:
										logger.info("Case 1 Invoking for PUT Starting...");
										List<String> sysLoadList1 = apiDto.getSystemLoadID();
										processApis(sysLoadList1, null, sysCamFile, apis[1], apiDto, "sys");
										logger.info("Case 1 Invoking for PUT Finished...");
	
										break;
									case 2:
										logger.info("Case 2 Invoking for PUT Starting...");
										List<String> sysList = apiDto.getSystemLoadID();
										List<String> camList = apiDto.getCarrierCodes();
										int loadSize = sysList.size();
										int camSize = camList.size();
										if ((loadSize > 0 && camSize > 0) && loadSize == camSize) {
											processApis(sysList, camList, sysCamFile, apis[1],
													apiDto, "cam");
										}
										logger.info("Case 2 Invoking for PUT Finished...");
										break;
	
								}

						  }
					  }

				}

			} catch (Exception e) {
				success = false;
				throw new WatchDogException("Error :  {Edi-0001} :  " + e.getMessage());
			}

			return success;
		}
	}

	/***
	 * This method will load system load and carrier code for 
	 * sending to TMS
	 * @param apiDto
	 * @param outputFile
	 * @return {@link APIDto}
	 * @throws FileNotFoundException
	 * @throws JAXBException
	 */
	private APIDto getSystemLoadAndCarrier(APIDto apiDto) 
			throws FileNotFoundException, JAXBException {
		logger.info("In Load And Cam {method} ");
		LoadsEntity loadEntity = (LoadsEntity) xmlUtilService.
				convertXMLToObject(LoadsEntity.class, apiDto.getOutputFile());
		
		Entity[] entity = loadEntity.getEntity();
		List<String> sList = new ArrayList<>(0);
		List<String> cList = new ArrayList<>(0);
		if (entity != null && entity.length > 0) {
			for (Entity en : entity) {
				if (en.getLoad().getSystemLoadID() != null) {
					sList.add(en.getLoad().getSystemLoadID());
					logger.info("System Loads :"+en.getLoad().getSystemLoadID());
				}
				if (en.getLoad().getCarrierCode() != null) {
					cList.add(en.getLoad().getCarrierCode());
					logger.info("CarrierCodes :"+en.getLoad().getCarrierCode());

				}
			}
			apiDto.setSystemLoadID(sList);
			apiDto.setCarrierCodes(cList);
		}
		return apiDto;
	}
	/***
	 * Taking systemload and carrier code it will process the API
	 * @param systemLoadList
	 * @param carrierLoadList
	 * @param sysCamFile
	 * @param apis
	 * @param apiDto
	 * @param type
	 * @return {@link Boolean}
	 * @throws WatchDogException
	 */
	private boolean processApis(List<String> systemLoadList, List<String> carrierLoadList, File sysCamFile,
			String apis, APIDto apiDto, String type) throws WatchDogException {
		boolean isRun = false;
		if ("sys".equals(type)) {
			for (String systemLoadId : systemLoadList) {
				// modify SystemLoadId
			     logger.info("Processing SystemLoadID [ "+  systemLoadId  +" ]");
				if (xmlUtilService.modifyValuesInXML(sysCamFile, SYSTEM_LOAD_ID, systemLoadId)) {
					isRun = prepareBatchRun(sysCamFile, apis, apiDto);
					logger.info("APIs {  "+apis+" }  response status [ "+isRun+ "]");

					try {
						Thread.sleep(100);
					 } catch (Exception e) {
					}
				}

			}
		} else {
             
			for (int i = 0; i < systemLoadList.size(); i++) {  //bcoz sysLoad and camCode size are equals
				String sysLoad = systemLoadList.get(i);
				String camCode = carrierLoadList.get(i);
			     logger.info("Processing SystemLoadID  [ "+  sysLoad  +" ]  and CarrierCode [  "+camCode+  "]");

				    isRun = xmlUtilService.modifyValuesInXML(sysCamFile,SYSTEM_LOAD_ID, sysLoad);
				if (isRun)
					isRun = xmlUtilService.modifyValuesInXML(sysCamFile, CARRIER_CODE, camCode);

				if (isRun) {
					isRun = prepareBatchRun(sysCamFile, apis, apiDto);
					logger.info("APIs {  "+apis+" }  response status [ "+isRun+ "]");
					try {
						Thread.sleep(100); //  would be fine
					} catch (Exception e) {
					}
				}

			}

		}
		return isRun;
	}


	/***
	 * This method will invoke the Batch File with parameter
	 * @param sysCamFile
	 * @param apis
	 * @param apiDto
	 * @return {@link Boolean}
	 * @throws WatchDogException
	 */

	private boolean prepareBatchRun(File sysCamFile, String apis, APIDto apiDto)
			throws WatchDogException {
		
		String [] command=prepareCommandArgs(sysCamFile.getName(),apis);
		logger.info("Invoking {"+apis+"} ");
		ResponseDto respD=commonService.runCommandLineBatch(dataLoader.configDto.getBatchFilePath(),
				command[0],command[1]);
		if(respD.isCommandRun()) {
			return checkResponseCode(apiDto, sysCamFile,command[1]);
		}
		return false;		
	}


	/***
	 * Manipulation the API which is coming from UI
	 * @param str
	 * @return {@link List}
	 * @throws WatchDogException
	 */
	private List<String> getTagsAndValueApi(String str) throws WatchDogException {
		String apiString = AppUtil.checkAndRemoveCurlyStr(str);;
		if (apiString.isEmpty()) {
			logger.info("NonEdi API args are Empty {} ");
			return Collections.emptyList();
		}
		return commonService.split(apiString, "\\|");
	}
	

	/***
	 * This method will check the response xml which is sending by TMS in specific folder
	 * @param inputFile
	 * @return {@link Boolean}
	 * @throws WatchDogException
	 */
	private boolean checkResponseCode(APIDto apiDto,File f,String responeFile) throws WatchDogException {
         File inputFile=f;
		logger.info("NonEdi Scanning Response XML in directory  [ " + dataLoader.configDto.getOutputFolderPath() + " ]");
		String fileName = inputFile.getName();

		if (!commonService.checkFile(responeFile)) {
			logger.error("NonEdi { :: "+AppUtil.RESPONSE_NOT_FOUND_FROM_TMS+" :: } ===>  for file [ " + fileName + " ]");
			commonService.addFilesForReports(fileName,AppUtil.RESPONSE_NOT_FOUND_FROM_TMS,"F");
		    apiDto.setMailDescription(AppUtil.RESPONSE_NOT_FOUND_FROM_TMS);

			return false;
		}

		logger.info("NonEdi { :: "+AppUtil.RESPONSE_FOUND_FROM_TMS+" :: } ===>  for file [ " + responeFile + " ]");

		try {
			File responseFile = Paths.get(responeFile).toFile();
			String responseTagVal = xmlUtilService.getValueFromXML(responseFile,
					dataLoader.configDto.getResponeCodeTag());
			apiDto.setOutputFile(responseFile);
			// Print XML
			xmlUtilService.printXML(responseFile);
			if (AppUtil.FALSE_STR.equalsIgnoreCase(responseTagVal)) {
				commonService.addFilesForReports(fileName, AppUtil.CHECK_RESP_XML,"F");
			    apiDto.setMailDescription(AppUtil.CHECK_RESP_XML);

				return false;
			}else {
				commonService.addFilesForReports(fileName,"Passed","P");
				return true;
			}

		} catch (Exception e) {
			throw new WatchDogException("Error: {Edi-0002} :  WatchDog Exeption " + e.getMessage());
		}

	}

	
	
	/***
	 * This method will prepare the arguments for command line processing.
	 * @param fileName
	 * @return {@link String}
	 */

	private String[] prepareCommandArgs(String fileName, String apiName) {
		// String
		String [] out_with_Args=new String[2];
		String inputFolderFileName = dataLoader.configDto.getNonEdiCamInputFolderPath() + File.separator + fileName;
		String outPutFolder = dataLoader.configDto.getOutputFolderPath() 
				+ File.separator 
				+ dataLoader.configDto.getResponseFilePrefix()
				+ FilenameUtils.getBaseName(fileName)
				+ AppUtil.UNDERSCORE_STR
				+ AppUtil.currentTime()
				+ AppUtil.DOT_STR+dataLoader.configDto.getFileExtension();

		StringBuilder sb = new StringBuilder(apiName).append(AppUtil.AND_STR).append(inputFolderFileName).append(AppUtil.AND_STR)
				.append(outPutFolder);
		out_with_Args[0]=sb.toString();
		out_with_Args[1]=outPutFolder;

		return out_with_Args;
	}

	/***
	 * This method will check the file convention for further process
	 * 
	 * @param fileName
	 * @return {@link Boolean}

	 */
	private boolean checkFileConvention(String fileName) {

		if (!StringUtils.getFilenameExtension(fileName).equalsIgnoreCase(dataLoader.configDto.getFileExtension())) {

			return false;
		}
		fileType = "NOT VALID";
		final String fname = fileName;
		try {
			String fileEndWith = dataLoader.configDto.getNonEdiCamFileSupports()
					.concat(AppUtil.COMMA_SEPERATOR).concat(dataLoader.configDto.getNonEdiCamWineFileSupports());
			if (fileEndWith.isEmpty() && fileEndWith.length() == 0) {
				throw new WatchDogException(
						"NonEdi File naming convention {empty}. Check properties file for your reference.");
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
			logger.error("Error: {Edi-0003} : File Convention : " + (fileName) + "  " + e.getMessage());
			return false;
		}
		return false;
	}

}
