package com.hcl;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
/***
 * @author intakhabalam.s@hcl.com
 * @see {@link ServletInitializer}
 * @see {@link SpringBootServletInitializer}
 * @see {@link WatchDogSolution}
 */
public class ServletInitializer extends SpringBootServletInitializer {
   /***
    *@see SpringApplicationBuilder
    *@param application {@link SpringApplicationBuilder}
    */
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(WatchDogSolution.class);
	}
}
