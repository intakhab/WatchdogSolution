package com.hcl.dog.common;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

import com.hcl.dog.service.PrepMailService;

/**
 * @author intakhabalam.s@hcl.com
 * @see PrepMailService
 *
 */
public class EmailTemplate {

	private String templateId;

	private String template;

	private Map<String, String> replacementParams;
	
    /***
     * Parameterize construction
     * @param templateId {@link String}
     * return {@link EmailTemplate}
     */
	public EmailTemplate(String templateId) {
		this.templateId = templateId;
		try {
			this.template = loadTemplate(templateId);
		} catch (Exception e) {
			this.template = AppUtil.EMPTY_STR;
		}
	}
	
	
    /***
     * @see EmailTemplate
     * @param templateId
     * @return
     * @throws Exception
     */
	private String loadTemplate(String templateId) throws Exception {
		File file = Paths.get("templates/" + templateId).toFile();
		String content = AppUtil.EMPTY_STR;
		try {
			content = new String(Files.readAllBytes(file.toPath()));
		} catch (IOException e) {
			throw new Exception("Could not read template with ID = " + templateId);
		}
		return content;
	}
    /***
     * @param replacements {@link Map}
     * @return {@link String}
     */
	public String getTemplate(Map<String, String> replacements) {
		String cTemplate = this.getTemplate();

		if (!cTemplate.isEmpty()) {
			for (Map.Entry<String, String> entry : replacements.entrySet()) {
				cTemplate = cTemplate.replace("{{" + entry.getKey() + "}}", entry.getValue());
			}
		}
		
		return cTemplate;
	}

	/**
	 * @return the templateId
	 */
	public String getTemplateId() {
		return templateId;
	}

	/**
	 * @param templateId
	 *            the templateId to set
	 */
	public void setTemplateId(String templateId) {
		this.templateId = templateId;
	}

	/**
	 * @return the template
	 */
	public String getTemplate() {
		return template;
	}

	/**
	 * @param template
	 *            the template to set
	 */
	public void setTemplate(String template) {
		this.template = template;
	}

	/**
	 * @return the replacementParams
	 */
	public Map<String, String> getReplacementParams() {
		return replacementParams;
	}

	/**
	 * @param replacementParams
	 *            the replacementParams to set
	 */
	public void setReplacementParams(Map<String, String> replacementParams) {
		this.replacementParams = replacementParams;
	}

}
