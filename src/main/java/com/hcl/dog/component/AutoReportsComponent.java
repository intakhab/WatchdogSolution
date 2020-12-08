package com.hcl.dog.component;

import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.hcl.dog.common.AppUtil;
import com.hcl.dog.domain.Report;
import com.hcl.dog.domain.Reports;
import com.hcl.dog.dto.MailDto;
import com.hcl.dog.service.CommonService;
import com.hcl.dog.service.PrepMailService;

/***
 * @author intakhabalam.s@hcl.com
 * @see Component
 * @see DataLoaderComponent {@link AutoReportsComponent} 
 * @see PrepMailService {@link PrepMailService}
 * @see CommonService {@link CommonService}
 * @see Environment {@link Environment}
 */
@Component
public class AutoReportsComponent {
	private Logger logger = LogManager.getLogger("auto-reports-comp");

	@Autowired
	private DataLoaderComponent dataLoader;

	@Autowired
	private PrepMailService prepMailService;
	
	@Autowired
	private CommonService commonService;
	
	@Autowired
	private Environment env;


	@Bean
	public String autoReportsCron() {
		return dataLoader.configDto.getReportsCron();
	}

	/***
	 * Auto
	 */
	@Scheduled(cron = "#{@autoReportsCron}")
	public void invokeReportsRun() {
		if (validation()) {
			return;
		}
		
		
		final long startTime = System.currentTimeMillis();
		logger.info("=======================================================================");
		logger.info("Auto Reports Starting Time [ " + LocalTime.now() + " ]");
				   
		Reports repo = commonService.loadRerorts(env.getProperty("reports.path"));
		List<Report> repoList = repo.getReports();
		final List<Report> rList=repoList.stream().filter(s -> AppUtil.dateDifference(s.filedat).equals("0")).collect(Collectors.<Report>toList());
        logger.info("Reports Size for Sending mail::"+rList.size());
        autoReportsMail(rList);
		
		final long endTime = System.currentTimeMillis();
		final double totalTimeTaken = (endTime - startTime) / (double) 1000;
		logger.info("Auto Reports Finishing Time [ " + LocalTime.now() + " ] => Total time taken to be completed  [ "
				+ totalTimeTaken + "s ]");

	}

	
	/***
	 * Auto pilot mail
	 */
	private void autoReportsMail(List<Report> rList) {
				String subject = "TMS Processed File Summary for Today's date";
				String message = "TMS Processed Summary for Todays's date";
				try {

					MailDto mailDto = new MailDto(dataLoader.configDto.getFromMail(), 
							dataLoader.configDto.getToWhomEmail(), subject, message);
					prepMailService.prepareReportsAndSend(rList,mailDto);
					
				} catch (Exception e) {
					logger.error("Exception {autoReportsMail} " + e.getMessage());

				}
	}
	
	/**
	 * Check validation
	 * @return {@link Boolean}
	 */
	public boolean validation() {

		if (!dataLoader.configDto.isAutoReports()) {
			logger.info("Auto Reports is stoped... For starting reconfigure from settings {} ");
			return true;
		}
		return false;
	}

}
