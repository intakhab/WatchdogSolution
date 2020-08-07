package com.hcl.dog.controller;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBException;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.support.CronSequenceGenerator;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.util.FileCopyUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.hcl.dog.common.AppUtil;
import com.hcl.dog.component.DataLoaderComponent;
import com.hcl.dog.config.PropertiesConfig;
import com.hcl.dog.domain.PlanId;
import com.hcl.dog.domain.PlanIds;
import com.hcl.dog.domain.Report;
import com.hcl.dog.domain.Reports;
import com.hcl.dog.dto.FileDto;
import com.hcl.dog.dto.SettingsInfoDto;
import com.hcl.dog.service.AlertService;
import com.hcl.dog.service.CommonService;
import com.hcl.dog.service.SettingsService;
import com.hcl.dog.service.XMLUtilService;


/***
 * @author intakhabalam.s@hcl.com
 * @see Controller {@link Controller}
 * @see CrossOrigin {@link CrossOrigin}
 * @see AlertService {@link AlertService}
 * @see Environment {@link Environment}
 * @see XMLUtilService {@link XMLUtilService}
 * @see SettingsService {@link SettingsService}
 * @see CommonService {@link CommonService}
 * @see PropertiesConfig {@link PropertiesConfig}
 * @see RequestMapping  {@link RequestMapping}
 * @see ResponseBody {@link ResponseBody}
 */
@Controller
@CrossOrigin
public class DogController {

	@Autowired
	private CommonService commonService;
	@Autowired
	private SettingsService dogConfigService;
	@Autowired
	private AlertService alertService;
	@Autowired
	private DataLoaderComponent dataLoader;
	@Autowired
	private Environment env;
	@Autowired
	private XMLUtilService xmlUtilService;

	
	/**
	 * @return {@link String}
	 */
	@RequestMapping("/run")
    @ResponseBody
	public String startedApps() {
		return "OK";
	}
	
	/***
	 * @param model {@link Map}
	 * @return {@link String}
	 */
	@RequestMapping("/")
	public String defaultPage(Map<String, Object> model) {
		model.put("statusList", commonService.getServerStatus());
		return "status";
	}


	/***
	 * Status page
	 * @param model {@link Model}
	 * @return {@link String}
	 */
	@RequestMapping("/status")
	public String statusPage(Map<String, Object> model) {
		model.put("statusList", commonService.getServerStatus());
		return "status";
	}


	/***
	 * This will load all configuration data from DB
	 * @param model {@link Model}
	 * @return {@link String}
	 */

	@RequestMapping("/configfile")
	public String fileConfig(ModelMap model) {
		SettingsInfoDto infoObj;
		try {
			infoObj = dogConfigService.convertObjToDto("1");
			model.addAttribute("infoObj", infoObj);

		} catch (FileNotFoundException | JAXBException e) {
			try {
				infoObj = dogConfigService.convertObjToDto("2");
				model.addAttribute("infoObj", infoObj);

			} catch (Exception fj) {
				model.addAttribute("msg", e.getMessage());
				return "redirect:/errorpage?msg=Settings Configuration file loading error " + fj.getMessage();

			}
		}

		return "configfile";
	}
	
	
	
	
	/***
	 * This method will restore 
	 * the configuration file when db would crashed.
	 * @param model {@link Model}
	 * @return {@link String}
	 */
	@RequestMapping("/restoreconfig")
	public String restoreConfig(ModelMap model) {
		SettingsInfoDto infoObj;
		try {
			commonService.reloaConfigBackup();
			infoObj = dogConfigService.convertObjToDto("1");
			model.addAttribute("infoObj", infoObj);
			model.addAttribute("msg", alertService.sucess("Settings Configuration load successfully"));

		} catch (Exception e) {
			return "redirect:/errorpage?msg=Configuration file loading error " + e.getMessage();

		}
		return "configfile";
	}

