package com.hcl.dog.common;

/***
 * 
 * @author intakhabalam.s@hcl.com
 * @see Exception 
 */
public class WatchDogException extends Exception {
	private static final long serialVersionUID = 1L;
    /**
     * 
     * @param message {@link String}
     */
	public WatchDogException(String message) {
		super(message);
	}

}
