package com.hcl.dog.component;

import java.io.File;
import java.nio.file.Paths;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.DisallowConcurrentExecution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import com.hcl.dog.common.AppUtil;
import com.hcl.dog.common.DogFileFilter;
import com.hcl.dog.common.WatchDogException;
import com.hcl.dog.dto.APIDto;
import com.hcl.dog.service.PrepMailService;
import com.hcl.dog.service.CommonService;
import com.hcl.dog.service.SoOrderService;
/***
 * 
 * @author intakhabalam.s@hcl.com
 * @see {@link Component}
 * @see {@link Repository}
 * @see {@link Service}
 * @see {@link Controller}
 * @see {@link DisallowConcurrentExecution}
 */
@DisallowConcurrentExecution
@Component
public class SOComponent {
	private Logger logger = LogManager.getLogger("so-comp");
	
	@Autowired
	private SoOrderService soOrderService;
	@Autowired
	private CommonService commonService;
	@Autowired
	private DataLoaderComponent dataLoader;
	@Autowired
	private PrepMailService prepMailService;
	private boolean isRunNext = false;
    
	
	 @Bean
     public String soBatchG1(){
	        return dataLoader.configDto.getSoCronTimeG1();
     }
	 
	 @Bean
     public String soBatchG2(){
	        return dataLoader.configDto.getSoCronTimeG2();
     }
	