	/***
	 * This method will save config information
	 * @param infoObj  {@link SettingsInfoDto} 
	 * @param result  {@link BindingResult} 
	 * @param model {@link Model} 
	 * @return {@link String}
	 */
	@RequestMapping("/saveconfiginfo")
	public String saveConfigInfo( @ModelAttribute("infoObj") SettingsInfoDto infoObj, BindingResult result,
			ModelMap model) {
		try {
			if (result.hasErrors()) {
				return "redirect:/errorpage?msg=Data entered is not valid";
			}
			String tabId = infoObj.getTabId();
			boolean cronHit=infoObj.isCronHit();
			dogConfigService.saveSettingsInfo(infoObj);
			model.addAttribute("msg", alertService.sucess("Settings Configuration saved successfully"));
			infoObj = dogConfigService.convertObjToDto("1");
			infoObj.setCronHit(cronHit);
			model.addAttribute("infoObj", infoObj);
			model.addAttribute("tabId", tabId);
			model.addAttribute("cronHit",cronHit);

			dataLoader.refreshData(infoObj);

		} catch (Exception e) {
			model.addAttribute("msg", e.getMessage());
			return "redirect:/errorpage?msg=Saving Settings Configuration error " + e.getMessage();
		}
		return "configfile";
	}
	
	
	/***
	 * CronValidator
	 * @param req link {@link HttpServletRequest}
	 * @return {@link Boolean}
	 */
	@RequestMapping("/cronvalidator")
    @ResponseBody
	public boolean cronValidator(HttpServletRequest req) {
		String cronStr=req.getParameter("param");
		if(!CronSequenceGenerator.isValidExpression(cronStr)) {
			return false;
		}
		return true;
	}

	/***
	 * Logs page
	 * @param model {@link ModelMap}
	 * @return string {@link ModelAndView} 
	 */

	@RequestMapping("/logspage")
	public String logsPage(ModelMap model) {
		List<FileDto> fileDtoList = commonService.loadFiles(env.getProperty("logs.dir"),
				Integer.valueOf(dataLoader.configDto.getLimitFilesFolder()));
		model.put("fileList", fileDtoList);
		return "logspage";
	}

	/***
	 * This url will call from ajx internally which
	 * load the page without loading whole page
	 * @param model link {@link Model}
	 * @return {@link String}
	 */
	@RequestMapping("/logspagecontent")
	public String logsPageContent(ModelMap model) {
		List<FileDto> fileDtoList = commonService.loadFiles(env.getProperty("logs.dir"),
				Integer.valueOf(dataLoader.configDto.getLimitFilesFolder()));
		model.put("fileList", fileDtoList);
		return "logspagecontent";
	}



	/***
	 * Report Page
	 * @param model {@link Model}
	 * @param request {@link HttpServletRequest}
	 * @return {@link String}
	 */
	@RequestMapping("/reportspage")
	public String reportsPage(ModelMap model, HttpServletRequest request) {
		try {

			String dbNmae = request.getParameter("db");
			List<String> dirList = commonService.loadReportsDir();
			model.addAttribute("dirList", dirList);
			Reports reports = null;
			if (dbNmae == null) {
				reports = commonService.loadRerorts(env.getProperty("reports.path"));
			} else {
				reports = commonService.loadReportsFromURL(dbNmae);
			}
			if (reports != null) {
				model.addAttribute("infoObj", reports.getReports());
			} else {
				model.addAttribute("infoObj", Collections.emptyList());
			}

		} catch (Exception e) {
			return "redirect:/errorpage?msg=Reports DB loading error {} " + e.getMessage();

		}
		return "reportspage";
	}

	/***
	 * 
	 * Report List
	 * @param reportsDb {@link String}
	 * @return list {@link Report}
	 */
	@RequestMapping(value = "/reportslist/{reportsdb}", method = RequestMethod.POST)
	public @ResponseBody List<Report> getReportsList(@PathVariable("reportsdb") String reportsDb) {

		Reports reports = commonService.loadReportsFromURL(reportsDb);
		if (reports != null) {
			return reports.getReports();
		} else {
			return Collections.emptyList();
		}
	}

	/****
	 * Response out put folder url
	 * @return responseout {@link ModelAndView} 
	 */
	@RequestMapping("/responseout")
	public ModelAndView responseOut() {
		ModelAndView mav = new ModelAndView();
		List<FileDto> fileDtoList = commonService.loadFiles(dataLoader.configDto.getOutputFolderPath(),
				Integer.valueOf(dataLoader.configDto.getLimitFilesFolder()));
		mav.addObject("fileList", fileDtoList);
		mav.setViewName("responseout");

		return mav;
	}

	/***
	 * This url will call from ajx internally which
	 * load the page without loading whole page
	 * @return responseoutcontent {@link ModelAndView} 
	 */
	@RequestMapping("/responseoutcontent")
	public ModelAndView responseOutConent() {
		ModelAndView mav = new ModelAndView();
		List<FileDto> fileDtoList = commonService.loadFiles(dataLoader.configDto.getOutputFolderPath(),
				Integer.valueOf(dataLoader.configDto.getLimitFilesFolder()));
		mav.addObject("fileList", fileDtoList);
		mav.setViewName("responseoutcontent");

		return mav;
	}

	

