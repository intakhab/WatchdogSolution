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
import com.hcl.dog.service.PrepMailService;
import com.hcl.dog.service.CommonService;
import com.hcl.dog.service.NonEdiService;

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
public class NonEdiComponent {
	private Logger logger = LogManager.getLogger("nonedi-comp");

	@Autowired
	private NonEdiService nonEdiService;
	@Autowired
	private CommonService commonService;
	@Autowired
	private DataLoaderComponent dataLoader;
	@Autowired
	private PrepMailService prepMailService;
	
    /**
     * @see Bean 
     * @return NonEdi {@link String}
     */
	@Bean
	public String nonEdiBatchG1() {
		return commonService.
				getMins(dataLoader.configDto.getNonEdiPoollingTimeG1());
	}
    /**
     * @see Bean
     * @return NonEdi Wine 1 {@link String}
     */
	@Bean
	public String nonEdiWineBatchW1() {
		return commonService.
				getMins(dataLoader.configDto.getNonEdiPoollingTimeW1());
	}
    /**
     * @see Bean
     * @return NonEdi Wine2 {@link String}
     */
	@Bean
	public String nonEdiWineBatchW2() {
		return commonService.
				getMins(dataLoader.configDto.getNonEdiPoollingTimeW2());
	}
	 
	/*****
	 * This is for Non Edi Carrier
	 * @see Scheduled
	 */
	@Scheduled(fixedRateString = "#{@nonEdiBatchG1}", initialDelayString = "60000")
	public void invokeNonEdiRunG1() {
		// validate before execution of program.
		if(!validate()) {
			return;
		}

		final long startTime = System.currentTimeMillis();
		logger.info("=======================================================================");
		logger.info("G1: NonEdi Starting Time [ " + LocalTime.now() + " ]");
		String fileName = AppUtil.EMPTY_STR;
		try {
			final File inputDirFiles = Paths.get(dataLoader.configDto.getNonEdiCamInputFolderPath()).toFile();
			logger.info("G1: NonEdi Scanning Input directory [ " + dataLoader.configDto.getNonEdiCamInputFolderPath()
					+ " ]");
			
			File[] filesInDir = inputDirFiles.listFiles(new DogFileFilter(dataLoader.configDto.getNonEdiCamFileSupports()));
			if (filesInDir==null || filesInDir.length == 0) {
				logger.info("G1: NonEdi Input directory is Empty.");
			} else {

				int count = 1;
				boolean isRunNext = false;
				logger.info("G1: NonEdi Input directory size [ " + filesInDir.length + " ] ");

				List<APIDto> apiDtoList = getAPINonEdiDto("G1",dataLoader.configDto.getNonEdiCamSupportsAPI());
				Set<File> succesFailList = new LinkedHashSet<File>();
				for (APIDto api : apiDtoList) {
					for (File fileToProcess : filesInDir) {
						fileName = fileToProcess.getName();
						if (fileToProcess.getName().startsWith(api.getApiName()) && fileToProcess.getName().split(AppUtil.FILE_SEPARTOR)[1].contentEquals("nonedi.xml")) {
							succesFailList.add(fileToProcess);
							api.setFileName(fileName);
							api.setFile(fileToProcess);
							isRunNext = nonEdiService.processNonEdiRun(api, count,"G1");
							api.setStatus(isRunNext);
							api.setMessageHeader("NonEdi-"+api.getApiName().split("_")[1]);
                            api.setGroupType("NonEdi");
							// sleep time being
							try {
								TimeUnit.SECONDS.sleep(2);
							} catch (InterruptedException e1) {
								logger.error("G1: NonEdi InterruptedException {} " + e1.getMessage());
							}
							count++;
						}
					}
				}
				prepMailService.prepareAndSendAPIsMails(apiDtoList);
			}
		} catch (Exception ex) {
			logger.error("G1: NonEdi Run into an error {WatchDog Exception}", ex);
		}

		endRunMsg("G1: NonEdi ",startTime);
	}
	
	/****
	 * Wine Group G1
	 * @see Scheduled
	 */
	
