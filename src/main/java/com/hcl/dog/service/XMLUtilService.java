package com.hcl.dog.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.hcl.dog.common.AppUtil;
import com.hcl.dog.common.WatchDogException;
import com.hcl.dog.domain.PlanId;
import com.hcl.dog.domain.User;

/***
 * @author intakhabalam.s@hcl.com
 * provide the input service of XML manipulation
 * @see Environment {@link Environment}
 */
@Service
public class XMLUtilService {
	
	private final Logger logger = LogManager.getLogger("xmlutil-serv");
	@Autowired
	private Environment env;

	/**
	 * This method will convert given object/class into XML 
	 * @param object {@link Object}
	 * @return string {@link String}
	 */
	public String convertObjectToXML(Object object) {
		try {
			StringWriter stringWriter = new StringWriter();
			JAXBContext context = JAXBContext.newInstance(object.getClass());
			Marshaller marshaller = context.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			marshaller.marshal(object, stringWriter);
			return stringWriter.toString();
		} catch (JAXBException e) {
			logger.error("Error {X-1010}: marshalling xml ", e.getMessage());
		}
		return null;
	}
	/***
	 * 
	 * This method will convert XML to Object from given input file
	 * @param clazz  {@link Class}
	 * @param file  {@link File}
	 * @return class
	 * @throws JAXBException  {@link JAXBException}
	 * @throws FileNotFoundException {@link FileNotFoundException}
	 */
	public <T> T convertXMLToObject(Class<?> clazz, File file) throws JAXBException, FileNotFoundException {
		try {
			JAXBContext context = JAXBContext.newInstance(clazz);
			Unmarshaller um = context.createUnmarshaller();
			@SuppressWarnings("unchecked")
			T unmarshal = (T) um.unmarshal(file);
			logger.info("Data load sucessfully { "+file.getName()+" }");
			return unmarshal;
		} catch (JAXBException je) {
			logger.error("Error {X-1011}: Interpreting XML response {} ", je.getMessage());

		}
		return null;
	}

