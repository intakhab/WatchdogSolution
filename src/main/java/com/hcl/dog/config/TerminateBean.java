package com.hcl.dog.config;


import javax.annotation.PreDestroy;
/**
 * 
 * @author intakhabalam.s@hcl.com
 * 
 */
public class TerminateBean {
   /**
    * @see PreDestroy
    * @throws {@link Exception}
    */
    @PreDestroy
    public void onDestroy() throws Exception {
        
    }
}