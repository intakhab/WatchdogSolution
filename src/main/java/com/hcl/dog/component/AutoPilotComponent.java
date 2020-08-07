package com.hcl.dog.component;

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
import com.hcl.dog.dto.MailDto;
import com.hcl.dog.service.PrepMailService;
import com.hcl.dog.service.CommonService;

/***
 * @author intakhabalam.s@hcl.com
 * @see ApplicationContext {@link ApplicationContext} 
 * @see Component
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
