package com.hcl.dog.controller;

import java.awt.Desktop;
import java.io.File;
import java.nio.file.Paths;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.hcl.WatchDogSolution;
/**
 * 
 * @author intakhabalam.s@hcl.com
 * @see {@link ApplicationContextAware}
 * @see {@link RestController}
 * Enable shutdown controller to allow shutdown application.
 */
@RestController
public class ShutdownController implements ApplicationContextAware {

	private ApplicationContext context;
	@Autowired
	private Environment env;
	
	@GetMapping("/shutdownContext")
	public void shutdownContext() {

		closeBanner();
		((ConfigurableApplicationContext) context).close();
	}
	/***
	 * 
	 * @return {@link String}
	 */
	@RequestMapping("/restartContext")
	@ResponseBody
	public String satrtContext() {

		ConfigurableApplicationContext ctx=(ConfigurableApplicationContext) context;
		 Thread restartThread = new Thread(() -> {
		        try {
		            Thread.sleep(2000);
		            WatchDogSolution.restart(ctx);
		        } catch (InterruptedException ignored) {
		        }
		    });
		    restartThread.setDaemon(false);
		    restartThread.start();
		 return "OK";   
	}
	

	 /***
     * 	
     */
	public static void startBanner(){
		try {
			File htmlFile = Paths.get("start.html").toFile();
			if (htmlFile.exists()) {
				Desktop.getDesktop().browse(htmlFile.toURI());
				
			}
		} catch (Exception e) {
		}
	}
	
	/***
	 * 
	 */
	private void closeBanner() {
		try {

			File htmlFile = Paths.get(env.getProperty("error.info")).toFile();
			if (htmlFile.exists()) {
				Desktop.getDesktop().browse(htmlFile.toURI());
			}
			final String filePath = env.getProperty("remote.start");
			File filepath = Paths.get(filePath).toFile();
			if (filepath.exists()) {
				FileUtils.deleteQuietly(filepath);
			}
		} catch (Exception e) {
		}
	}

	@Override
	public void setApplicationContext(ApplicationContext ctx) throws BeansException {
		this.context = ctx;
	}
}