	/***
	 * Archive Folder URL
	 * @return archiveout {@link ModelAndView} 
	 */
	@RequestMapping("/archiveout")
	public ModelAndView archiveOut() {
		ModelAndView mav = new ModelAndView();
		List<FileDto> fileDtoList = commonService.loadFiles(dataLoader.configDto.getArchiveFolderPath(),
				Integer.valueOf(dataLoader.configDto.getLimitFilesFolder()));
		mav.addObject("fileList", fileDtoList);
		mav.setViewName("archiveout");
		return mav;
	}


	/***
	 * This url will call from ajx internally which
	 * load the page without loading whole page
	 * @return archiveoutcontent {@link ModelAndView} 
	 */
	@RequestMapping("/archiveoutcontent")
	public ModelAndView archiveOutContent() {
		ModelAndView mav = new ModelAndView();
		List<FileDto> fileDtoList = commonService.loadFiles(dataLoader.configDto.getArchiveFolderPath(),
				Integer.valueOf(dataLoader.configDto.getLimitFilesFolder()));
		mav.addObject("fileList", fileDtoList);
		mav.setViewName("archiveoutcontent");
		return mav;
	}

	/***
	 * load the input directory file
	 * @return inputdir {@link ModelAndView} 
	 */
	@RequestMapping("/inputdir")
	public ModelAndView inputDir() {
		ModelAndView mav = new ModelAndView();
		List<FileDto> fileDtoList = commonService.loadFiles(dataLoader.configDto.getInputFolderPath(),
				Integer.valueOf(dataLoader.configDto.getLimitFilesFolder()));
		mav.addObject("fileList", fileDtoList);
		mav.addObject("processingFile", AppUtil.CURRENT_FILE_PROCESSING_NAME);

		mav.setViewName("inputdir");
		return mav;
	}

	/***
	 * This url will call from ajx internally which
	 * load the page without loading whole page
	 * @return inputdircontent {@link ModelAndView} 
	 */
	@RequestMapping("/inputdircontent")
	public ModelAndView inputDirContent() {
		ModelAndView mav = new ModelAndView();
		List<FileDto> fileDtoList = commonService.loadFiles(dataLoader.configDto.getInputFolderPath(),
				Integer.valueOf(dataLoader.configDto.getLimitFilesFolder()));
		mav.addObject("fileList", fileDtoList);
		mav.addObject("processingFile", AppUtil.CURRENT_FILE_PROCESSING_NAME);

		mav.setViewName("inputdircontent");
		return mav;
	}

	
	

	/***
	 * Input url for fin directory
	 * @return inputdirfin {@link ModelAndView} 
	 */
	@RequestMapping("/inputdirfin")
	public ModelAndView inputDirFin() {
		ModelAndView mav = new ModelAndView();
		List<FileDto> fileDtoList = commonService.loadFiles(dataLoader.configDto.getOptInputFolderPath(),
				Integer.valueOf(dataLoader.configDto.getLimitFilesFolder()));
		mav.addObject("fileList", fileDtoList);
		mav.setViewName("inputdirfin");
		return mav;
	}
	
	/**
	 * input url for fbpay directory
	 * @return inputdirfbpay {@link ModelAndView} 
	 */
	@RequestMapping("/inputdirfbpay")
	public ModelAndView inputDirFBPay() {
		ModelAndView mav = new ModelAndView();
		List<FileDto> fileDtoList = commonService.loadFiles(dataLoader.configDto.getFbPayInputFolderPath(),
				Integer.valueOf(dataLoader.configDto.getLimitFilesFolder()));
		mav.addObject("fileList", fileDtoList);
		mav.setViewName("inputdirfbpay");
		return mav;
	}
	
	
	
	
	/***
	 * Input url for SO Order directory
	 * @return inputdirso {@link ModelAndView} 
	 */
	@RequestMapping("/inputdirso")
	public ModelAndView inputDirSO() {
		ModelAndView mav = new ModelAndView();
		List<FileDto> fileDtoList = commonService.loadFiles(dataLoader.configDto.getSoOrderInputFolderPath(),
				Integer.valueOf(dataLoader.configDto.getLimitFilesFolder()));
		mav.addObject("fileList", fileDtoList);
		mav.setViewName("inputdirso");
		return mav;
	}
	
	
	/***
	 * Input url for nonedi input directory
	 * @return inputdirnonedi {@link ModelAndView} 
	 */
	@RequestMapping("/inputdirnonedi")
	public ModelAndView inputDirNonEdi() {
		ModelAndView mav = new ModelAndView();
		List<FileDto> fileDtoList = commonService.loadFiles(dataLoader.configDto.getNonEdiCamInputFolderPath(),
				Integer.valueOf(dataLoader.configDto.getLimitFilesFolder()));
		mav.addObject("fileList", fileDtoList);
		mav.setViewName("inputdirnonedi");
		return mav;
	}
	
	
	/***
	 * Input url for nonedi input directory
	 * @return inputdirnonedi {@link ModelAndView} 
	 */
	@RequestMapping("/inputdirbulk")
	public ModelAndView inputDirBLK() {
		ModelAndView mav = new ModelAndView();
		List<FileDto> fileDtoList = commonService.loadFiles(dataLoader.configDto.getBulkInputFolderPath(),
				Integer.valueOf(dataLoader.configDto.getLimitFilesFolder()));
		mav.addObject("fileList", fileDtoList);
		mav.setViewName("inputdirbulk");
		return mav;
	}
	
