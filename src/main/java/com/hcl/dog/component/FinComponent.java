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
import com.hcl.dog.service.FinService;

/***
 * 
 * @author intakhabalam.s@hcl.com
 * @see {@link Component}
 * @see {@link Repository}
 * @see {@link Service}
 * @see {@link Controller}
 * @see {@link DisallowConcurrentExecution}
 */
@Component
public class FinComponent {
	private Logger logger = LogManager.getLogger("fin-comp");

	@Autowired
	private FinService financialBatchService;
	@Autowired
	private DataLoaderComponent dataLoader;
	@Autowired
	private PrepMailService prepMailService;
	
	/***************************************/
   /**
    * @see Bean
    * @return cron
    */
   @Bean
    public String finGroupG1(){
        return dataLoader.configDto.getFinCronTimeG1();
    }
   /**
    * @see Bean
    * @return cron
    */
   @Bean
   public String finGroupG2(){
       return dataLoader.configDto.getFinCronTimeG2();
   }
   /**
    * @see Bean
    * @return cron
    */
   @Bean
   public String finGroupG3(){
       return dataLoader.configDto.getFinCronTimeG3();
   }
   /**
    * @see Bean
    * @return cron
    */
   @Bean
   public String finGroupG4(){
       return dataLoader.configDto.getFinCronTimeG4();
   }

	/***
	 * G1
	 * @see Scheduled {@link Scheduled}
	 */
	@Scheduled(cron = "#{@finGroupG1}")
	public void invokeFinOPTRunGrop1() {
		if (!validateFinRun()) {
			return;
		}

		final long startTime = System.currentTimeMillis();
		startRunMsg("FinG1");
		try {
			final File inputDirFiles = Paths.get(dataLoader.configDto.getOptInputFolderPath()).toFile();
			logger.info("FinG1 Scanning Input directory [ " + dataLoader.configDto.getOptInputFolderPath() + " ]");
			
			File[] filesInDir = inputDirFiles.listFiles(new DogFileFilter(dataLoader.configDto.getOptFileSupports()));
			if (filesInDir==null || filesInDir.length == 0) {
				logger.info("FinG1 Input directory is Empty.");
			} else {
				int count = 1;
				boolean isRunNext = false;
				logger.info("FinG1 Input directory size [ " + filesInDir.length + " ] ");
				List<APIDto> apiDtoList = getAPIOptDto("G1");
				Set<File> succesFailList = new LinkedHashSet<File>();
				boolean isStop = false;
				for (APIDto api : apiDtoList) {
					for (File fileToProcess : filesInDir) {
						String fileName = fileToProcess.getName();
						if (fileToProcess.getName().startsWith(api.getApiName())) {
							succesFailList.add(fileToProcess);
							isRunNext = financialBatchService.processFinRun(fileToProcess, count, api, "FinG1", false);
							api.setFileName(fileName);
							api.setFile(fileToProcess);
							api.setStatus(isRunNext);
							api.setMessageHeader("Fin G1-"+api.getApiName());
                            api.setGroupType("Fin");
							if (!isRunNext) {
								isStop = true;
								break;
							}
							// sleep time being
							try {
								TimeUnit.SECONDS.sleep(2);
							} catch (InterruptedException e1) {
								logger.error("FinG1 InterruptedException {} " + e1.getMessage());
							}
							count++;
						}
					}
					if (isStop) {
						break;
					}
				}
				prepMailService.prepareAndSendAPIsMails(apiDtoList);
			}

		} catch (WatchDogException ex) {
			logger.error("FinG1 Run into an error {WatchDog Exception}", ex);
		}
		endRunMsg("FinG1",startTime);
	}
	

