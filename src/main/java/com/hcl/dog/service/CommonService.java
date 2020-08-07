package com.hcl.dog.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.nio.file.DirectoryStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.FileTime;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXParseException;

import com.hcl.dog.common.AppUtil;
import com.hcl.dog.common.HTMLTemplate;
import com.hcl.dog.component.DataLoaderComponent;
import com.hcl.dog.config.PropertiesConfig;
import com.hcl.dog.domain.Reports;
import com.hcl.dog.dto.FileDto;
import com.hcl.dog.dto.PlanDescriptionDto;
import com.hcl.dog.dto.ReportDto;
import com.hcl.dog.dto.ResponseDto;
import com.hcl.dog.dto.StatusInfoDto;
import com.hcl.dog.dto.UserDto;

/***
 * Common class for watchdog
 * 
 * @author intakhabalam.s@hcl.com
 * @see Service
 * @see EmailService {@link EmailService}
 * @see XMLUtilService {@link XMLUtilService}
 * @see DataLoaderComponent {@link DataLoaderComponent}
 * @see PropertiesConfig {@link PropertiesConfig}
 * @see Environment {@link Environment}
 *
 */
@Service
public class CommonService {

	private final Logger logger = LogManager.getLogger("comon-serv");

	
	
	@Autowired
	private Environment env;
	@Autowired
	private DataLoaderComponent dataLoader;
	@Autowired
	private PropertiesConfig propertiesConfig;
	@Autowired
	private XMLUtilService xmlUtilService;

	

	/**
	 * This method will find the Exception from String
	 * 
	 * @param str
	 *            value
	 * @return TrueFalse
	 */
	public boolean findExceptionFromString(String str) {
		try {
			if (searchRegexText(str, AppUtil.exceptionPatternREG)) {
				return true;
			}
			if (searchRegexText(str, AppUtil.exceptionPatternCAP)) {
				return true;
			}

			final List<String> foundList = Arrays.asList(str.split(" "));

			if (foundList.contains(AppUtil.exceptionPatternCAP)) {
				return true;
			}
			if (foundList.contains(AppUtil.exceptionPatternSML)) {
				return true;
			}
			if (str.indexOf(AppUtil.exceptionPatternCAP) != -1) {
				return true;
			}
			if (str.indexOf(AppUtil.exceptionPatternSML) != -1) {
				return true;
			}

		} catch (Exception e) {
			return false;
		}
		return false;
	}

	/**
	 * Method will laod the files from the folder on the based of folder path and
	 * here limit the folder files number
	 * 
	 * @return list
	 * @param folderPath
	 *            {@link String}
	 * @param limit
	 *            {@link Integer}
	 */
	public List<FileDto> loadFiles(String folderPath, int limit) {
		if (folderPath == null) {
			logger.error("Folders Paths are not defined. Please check configuartion setting");
			return null;
		}

		Path dir = FileSystems.getDefault().getPath(folderPath);
		List<FileDto> fileDtoList = new ArrayList<>();
		AtomicInteger counter = new AtomicInteger(0);

		try (DirectoryStream<Path> files = Files.newDirectoryStream(dir)) {
			StreamSupport.stream(files.spliterator(), false).sorted((o1, o2) -> {
				try {
					return Files.getLastModifiedTime(o2).compareTo(Files.getLastModifiedTime(o1));
				} catch (IOException ex) {
				}
				return 0;
			}).filter(file -> Files.isRegularFile(file)).limit(limit).forEach(file -> {
				// if(f.isFile()) {
				try {
					FileDto fdto = new FileDto();
					fdto.setId(String.valueOf(counter.incrementAndGet()));
					fdto.setFileName(file.getFileName().toString());
					fdto.setLastModifiedDate(printFileTime(Files.getLastModifiedTime(file)));
					fdto.setFileFullPath(FileSystems.getDefault().getPath(file.toString()).toString());
					fdto.setFileStatus(checkFileConvention(file.getFileName().toString()));
					fdto.setFileEndWith(getFileSupports());
					fileDtoList.add(fdto);

				} catch (Exception e) {
					// TODO: handle exception
				}
			});
		} catch (Exception e1) {
			logger.error("Error: {Com-0001} file loading problem {} ", e1);

		}

		return fileDtoList;

	}

	/***
	 * This method will load reports from reports dir
	 * 
	 * @return list
	 */
	public List<String> loadReportsDir() {
		final String reportsPath = env.getProperty("reports.path").split("/")[0];
		Path dir = FileSystems.getDefault().getPath(reportsPath);
		List<String> dirList = new ArrayList<>();
		try (DirectoryStream<Path> files = Files.newDirectoryStream(dir)) {
			StreamSupport.stream(files.spliterator(), false).sorted((o2, o1) -> {
				try {
					return Files.getLastModifiedTime(o1).compareTo(Files.getLastModifiedTime(o2));
				} catch (IOException ex) {
				}
				return 0;
			}).filter(file -> Files.isRegularFile(file)).limit(10).forEach(file -> {
				dirList.add(file.getFileName().toString());
			});
		} catch (Exception e1) {
			logger.error("Error: {Com-0002} report loading problem {} ", e1);

		}
		return dirList;
	}