	/***
	 * Plan Id UI
	 * @return planid {@link ModelAndView} 
	 */
	@RequestMapping("/planid")
	public ModelAndView planId() {
		ModelAndView mav = new ModelAndView();
		try {
			
			PlanIds planIds= (PlanIds) xmlUtilService.convertXMLToObject(PlanIds.class, Paths.get(env.getProperty("db.planid")).toFile());
			if(planIds==null) {
		     	mav.addObject("planList", new ArrayList<PlanId>());
			}else {
				mav.addObject("planList", planIds.getPlanId()!=null?planIds.getPlanId():new ArrayList<PlanId>());
			}
			mav.setViewName("planid");
			
		} catch (FileNotFoundException | JAXBException e) {
			e.printStackTrace();
		}
		return mav;
	}

	/***
	 * URL for failure directory
	 * @param httpRequest {@link HttpServletRequest}
	 * @return {@link String}
	 */
	@RequestMapping("/failuredir")
	public ModelAndView failureDir(HttpServletRequest httpRequest) {
		ModelAndView mav = new ModelAndView();
		List<FileDto> fileDtoList = commonService.loadFiles(dataLoader.configDto.getFailureFolderPath(),
				Integer.valueOf(dataLoader.configDto.getLimitFilesFolder()));
		String errorMsg = httpRequest.getParameter("msg");
		mav.addObject("msg", errorMsg);
		mav.addObject("fileList", fileDtoList);
		mav.setViewName("failuredir");
		return mav;
	}

	/***
	 * This url will call from ajx internally which load the page
	 * @param httpRequest {@link HttpServletRequest}}
	 * @return failuredircontent {@link String}
	 */
	@RequestMapping("/failuredircontent")
	public ModelAndView failureDirContent(HttpServletRequest httpRequest) {
		ModelAndView mav = new ModelAndView();
		List<FileDto> fileDtoList = commonService.loadFiles(dataLoader.configDto.getFailureFolderPath(),
				Integer.valueOf(dataLoader.configDto.getLimitFilesFolder()));
		mav.addObject("fileList", fileDtoList);
		mav.setViewName("failuredircontent");
		return mav;
	}

	/***
	 * download all files on the basis of file type
	 * @param fileName {@link String}
	 * @param fileType {@link String}
	 * @param response {@link HttpServletResponse}
	 * @return {@link String}
	 */
	@RequestMapping("/downloadfile/{fileName}/{fileType}")
	public String downloadFile(@PathVariable("fileName") String fileName, @PathVariable("fileType") String fileType,
			HttpServletResponse response) {
		String filetoDownload = "";
		try {
			switch (fileType) {

			case "in":
				filetoDownload = dataLoader.configDto.getInputFolderPath();
				break;
			case "fin":
				filetoDownload = dataLoader.configDto.getOptInputFolderPath();
				break;
				
			case "fbpay":
				filetoDownload = dataLoader.configDto.getFbPayInputFolderPath();
				break;		
			case "so":
				
				filetoDownload = dataLoader.configDto.getSoOrderInputFolderPath();
				break;	
			case "edi":
				filetoDownload = dataLoader.configDto.getNonEdiCamInputFolderPath();
				break;
			case "bulk":
				filetoDownload = dataLoader.configDto.getBulkInputFolderPath();
				break;	
			case "out":
				filetoDownload = dataLoader.configDto.getOutputFolderPath();
				break;
			case "ar":
				filetoDownload = dataLoader.configDto.getArchiveFolderPath();
				break;
			case "fail":
				filetoDownload = dataLoader.configDto.getFailureFolderPath();
				break;
			case "logs":
				filetoDownload = env.getProperty("logs.dir");
				break;
			default:
				break;
			}

			if (!filetoDownload.isEmpty()) {
				final String filePath = filetoDownload.concat(File.separator).concat(fileName);
				return fileDonwload(filePath, response);
			} else {
				return "redirect:/errorpage?msg=Error in downloading file";
			}
		} catch (Exception e) {
			return "redirect:/errorpage?msg=Error in downloading file";

		}
	}

