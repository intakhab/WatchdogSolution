package com.hcl.dog.component;

import java.io.File;
import java.net.InetAddress;
import java.nio.file.Paths;
import java.time.LocalTime;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.DisallowConcurrentExecution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.hcl.dog.common.AppUtil;
import com.hcl.dog.common.DogFileFilter;
import com.hcl.dog.common.WatchDogException;
import com.hcl.dog.config.PropertiesConfig;
import com.hcl.dog.service.CommonService;
import com.hcl.dog.service.FBpayService;
import com.hcl.dog.service.FileService;
import com.hcl.dog.service.XMLUtilService;
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
public class DogComponent {
	private Logger logger = LogManager.getLogger("dog-comp");

	@Autowired
	private FileService fileService;
	
	@Autowired
	private PropertiesConfig propertiesConfig;
	
	@Autowired
	private CommonService commonService;
	
    @Autowired
    private DataLoaderComponent dataLoader;
    
    @Autowired
    private Environment env;
    
    @Autowired
    private XMLUtilService xmlUtilService;
    
    @Autowired
    private FBpayService  fbPayService;

	private String FBPAY="fbpay";
    
	/**
	 * @see Bean
	 * @return polling time {@link String}
	 */
    
    @Bean
    public String fileDogBatch(){
    	return commonService.
				getMins(dataLoader.configDto.getFilePollingTime());
        
    }

	/**
	 * @see PostConstruct
	 * @throws WatchDogException
	 */
	@PostConstruct
	private void buildConfiguration() throws WatchDogException  {
	    logger.info(AppUtil.OPEN_BANNER);
        if(dataLoader.configDto!=null && !dataLoader.configDto.getFlag()) {
    	   return;
        }
		validation();
		
		logger.info("===================== Read the Configuaration Information ===========================");
		logger.info("Polling Time [ " + dataLoader.configDto.getFilePollingTime() +" ]");
		logger.info("Intial Polling Time [ " + propertiesConfig.initialPollingDelay +" ]");
		
		logger.info("Batch File Path [ " + dataLoader.configDto.getBatchFilePath() +" ]");
		logger.info("Input Directory [ " + dataLoader.configDto.getInputFolderPath() +" ]");
		logger.info("Output Directory [ " + dataLoader.configDto.getOutputFolderPath() +" ]");
		logger.info("Archive Directory [  " + dataLoader.configDto.getArchiveFolderPath() +" ]");
		logger.info("Filure Directory [  " + dataLoader.configDto.getFailureFolderPath() +" ]");
		logger.info("File Extension [ " + dataLoader.configDto.getFileExtension() +" ]");
		logger.info("File Type Separator [ " + dataLoader.configDto.getFileTypeSeparator() +" ]");
		logger.info("Response XML file start with [ " + dataLoader.configDto.getResponseFilePrefix() +" ]");
		logger.info("File Supports with [ " + dataLoader.configDto.getFileSupports()  +" ]");
		//
		logger.info("Fin Input Directory [ " + dataLoader.configDto.getOptInputFolderPath() +" ]");
		logger.info("NonEdi Input Directory [  " + dataLoader.configDto.getNonEdiCamInputFolderPath() +" ]");
		logger.info("Fin File Supports with [ " + dataLoader.configDto.getOptFileSupports()  +" ]");
		logger.info("SO Opt Input Directory [ " + dataLoader.configDto.getSoOrderInputFolderPath()  +" ]");
		logger.info("FBPay Input Directory [ " + dataLoader.configDto.getFbPayInputFolderPath()  +" ]");
		logger.info("BLK Input Directory [ " + dataLoader.configDto.getBulkInputFolderPath()  +" ]");

		logger.info("File Run [ " + dataLoader.configDto.isStopFileRun()  +" ]");
		logger.info("Fin Run [ " + dataLoader.configDto.isStopBatchRun()  +" ]");
		logger.info("NonEdi Run [ " + dataLoader.configDto.isStopNonEdiBatchRun()  +" ]");
		logger.info("SO Run [ " + dataLoader.configDto.isStopBatchRun()  +" ]");
		logger.info("BLK Run [ " + dataLoader.configDto.isStopBulkBatchRun()  +" ]");

		logger.info("======================================================================================");
	}
	
	
	/***
	 * This scheduler will invoke the file processing
	 * @see Scheduled
	 * @see DogComponent
	 * 
	 */
    