	/***
	 * Will print time
	 * 
	 * @param fileTime
	 *            {@link String}
	 * @return time {@link String}
	 */
	private String printFileTime(FileTime fileTime) {
		try {
			return AppUtil.dateFormat.format(fileTime.toMillis());
		} catch (Exception e) {
			return "" + System.currentTimeMillis();
		}
	}

	/***
	 * This method will check file convention which is supporting by watchdog
	 * application
	 * 
	 * @param fileName
	 *            {@link String}
	 * @return filetype {@link String}
	 */
	public String checkFileConvention(String fileName) {
		String fileType = "Not Supported File";
		final String fname = fileName;
		try {
			String fileEndWith = dataLoader.configDto.getFileSupports().concat(AppUtil.COMMA_SEPERATOR)
					.concat(dataLoader.configDto.getOptFileSupports()).concat(AppUtil.COMMA_SEPERATOR)
					.concat(dataLoader.configDto.getNonEdiCamFileSupports()).concat(AppUtil.COMMA_SEPERATOR)
					.concat(dataLoader.configDto.getBulkFileSupports()).concat(AppUtil.COMMA_SEPERATOR)
					.concat(dataLoader.configDto.getNonEdiCamWineFileSupports());

			String fileNameAfterFileSepartor = fname.split(dataLoader.configDto.getFileTypeSeparator())[1]
					.split(AppUtil.DOT_STR_COMP)[0];
			List<String> fileDcOrCam = split(fileEndWith.trim(), AppUtil.COMMA_SEPERATOR);
			for (String fn : fileDcOrCam) {
				String noSpaceStr = fn.replaceAll(AppUtil.WHITE_SPACE, AppUtil.EMPTY_STR);
				if (fileNameAfterFileSepartor.equalsIgnoreCase(noSpaceStr)
						|| StringUtils.startsWithIgnoreCase(fileNameAfterFileSepartor, noSpaceStr)) {
					fileType = fn;
				}
			}

		} catch (Exception e) {
			return fileType;
		}
		return fileType;

	}

	/***
	 * Method will check the file naming convention which end with like @edi,@dc
	 * 
	 * @return file type
	 */
	public String getFileSupports() {
		String fileEndWith = dataLoader.configDto.getFileSupports().concat(AppUtil.COMMA_SEPERATOR)
				.concat(dataLoader.configDto.getOptFileSupports()).concat(AppUtil.COMMA_SEPERATOR)
				.concat(dataLoader.configDto.getNonEdiCamFileSupports());
		List<String> fileDcOrCam = split(fileEndWith.trim(), AppUtil.COMMA_SEPERATOR);
		StringBuilder sb = new StringBuilder("<select id='fileEndWithId'>");
		sb.append("<option value=''>").append("Select File Type").append("</option>");
		for (String s : fileDcOrCam) {
			sb.append("<option value=" + s + ">").append(s).append("</option>");
		}
		sb.append("</select>");
		return sb.toString();
	}

	/***
	 * @param str
	 *            {@link String}
	 * @param regex
	 *            {@link String}
	 * @return {@link Boolean}
	 */

	public boolean searchRegexText(String str, String regex) {
		// Create a Pattern object
		Pattern r = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
		// Now create matcher object.
		Matcher m = r.matcher(str);
		if (m.find()) {
			logger.info("During XML parsing found(s) error ==> " + m.group());
			return true;
		} else {
			logger.info("NO MATCH ===> Pattern { " + regex + " }");
			return false;
		}
	}

	/***
	 * Creating dir method will scan the directory, if not exist then it will create
	 */
	public void scanDirectory() {
		logger.info("Scanning all directory...");

		createFolder(dataLoader.configDto.getInputFolderPath(), "Input");
		createFolder(dataLoader.configDto.getOutputFolderPath(), "Output");
		createFolder(dataLoader.configDto.getArchiveFolderPath(), "Archive");
		createFolder(dataLoader.configDto.getFailureFolderPath(), "Failure");
		//
		createFolder(dataLoader.configDto.getOptInputFolderPath(), "Fin Input");
		createFolder(dataLoader.configDto.getNonEdiCamInputFolderPath(), "NonEdi Input");
		createFolder(dataLoader.configDto.getSoaOutputFolderPath(), "Fin SOA Folder");

		createFolder(env.getProperty("backup.dir"), "Backup Folder");
		createFolder("reports", "Reports");
		createFolder(dataLoader.configDto.getSoOrderInputFolderPath(), "SO Folder");
		createFolder(dataLoader.configDto.getFbPayInputFolderPath(), "FBPay");
		createFolder(dataLoader.configDto.getBulkInputFolderPath(), "BULK");

		//

	}

	/***
	 * Write start html path at runtime
	 * 
	 * @param hostname
	 *            {@link String}
	 */
	public void writeStartFile(String hostname) {
		String newData = HTMLTemplate.getStartTemplate(hostname, env.getProperty("server.port"));
		File htmlFile = Paths.get("start.html").toFile();
		if (!htmlFile.exists()) {
			writeFile(newData, htmlFile.getPath());
		}
	}

