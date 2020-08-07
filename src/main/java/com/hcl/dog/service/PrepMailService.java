package com.hcl.dog.service;

import java.io.File;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.hcl.dog.common.AppUtil;
import com.hcl.dog.common.EmailTemplate;
import com.hcl.dog.component.DataLoaderComponent;
import com.hcl.dog.config.PropertiesConfig;
import com.hcl.dog.dto.APIDto;
import com.hcl.dog.dto.ErrorDto;
import com.hcl.dog.dto.MailDto;
/***
 * Common Mail Services for watchdog  
 * @author intakhabalam.s@hcl.com
 * Provide the supports of mail
 * @see Service 
 * @see Environment
 * @see PropertiesConfig {@link PropertiesConfig}
 * @see EmailService {@link EmailService}
 */
@Service
public class PrepMailService{

	
	private final Logger logger = LogManager.getLogger("prepmail-serv");
	@Autowired
	Environment env;
	@Autowired
	PropertiesConfig propertiesConfig;
	@Autowired
	DataLoaderComponent dataLoader;
	
	@Autowired
	EmailService emailService;
	/**
	 * @param mailDto {@link MailDto}
	 * @throws Exception {@link Exception}
	 */
	public void sendEmailTemplate(MailDto mailDto) throws Exception {

		EmailTemplate template = new EmailTemplate("email-template.html");

		Map<String, String> replacements = new HashMap<String, String>();
		replacements.put("body", mailDto.getMessage());
		String tdy=String.valueOf(new Date())+"-"+propertiesConfig.ipAddress;
		replacements.put("today", tdy);
		String message = template.getTemplate(replacements);
		//MailDto mailDto = new MailDto(from, to, subject, message,fileURL);
		mailDto.setMessage(message);
		mailDto.setHtml(true);
		emailService.send(mailDto);
	}
	
	
	/***
	 * This method will prepare the APIS mail
	 * @param apiDtoList {@link List}
	 */
	public void prepareAndSendAPIsMails(List<APIDto> apiDtoList) {
		StringBuilder sb = new StringBuilder(AppUtil.EMPTY_STR);
		String type=AppUtil.EMPTY_STR;
		String oldType=AppUtil.EMPTY_STR;
		String mailDesc=AppUtil.EMPTY_STR;
		String mesgHeader=AppUtil.EMPTY_STR;
		
		sb.append(
				"<table border='1' cellpadding='10' style='border: 1px solid #000080;' width='100%'><tr bgcolor='#A9A9A9'>"
				+ "<td> API NAME </td><td>  DESCRIPTION </td> <td> PROCESS </td> <td> STATUS </td></tr>");
		for (APIDto dto : apiDtoList) {
			String s = dto.getFileName() != null ? "<span style='color:green'> Process </span>" : "<span style='color:red'>Not Process</span>";
			//boolean status = dto.isStatus();// Drop 
			// file and status both fail to go next
			
			/*String statusStr = "<span style='color:red;font-weight:bold;'><strong>Failed</strong></span>";
			if (status) {
				statusStr = "<span style='color:green;font-weight:bold'>Passed</span>";
			}*/
			String statusStr = dto.getFileName() != null ? "<span style='color:green'> Passed </span>" : "<span style='color:red'>Failed</span>";
			type=dto.getGroupType();
			
			if(type==null) {
				type=oldType;
			}else {
				oldType=dto.getGroupType();
			}
			mailDesc=dto.getMailDescription();
			if(mailDesc==null) {
				mailDesc="Above APIs failed to run, It is also failed to run";
			}
			mesgHeader=dto.getMessageHeader();
			if(mesgHeader==null) {
				mesgHeader="-";
			}
			String toMailContent = "<tr><td>" + mesgHeader + "</td><td>" + mailDesc + "</td><td>"+s+"</td><td>" + statusStr + "</td></tr>";
			// logger.info(toMailContent);
			sb.append(toMailContent);
		}
		sb.append("</table>");
   	    sendAPIsMail(sb.toString(), type);
		
	}
	/***
	 * This method will send the Email
	 * @param inputFile {@link File}
	 * @param contentXML {@link String}
	 * @param type {@link String}
	 */
	public void sendMail(File inputFile, String contentXML, String type) {
		
	    contentXML = contentXML.replaceAll("<","&lt;").replaceAll(">","&gt;").replaceAll("\r", "<br>");

		if (!dataLoader.configDto.getEnableMail()) {
			return;
		}
		logger.info("Sending email {}...");
		String msgBody = AppUtil.EMPTY_STR;
		if ("1".equals(type)) {
			msgBody = env.getProperty("mail.response.body.error1");
		}
		if ("2".equals(type)) {
			MessageFormat msgFormat = new MessageFormat(env.getProperty("mail.response.body.error2"));
			msgBody = msgFormat.format(new Object[] { contentXML });
		}
		if ("3".equals(type)) {
			MessageFormat msgFormat = new MessageFormat(env.getProperty("mail.response.body.error3"));
			msgBody = msgFormat.format(new Object[] { contentXML });
		}
		if ("4".equals(type)) {
			MessageFormat msgFormat = new MessageFormat(env.getProperty("mail.response.body.error4"));
			msgBody = msgFormat.format(new Object[] { contentXML });
		}


		final String[] toMail = dataLoader.configDto.getToWhomEmail().split(AppUtil.COMMA_SEPERATOR);
		final String sub = env.getProperty("mail.response.body.sub").concat("[ " + inputFile.getName() + " ]");
		try {

			MailDto mailDto = new MailDto(dataLoader.configDto.getFromMail(), 
					dataLoader.configDto.getToWhomEmail(), sub, msgBody,inputFile);
			
			sendEmailTemplate(mailDto);
			
			
			logger.info("Email sent to  " + Arrays.toString(toMail));

		} catch (Exception e) {
			logger.error("Error: {Com-0004} Problem in sending email {} " + e.getMessage());

		}
	}
	
