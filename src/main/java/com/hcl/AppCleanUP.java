package com.hcl;

import java.io.FileNotFoundException;
import java.nio.file.Paths;

import javax.annotation.PreDestroy;
import javax.xml.bind.JAXBException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import com.hcl.dog.common.AppUtil;
import com.hcl.dog.component.DataLoaderComponent;
import com.hcl.dog.dto.MailDto;
import com.hcl.dog.dto.StatusInfoDto;
import com.hcl.dog.service.CommonService;
import com.hcl.dog.service.PrepMailService;
import com.hcl.dog.service.XMLUtilService;

/***
 * 
 * @author intakhabalam.s@hcl.com
 * @see {@link ApplicationRunner}
 * @see {@link Component}
 * @see {@link Repository}
 * @see {@link Service}
 * @see {@link Controller}
 */
@Component
public class AppCleanUP implements ApplicationRunner {

	private Logger logger = LogManager.getLogger("app-cleanup");
	@Autowired
	private Scheduler scheduler;
	@Autowired
	private PrepMailService prepMailService;
	@Autowired
	private Environment env;
	@Autowired
	private DataLoaderComponent dataLoader;
	@Autowired
	private CommonService commonService;
	@Autowired
	private XMLUtilService xmlUtilService;

	/*****
	 * 
	 */

	@Override
	public void run(ApplicationArguments args) {
		
		//Init
		commonService.getServerStatus();
		AppUtil.STARTED_TIME=AppUtil.currentTime();
		checkUserStatus();
		
			if (dataLoader.configDto.isEnableStartupEmail()) {
				
				new Thread(new Runnable() {
					@Override
					public void run() {
						String message = env.getProperty("watchdog.server.up.body");
						String subject = env.getProperty("watchdog.server.up");
						try {
							
							MailDto mailDto = new MailDto(dataLoader.configDto.getFromMail(), 
									dataLoader.configDto.getToWhomEmail(), subject, message);
							prepMailService.sendEmailTemplate(mailDto);
						} catch (Exception e) {
							logger.error("Exception {run} " + e.getMessage());
						}
					}
				}).start();
			} else {
				logger.info("Startup mail sender is off {} ");

			}
		
		
	}

	/**
	 * Cleanup/Mail shoot
	 */
	@PreDestroy
	private void onClose() {
			if (dataLoader.configDto.isEnableShutdownEmail()) {
				new Thread(new Runnable() {
					@Override
					public void run() {
						String message = env.getProperty("watchdog.server.down.body");
						String subject = env.getProperty("watchdog.server.down");
						try {
							
							MailDto mailDto = new MailDto(dataLoader.configDto.getFromMail(), 
									dataLoader.configDto.getToWhomEmail(), subject, message);
							
							prepMailService.sendEmailTemplate(mailDto);
						} catch (Exception e) {
							logger.error("Exception {onClose} " + e.getMessage());

						}
					}
				}).start();
			} else {
				logger.info("Startup mail sender is off {} ");

			}

		logger.info("Before closing called System GC.");
		commonService.deleteRemoteFile();
		commonService.deleteStatus();
		
		System.gc();

		try {
			scheduler.clear();
			scheduler.shutdown();
		} catch (SchedulerException e) {
			logger.error("SchedulerException {onClose} " + e.getMessage());

		}

		logger.info("Happy to safe closing with ( CTR + C ).");
		logger.info("\nSometimes it took serveral minutes to close as it has been running since long time.");
		logger.info("Be Patience... Resources Cleanup Running \n");
		logger.info(AppUtil.CLOSE_BANNER);
	}

	/***
	 * 
	 */
	private void checkUserStatus() {
		logger.info("Checking server status");
		try {
			StatusInfoDto dog = (StatusInfoDto) xmlUtilService.convertXMLToObject(StatusInfoDto.class,
					Paths.get("status.db").toFile());
			if (dog != null && dog.getPort() != null && !dog.getPort().equals("-1")) {
				if (!dog.getPort().equals(env.getProperty("server.port"))) {
					logger.info("WatchDog application Port has been changed {} ");
					commonService.writeStartFileForcely("localhost");
				}
			}
		} catch (FileNotFoundException e) {
			logger.error("Error: checking status {} ", e.getMessage());

		} catch (JAXBException e) {
			logger.error("Error: Parsing problem {} ", e.getMessage());
		}
	}
}
