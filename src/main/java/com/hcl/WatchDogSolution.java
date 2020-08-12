package com.hcl;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.ApplicationPidFileWriter;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.bind.annotation.ControllerAdvice;

import com.hcl.dog.common.HTMLTemplate;

/***
 * 
 * @author intakhabalam.s@hcl.com Main Application Entry point
 * @see java.lang.Object
 * @See {@link SpringApplication}
 * @see {@link EnableScheduling}
 * @see {@link ControllerAdvice}
 *
 */
@SpringBootApplication
@EnableScheduling
@ComponentScan(basePackages = "com.hcl")
@ControllerAdvice("com.hcl.dog.controller")
public class WatchDogSolution {

	private static String[] args;

	/***
	 * 
	 * @param args as string array
	 * @see SpringApplication
	 * @see ApplicationPidFileWriter
	 * @see ConfigurableApplicationContext
	 */
	public static void main(String[] args) {

		WatchDogSolution.args = args;
		ConfigurableApplicationContext application = SpringApplication.run(WatchDogSolution.class, args);
		application.addApplicationListener(new ApplicationPidFileWriter());
	}

	/***
	 * Re-Starting the application
	 * 
	 * @param ctx as ConfigurableApplicationContext
	 * @see SpringApplication
	 */
	public static void restart(ConfigurableApplicationContext ctx) {
		// close previous context
		ctx.close();
		// and build new one
		SpringApplication.run(WatchDogSolution.class, args);
	}

	@Bean
	public void invokeBanner() {
		HTMLTemplate.startBanner();
	}
}