	/***
	 * @param contentXML {@link String}
	 * @param type {@link String}
	 */
	private void sendAPIsMail(String contentXML,String type) {
		if (!dataLoader.configDto.getEnableMail()) {
			return;
		}
		logger.info("Sending email {}...");
		String msgBody = AppUtil.EMPTY_STR;
		String sub = AppUtil.EMPTY_STR; 	

		MessageFormat msgFormat = new MessageFormat(env.getProperty("mail.api.body"));
		msgBody = msgFormat.format(new Object[] { type, contentXML });
		
		MessageFormat msgFormat2 = new MessageFormat(env.getProperty("mail.api.sub"));
		sub = msgFormat2.format(new Object[] { type });

		try {
			
			MailDto mailDto = new MailDto(dataLoader.configDto.getFromMail(), 
					dataLoader.configDto.getToWhomEmail(),sub , msgBody);
		
			sendEmailTemplate(mailDto);
			
			logger.info("Email sent to  " + dataLoader.configDto.getToWhomEmail());

		} catch (Exception e) {
			logger.error("Error: {Com-0005} Email sending problem  {} " + e.getMessage());

		}
	}


	public void sendXMail(File inputFile, List<ErrorDto> errorList) {

		if (!dataLoader.configDto.getEnableMail()) {
			return;
		}
		
		
		String msgBdy=createBody(errorList);
		
		MessageFormat msgFormat = new MessageFormat(env.getProperty("mail.response.body.error2"));
		String msgBody = msgFormat.format(new Object[] { msgBdy }); //ned to write


		final String[] toMail = dataLoader.configDto.getToWhomEmail().split(AppUtil.COMMA_SEPERATOR);
		
		final String sub = env.getProperty("mail.response.body.sub").concat("[ " + inputFile.getName() + " ]");
		
		try {

			MailDto mailDto = new MailDto(dataLoader.configDto.getFromMail(), 
					dataLoader.configDto.getToWhomEmail(), sub, msgBody,inputFile);
			
			sendEmailTemplate(mailDto);
			
			
			logger.info("Email sent to  " + Arrays.toString(toMail));

		} catch (Exception e) {
			logger.error("Error: {Com-0004} Problem in sending email {} " + e.getMessage());

		}
	
		
	}


	private String createBody(List<ErrorDto> errorList) {

        StringBuilder sb=new StringBuilder("");
		sb.append(
				"<table border='1' cellpadding='10' style='border: 1px solid #000080;' width='100%'><tr bgcolor='#A9A9A9'>"
				+ "<td> API Run </td><td>  Severity </td> <td> Error Code </td> <td> User Message </td> <td> System Message </td></tr>");
		for (ErrorDto dto : errorList) {

	     String toMailContent = "<tr>"
						     		+ "<td>" + dto.getEntityType() + "</td>"
						     		+ "<td>" + dto.getServerity() + "</td>"
						     		+ "<td>"+dto.getErrorCode()+"</td>"
						     		+ "<td width='40%'>" + dto.getUserMessage() + "</td>"
							   		+ "<td width='40%'>" + dto.getSystemMessage() + "</td>"

	     				   + "</tr>";
			sb.append(toMailContent);
		}
		sb.append("</table>");
		
		return sb.toString();
	}
	
  }