	/***
	 * @param hostname
	 *            {@link String}
	 */
	public void writeStartFileForcely(String hostname) {
		String newData = HTMLTemplate.getStartTemplate(hostname, env.getProperty("server.port"));
		File htmlFile = Paths.get("start.html").toFile();
		if (htmlFile.exists()) {
			FileUtils.deleteQuietly(htmlFile);

		}
		writeFile(newData, htmlFile.getPath());
	}

	/***
	 * This method will write error info
	 */
	public void writeErrorInfo() {
		String newData = HTMLTemplate.getErrorTemplate();
		File htmlFile = Paths.get(env.getProperty("error.info")).toFile();
		if (!htmlFile.exists()) {
			writeFile(newData, htmlFile.getPath());
		}
	}

	/***
	 * This method will delete the remote html which created during the startup
	 * 
	 */
	public void deleteRemoteFile() {
		try {
			final String filePath = env.getProperty("remote.start");
			File filepath = Paths.get(filePath).toFile();
			if (filepath.exists()) {
				FileUtils.deleteQuietly(filepath);
			}
		} catch (Exception e) {
		}
	}

	/***
	 * Will Create folder
	 * 
	 * @param path
	 *            {@link String}
	 * @param type
	 *            {@link String}
	 */
	public void createFolder(String path, String type) {
		File file = Paths.get(path).toFile();
		if (!file.exists()) {
			if (file.mkdir()) {
				logger.info(type + " directory is created");

			} else {
				logger.error("Error: {Com-8888} Failed to create directory { " + type + " }");

			}
		}
	}

	/**
	 * This method will move file to destination dir, if exits then replace it
	 * 
	 * @param src
	 *            {@link String}
	 * @param destSrc
	 *            {@link String}
	 */
	public void moveReplaceFile(String src, String destSrc) {
		Path sourcePath = Paths.get(src);
		Path destinationPath = Paths.get(destSrc);
		try {
			Files.move(sourcePath, destinationPath, StandardCopyOption.REPLACE_EXISTING);
		} catch (Exception e) {
			logger.error("Error: {Com-0003} File Moving problem {} " + e);
		}
	}

	/**
	 * This method will copy and replace file
	 * 
	 * @param src
	 *            {@link String}
	 * @param destSrc
	 *            {@link String}
	 */
	public void copyReplaceFile(String src, String destSrc) {
		Path sourcePath = Paths.get(src);
		Path destinationPath = Paths.get(destSrc);
		try {
			Files.copy(sourcePath, destinationPath, StandardCopyOption.REPLACE_EXISTING);
		} catch (FileAlreadyExistsException e) {
			// throw new FileException();
			logger.error("Error: {Com-0006} Destination file already exists {} " + e);
			// destination file already exists
		} catch (Exception e) {
			// something else went wrong
			logger.error("Error:  {Com-0007} - " + e);
		}

	}

	/***
	 * Util method which will separate API string
	 * 
	 * @param str
	 *            {@link String}
	 * @return split str
	 */
	public String[] separateApiString(String str) {
		String apiStr = AppUtil.checkAndRemoveCurlyStr(str);

		// if APIs separated with pipe.
		return apiStr.split("\\|");
	}

	/***
	 * 
	 * @param src
	 *            {@link String}
	 * @param what
	 *            {@link String}
	 * @return {@link Boolean}
	 */
	public boolean containsIgnoreCaseRegexp(String src, String what) {
		return Pattern.compile(Pattern.quote(what), Pattern.CASE_INSENSITIVE).matcher(src).find();
	}

	/***
	 * @param d
	 *            {@link Double}
	 * @return {@link String}
	 */
	public String getDecimalNumber(double d) {
		DecimalFormat df2 = new DecimalFormat("#.##");
		return df2.format(d);
	}

	/***
	 * Backup of config file
	 * 
	 * @param fileName
	 *            {@link String}
	 */
	public void backupConfigFile(String fileName) {
		String newFile = env.getProperty("backup.dir") + "/" + "config_" + AppUtil.currentTime() + ".db";
		File file = Paths.get(fileName).toFile();
		if (file.exists()) {
			copyReplaceFile(fileName, newFile);

		}
		logger.info("Config DB backup done sucessfully...");

	}

	/***
	 * Backup backup User File
	 */
	public void backupUserFile() {
		String newFile = env.getProperty("backup.dir") + "/" + "users_" + AppUtil.currentTime() + ".db";
		File file = Paths.get(env.getProperty("db.user")).toFile();
		if (file.exists()) {
			copyReplaceFile(file.getPath(), newFile);

		}
		logger.info("User DB backup done sucessfully...");

	}

	/**
	 * Will backup file plan id
	 */
	public void backupPlanIdFile() {
		String newFile = env.getProperty("backup.dir") + "/" + "planid" + AppUtil.currentTime() + ".db";
		File file = Paths.get(env.getProperty("db.planid")).toFile();
		if (file.exists()) {
			copyReplaceFile(file.getPath(), newFile);
			FileUtils.deleteQuietly(file);
		}
		logger.info("PlanID DB backup done sucessfully...");

	}