	/***
	 * @see Scheduled
	 * G2
	 */
	@Scheduled(cron = "#{@finGroupG2}")
	public void invokeFinOPTRunGrop2() {
		// validate before execution of program.
		if (!validateFinRun()) {
			return;
		}
		final long startTime = System.currentTimeMillis();
		startRunMsg("FinG2");
		try {

			final File inputDirFiles = Paths.get(dataLoader.configDto.getOptInputFolderPath()).toFile();
			logger.info("FinG2 Scanning Input directory [ " + dataLoader.configDto.getOptInputFolderPath() + " ]");


			File[] filesInDir = inputDirFiles.listFiles(new DogFileFilter(dataLoader.configDto.getOptFileSupports()));
			if (filesInDir==null || filesInDir.length == 0) {
				logger.info("FinG2 Input directory is Empty.");
			} else {
				int count = 1;
				boolean isRunNext = false;
				logger.info("FinG2 Input directory size [ " + filesInDir.length + " ] ");

				List<APIDto> apiDtoList = getAPIOptDto("G2");
				Set<File> succesFailList = new LinkedHashSet<File>();
				boolean isStop = false;
				for (APIDto api : apiDtoList) {
					for (File fileToProcess : filesInDir) {
						String fileName = fileToProcess.getName();
						if (fileToProcess.getName().startsWith(api.getApiName())) {
							succesFailList.add(fileToProcess);
							isRunNext = financialBatchService.processFinRun(fileToProcess, count, api, "FinG2", true);
							api.setFileName(fileName);
							api.setFile(fileToProcess);
							api.setStatus(isRunNext);
							api.setMessageHeader("Fin G2-"+api.getApiName());
                            api.setGroupType("Fin");
							if (!isRunNext) {
								isStop = true;
								break;
							}
							// sleep time being
							try {
								TimeUnit.SECONDS.sleep(2);
							} catch (InterruptedException e1) {
								logger.error("FinG2 InterruptedException {} " + e1.getMessage());
							}
							count++;
						}
					}
					if (isStop) {
						break;
					}
				}
				prepMailService.prepareAndSendAPIsMails(apiDtoList);
			}

		} catch (WatchDogException ex) {
			logger.error("FinG2 Run into an error {WatchDog Exception}", ex);
		}
		endRunMsg("FinG2",startTime);
	}