	/***
	 * This url which will rename the invalid file means which file is not
	 * supporting by Watchdog
	 * @param fileName {@link String}
	 * @param fileType {@link String}
	 * @param model {@link Model}
	 * @return {@link String}
	 */
	@RequestMapping("/renameinvalidfile/{fileName}/{fileType}")
	public String renameInvalidFile(@PathVariable("fileName") String fileName,
			@PathVariable("fileType") String fileType, Model model) {

		try {
			final String filePath = dataLoader.configDto.getInputFolderPath().concat(File.separator).concat(fileName);
			File file = Paths.get(filePath).toFile();
			if (file.exists()) {
				String currFileBase = fileName.split(dataLoader.configDto.getFileTypeSeparator())[0];
				// String currFileExtension=FilenameUtils.getExtension(fileName);
				String renameFileName = currFileBase.concat(dataLoader.configDto.getFileTypeSeparator())
						.concat(fileType)
						.concat(AppUtil.UNDERSCORE_STR)
						.concat(AppUtil.currentTime())
						.concat(AppUtil.DOT_STR).concat(dataLoader.configDto.getFileExtension());
				
				boolean isRename = file.renameTo(Paths.get(
						dataLoader.configDto.getInputFolderPath().concat(File.separator).concat(renameFileName)).toFile());
				
				if (isRename) {
					model.addAttribute("msg",
							alertService.sucess("File Rename Successfully to " + renameFileName + ""));
					return "redirect:/inputdir?msg=File Rename Successfully to " + renameFileName;

				}
			}

		} catch (Exception e) {
			return "redirect:/errorpage?msg=Error occured during Re-naming file";
		}
		return "redirect:/errorpage?msg=Error occured during Re-naming file";

	}

	/***
	 * file upload url
	 * @param model {@link Model}
	 * @return file upload {@link String}
	 * 
	 */
	@RequestMapping("/fileupload")
	public String fileUpload(Map<String, Object> model) {
		return "fileupload";
	}

	/***
	 * Upload single file using Spring Controller
	 * @param file  {@link File}
	 * @return link {@link ModelAndView}
	 */
	@RequestMapping(value = "/uploadfile", method = RequestMethod.POST)
	public ModelAndView uploadFile(@RequestParam("file") MultipartFile file) {
		if (file != null && !file.isEmpty()) {
			try {

				MultipartFile multipartFile = file;
				String uploadPath = dataLoader.configDto.getInputFolderPath() + File.separator;
				// Now do something with file...
				FileCopyUtils.copy(file.getBytes(), Paths.get(uploadPath + file.getOriginalFilename()).toFile());
				String fileName = multipartFile.getOriginalFilename();
				return new ModelAndView("fileupload", "msg",
						alertService.sucess("File saved successfully " + fileName + ""));

			} catch (Exception e) {
				return new ModelAndView("fileupload", "msg",
						alertService.error("Problem in saving file, Check file size before upload"));

			}
		} else {
			return new ModelAndView("fileupload", "msg", alertService.error("Select input xml file for upload"));

		}
	}