	/***
	 * G1
	 */
	@Scheduled(cron = "#{@soBatchG1}")
	public void invokeSoRunGrop1() {
		if(validation()) {
			return;
		}
		final long startTime = System.currentTimeMillis();
		logger.info("=======================================================================");
		logger.info("SOG1 Starting Time [ " + LocalTime.now() + " ]");
		String fileName = AppUtil.EMPTY_STR;
		try {
			final File inputDirFiles = Paths.get(dataLoader.configDto.getSoOrderInputFolderPath()).toFile();
			logger.info("SOG1 Scanning Input directory [ " + dataLoader.configDto.getSoOrderInputFolderPath() + " ]");
			
			File[] filesInDir = inputDirFiles.listFiles(new DogFileFilter(dataLoader.configDto.getFileSupports()));
			if (filesInDir==null || filesInDir.length == 0) {
				logger.info("SOG1 Input directory is Empty.");
			} else {
				logger.info("SOG1 Input directory size [ " + filesInDir.length + " ] ");
				List<APIDto> apiDtoList = getSoAPIDto("G1");
				Set<File> succesFailList = new LinkedHashSet<File>();
				for (APIDto api : apiDtoList) {
					for (File fileToProcess : filesInDir) {
						fileName = fileToProcess.getName();
						if (fileToProcess.getName().startsWith(api.getApiName())) {
							succesFailList.add(fileToProcess);
							isRunNext = soOrderService.processSoOrder(fileToProcess, 1, api,"G1");
							api.setFileName(fileName);
							api.setFile(fileToProcess);
							api.setStatus(isRunNext);
							api.setMessageHeader("SO G1-"+api.getApiName());
                            api.setGroupType("SO");
							// sleep time being
							try {
								TimeUnit.SECONDS.sleep(2);
							} catch (InterruptedException e1) {
								logger.error("SOG1 InterruptedException {} " + e1.getMessage());
							}
						}

					}
				}
				prepMailService.prepareAndSendAPIsMails(apiDtoList);
			}

		} catch (WatchDogException ex) {
			logger.error("FinG1 Run into an error {WatchDog Exception}", ex);
		}
		final long endTime = System.currentTimeMillis();
		final double totalTimeTaken = (endTime - startTime) / (double) 1000;
		logger.info("SOG1 Finishing Time [ " + LocalTime.now() + " ] => Total time taken to be completed  [ "
				+ totalTimeTaken + "s ]");

	}
	
	
	/***
	 * G2
	 */
	@Scheduled(cron = "#{@soBatchG2}")
	public void invokeSoRunGrop2() {
		if(validation()) {
			return;
		}
		final long startTime = System.currentTimeMillis();
		logger.info("=======================================================================");
		logger.info("SOG2 Starting Time [ " + LocalTime.now() + " ]");
		String fileName = AppUtil.EMPTY_STR;
		try {
			final File inputDirFiles = Paths.get(dataLoader.configDto.getSoOrderInputFolderPath()).toFile();
			logger.info("SOG2 Scanning Input directory [ " + dataLoader.configDto.getSoOrderInputFolderPath() + " ]");
			
			File[] filesInDir = inputDirFiles.listFiles(new DogFileFilter(dataLoader.configDto.getFileSupports()));
			if (filesInDir==null || filesInDir.length == 0) {
				logger.info("SOG2 Input directory is Empty.");
			} else {
				logger.info("SOG2 Input directory size [ " + filesInDir.length + " ] ");
				List<APIDto> apiDtoList = getSoAPIDto("G2");
				Set<File> succesFailList = new LinkedHashSet<File>();
				for (APIDto api : apiDtoList) {
					for (File fileToProcess : filesInDir) {
						fileName = fileToProcess.getName();
						if (fileToProcess.getName().startsWith(api.getApiName())) {
							succesFailList.add(fileToProcess);
							if(isRunNext) {
								isRunNext = soOrderService.processSoOrder(fileToProcess, 2, api,"G2");
								api.setFileName(fileName);
								api.setFile(fileToProcess);
								api.setStatus(isRunNext);
								api.setMessageHeader("SO G2-"+api.getApiName());
	                            api.setGroupType("SO");
								// sleep time being
								try {
									TimeUnit.SECONDS.sleep(2);
								} catch (InterruptedException e1) {
									logger.error("SOG2 InterruptedException {} " + e1.getMessage());
								}
							}else {
								api.setMailDescription("SO G1-"+api.getApiName() +" does not run successfully.");
								api.setMessageHeader("SO G2-"+api.getApiName());
	                            api.setGroupType("SO");
							}
						}

					}
				}
				if(isRunNext) {
					//If both Batch Successfully run then take the backup of this file.
					commonService.backupPlanIdFile();
				}
				prepMailService.prepareAndSendAPIsMails(apiDtoList);

			}

		} catch (WatchDogException ex) {
			logger.error("SOG2 Run into an error {WatchDog Exception}", ex);
		}
		final long endTime = System.currentTimeMillis();
		final double totalTimeTaken = (endTime - startTime) / (double) 1000;
		logger.info("SOG2 Finishing Time [ " + LocalTime.now() + " ] => Total time taken to be completed  [ "
				+ totalTimeTaken + "s ]");

	}
	
	
	
	
	/***
	 * This method will give Object of API
	 * @return {@link List}
	 */
	private List<APIDto> getSoAPIDto(String group) {
		String[] fileOrder = dataLoader.configDto.getSoOrderSupportsAPI();
		List<APIDto> apiDtoList = new ArrayList<>();
		for (String s : fileOrder) {
			APIDto apiDto = new APIDto();
			String apiName = s.split(dataLoader.configDto.getFileTypeSeparator())[0];
			String apiStrArgs = s.split(dataLoader.configDto.getFileTypeSeparator())[1];
			if (apiName.startsWith(group)) {
				apiName = apiName.replaceAll(group.concat("-"), AppUtil.EMPTY_STR);
				apiDto.setApiName(apiName);
				apiDto.setApiStrProcess(apiStrArgs);
				apiDtoList.add(apiDto);
			}
		}
		return apiDtoList;
	}

	/**
	 * Check validation
	 * @return {@link Boolean}
	 */
	public boolean validation() {

		if (dataLoader.configDto.isStopSoBatchRun()) {
			logger.info("SO Batch is stoped... For starting reconfigure from settings {} ");
			return true;
		}
		if (dataLoader.configDto.getSoOrderInputFolderPath().isEmpty()) {
			logger.info("SO Input Folder is not configure {} ");
			return true;
		}
		return false;
	}
	
}

