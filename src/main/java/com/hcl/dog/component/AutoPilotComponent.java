package com.hcl.dog.component;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalTime;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.hcl.WatchDogSolution;
import com.hcl.dog.common.AppUtil;
import com.hcl.dog.dto.MailDto;
import com.hcl.dog.service.CommonService;
import com.hcl.dog.service.PrepMailService;

/***
 * @author intakhabalam.s@hcl.com
 * Auto Pilot will restart WatchDog automatically and copy the all files from archive, output and failure folder 
 * from respective directory and archive in temp folder in zip format due to smooth run of watchdog
 * @see ApplicationContext {@link ApplicationContext} 
 * @see Component {@link Component}
 * @see DataLoaderComponent {@link AutoPilotComponent} 
 * @see PrepMailService {@link PrepMailService}
 * @see CommonService {@link CommonService}
 * @see Environment {@link Environment}
 */
@Component
public class AutoPilotComponent {
	private Logger logger = LogManager.getLogger("auto-pilot-comp");

	@Autowired
	private DataLoaderComponent dataLoader;

	@Autowired
	private PrepMailService prepMailService;
	
	@Autowired
	private ApplicationContext context;
	
	@Autowired
	private CommonService commonService;
	
	@Autowired
	private Environment env;

	
	

	@Bean
	public String autoPilotCron() {
		return dataLoader.configDto.getAutoPilotCron();
	}

   
	/***
	 * Auto
	 */
	@Scheduled(cron = "#{@autoPilotCron}")
	public void invokeAutoPilotRun() {
		if (validation()) {
			return;
		}
		
		final long startTime = System.currentTimeMillis();
		logger.info("=======================================================================");
		logger.info("Auto Pilot Starting Time [ " + LocalTime.now() + " ]");
		//CLeanup and automail
				autoPilotMail();
				   try {
						TimeUnit.SECONDS.sleep(10);
			        } catch (InterruptedException ignored) {
			       }
				commonService.loadRerorts(env.getProperty("reports.path"));
				
				//
		try {
			// Backup all file in particular folder....
			try {
				if (AppUtil.TRUE_STR.equalsIgnoreCase(env.getProperty("auto.pilot.folder.cleanup"))) {
					String tempFolder = commonService.getTempFolderPath();
					logger.info("TempFolder path==>" + tempFolder);
					AppUtil.createFolder(tempFolder, AppUtil.CLEAN_UP_FOLDER_NAME);
					Path[] sPath = { Paths.get(dataLoader.configDto.getOutputFolderPath()),
							Paths.get(dataLoader.configDto.getArchiveFolderPath()),
							Paths.get(dataLoader.configDto.getFailureFolderPath()) };

					AppUtil.zipFolder(tempFolder, sPath);
					logger.info("Clean Up Completed - All file archive to folder : " + tempFolder);

				} else {
					logger.info("Auto Pilot Cleanup functionality is disabled, for enable change in properties file");
				}
			}catch(Exception e) {
				logger.error("Clean up error {}", e);

			}
			
			ConfigurableApplicationContext ctx=(ConfigurableApplicationContext) context;
			 Thread restartThread = new Thread(() -> {
			        try {
						TimeUnit.SECONDS.sleep(10);
			        } catch (InterruptedException ignored) {
			        }
					 WatchDogSolution.restart(ctx);

			    });
			    restartThread.setDaemon(false);
			    restartThread.start();
			

		} catch (Exception ex) {
			logger.error("Auto Pilot run into an error {WatchDog Exception}", ex);
		}finally {
			//backup all 
		}
		final long endTime = System.currentTimeMillis();
		final double totalTimeTaken = (endTime - startTime) / (double) 1000;
		logger.info("Auto Pilot Finishing Time [ " + LocalTime.now() + " ] => Total time taken to be completed  [ "
				+ totalTimeTaken + "s ]");

	}

	
	/***
	 * Auto pilot mail
	 */
	private void autoPilotMail() {
				String subject = "Auto Pilot Starting...";
				String message = "Auto Pilot has started Maintenance Session, Please wait till startup WatchDog";
				try {

					MailDto mailDto = new MailDto(dataLoader.configDto.getFromMail(), 
							dataLoader.configDto.getToWhomEmail(), subject, message);
					prepMailService.sendEmailTemplate(mailDto);
					
				} catch (Exception e) {
					logger.error("Exception {autoPilotMail} " + e.getMessage());

				}
	}
	/**
	 * Check validation
	 * @return {@link Boolean}
	 */
	public boolean validation() {

		if (!dataLoader.configDto.isAutoPilot()) {
			logger.info("Auto Pilot is stoped... For starting reconfigure from settings {} ");
			return true;
		}
		return false;
	}

}
