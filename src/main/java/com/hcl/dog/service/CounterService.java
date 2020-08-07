package com.hcl.dog.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Paths;

import javax.xml.bind.JAXBException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.hcl.dog.domain.Counter;
/**
 * @author intakhabalam.s@hcl.com
 * This is service class which will give auto number
 * @see Environment {@link Environment}
 * @see XMLUtilService {@link XMLUtilService}
 */
@Service
public class CounterService {

	private final Logger logger = LogManager.getLogger("counter-serv");
	@Autowired
	private Environment env;
	@Autowired
	private XMLUtilService xmlUtilService;
	
	/***
	 * This method will return the counter object from xml
	 * @param counter {@link Counter}
	 * @return counter {@link Counter}
	 * @throws JAXBException {@link JAXBException}
	 * @throws FileNotFoundException {@link FileNotFoundException}
	 */
	public Counter counterObjectToXml(Counter counter) throws JAXBException, FileNotFoundException {
		String fileName=env.getProperty("db.counter");
		
		return (Counter) xmlUtilService.convertObjectToXML(counter,fileName);

	}

	/***
	 * XML count 
	 * @return counter {@link Counter}
	 * @throws JAXBException {@link JAXBException}
	 * @throws FileNotFoundException @link {@link FileNotFoundException}
	 */
	public Counter xmlToCounterObject() throws JAXBException, FileNotFoundException {
		File file = Paths.get(env.getProperty("db.counter")).toFile();
		return xmlUtilService.convertXMLToObject(Counter.class, file);
		
	}

	/***
	 * Here is logic for auto counter creation.
	 * @return {@link String}
	 */
	public String getCounter() {
		String sb="99999";
		try {
			Counter count=xmlToCounterObject();
			int num=Integer.parseInt(count.getAutonumber())+1;
			sb = String.format(env.getProperty("auto.limit"), num);
			counterObjectToXml(new Counter(sb));
			
		} catch (FileNotFoundException | JAXBException e) {
			logger.error("Error: {Counter:0001 } finding auto number problem  ",e);
		}
		logger.info("Returning auto number [  "+sb+ "]  ");
		return sb;
		
	}
	
}

