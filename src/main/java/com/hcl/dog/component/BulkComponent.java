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
import com.hcl.dog.dto.APIDto;
import com.hcl.dog.service.BulkService;
import com.hcl.dog.service.PrepMailService;

/***
 * 
 * @author intakhabalam.s@hcl.com
 * @see  {@link Component}
 * @see  {@link Repository}
 * @see  {@link Service}
 * @see  {@link Controller}
 * @see  {@link DisallowConcurrentExecution}
 */
@DisallowConcurrentExecution
@Component
public class BulkComponent {
	private Logger logger = LogManager.getLogger("blk-comp");

	@Autowired
	private BulkService BulkService;

	@Autowired
	private DataLoaderComponent dataLoader;
	
	@Autowired
	private PrepMailService prepMailService;
	
    /**
     * @see Bean 
     * @return Bulk {@link String}
     */
	@Bean
	public String bulkBatchG1() {
		return 
				dataLoader.configDto.getBulkPoollingTimeG1();
	}
	
	 /**
     * @see Bean 
     * @return Bulk {@link String}
     */
	@Bean
	public String bulkBatchG2() {
		return 
				dataLoader.configDto.getBulkPoollingTimeG2();
	}
  
	 
	/*****
	 * This is for Bulk Carrier
	 * @see Scheduled
	 */
	@Scheduled(cron = "#{@bulkBatchG1}")

	public void invokeBulkRunG1() {
		// validate before execution of program.
		if(!validate()) {
			return;
		}

		final long startTime = System.currentTimeMillis();
		logger.info("=======================================================================");
		logger.info("G1: Bulk Starting Time [ " + LocalTime.now() + " ]");
		String fileName = AppUtil.EMPTY_STR;
		try {
			final File inputDirFiles = Paths.get(dataLoader.configDto.getBulkInputFolderPath()).toFile();
			logger.info("G1: Bulk Scanning Input directory [ " + dataLoader.configDto.getBulkInputFolderPath()
					+ " ]");
			
			File[] filesInDir = inputDirFiles.listFiles(new DogFileFilter(dataLoader.configDto.getBulkFileSupports()));
			if (filesInDir==null || filesInDir.length == 0) {
				logger.info("G1: Bulk Input directory is Empty.");
			} else {

				int count = 1;
				boolean isRunNext = false;
				logger.info("G1: Bulk Input directory size [ " + filesInDir.length + " ] ");

				List<APIDto> apiDtoList = getAPIBulkDto("G1",dataLoader.configDto.getBulkSupportsAPI());
				Set<File> succesFailList = new LinkedHashSet<File>();
				for (APIDto api : apiDtoList) {
					for (File fileToProcess : filesInDir) {
						fileName = fileToProcess.getName();
						if (fileToProcess.getName().startsWith(api.getApiName()) && fileToProcess.getName().split(AppUtil.FILE_SEPARTOR)[1].contentEquals("blk.xml")) {
							succesFailList.add(fileToProcess);
							api.setFileName(fileName);
							api.setFile(fileToProcess);
							isRunNext = BulkService.processBulkRun(api, count,"G1");
							api.setStatus(isRunNext);
							api.setMessageHeader("Bulk-"+api.getApiName().split("_")[1]);
                            api.setGroupType("Bulk");
							// sleep time being
							try {
								TimeUnit.SECONDS.sleep(2);
							} catch (InterruptedException e1) {
								logger.error("G1: Bulk InterruptedException {} " + e1.getMessage());
							}
							count++;
						}
					}
				}
				prepMailService.prepareAndSendAPIsMails(apiDtoList);
			}
		} catch (Exception ex) {
			logger.error("G1: Bulk Run into an error {WatchDog Exception}", ex);
		}

		endRunMsg("G1: Bulk ",startTime);
	}
	
	
	/***
	 * 
	 */
	
	@Scheduled(cron = "#{@bulkBatchG2}")
	public void invokeBulkRunG2() {
		// validate before execution of program.
		if(!validate()) {
			return;
		}

		final long startTime = System.currentTimeMillis();
		logger.info("=======================================================================");
		logger.info("G2: Bulk Starting Time [ " + LocalTime.now() + " ]");
		String fileName = AppUtil.EMPTY_STR;
		try {
			final File inputDirFiles = Paths.get(dataLoader.configDto.getBulkInputFolderPath()).toFile();
			logger.info("G2: Bulk Scanning Input directory [ " + dataLoader.configDto.getBulkInputFolderPath()
					+ " ]");
			
			File[] filesInDir = inputDirFiles.listFiles(new DogFileFilter(dataLoader.configDto.getBulkFileSupports()));
			if (filesInDir==null || filesInDir.length == 0) {
				logger.info("G2: Bulk Input directory is Empty.");
			} else {

				int count = 1;
				boolean isRunNext = false;
				logger.info("G2: Bulk Input directory size [ " + filesInDir.length + " ] ");

				List<APIDto> apiDtoList = getAPIBulkDto("G1",dataLoader.configDto.getBulkSupportsAPI());
				Set<File> succesFailList = new LinkedHashSet<File>();
				for (APIDto api : apiDtoList) {
					for (File fileToProcess : filesInDir) {
						fileName = fileToProcess.getName();
						if (fileToProcess.getName().startsWith(api.getApiName()) && fileToProcess.getName().split(AppUtil.FILE_SEPARTOR)[1].contentEquals("blk.xml")) {
							succesFailList.add(fileToProcess);
							api.setFileName(fileName);
							api.setFile(fileToProcess);
							isRunNext = BulkService.processBulkRun(api, count,"G2");
							api.setStatus(isRunNext);
							api.setMessageHeader("Bulk-"+api.getApiName().split("_")[1]);
                            api.setGroupType("Bulk");
							// sleep time being
							try {
								TimeUnit.SECONDS.sleep(2);
							} catch (InterruptedException e1) {
								logger.error("G2: Bulk InterruptedException {} " + e1.getMessage());
							}
							count++;
						}
					}
				}
				prepMailService.prepareAndSendAPIsMails(apiDtoList);
			}
		} catch (Exception ex) {
			logger.error("G2: Bulk Run into an error {WatchDog Exception}", ex);
		}

		endRunMsg("G2: Bulk ",startTime);
	}
    /***
     * 
     * @param group {@link String}
     * @param startTime  {@link Long}
     */
	private void endRunMsg(String group, long startTime) {
		final long endTime = System.currentTimeMillis();
		final double totalTimeTaken = (endTime - startTime) / (double) 1000;
		logger.info(group+" Finishing Time [ " + LocalTime.now() + " ] => Total time taken to be completed  [ "
				+ totalTimeTaken + "s ]");
	}

	
	/****
	 * v
	 * @return
	 */
	private boolean validate() {
		if (dataLoader.configDto != null && !dataLoader.configDto.getFlag()) {
			logger.info("Please configure setting, Watch Dog will start once configuration setting done {} ");
			return false;
		}
		if (dataLoader.configDto != null && dataLoader.configDto.isStopBulkBatchRun()) {
			logger.info("Bulk Dog is stoped. For starting reconfigure settings {} ");
			return false;
		}
		if (dataLoader.configDto.getBulkInputFolderPath().isEmpty()) {
			logger.info("Bulk Input Folder is not configur {} ");
			return false;
		}
		return true;
	}


	/***
	 * This API Op
	 * @param group {@link String}
	 * @param fileOrder {@link String[]}
	 * @return {@link List}
	 */
	
	private List<APIDto> getAPIBulkDto(String group,String[] fileOrder) {
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

}
