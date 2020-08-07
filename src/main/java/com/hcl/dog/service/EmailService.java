package com.hcl.dog.service;

import java.io.UnsupportedEncodingException;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.hcl.dog.common.AppUtil;
import com.hcl.dog.component.DataLoaderComponent;
import com.hcl.dog.dto.MailDto;
/***
 * 
 * @author intakhabalam.s@hcl.com
 * Common Mail Service use this class for mail sending  
 * @see Service
 * @see DataLoaderComponent  {@link DataLoaderComponent}
 */
@Service
public class EmailService {

	private final Logger logger = LogManager.getLogger("email-serv");
	//private JavaMailSender mailSender;
	
	@Autowired
	DataLoaderComponent dataLoaderComponent;

	@PostConstruct
	public JavaMailSender getJavaMailSender() {
		
		JavaMailSenderImpl  mailSender = new JavaMailSenderImpl();
          if(dataLoaderComponent.configDto==null) return mailSender;
          
			mailSender.setHost(dataLoaderComponent.configDto.getHost());
			mailSender.setPort(dataLoaderComponent.configDto.getPort());
			mailSender.setUsername(dataLoaderComponent.configDto.getMailUserName());
			mailSender.setPassword(dataLoaderComponent.configDto.getMailPassword());
			Properties props = mailSender.getJavaMailProperties();
			props.put("mail.transport.protocol", "smtp");
			props.put("mail.smtp.auth", "false");
			props.put("mail.smtp.starttls.enable", "true");
			props.put("mail.debug", dataLoaderComponent.configDto.isDebugMail());
			//props.put("mail.smtp.timeout", "1000");    
			//props.put("mail.smtp.connectiontimeout", "1000");    

		return mailSender;
	}
    /**
     * 
     * @param eParams {@link MailDto}
     */
	public void send(MailDto eParams) {

		if (eParams.isHtml()) {
			try {
				sendHtmlMail(eParams);
			} catch (MessagingException e) {
				logger.error("Could not send email to : {} Error1 = {}", eParams.getToAsList(), e.getMessage());
			} catch (UnsupportedEncodingException e) {
				logger.error("Could not send email to : {} Error2 = {}", eParams.getToAsList(), e.getMessage());

			}
		} else {
			sendPlainTextMail(eParams);
		}

	}
    /****
     */
	private void sendHtmlMail(MailDto eParams) throws MessagingException, UnsupportedEncodingException {

		boolean isHtml = true;

		MimeMessage message = getJavaMailSender().createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message, true);
		helper.setTo(eParams.getTo().toArray(new String[eParams.getTo().size()]));
		helper.setReplyTo(eParams.getFrom());
		helper.setPriority(1);
		helper.setFrom(eParams.getFrom(),AppUtil.WATCH_DOG_GREET_MSG);
		helper.setSubject(eParams.getSubject());

		helper.setText(eParams.getMessage(), isHtml);
		if (eParams.getCc().size() > 0) {
			helper.setCc(eParams.getCc().toArray(new String[eParams.getCc().size()]));
		}
		if (eParams.getFileURL() != null && eParams.getFileURL().exists()) {
			helper.addAttachment(eParams.getFileURL().getName(), eParams.getFileURL());
		}
		getJavaMailSender().send(message);
	}

	
	/***
	 * 
	 * @param eParams
	 */
	private void sendPlainTextMail(MailDto eParams) {

		SimpleMailMessage mailMessage = new SimpleMailMessage();

		eParams.getTo().toArray(new String[eParams.getTo().size()]);
		mailMessage.setTo(eParams.getTo().toArray(new String[eParams.getTo().size()]));
		mailMessage.setReplyTo(eParams.getFrom());
		mailMessage.setFrom(eParams.getFrom());
		mailMessage.setSubject(eParams.getSubject());
		mailMessage.setText(eParams.getMessage());

		if (eParams.getCc().size() > 0) {
			mailMessage.setCc(eParams.getCc().toArray(new String[eParams.getCc().size()]));
		}

		getJavaMailSender().send(mailMessage);

	}
	
	

}
