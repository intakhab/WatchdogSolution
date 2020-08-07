package com.hcl.dog.common;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.hcl.dog.dto.MailDto;
/***
 * 
 * @author intakhabalam.s@hcl.com
 * @see {@link MailDto}
 */
public class AppUtil {

	
	public static final String COMMA_SEPERATOR = ",";
	public static final String EMPTY_STR = "";
	public static final String WHITE_SPACE = "\\s";
    public static final String DOT_STR_COMP="\\.";
	public static final String WATCH_DOG_GREET_MSG = "\\\\/\\/atch[|)og";
	public static final String FILE_SEPARTOR = "@";
	
	public static final  String exceptionPatternREG = "(.*)(Exception+)(.*)";
	public static final  String exceptionPatternCAP = "Exception";
	public static final  String exceptionPatternSML = "exception";
	
	public static final String TNS_SYSTEM_PLAN_ID = "tns:SystemPlanID";
	public static final String SYSTEM_PLAN_ID = "SystemPlanID";
	
	public static final String TNS_SHIPMENT_NO = "tns:ShipmentNumber";
	public static final String SHIPMENT_NO = "ShipmentNumber";
	
	public static final String PLAN_NOT_FOUND_CODE="999";

	public final static SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy_HH-mm-ss");

	public static final String CLOSE_BANNER = "\n\n __        __          _            _         ____                       ____   _                 _                 \r\n"
			+ " \\ \\      / /   __ _  | |_    ___  | |__     |  _ \\    ___     __ _     / ___| | |   ___    ___  (_)  _ __     __ _ \r\n"
			+ "  \\ \\ /\\ / /   / _` | | __|  / __| | '_ \\    | | | |  / _ \\   / _` |   | |     | |  / _ \\  / __| | | | '_ \\   / _` |\r\n"
			+ "   \\ V  V /   | (_| | | |_  | (__  | | | |   | |_| | | (_) | | (_| |   | |___  | | | (_) | \\__ \\ | | | | | | | (_| |\r\n"
			+ "    \\_/\\_/     \\__,_|  \\__|  \\___| |_| |_|   |____/   \\___/   \\__, |    \\____| |_|  \\___/  |___/ |_| |_| |_|  \\__, |\r\n"
			+ "                                                              |___/                                           |___/ \r\n"
			+ "";

	public static  final String OPEN_BANNER = "\n\n __        __          _            _         ____                      ____    _                    _     _                 \r\n"
			+ " \\ \\      / /   __ _  | |_    ___  | |__     |  _ \\    ___     __ _    / ___|  | |_    __ _   _ __  | |_  (_)  _ __     __ _ \r\n"
			+ "  \\ \\ /\\ / /   / _` | | __|  / __| | '_ \\    | | | |  / _ \\   / _` |   \\___ \\  | __|  / _` | | '__| | __| | | | '_ \\   / _` |\r\n"
			+ "   \\ V  V /   | (_| | | |_  | (__  | | | |   | |_| | | (_) | | (_| |    ___) | | |_  | (_| | | |    | |_  | | | | | | | (_| |\r\n"
			+ "    \\_/\\_/     \\__,_|  \\__|  \\___| |_| |_|   |____/   \\___/   \\__, |   |____/   \\__|  \\__,_| |_|     \\__| |_| |_| |_|  \\__, |\r\n"
			+ "                                                              |___/                                                    |___/ \r\n"
			+ "";

	public static  final Long MINS = (long) (1000 * 60 * 60);
	public static  final String AND_STR = "&";
	public static  final String DOT_STR = ".";
	public static  final String UNDERSCORE_STR = "_";
	public static  final String EQUAL_STR = "=";
	public static  final String FALSE_STR = "false";
	public static  final String TRUE_STR = "true";

	public static  final String DUPLICATE_STR = "duplicate";
	public static  final String ERROR_CODE = "Code";
	public static  final String FILE_PROCESS_MSG = "######### File process completed ###########";
	public static  String CURRENT_FILE_PROCESSING_NAME = "";
	public static  final String RESPONSE_NOT_FOUND_FROM_TMS = "Response xml not got from TMS";
	public static  final String RESPONSE_FOUND_FROM_TMS = "Response xml got from TMS";

	public static  final String CHECK_RESP_XML = "Check Output xml from TMS";

	public static  final String SUCCESS_MSG = "Successfull run APIs";

	public static  final String FILE_INTERNAL_PROBLEM = "File having internal Problem";

	public static  String STARTED_TIME = "";

	
	
	/**
	 * This method will give YYYY-DD-MM and Hrs
	 * 
	 * @return time
	 */
	public static String currentTime() {
		return dateFormat.format(new Date());
	}
	/***
	 * @param listOfItems {@link List}
	 * @param separator {@link String}
	 * @return {@link String}
	 */
	public static String concatenate(List<String> listOfItems, String separator) {
		StringBuilder sb = new StringBuilder();
		Iterator<String> stit = listOfItems.iterator();

		while (stit.hasNext()) {
			sb.append(stit.next());
			if (stit.hasNext()) {
				sb.append(separator);
			}
		}

		return sb.toString();
	}
	
	
	
	/***
	 * @param apiString
	 * @return {@link String}
	 */
	public static String checkAndRemoveCurlyStr(String apiString) {
		
		if (apiString.contains("{") || apiString.contains("}")) {
			apiString = apiString.replaceAll("\\{", EMPTY_STR).replaceAll("\\}", EMPTY_STR);
		}
		return apiString;
	}
	/**
	 * Checks if is collection empty.
	 * @param collection {@link Collection}
	 * @return {@link Boolean}
	 */
	private static boolean isCollectionEmpty(Collection<?> collection) {
		if (collection == null || collection.isEmpty()) {
			return true;
		}
		return false;
	}
	
	/**
	 * Checks if is object empty.
	 * @param object {@link Object}
	 * @return {@link Boolean}
	 */
	public static boolean isObjectEmpty(Object object) {
		if(object == null) return true;
		else if(object instanceof String) {
			if (((String)object).trim().length() == 0) {
				return true;
			}
		} else if(object instanceof Collection) {
			return isCollectionEmpty((Collection<?>)object);
		}
		return false;
	}
}