	/***
	 * @param fileName {@link String}
	 * @param type {@link String}
	 * @param response {@link String}
	 * @return output {@link String}
	 */
	@RequestMapping("/movetoinputdir/{fileName}/{type}")
	public String moveToInputDir(@PathVariable("fileName") String fileName, @PathVariable("type") String type,
			HttpServletResponse response) {
		String mesg = "File moved successfully";
		try {
			String filePath = dataLoader.configDto.getArchiveFolderPath().concat(File.separator).concat(fileName);
			String action = "redirect:/archiveout?msg=" + mesg;
			switch (type) {
			case "fail":
				filePath = dataLoader.configDto.getFailureFolderPath().concat(File.separator).concat(fileName);
				action = "redirect:/failuredir?msg=" + mesg;
				break;

			case "in":
				filePath = dataLoader.configDto.getInputFolderPath().concat(File.separator).concat(fileName);
				action = "redirect:/inputdir?msg=" + mesg;
				break;

			default:
				break;
			}

			File file = Paths.get(filePath).toFile();
			if (!file.exists()) {
				String errorMessage = "Sorry. The file you are looking for does not exist";
				return "redirect:/errorpage?msg=" + errorMessage;

			}
			doMoveFile(filePath, fileName, type);
			return action;
		} catch (Exception e) {
			return "redirect:/errorpage?msg=Error occured during moving to input folder";
		}

	}

	/***
	 * This method will delete the input file on the base of input/file
	 * @param fileName  {@link String}
	 * @param fromwhere {@link String}
	 * @param response  {@link HttpServletResponse}
	 * @return string   {@link String}
	 */

	@RequestMapping("/deletefilefrominput/{fromwhere}/{fileName}")
	public String deleteFileFromInputDir(@PathVariable("fileName") String fileName,
			@PathVariable("fromwhere") String fromwhere, HttpServletResponse response) {

		try {
			String filePath = dataLoader.configDto.getInputFolderPath().concat(File.separator).concat(fileName);
			switch (fromwhere) {
			case "fin":
				filePath = dataLoader.configDto.getOptInputFolderPath().concat(File.separator).concat(fileName);
				break;
			case "nonedi":
				filePath = dataLoader.configDto.getNonEdiCamInputFolderPath().concat(File.separator).concat(fileName);
				break;
			case "so":
				filePath = dataLoader.configDto.getSoOrderInputFolderPath().concat(File.separator).concat(fileName);
				break;	
			case "fbpay":
				filePath = dataLoader.configDto.getFbPayInputFolderPath().concat(File.separator).concat(fileName);
				break;		
			case "bulk":
				filePath = dataLoader.configDto.getBulkInputFolderPath().concat(File.separator).concat(fileName);
				break;		
			default:
				break;
			}
			File file = Paths.get(filePath).toFile();
			if (file.exists()) {
				FileUtils.deleteQuietly(FileUtils.getFile(filePath));
			}

		} catch (Exception e) {
			return "redirect:/errorpage?msg=Error occured during deleting file from input folder";
		}
		return "redirect:/inputdir";

	}

	/**
	 * Move file one location to another location
	 * @param arachiveFilePath {@link String}
	 * @param fileName {@link String}
	 * @param type {@link String}}
	 * @see FileUtils {@link FileUtils}
	 */
	public void doMoveFile(String arachiveFilePath, String fileName, String type) {
		try {

			String inputFolderFile = dataLoader.configDto.getInputFolderPath() + File.separator + fileName;
			if ("in".equals(type)) {
				inputFolderFile = dataLoader.configDto.getArchiveFolderPath() + File.separator + fileName;
			}
			commonService.moveReplaceFile(arachiveFilePath, inputFolderFile);

		} catch (Exception e) {
			FileUtils.deleteQuietly(FileUtils.getFile(arachiveFilePath));

		}
	}


	/***
	 * This method will download the file
	 * @param fileDir {@link String}
	 * @param response  {@link HttpServletResponse}
	 * @return file  {@link String}
	 * @throws IOException {@link IOException}
	 */
	public String fileDonwload(String fileDir, HttpServletResponse response) throws IOException {
		
		File file = Paths.get(fileDir).toFile();
		if (!file.exists()) {
			String errorMessage = "Sorry. The file you are looking for does not exist";

			return "redirect:/errorpage?msg=" + errorMessage;
		}
		String mimeType = URLConnection.guessContentTypeFromName(file.getName());
		if (mimeType == null) {
			mimeType = "application/xml";
		}

		response.setContentType(mimeType);
		// response.setHeader("Content-Disposition", String.format("inline; filename=\""
		// + file.getName() +"\""));

		/**
		 * "Content-Disposition : attachment" will be directly download, may provide
		 *  save as popup, based on your browser setting
		 */
		response.setHeader("Content-Disposition", String.format("attachment; filename=\"%s\"", file.getName()));

		response.setContentLength((int) file.length());

		InputStream inputStream = new BufferedInputStream(new FileInputStream(file));

		// Copy bytes from source to destination(outputstream in this example), closes
		// both streams.
		FileCopyUtils.copy(inputStream, response.getOutputStream());
		return null;
	}

}

