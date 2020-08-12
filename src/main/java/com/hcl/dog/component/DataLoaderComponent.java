package com.hcl.dog.component;

import java.io.FileNotFoundException;

import javax.annotation.PostConstruct;
import javax.xml.bind.JAXBException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import com.hcl.dog.common.AppUtil;
import com.hcl.dog.common.WatchDogException;
import com.hcl.dog.dto.SettingsInfoDto;
import com.hcl.dog.service.SettingsService;

/***
 * 
 * @author intakhabalam.s@hcl.com
 * @see  {@link Component}
 * @see {@link Repository}
 * @see {@link Service}
 * @see {@link Controller}
 * @see {@link Order}
 */
@Component
@Order(1)
public class DataLoaderComponent {
	private Logger logger = LogManager.getLogger("dataload-comp");

	@Autowired
	private SettingsService watchDogConfigService;
	public SettingsInfoDto configDto;

	/***
	 * @see PostConstruct
	 */
	@PostConstruct
	public void loadData() {
		try {
			this.configDto = watchDogConfigService.convertObjToDto("1");
			logger.info("Configuration data loading from DB");
		} catch (FileNotFoundException | JAXBException e) {
			logger.error("Data Loading fail, Loading data from another source {}  " + e.getMessage());
			try {
				this.configDto = watchDogConfigService.convertObjToDto("2");
				logger.info("Configuration data loading from Temp DB");
			} catch (FileNotFoundException | JAXBException e1) {
				logger.error("Data Loading fail  {} " + e1.getMessage());

			}

		}
	}

	/***
	 * This method will refresh the data once you save the data from UI
	 * 
	 * @param fileDogInfoDto {@link SettingsInfoDto}
	 */
	public void refreshData(SettingsInfoDto fileDogInfoDto) {
		logger.info("Refreshing data, change DB Mode");
		this.configDto = fileDogInfoDto;
		buildConfiguration();
	}

	/***
	 * buildConfiguration
	 */
	private void buildConfiguration() {
		validateDirectories();
		logger.info("============ Read the Configuaration Information ======================");

		logger.info("Batch File Path [ " + configDto.getBatchFilePath() + " ]");
		logger.info("Input Directory [ " + configDto.getInputFolderPath() + " ]");
		logger.info("Output Directory [ " + configDto.getOutputFolderPath() + " ]");
		logger.info("Archive Directory [  " + configDto.getArchiveFolderPath() + " ]");
		logger.info("Failure Directory [  " + configDto.getFailureFolderPath() + " ]");
		logger.info("File Extension [ " + configDto.getFileExtension() + " ]");
		logger.info("File Type Separator [ " + configDto.getFileTypeSeparator() + " ]");
		logger.info("Response XML file start with [ " + configDto.getResponseFilePrefix() + " ]");
		logger.info("File Supports with [ " + configDto.getFileSupports() + " ]");
		//
		logger.info("Fin Input Directory [ " + configDto.getOptInputFolderPath() + " ]");
		logger.info("NonEdi Input Directory [ " + configDto.getNonEdiCamInputFolderPath() + " ]");
		logger.info("Fin File Supports with [ " + configDto.getOptFileSupports() + " ]");
		logger.info("Fin SOA In/Out Directory [ " + configDto.getSoaOutputFolderPath() + " ]");
		logger.info("SO Opt Input Directory [ " + configDto.getSoOrderInputFolderPath() + " ]");
		logger.info("FBPay Input Directory [ " + configDto.getFbPayInputFolderPath() + " ]");
		logger.info("BLK Input Directory [ " + configDto.getBulkInputFolderPath() + " ]");

		logger.info("File Batch Run [ " + configDto.isStopFileRun() + " ]");
		logger.info("Fin Batch Run [ " + configDto.isStopBatchRun() + " ]");
		logger.info("NonEdi Batch Run [ " + configDto.isStopNonEdiBatchRun() + " ]");
		logger.info("SO Batch Run [ " + configDto.isStopBatchRun() + " ]");
		logger.info("BLK Batch Run [ " + configDto.isStopBulkBatchRun() + " ]");

		logger.info("=======================================================================");
	}

	/**
	 * @see DataLoaderComponent {@link DataLoaderComponent}
	 * @throws WatchDogException
	 */
	private void validateDirectories() {
		AppUtil.createFolder(configDto.getInputFolderPath(), "Input");
		AppUtil.createFolder(configDto.getOutputFolderPath(), "Output");
		AppUtil.createFolder(configDto.getArchiveFolderPath(), "Archive");
		AppUtil.createFolder(configDto.getFailureFolderPath(), "Failure");
		//
		AppUtil.createFolder(configDto.getOptInputFolderPath(), "Fin Input");
		AppUtil.createFolder(configDto.getSoaOutputFolderPath(), "Fin SOA");
		AppUtil.createFolder(configDto.getNonEdiCamInputFolderPath(), "Non Edi");
		AppUtil.createFolder(configDto.getSoOrderInputFolderPath(), "SO");
		AppUtil.createFolder(configDto.getFbPayInputFolderPath(), "FBPay");
		AppUtil.createFolder(configDto.getBulkInputFolderPath(), "BULK");
		//
		logger.info("Validated directories pass {} ");
	}
 
}