	/***
	 * 
	 * Method will convert Object to XML with class and save in disk
	 * @param classz {@link Object}
	 * @param fileName {@link String}
	 * @return object {@link Object}
	 * @throws JAXBException  {@link JAXBException}
	 * @throws FileNotFoundException  {@link FileNotFoundException}
	 */
	public Object convertObjectToXML(Object classz, String fileName) throws JAXBException, FileNotFoundException {
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(classz.getClass());
			Marshaller marshaller = jaxbContext.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			marshaller.marshal(classz, Paths.get(fileName).toFile());
			logger.info("Data saved sucessfully { " +fileName  +"  }");
			return classz;
		} catch (JAXBException e) {
			logger.error("Error {X-1012}: saving object to XML {} ", e.getMessage());
		}
		return null;
	}

	/**
	 * This method will give the values from XML with given tagname
	 * @param xml {@link File}
	 * @param elementName  {@link String}
	 * @return  {@link String}
	 * @throws WatchDogException as Exception
	 */
	public String getValueFromXML(File xml, String elementName){
		String elementVal = null;
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			/*
			 * document = builder.parse(new InputSource(new StringReader(xml.toString())));
			 */
			Document document = builder.parse(xml);
			Element rootElement = document.getDocumentElement();
			elementVal = getValueFromXMLNode(elementName, rootElement);
			logger.info("Parsing in XML found element name: [ " + elementName + " ] ==> element value: [ " + elementVal
					+ " ]");
		} catch (SAXException e) {
			logger.error("Error {X-1013}: Parsing Exception " + e.getMessage());

		} catch (IOException e) {
			logger.error("Error {X-1014}: I/O Exception " + e.getMessage());
		} catch (ParserConfigurationException e) {
			logger.error("Error {X-1014}: Parsing Config Exception " + e.getMessage());
		} catch (WatchDogException e) {
			logger.error("Error {X-1014}: WatchDog Exception " + e.getMessage());

		}
		return elementVal;
	}

	/***
	 * Search the tag element in XML
	 * @param tagName  {@link String}
	 * @param element  {@link Element}
	 * @return {@link String}
	 * @throws WatchDogException
	 */
	private String getValueFromXMLNode(String tagName, Element element) throws WatchDogException {

		NodeList list = element.getElementsByTagName(tagName);
		if (list != null && list.getLength() > 0) {
			NodeList subList = list.item(0).getChildNodes();

			if (subList != null && subList.getLength() > 0) {
				return subList.item(0).getNodeValue();
			}
		}

		return null;
	}

	/***
	 * This method will modify the xml values with given input file  tagname and value
	 * @param xml  {@link File}
	 * @param elementName  {@link String}
	 * @param value  {@link String}
	 * @return  {@link Boolean}
	 * @throws WatchDogException {@link WatchDogException}
	 * 
	 */
	public boolean modifyValuesInXML(File xml, String elementName, String value)  {
		//Before Parsing XML we need to take a backup of that xml,
		//As some time due to error blank xml file will created.  //todo
		try {
            Document doc=parseDocument(xml);
			NodeList tagList = doc.getElementsByTagName(elementName);
			Node name = modifyNodeListValue(tagList);
			name.setTextContent(value);
			doc.getDocumentElement().normalize();
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(xml);
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.transform(source, result);
			logger.info("Parsing XML for modification {} found element name: [ " + elementName
					+ " ] ==> element value: [ " + value + " ]");

			return true;
		} catch (Exception e) {
			logger.error("Error {X-1015}: in modification XML {} " + e.getMessage());
		}
		return false;
	}
	

	/***
	 * 
	 * Modify single values in XML, but multiple values 
	 * @param filepath {@link String}
	 * @param elementName  {@link String}
	 * @param value  {@link List}
	 * @return {@link Boolean}
	 * @throws WatchDogException {@link WatchDogException}
	 */
	public boolean modifyValuesInXMLTags(File filepath, String elementName, 
			List<String> value) {
		try {
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			Document doc = docBuilder.parse(filepath);
			NodeList list = doc.getElementsByTagName(elementName);
			int len = list.getLength();
			logger.info("Tag [ " + elementName + " ] total of elements  : " + len);
			for (int i = 0; i < len; i++) {
				Node node = modifyNodeValue(list.item(i));
				node.setTextContent(value.get(i));
				logger.info("Parsing XML for modification {} found element name: [ " + elementName
						+ " ] ==> element value: [ " + value.get(i) + " ]");

			}
			doc.getDocumentElement().normalize();
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(filepath);
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.transform(source, result);
			return true;
		} catch (Exception e) {
			logger.error("Error {X-1016}: in modifyValuesInXMLTags XML {} " + e.getMessage());

		}
		return false;
	}

	/***
	 * 
	 * @param node {@link Node}
	 * @return {@link Node}
	 * @throws WatchDogException  {@link WatchDogException}
	 */
	private Node modifyNodeValue(Node node) throws WatchDogException {
		NodeList subList = node.getChildNodes();
		if (subList != null && subList.getLength() > 0) {
			return subList.item(0);
		} else {
			return node;
		}
	}

	/***
	 * 
	 * @param list {@link List}
	 * @return {@link Node}
	 * @throws WatchDogException  {@link WatchDogException}
	 */
	private Node modifyNodeListValue(NodeList list) throws WatchDogException {
		if (list != null && list.getLength() > 0) {
			NodeList subList = list.item(0).getChildNodes();
			if (subList != null && subList.getLength() > 0) {
				return subList.item(0);
			} else {
				return list.item(0);
			}
		}
		return null;
	}

	/***
	 * This method will print the XML content
	 * @param file {@link File}
	 * @return {@link String}
	 */
	public String printXML(File file) {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setValidating(false);
		DocumentBuilder db;
		String content = AppUtil.EMPTY_STR;
		Writer out = new StringWriter();
		try {
			db = dbf.newDocumentBuilder();
			Document doc = db.parse(file);
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			
			transformer.transform(new DOMSource(doc), new StreamResult(out));
			content = out.toString();
			logger.info("\n" + content);
			//out.close();
		} catch (ParserConfigurationException | SAXException | IOException | TransformerException e) {
			logger.error("Error {X-1017}: in printing XML  {} : " + e.getMessage());
		}finally {
			try {
				out.close();
			} catch (IOException e) {
			}
		}
		return content;

	}

    /****
     * This method will remove the TNS prefix,URI,DTD and TAGS
     * @param file {@link File}
     * @return {@link String}
     */
	public String modfiyXMLTNS(File file) {

		logger.info("Modifying TNS Tags and Schema from XML");
		
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setValidating(false);
		DocumentBuilder db;
		String content = AppUtil.EMPTY_STR;
		Writer out = new StringWriter();
		try {
			db = dbf.newDocumentBuilder();
			Document doc = db.parse(new FileInputStream(file));
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			transformer.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, "yes");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");

			transformer.transform(new DOMSource(doc), new StreamResult(out));
			content = out.toString();
			String[] prefixRemove = env.getProperty("tag.prefix.remove").
							split(AppUtil.COMMA_SEPERATOR);
			
			for (String prefx : prefixRemove) {
				content = content.replaceAll(prefx, AppUtil.EMPTY_STR);// Removing TnS/TSO
			}
			content = removeNameSpace(content);// Removing Tag

		} catch (ParserConfigurationException | SAXException | IOException | TransformerException e) {
			logger.error("Error {X-1018}: at {modfiyXMLTNS} [ " + file.getName() + " ]", e);

		} finally {
			try {
				out.close();
			} catch (IOException e) {
			}
		}
		writeXMLString(content, file.getPath());
		logger.info("Successfully modify TNS Tags and Schema in current file => " + file.getName());
		return content;
	}

	/***
	 * This method will remove the XML URI and dtd
	 * @param xml {@link String}
	 * @return {@link String}
	 */
	public String removeNameSpace(String xml) {
		try {
			logger.info("Removing uri and dtd from XML {} ");
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer transformer = tf.newTransformer();
			transformer.setOutputProperty(OutputKeys.METHOD, "xml");
			transformer.setOutputProperty(OutputKeys.INDENT, "true");
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			InputSource inputSource = new InputSource(new StringReader(xml));
			Document xmlDoc = builder.parse(inputSource);
			Node root = xmlDoc.getDocumentElement();
			NodeList rootchildren = root.getChildNodes();
			Element newroot = xmlDoc.createElement(root.getNodeName());
			for (int i = 0; i < rootchildren.getLength(); i++) {
				newroot.appendChild(rootchildren.item(i).cloneNode(true));
			}
			xmlDoc.replaceChild(newroot, root);
			String[] tags = env.getProperty("tag.remove").split(AppUtil.COMMA_SEPERATOR);
			if (tags != null && tags.length > 0) {
				for (String s : tags) {
					removeTag(xmlDoc, s);
				}
			}
			DOMSource requestXMLSource = new DOMSource(xmlDoc.getDocumentElement());
		    try(StringWriter xmlString = new StringWriter()){
		    	StreamResult requestXMLStreamResult = new StreamResult(xmlString);
			    transformer.transform(requestXMLSource, requestXMLStreamResult);
			return xmlString.toString();
		    }

		} catch (Exception e) {
			logger.error("Error {X-1019}: Could not parse message as xml: " + e.getMessage());
		}
		return AppUtil.EMPTY_STR;
	}

	/***
	 * TNS Remove the tag, which we will give in prop file
	 * @param xmlDoc {@link String}
	 * @param tag {@link String}
	 */

	public void removeTag(Document xmlDoc, String tag) {
		try {
			logger.info("Removing Tag name {  " + tag + " }");
			Element element = (Element) xmlDoc.getElementsByTagName(tag).item(0);
			Node parent = element.getParentNode();
			parent.removeChild(element);
		} catch (Exception e) {
			logger.debug("Error {X-1020}: Could not parse tag in xml: " + e.getMessage());
		}
	}

	/***
	 * 
	 * @param XMLStr {@link String}
	 * @param filename {@link String}
	 * @return {@link Boolean}
	 */

	public boolean writeXMLString(String XMLStr, String filename) {

		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.parse(new InputSource(new StringReader(XMLStr)));
			document.getDocumentElement().normalize();

			TransformerFactory tranFactory = TransformerFactory.newInstance();
			Transformer aTransformer = tranFactory.newTransformer();
			Source src = new DOMSource(document);
			Result dest = new StreamResult(Paths.get(filename).toFile());
			aTransformer.transform(src, dest);
		} catch (Exception e) {
			logger.error("Error {X-1021}: in Writing XML  {} : " + e.getMessage());
			return false;
		}
		return true;
	}

	/***
	 * This method will give the List of value which present in XML
	 * @param fileName {@link String}
	 * @param tagName {@link String} 
	 * @return list {@link List}
	 */
	public List<String> getValuesFromXML(File fileName, String tagName) {
		List<String> nonEdiList = new ArrayList<>(0);
		try {
			Document doc = parseDocument(fileName);
			NodeList nodeList = doc.getElementsByTagName(tagName);
			for (int i = 0; i < nodeList.getLength(); i++) {
				Element element = (Element) nodeList.item(i);
				nonEdiList.add(element.getTextContent().trim());

			}
			logger.debug("Load values against  tagname { " + tagName + " } : size [ " + nonEdiList.size() + " ]");
		} catch (Exception e) {
			logger.error("Error {X-1022}: {getValuesFromXML} : " + e.getMessage());
		}

		return nonEdiList;
	}
	
	/****
	 * Method will edit user from attribute id
	 * @param xmlPath {@link File}}
	 * @param user {@link User} 
	 * @return {@link Boolean}
	 */
	public boolean editUserXMLWithAttributeId(File xmlPath,User user) {
		try {
			
			Document doc=parseDocument(xmlPath);
			updateUserElement(doc,user);//updating
			doc.getDocumentElement().normalize();
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(xmlPath);
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.transform(source, result);
            logger.info("User DB updated successfully...");
			return true;
		}catch (Exception e) {
			
			logger.error("Error {X-1023}: {editUserXMLWithAttributeId} : " + e.getMessage());
			return false;
		}
		
	}
	
	/***
	 * This method will update xml values
	 * @param doc
	 * @param user
	 */
	private static void updateUserElement(Document doc, User user) {
		// loop for each user
		NodeList usersList = doc.getElementsByTagName("user");
		for (int i = 0; i < usersList.getLength(); i++) {
			Element elem = (Element) usersList.item(i);
			String id = elem.getAttribute("id");
			if (id.equals(user.getId())) {
				elem.getElementsByTagName("username").item(0).getFirstChild().setNodeValue(user.getUsername());
				elem.getElementsByTagName("userpass").item(0).getFirstChild().setNodeValue(user.getUserpass());
				elem.getElementsByTagName("active").item(0).getFirstChild().setNodeValue(String.valueOf(user.isActive()));
			}
		}
	}

	/***
	 * Method will edit planid xml from properties id
	 * @param xmlPath {@link File}
	 * @param planId {@link String}
	 * @return {@link Boolean}
	 */
	public boolean editPlanIDXMLWithAttributeId(File xmlPath, PlanId planId) {
		try {

			Document doc = parseDocument(xmlPath);
			updatePlanIDElement(doc, planId);//updating
			doc.getDocumentElement().normalize();
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(xmlPath);
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.transform(source, result);
			logger.info("PlanID DB updated successfully...");
			return true;
		} catch (Exception e) {

			logger.error("Error {X-1024}: {editPlanIDXMLWithAttributeId} : " + e.getMessage());
			return false;
		}

	}

	/***
	 * This method will update xml values
	 * @param doc
	 * @param user
	 */
	private static void updatePlanIDElement(Document doc, PlanId planId) {
		// loop for each user
		NodeList usersList = doc.getElementsByTagName("PlanId");
		for (int i = 0; i < usersList.getLength(); i++) {
			Element elem = (Element) usersList.item(i);
			String id = elem.getAttribute("id");
			if (id.equals(planId.getId())) {
				elem.getElementsByTagName("CreatedDate").item(0).getFirstChild().setNodeValue(planId.getCreatedDate());
				elem.getElementsByTagName("IsRun").item(0).getFirstChild().setNodeValue(planId.getIsRun());
			}
		}
	}

   /***
    * 	
    * @param fileName
    * @return {@link Document}
    * @throws ParserConfigurationException
    * @throws SAXException
    * @throws IOException
    */
   private Document parseDocument(File fileName) throws ParserConfigurationException, SAXException, IOException {
	    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc = db.parse(fileName);
		doc.getDocumentElement().normalize();
		return doc;
   }
   
}