	/***
	 * @see Scheduled
	 * G3
	 */
	@Scheduled(cron = "#{@finGroupG3}")
	public void invokeFinOPTRunGrop3() {
		// validate before execution of program.
		if (!validateFinRun()) {
			return;
		}

		final long startTime = System.currentTimeMillis();
		startRunMsg("FinG3");
		try {

			final File inputDirFiles = Paths.get(dataLoader.configDto.getOptInputFolderPath()).toFile();
			
			logger.info("FinG3 Scanning Input directory [ " + dataLoader.configDto.getOptInputFolderPath() + " ]");
			File[] filesInDir = inputDirFiles.listFiles(new DogFileFilter(dataLoader.configDto.getOptFileSupports()));
			
			if (filesInDir==null || filesInDir.length == 0) {
				logger.info("FinG3 Input directory is Empty.");
			} else {
				int count = 1;
				boolean isRunNext = false;
				logger.info("FinG3 Input directory size [ " + filesInDir.length + " ] ");

				List<APIDto> apiDtoList = getAPIOptDto("G3");
				Set<File> succesFailList = new LinkedHashSet<File>();
				boolean isStop = false;
				for (APIDto api : apiDtoList) {
					for (File fileToProcess : filesInDir) {
						String fileName = fileToProcess.getName();
						if (fileToProcess.getName().startsWith(api.getApiName())) {
							succesFailList.add(fileToProcess);
							isRunNext = financialBatchService.processFinRun(fileToProcess, count, api, "FinG3", true);
							api.setFileName(fileName);
							api.setFile(fileToProcess);
							api.setStatus(isRunNext);
							api.setMessageHeader("Fin G3-"+api.getApiName());
                            api.setGroupType("Fin");
							if (!isRunNext) {
								isStop = true;
								break;
							}
							// sleep time being
							try {
								TimeUnit.SECONDS.sleep(2);
							} catch (InterruptedException e1) {
								logger.error("FinG3 InterruptedException {} " + e1.getMessage());
							}
							count++;
						}
					}
					if (isStop) {
						break;
					}
				}
				prepMailService.prepareAndSendAPIsMails(apiDtoList);

			}

		} catch (WatchDogException ex) {
			logger.error("FinG3 Run into an error {WatchDog Exception}", ex);
		}
		endRunMsg("FinG3",startTime);
	}

	
	/***
	 * @see Scheduled
	 * G4
	 */
	@Scheduled(cron = "#{@finGroupG4}")
	public void invokeFinOPTRunGrop4() {
		// validate before execution of program.
		if (!validateFinRun()) {
			return;
		}

		final long startTime = System.currentTimeMillis();
		startRunMsg("FinG4");
		try {

			final File inputDirFiles = Paths.get(dataLoader.configDto.getOptInputFolderPath()).toFile();
			
			logger.info("FinG4 Scanning Input directory [ " + dataLoader.configDto.getOptInputFolderPath() + " ]");
			File[] filesInDir = inputDirFiles.listFiles(new DogFileFilter(dataLoader.configDto.getOptFileSupports()));
			
			if (filesInDir==null || filesInDir.length == 0) {
				logger.info("FinG4 Input directory is Empty.");
			} else {
				int count = 1;
				boolean isRunNext = false;
				logger.info("FinG4 Input directory size [ " + filesInDir.length + " ] ");

				List<APIDto> apiDtoList = getAPIOptDto("G4");
				Set<File> succesFailList = new LinkedHashSet<File>();
				boolean isStop = false;
				for (APIDto api : apiDtoList) {
					for (File fileToProcess : filesInDir) {
						String fileName = fileToProcess.getName();
						if (fileToProcess.getName().startsWith(api.getApiName())) {
							succesFailList.add(fileToProcess);
							isRunNext = financialBatchService.processFinRun(fileToProcess, count, api, "FinG4", true);
							api.setFileName(fileName);
							api.setFile(fileToProcess);
							api.setStatus(isRunNext);
							api.setMessageHeader("Fin G4-"+api.getApiName());
                            api.setGroupType("Fin");
							if (!isRunNext) {
								isStop = true;
								break;
							}
							// sleep time being
							try {
								TimeUnit.SECONDS.sleep(2);
							} catch (InterruptedException e1) {
								logger.error("FinG3 InterruptedException {} " + e1.getMessage());
							}
							count++;
						}
					}
					if (isStop) {
						break;
					}
				}
				prepMailService.prepareAndSendAPIsMails(apiDtoList);

			}

		} catch (WatchDogException ex) {
			logger.error("FinG4 Run into an error {WatchDog Exception}", ex);
		}
		endRunMsg("FinG4",startTime);
	}
	
    /***
     * 
     * @param group
     * @param startTime
     */
	private void endRunMsg(String group, long startTime) {
		final long endTime = System.currentTimeMillis();
		final double totalTimeTaken = (endTime - startTime) / (double) 1000;
		logger.info(group+" Finishing Time [ " + LocalTime.now() + " ] => Total time taken to be completed  [ "
				+ totalTimeTaken + "s ]");
	}
	/***
	 * 
	 * @param group
	 */
	private void startRunMsg(String group) {
		logger.info("=======================================================================");
		logger.info(group+" Starting Time [ " + LocalTime.now() + " ]");
	}
	/***
	 * This method will give Object of API
	 * 
	 * @return
	 */
	private List<APIDto> getAPIOptDto(String group) {
		String[] fileOrder = dataLoader.configDto.getOptSupportsAPI();
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
	

	/****
	 * This is method is for Batch Optimization API.
	 */
	private boolean validateFinRun() {
		// validate before execution of program.
		if (dataLoader.configDto != null && !dataLoader.configDto.getFlag()) {
			logger.info("Please configure setting, Watch Dog will start once configuration setting done {} ");
			return false;
		}
		if (dataLoader.configDto != null && dataLoader.configDto.isStopBatchRun()) {
			logger.info("Fin Dog is stoped. For starting reconfigure settings {} ");
			return false;
		}
		if (dataLoader.configDto.getOptInputFolderPath().isEmpty()) {
			logger.info("Fin Input Folder is not configure {} ");
			return false;
		}
		return true;
	}

}