	@Scheduled(fixedRateString = "#{@nonEdiWineBatchW1}", initialDelayString = "120000")
	public void invokeNonEdiWineRunW1() {
		// validate before execution of program.
		if(!validate()) {
			return;
		}

		final long startTime = System.currentTimeMillis();
		logger.info("=======================================================================");
		logger.info("W1: NonEdi Wine Starting Time [ " + LocalTime.now() + " ]");
		String fileName = AppUtil.EMPTY_STR;
		try {
			final File inputDirFiles = Paths.get(dataLoader.configDto.getNonEdiCamInputFolderPath()).toFile();
			logger.info("W1: NonEdi Wine Scanning Input directory [ " + dataLoader.configDto.getNonEdiCamInputFolderPath()
					+ " ]");
			
			File[] filesInDir = inputDirFiles.listFiles(new DogFileFilter(dataLoader.configDto.getNonEdiCamWineFileSupports()));
			if (filesInDir==null || filesInDir.length == 0) {
				logger.info("W1: NonEdi Wine Input directory is Empty.");
			} else {

				int count = 1;
				boolean isRunNext = false;
				logger.info("W1: NonEdi Wine Input directory size [ " + filesInDir.length + " ] ");

				List<APIDto> apiDtoList = getAPINonEdiDto("W1",dataLoader.configDto.getNonEdiCamWineSupportsAPI());
				Set<File> succesFailList = new LinkedHashSet<File>();
				for (APIDto api : apiDtoList) {
					for (File fileToProcess : filesInDir) {
						fileName = fileToProcess.getName();
						if (fileToProcess.getName().startsWith(api.getApiName()) && fileToProcess.getName().split(AppUtil.FILE_SEPARTOR)[1].contentEquals("wine.xml")) {
							succesFailList.add(fileToProcess);
							api.setFileName(fileName);
							api.setFile(fileToProcess);
							isRunNext = nonEdiService.processNonEdiRun(api, count,"W1");
							api.setStatus(isRunNext);
							api.setMessageHeader("Wine W1-"+api.getApiName().split("_")[1]);
                            api.setGroupType("Wine");

							// sleep time being
							try {
								TimeUnit.SECONDS.sleep(2);
							} catch (InterruptedException e1) {
								logger.error("W1: NonEdi Wine InterruptedException {} " + e1.getMessage());
							}
							count++;
						}
					}
				}
				prepMailService.prepareAndSendAPIsMails(apiDtoList);
			}
		} catch (Exception ex) {
			logger.error("W1: NonEdi Wine Run into an error {WatchDog Exception}", ex);
		}

		endRunMsg("W1: NonEdi Wine",startTime);
	}
	
	/****
	 * Wine Group G2
	 * @see Scheduled
	 */
	@Scheduled(fixedRateString = "#{@nonEdiWineBatchW2}", initialDelayString = "360000")
	public void invokeNonEdiRunW2() {
		// validate before execution of program.
		if(!validate()) {
			return;
		}
		final long startTime = System.currentTimeMillis();
		logger.info("=======================================================================");
		logger.info("W2: NonEdi Wine Starting Time [ " + LocalTime.now() + " ]");
		String fileName = AppUtil.EMPTY_STR;
		try {
			final File inputDirFiles = Paths.get(dataLoader.configDto.getNonEdiCamInputFolderPath()).toFile();
			logger.info("W2: NonEdi Wine Scanning Input directory [ " + dataLoader.configDto.getNonEdiCamInputFolderPath()
					+ " ]");
			
			File[] filesInDir = inputDirFiles.listFiles(new DogFileFilter(dataLoader.configDto.getNonEdiCamWineFileSupports()));
			if (filesInDir==null || filesInDir.length == 0) {
				logger.info("W2: NonEdi Wine Input directory is Empty.");
			} else {

				int count = 1;
				boolean isRunNext = false;
				logger.info("W2: NonEdi Wine Input directory size [ " + filesInDir.length + " ] ");

				List<APIDto> apiDtoList = getAPINonEdiDto("W2",dataLoader.configDto.getNonEdiCamWineSupportsAPI());
				Set<File> succesFailList = new LinkedHashSet<File>();
				for (APIDto api : apiDtoList) {
					for (File fileToProcess : filesInDir) {
						fileName = fileToProcess.getName();
						if (fileToProcess.getName().startsWith(api.getApiName()) && fileToProcess.getName().split(AppUtil.FILE_SEPARTOR)[1].contentEquals("wine.xml")) {
							succesFailList.add(fileToProcess);
							api.setFileName(fileName);
							api.setFile(fileToProcess);
							isRunNext = nonEdiService.processNonEdiRun(api, count,"W2");
							api.setStatus(isRunNext);
							api.setMessageHeader("Wine W2-"+api.getApiName().split("_")[1]);
                            api.setGroupType("Wine");
							// sleep time being
							try {
								TimeUnit.SECONDS.sleep(2);
							} catch (InterruptedException e1) {
								logger.error("W2: NonEdi Wine InterruptedException {} " + e1.getMessage());
							}
							count++;
						}
					}
				}
				prepMailService.prepareAndSendAPIsMails(apiDtoList);
			}
		} catch (Exception ex) {
			logger.error("W2: NonEdi Wine Run into an error {WatchDog Exception}", ex);
		}

		endRunMsg("W2: NonEdi Wine",startTime);
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
		if (dataLoader.configDto != null && dataLoader.configDto.isStopNonEdiBatchRun()) {
			logger.info("NonEdi Dog is stoped. For starting reconfigure settings {} ");
			return false;
		}
		if (dataLoader.configDto.getNonEdiCamInputFolderPath().isEmpty()) {
			logger.info("NonEdi Input Folder is not configur {} ");
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
	
	private List<APIDto> getAPINonEdiDto(String group,String[] fileOrder) {
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
