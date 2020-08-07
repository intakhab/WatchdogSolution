package com.hcl.dog.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
/***
 * 
 * @author intakhabalam.s@hcl.com
 * @see {@link Configuration}
 * @see {@link Bean}
 * @see {@link TerminateBean}
 */
@Configuration
public class ShutdownConfig {
   /***
    * @return {@link TerminateBean}
    */
    @Bean
    public TerminateBean getTerminateBean() {
        return new TerminateBean();
    }
}