	@Scheduled(fixedRateString = "#{@fileDogBatch}", initialDelayString = "${initial.polling.delay}")
	public void invokeDog() {
		     // validate before execution of program.
		if(dataLoader.configDto!=null && !dataLoader.configDto.getFlag() ) {
			logger.info("Please configure settings, Watch Dog will start once configuration settings done {} ");
			  return;
		}
		if(dataLoader.configDto!=null  && dataLoader.configDto.isStopFileRun()) {
			  logger.info("File Dog is stoped. For starting reconfigure from settings {}");
			   return;
		}
		if(dataLoader.configDto.getInputFolderPath().isEmpty()) {
			logger.info("Input Folder is not configure {} ");
	       	return;
	    }
		
		final long startTime = System.currentTimeMillis();
		logger.info("=======================================================================");
		logger.info("Starting Time [ " + LocalTime.now() + " ]");
		try {
			final File inputDirFiles= Paths.get(dataLoader.configDto.getInputFolderPath()).toFile();
			logger.info("Scanning Input directory [ " + dataLoader.configDto.getInputFolderPath() + " ]");
			//create a FileFilter and override its accept-method
			File[] files = inputDirFiles.listFiles(new DogFileFilter(dataLoader.configDto.getFileSupports()));
			if (files==null || files.length == 0) {
				logger.info("File Input directory is Empty.");
			} else {
				int count =1;
				logger.info("File Input directory size [ " + files.length + " ] ");
				for (File f : files) {
					String fileName=f.getName();
					AppUtil.CURRENT_FILE_PROCESSING_NAME=fileName;
					  //before running, need to check file having tns prefix//
					  if(AppUtil.TRUE_STR.equalsIgnoreCase(env.getProperty("enable.tns"))) {
						  xmlUtilService.modfiyXMLTNS(f);
					  }
					  // if FBpay file will come then it will move to 
					  if(checkFBPayFiles(f)) {
						  if(!validateFBPayRun()) {
							  return;
						  }
						  logger.info("FBPay File found in input directory, Moving to FBPay Folder for further Processing {}");
						  String fbFileName=dataLoader.configDto.getFbPayInputFolderPath()+File.separator+fileName;
						  commonService.moveReplaceFile(f.getPath(), fbFileName);
						  
						  logger.info("File Moved to FBPay folder, It will be backed in input folder after processing done {} ");
							boolean b = false;
							try {
								b = fbPayService.processFBPay(f, Paths.get(fbFileName).toFile(), FBPAY, count);
							} catch (Exception e) {
							}
						 
						  if(b) {
							  logger.info("FBPay file Come back to input folder successfully, will satrt further processing {} ");
						  }else {
							  logger.info("FBPay file having problem please check manually {} ");
						  }
						  return;
					  }
					  
					fileService.processFileRun(f,count);
					//sleep time being 
			        try {
						TimeUnit.SECONDS.sleep(2);
					} catch (InterruptedException e1) {
						logger.error("InterruptedException {} "+e1.getMessage());
					}
					count++;
				}
			}

		} catch (WatchDogException ex) {
			logger.error("Run into an error {WatchDog Exception}", ex);

		}
		final long endTime = System.currentTimeMillis();
		final double totalTimeTaken = (endTime - startTime)/(double)1000;
		logger.info("Finishing Time [ " + LocalTime.now() + " ] => Total time taken to be completed  [ " + totalTimeTaken + "s ]");
		logger.info("Will wake up in [ "  +dataLoader.configDto.getFilePollingTime() +" ] m");
	}
	
	
	
	/***
	 * @see DogComponent
	 * @param f {@link File}
	 * @return {@link Boolean}
	 */
	
	private boolean checkFBPayFiles(File f) {
		String si=FilenameUtils.getBaseName(f.getName()).split(dataLoader.configDto.getFileTypeSeparator())[1];
		if(StringUtils.startsWithIgnoreCase(si, FBPAY)) {
			return true;
		}
		return false;
	}
	
	/**
	 * @see DogComponent
	 * @throws WatchDogException as Exception
	 */
	public void validation() throws WatchDogException {
		commonService.scanDirectory();
		try {
			InetAddress ipAddr = InetAddress.getLocalHost();
		    String address=ipAddr.getHostAddress();
		    commonService.writeStartFile(address);
		    commonService.writeErrorInfo();
		} catch (Exception e) {
			
		}
	}
	
	public boolean validateFBPayRun() {
		if (dataLoader.configDto != null && !dataLoader.configDto.getFlag()) {
			logger.info("Please configure setting, Watch Dog will start once configuration setting done {} ");
			return false;
		}
		if (dataLoader.configDto != null && dataLoader.configDto.isStopFBPayRun()) {
			logger.info("FBPay is stoped. For starting reconfigure settings {} ");
			return false;
		}
		return true;
	}
}