	/**
	 * This method will reload Config file once user will click button on to UI
	 */
	public void reloaUserbackup() {
		String dbLocation = env.getProperty("backup.dir");
		Path parentFolder = Paths.get(dbLocation);
		// if you're only interested in files...
		Optional<File> mostRecentFile = Arrays.stream(parentFolder.toFile().listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.startsWith("users");
			}
		})).filter(f -> f.isFile()).max((f1, f2) -> Long.compare(f1.lastModified(), f2.lastModified()));

		if (mostRecentFile.isPresent()) {
			File mostRecent = mostRecentFile.get();
			logger.info("Backup folder most recent file for backup {} " + mostRecent.getName());
			String dbPath = env.getProperty("db.user");
			copyReplaceFile(mostRecent.getPath(), dbPath);
		} else {
			logger.info("User backup folder Empty {} ");
		}
	}

	/**
	 * This method will reload Config file once user will click button on to UI
	 */
	public void reloaConfigBackup() {
		String dbLocation = env.getProperty("backup.dir");
		Path parentFolder = Paths.get(dbLocation);

		Optional<File> mostRecentFile = Arrays.stream(parentFolder.toFile().listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.startsWith("config");
			}
		})).filter(f -> f.isFile()).max((f1, f2) -> Long.compare(f1.lastModified(), f2.lastModified()));

		if (mostRecentFile.isPresent()) {
			File mostRecent = mostRecentFile.get();
			logger.info("Backup folder most recent file for backup {} " + mostRecent.getName());
			String dbPath = env.getProperty("db.location");
			copyReplaceFile(mostRecent.getPath(), dbPath);
		} else {
			logger.info("Backup folder Empty {} ");
		}

	}

	/**
	 * Based on type file will go to failure or archive folder
	 * 
	 * @param currentFileName
	 *            {@link String}
	 * @param fileName
	 *            {@link String}
	 * @param type
	 *            {@link String}
	 */
	public void gotoArchiveFailureFolder(String currentFileName, String fileName, String type) {
		try {
			String archiveFailureName = dataLoader.configDto.getArchiveFolderPath() + File.separator + fileName;
			if ("F".equals(type)) {
				archiveFailureName = dataLoader.configDto.getFailureFolderPath() + File.separator + fileName;
			}
			moveReplaceFile(currentFileName, archiveFailureName);
			logger.info("Moved file here : [ " + archiveFailureName + " ]");

		} catch (Exception e) {
			logger.error("Error: {Com-0008} : archive problem {} " + e.getMessage());
			// FileUtils.deleteQuietly(FileUtils.getFile(currentFileName));
		}
	}

	/***
	 * Some API need to go SOA folder after successfully run
	 * 
	 * @param currentFileName
	 *            {@link String}
	 * @param fileName
	 *            {@link String}
	 */
	public void gotSOAFolder(String currentFileName, String fileName) {
		try {
			String soaFolderPath = dataLoader.configDto.getSoaOutputFolderPath() + File.separator + fileName;

			copyReplaceFile(currentFileName, soaFolderPath);
			logger.info("Fin file here : [ " + soaFolderPath + " ]");
		} catch (Exception e) {
			logger.error("Error: {Com-0009} copy to soa folder problem {} " + e.getMessage());

		}
	}

	/****
	 * 
	 * @param fileName
	 *            {@link String}
	 * @return archiveFailureName {@link String}
	 */
	public String getFullFileNameForArchive(String fileName) {
		String baseName = FilenameUtils.getBaseName(fileName);
		String extension = FilenameUtils.getExtension(fileName);
		String archiveFailureName = dataLoader.configDto.getArchiveFolderPath() + File.separator + baseName
				+ AppUtil.UNDERSCORE_STR + AppUtil.currentTime() + AppUtil.UNDERSCORE_STR + extension;
		return archiveFailureName;
	}

	/****
	 * This method will save the planId
	 * 
	 * @param systemId
	 *            {@link String}
	 * @param 0
	 *            inserted, 1 updated {@link Boolean}
	 */
	public void storeSystemId(String systemId, PlanDescriptionDto pdto, String trueFalse) {
		pdto.createdTime = printFileTime(FileTime.fromMillis(System.currentTimeMillis()));
		saveSystemPlanId(systemId, pdto, trueFalse);
	}

	/***
	 * 
	 * @param systemId
	 *            {@link String}
	 * @param printFileTime
	 *            {@link String}
	 * @param trueFalse
	 *            {@link String}
	 */
	private void saveSystemPlanId(String systemId, PlanDescriptionDto pdto, String trueFalse) {

		final String planidPath = env.getProperty("db.planid");
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setNamespaceAware(false);
			factory.setValidating(false);
			DocumentBuilder builder = factory.newDocumentBuilder();
			File file = Paths.get(planidPath).toFile(); // XML file to read
			if (!file.exists()) {
				createDummyXML(planidPath, "PlanIds");
			}

			Document document = builder.parse(file);
			Element planid = document.getDocumentElement();
			Element sysplanid = document.createElement("PlanId");
			sysplanid.setAttribute("id", systemId);

			Element planDescription = document.createElement("PlanDescription");
			Text planDescriptionText = document.createTextNode(pdto.planDescription);
			planDescription.appendChild(planDescriptionText);
			sysplanid.appendChild(planDescription);
			//
			Element logisticsGroupCode = document.createElement("LogisticsGroupCode");
			Text logisticsGroupCodeText = document.createTextNode(pdto.logisticsGroupCode);
			logisticsGroupCode.appendChild(logisticsGroupCodeText);
			sysplanid.appendChild(logisticsGroupCode);
			//

			Element divisionCode = document.createElement("DivisionCode");
			Text divisionCodeText = document.createTextNode(pdto.divisionCode);
			divisionCode.appendChild(divisionCodeText);
			sysplanid.appendChild(divisionCode);
			//
			Element createDate = document.createElement("CreatedDate");
			Text descriptionText = document.createTextNode(pdto.createdTime);
			createDate.appendChild(descriptionText);
			sysplanid.appendChild(createDate);
			//

			Element isRun = document.createElement("IsRun");
			Text isRunText = document.createTextNode(trueFalse);
			isRun.appendChild(isRunText);
			sysplanid.appendChild(isRun);
			//

			planid.appendChild(sysplanid);

			transformDoc(document, file);
			logger.info("PlanID DB saved successfully...");

		} catch (SAXParseException | TransformerException e) {
			logger.error("Error: {Com-0017} PlanID Parsing problem {} ", e.getMessage());
			logger.info("Deleting PlanID and Creating again...");
			FileUtils.deleteQuietly(Paths.get(planidPath).toFile());
			saveSystemPlanId(systemId, pdto, trueFalse);
		} catch (Exception e) {
			logger.error("Error: {Com-0018} PlanID Parsing problem {} ", e.getMessage());
		}

	}

	/***
	 * This method will add files for reports creation
	 * 
	 * @param currentFileName
	 *            {@link String}
	 * @param desc
	 *            {@link String}
	 * @param type
	 *            {@link String}
	 */
	public void addFilesForReports(String currentFileName, String desc, String type) {

		long currtime = System.currentTimeMillis();
		ReportDto reports = new ReportDto();
		reports.setId("" + currtime);
		reports.setFilename(currentFileName);
		reports.setFiledat(printFileTime(FileTime.fromMillis(currtime)));
		reports.setDescription(desc);
		reports.setStatus(type);
		createReports(reports);
	}

	/****
	 * After taking input this method will create reports which will save in reports
	 * folder and shown onto UI
	 * 
	 * @param repDto
	 *            {@link ReportDto}
	 */
	public void createReports(ReportDto repDto) {
		final String reportsPath = env.getProperty("reports.path");
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setNamespaceAware(false);
			factory.setValidating(false);
			DocumentBuilder builder = factory.newDocumentBuilder();
			File file = Paths.get(reportsPath).toFile(); // XML file to read
			if (!file.exists()) {
				createDummyXML(reportsPath, "reports");
			}
			Document document = builder.parse(file);
			Element reports = document.getDocumentElement();
			Element fileReport = document.createElement("report");
			fileReport.setAttribute("id", repDto.getId());

			Element filename = document.createElement("filename");
			Text filenameText = document.createTextNode(Paths.get(repDto.getFilename()).toFile().getName());
			filename.appendChild(filenameText);
			fileReport.appendChild(filename);
			//
			Element filedat = document.createElement("filedat");
			Text filedatText = document.createTextNode(repDto.getFiledat());
			filedat.appendChild(filedatText);
			fileReport.appendChild(filedat);
			//
			Element description = document.createElement("description");
			Text descriptionText = document.createTextNode(repDto.getDescription());
			description.appendChild(descriptionText);
			fileReport.appendChild(description);

			Element status = document.createElement("status");
			Text statusText = document.createTextNode(repDto.getStatus().equals("F") ? "FAIL" : "PASS");
			status.appendChild(statusText);
			fileReport.appendChild(status);

			reports.appendChild(fileReport);

			transformDoc(document, file);
			logger.info("Reports saved successfully...");

			//
		} catch (SAXParseException | TransformerException e) {
			logger.error("Errror: {Com-0010-1} reports parsing problem {} ", e);
			FileUtils.deleteQuietly(Paths.get(reportsPath).toFile());

		} catch (Exception e) {
			logger.error("Errror: {Com-0010-2} Unknown problem {} ", e);
		}

	}

	/****
	 * This will create a dummy reports at first time, if reports not available
	 * 
	 * @param filePath
	 *            {@link String}
	 * @param tag
	 *            {@link String}
	 */
	private void createDummyXML(String filePath, String tag) {

		try {
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			Document doc = docBuilder.newDocument();
			Element rootElement = doc.createElement(tag);
			doc.appendChild(rootElement);
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(Paths.get(filePath).toFile());
			transformer.transform(source, result);
			logger.info("Dummy XML created for TAG { " + tag + " } ");

		} catch (Exception e) {
			logger.error("Error: {Com-0011} dummy XML creation problem {} ", e);
		}

	}

	/***
	 * This method will load all reports by default
	 * 
	 * @param path
	 *            {@link String}
	 * @return report {@link Reports}
	 */
	public Reports loadRerorts(String path) {
		Reports reports = null;
		try {
			File newFile = Paths.get(path).toFile();

			if (!newFile.exists()) {
				createDummyXML(newFile.getPath(), "reports");
			} else {
				int countTag = countXMLTag(newFile, "report");
				if (countTag > 500) {
					backUpReports(countTag, newFile);
					createDummyXML(newFile.getPath(), "reports");
				}
			}
			reports = convertXMLToReportsObj(newFile);
			logger.info("Reports data loaded sucessfully...");

		} catch (Exception e) {
			logger.error("Error: {Com-0012} reports loading problem {} ", e);
		}
		return reports;
	}

	/***
	 * This method will load reports when invoke from UI
	 * 
	 * @param reportsName
	 *            {@link String}
	 * @return reports {@link Reports}
	 */
	public Reports loadReportsFromURL(String reportsName) {
		Reports reports = null;
		try {
			String folder = env.getProperty("reports.path").split("/")[0];
			File newFile = Paths.get(folder + "/" + reportsName).toFile();
			reports = convertXMLToReportsObj(newFile);
			logger.info("Reports data loaded sucessfully...");

		} catch (Exception e) {
			logger.error("Error: {Com-0013} loading reports problem {} ", e);
		}
		return reports;
	}

	/***
	 * This method will register user with userdto
	 * 
	 * @param userDto
	 *            {@link UserDto}
	 */
	public void createRegisterUsers(UserDto userDto) {
		userDto.setId("" + System.currentTimeMillis());
		createUsers(userDto);
	}

	/****
	 * After taking input this method will create users which will save in db and
	 * shown onto UI
	 * 
	 * @param userDto
	 *            {@link UserDto}
	 */
	public void createUsers(UserDto userDto) {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setNamespaceAware(false);
			factory.setValidating(false);
			DocumentBuilder builder = factory.newDocumentBuilder();
			final String userPath = env.getProperty("db.user");
			File file = Paths.get(userPath).toFile(); // XML file to read
			if (!file.exists()) {
				createDummyUser(userPath);
			}
			Document document = builder.parse(file);
			Element users = document.getDocumentElement();
			Element user = document.createElement("user");
			user.setAttribute("id", userDto.getId());

			Element email = document.createElement("email");
			Text emailText = document.createTextNode(userDto.getEmail().trim());
			email.appendChild(emailText);
			user.appendChild(email);
			//
			Element username = document.createElement("username");
			Text usernameText = document.createTextNode(userDto.getUsername().trim());
			username.appendChild(usernameText);
			user.appendChild(username);
			//
			Element userpass = document.createElement("userpass");
			Text userpassText = document.createTextNode(userDto.getUserpass().trim());
			userpass.appendChild(userpassText);
			user.appendChild(userpass);

			Element status = document.createElement("active");
			Text statusText = document.createTextNode("" + userDto.isActive());
			status.appendChild(statusText);
			user.appendChild(status);

			Element lastlogin = document.createElement("createdate");
			Text lastloginText = document.createTextNode(userDto.getCreateDate());
			lastlogin.appendChild(lastloginText);
			user.appendChild(lastlogin);

			users.appendChild(user);// Parent

			/*
			 * TransformerFactory tfact = TransformerFactory.newInstance(); Transformer
			 * tform = tfact.newTransformer(); tform.setOutputProperty(OutputKeys.INDENT,
			 * "yes"); tform.setOutputProperty("{http://xml.apache.org/xslt}indent-amount",
			 * "3"); tform.transform(new DOMSource(document), new StreamResult(file));
			 */
			transformDoc(document, file);
			logger.info("User saved successfully...");

			//
		} catch (Exception e) {
			logger.error("Error: {Com-0014} users creation problem {} ", e);

		}
	}

	/***
	 * 
	 * @param document
	 *            {@link Document}
	 * @param file
	 *            {@link File}
	 * @throws TransformerException
	 */
	private void transformDoc(Document document, File file) throws TransformerException {
		TransformerFactory tfact = TransformerFactory.newInstance();
		Transformer tform = tfact.newTransformer();
		tform.setOutputProperty(OutputKeys.INDENT, "yes");
		tform.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "3");
		tform.transform(new DOMSource(document), new StreamResult(file));
	}

	/****
	 * This will create a dummy reports at first time, if reports not available
	 * 
	 * @param filePath
	 *            {@link String}
	 */
	private void createDummyUser(String filePath) {

		try {
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			Document doc = docBuilder.newDocument();
			Element rootElement = doc.createElement("users");
			doc.appendChild(rootElement);
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(Paths.get(filePath).toFile());
			transformer.transform(source, result);
			logger.info("Dummy users created successfully...");

		} catch (Exception e) {
			logger.error("Error: {Com-00015} dummy user creation problem {} ", e);
		}
	}

	/***
	 * Find the no of Tag in XML
	 * 
	 * @param filepath
	 *            {@link String}
	 * @param elementName
	 *            {@link String}
	 * @return {@link Integer}
	 */
	public int countXMLTag(File filepath, String elementName) {
		try {
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			Document doc = docBuilder.parse(filepath);
			NodeList list = doc.getElementsByTagName(elementName);
			return list.getLength();
		} catch (Exception e) {
			logger.error("Error: {Com-0016} countXMLTag XML {} ", e);
			return -1;
		}
	}

	/***
	 * This method is loading reports
	 * 
	 * @param newFile
	 *            {@link File}
	 * @return Reports {@link Reports}
	 * @throws JAXBException
	 */
	private Reports convertXMLToReportsObj(File newFile) throws JAXBException {
		JAXBContext jaxbContext = JAXBContext.newInstance(Reports.class);
		Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
		return (Reports) unmarshaller.unmarshal(newFile);
	}

	/***
	 * 
	 * @param countTag
	 *            {@link Integer}
	 * @param oldFile
	 *            {@link File}
	 */
	private void backUpReports(int countTag, File oldFile) {
		try {
			String folder = env.getProperty("reports.path").split("/")[0];
			final String strNewFile = folder + "/reports_" + AppUtil.currentTime() + ".db";
			File nowFile = Paths.get(strNewFile).toFile();
			if (oldFile.renameTo(nowFile)) {
				logger.info("Reports DB backup successfully");
			} else {
				logger.error("Sorry! the Reports DB can't be backup");
			}

		} catch (Exception e) {
			logger.error("Error: {Com-0013} backUpReports {}  ", e);
		}
	}

	/***
	 * This will write remote file
	 * 
	 * @param hostname
	 *            {@link String}
	 */
	public void writeRemoteFile(String hostname) {
		String newData = HTMLTemplate.getStartTemplate(hostname, env.getProperty("server.port"));
		final String filePath = env.getProperty("remote.start");
		if (!Paths.get(filePath).toFile().exists()) {
			writeFile(newData, filePath);
		}
	}

	/***
	 * This method will give application information
	 * 
	 * @return list {@link List}
	 */
	public List<StatusInfoDto> getServerStatus() {
		List<StatusInfoDto> dogList = new ArrayList<>();

		try {
			InetAddress ipAddr = InetAddress.getLocalHost();
			StatusInfoDto dog = new StatusInfoDto();
			dog.setServerStatus("Server Status");
			dog.setHostAddress("Host Address");
			dog.setHostName("Host Name");
			dog.setCononicalHostName("Canonical Host Name");
			dog.setUserName("User Name");
			dog.setPid("Process ID");
			dog.setVersionId("Version");
			dog.setStartedTime("Started Time");

			dogList.add(dog);

			StatusInfoDto dog1 = new StatusInfoDto();
			dog1.setServerStatus("Running");
			dog1.setHostAddress(ipAddr.getHostAddress());
			dog1.setHostName(ipAddr.getHostName());
			dog1.setCononicalHostName(ipAddr.getCanonicalHostName());
			String username = System.getProperty("user.name");
			if (username != null) {
				dog1.setUserName(username);
			} else {
				dog1.setUserName(AppUtil.EMPTY_STR);

			}
			propertiesConfig.ipAddress = " { " + username + " } " + ipAddr.getCanonicalHostName();

			String pid = ManagementFactory.getRuntimeMXBean().getName().split(AppUtil.FILE_SEPARTOR)[0];
			dog1.setPid(pid);
			dog1.setVersionId(propertiesConfig.versionId);
			dog1.setPort(env.getProperty("server.port"));
			dog1.setStartedTime(AppUtil.STARTED_TIME);

			dogList.add(dog1);

			writeStatus(dog1);
			writeRemoteFile(dog1.getHostAddress());

		} catch (Exception e) {
			return null;
		}

		return dogList;
	}

	/***
	 * Status XML will generate for see the running status of application
	 * 
	 * @param dogInfo
	 *            {@link StatusInfoDto}
	 */
	private void writeStatus(StatusInfoDto dogInfo) {

		try {
			File file = Paths.get("status.db").toFile();
			if (!file.exists()) {
				xmlUtilService.convertObjectToXML(dogInfo, "status.db");
			}

		} catch (FileNotFoundException | JAXBException e) {
		}
	}

	/***
	 * delete status
	 */
	public void deleteStatus() {

		try {
			File file = Paths.get("status.db").toFile();
			if (file.exists()) {
				FileUtils.deleteQuietly(file);
			}
		} catch (Exception e) {
		}
	}

	/**
	 * 
	 * @param str
	 *            {@link String}
	 * @param splitType
	 *            {@link String}
	 * @return splitstr
	 */
	public List<String> split(String str, String splitType) {
		return Stream.of(str.split(splitType)).map(elem -> new String(elem)).collect(Collectors.toList());
	}

	/**
	 * This code using Java ProcessBuilder class to run a batch file Java
	 * ProcessBuilder and BufferedReader classes are used to implement this.
	 * 
	 * @param batchFilePath
	 *            {@link String}
	 * @param commandArgs
	 *            {@link String}
	 * @param filename
	 *            {@link String}
	 * @return ResponseDto {@link ResponseDto}
	 */
	public synchronized ResponseDto runCommandLineBatch(String batchFilePath, String commandArgs, String filename) {
		// final String batchFilePath = propertiesConfig.batchFilePath;
		ResponseDto resDto = new ResponseDto();

		logger.info("Invoking Command Line Batch  [ " + batchFilePath + " ]");

		boolean processComplete = false;
		ProcessBuilder processBuilder = null;
		if (commandArgs == null) {
			processBuilder = new ProcessBuilder(batchFilePath);
			logger.info("Batch Invoking with no Args {EMPTY Args}");
		} else {
			logger.info("Batch Invoking with Following args  ");
			String[] splitArgs = commandArgs.split("\\&");
			logger.info("API : " + splitArgs[0]);
			logger.info("Input Folder : " + splitArgs[1]);
			logger.info("Output Folder : " + splitArgs[2]);
			resDto.setResponseFilePath(splitArgs[2]);
			// Process builder will take these arguments and will fire to batch file.
			processBuilder = new ProcessBuilder(batchFilePath, splitArgs[0], splitArgs[1], splitArgs[2]);
		}
		processBuilder.redirectErrorStream(true);
		try {
			final Process process = processBuilder.start();
			final InputStream is = process.getInputStream();
			// the background thread watches the output from the process
			new Thread(new Runnable() {
				public void run() {
					try (InputStreamReader input = new InputStreamReader(is);
							BufferedReader reader = new BufferedReader(input)) {
						String line;
						StringBuilder sb = new StringBuilder();
						while ((line = reader.readLine()) != null) {
							sb.append(line).append("\n");
						}
						if (AppUtil.TRUE_STR.equalsIgnoreCase(dataLoader.configDto.getEnableResponseCodeLog())) {
							logger.info("Respone code - " + sb.toString());
						}
						if (findExceptionFromString(sb.toString())) {
							writelogFile(sb.toString(), filename);
							resDto.setResponseError(true);
							resDto.setResponseErrorStr(sb.toString());
						}
						logger.info("Read all input data as response.");
					} catch (IOException e) {
						resDto.setResponseError(true);
						logger.error(
								"Error {Batch-00033} : ProcessBuilder: Exception occured during : When Invoking Batch File "
										+ e.getMessage());
					} finally {
						try {
							is.close();
						} catch (IOException e) {
							logger.error(
									"Error {Batch-00044} : Input stream closing problem : When Invoking Batch File "
											+ e.getMessage());

						}
					}
				}
			}).start();
			// Wait to get exit value
			// the outer thread waits for the process to finish

			try {
				processComplete = process.waitFor(1, TimeUnit.MINUTES);
			} catch (Exception e) {
				logger.error("Forcely Interrupted {} ", e);
				resDto.setCommandRun(false);
				process.destroyForcibly();
			}

			logger.info("Process Builder result code : [ " + processComplete + " ]");
			if (!processComplete) {
				//
				String msg = "Error: CIS Host server is not responding !!! \nTMS adapter is down...Timeout occured!!!";
				logger.error(msg);
				writelogFile(msg, "CISAgent_" + AppUtil.currentTime());
				process.destroyForcibly();
				//
			} else {
				logger.info("Process Done!!! ");
				resDto.setCommandRun(true);
			}
		} catch (Exception e) {
			String msg2 = "Error: Process Builder result code: [" + processComplete + " ] " + e.getMessage();
			logger.error(msg2);
			writelogFile(msg2, "CommandLine_" + AppUtil.currentTime());
			resDto.setCommandRun(false);
		}
		return resDto;
	}

	/***
	 * This method will write the file in logs directory with given content in
	 * string
	 * 
	 * @param data
	 *            {@link String}
	 * @param filename
	 *            {@link String}
	 */
	public void writelogFile(String data, String filename) {

		final String filePath = env.getProperty("logs.dir") + File.separator + "Error_"
				+ FilenameUtils.getBaseName(filename) + ".log";
		logger.info("Writing file in error folder with name [ " + filePath + "]");
		writeFile(data, filePath);
	}

	/***
	 * Write file
	 * 
	 * @param data
	 *            {@link String}
	 * @param filePath
	 *            {@link String}
	 */
	public void writeFile(String data, String filePath) {
		Path filepath = Paths.get(filePath);
		byte[] bytes = data.getBytes();
		try (OutputStream out = Files.newOutputStream(filepath)) {
			out.write(bytes);
		} catch (Exception e) {
			logger.error("Error: {Com-0015} during writing file {}  ", e);

		}
	}

	/**
	 * method will check the response xml in output folder.
	 * 
	 * @param responeFile
	 *            {@link String}
	 * @return {@link Boolean}
	 */
	public boolean checkFile(String responeFile) {
		File resFile = Paths.get(responeFile).toFile();
		if (!resFile.exists()) {
			logger.info("Response XML file [ " + resFile.getName() + " ] not found in directory [ "
					+ dataLoader.configDto.getOutputFolderPath() + " ]");

			return false;

		}
		return true;
	}

	/**
	 * 
	 * @param commonArgs
	 *            {@link String}
	 * @return {@link String}
	 */
	public String getMins(String commonArgs) {
		long l = (AppUtil.MINS * Long.parseLong(commonArgs)) / 60;
		return String.valueOf(l);
	}

	/***********************************************/

	
}
