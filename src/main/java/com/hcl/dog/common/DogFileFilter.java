package com.hcl.dog.common;

import java.io.File;
import java.io.FileFilter;

import com.hcl.dog.component.DogComponent;
import com.hcl.dog.component.FinComponent;
import com.hcl.dog.component.NonEdiComponent;
import com.hcl.dog.component.SOComponent;

/***
 * 
 * @author intakhabalam.s@hcl.com
 * @see FileFilter
 * @see DogComponent
 * @see FinComponent
 * @see NonEdiComponent
 * @see SOComponent
 */
public class DogFileFilter implements FileFilter {

	private String supportsFileStr;

	public DogFileFilter(String supportsFileStr) {
		this.supportsFileStr = supportsFileStr;
	}

	/***
	 * @param file
	 *            {@link File}
	 * @return {@link Boolean}
	 */
	public boolean accept(File file) {
		// if the file extension is .log return true, else false
		String[] supportsFile = supportsFileStr.split(AppUtil.COMMA_SEPERATOR);
		for (String s : supportsFile) {
			if (getEndWith(file.getName().toLowerCase()).startsWith(s.toLowerCase())) {
				return true;

			}
		}
		return false;
	}

	/***
	 * This method will check end with file
	 * 
	 * @param name
	 *            {@link String}
	 * @return {@link String}
	 */
	public String getEndWith(String name) {
		try {
			if (name.contains(AppUtil.FILE_SEPARTOR))
				return name.split(AppUtil.FILE_SEPARTOR)[1].split(AppUtil.DOT_STR_COMP)[0];// xyz@abc.xml
			else
				return name;

		} catch (Exception e) {
			return "NOTVALID";
		}
	}
}