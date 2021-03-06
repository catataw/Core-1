/**
 * Copyright (C) SAS Institute, All rights reserved.
 * General Public License: http://www.opensource.org/licenses/gpl-license.php
 **/
package org.safs.selenium.webdriver.lib;

import org.safs.SAFSException;

/**
 *
 * History:<br>
 *
 *  <br>   DEC 18, 2013    (Lei Wang) Initial release.
 */
public class SeleniumPlusException extends SAFSException{
	private static final long serialVersionUID = 4786042607406277363L;

	public static final String CODE_OBJECT_IS_INVISIBLE 	= SeleniumPlusException.class.getSimpleName()+":CODE_OBJECT_IS_INVISIBLE";
	public static final String CODE_OBJECT_IS_NULL 			= SeleniumPlusException.class.getSimpleName()+":CODE_OBJECT_IS_NULL";
	public static final String CODE_TYPE_IS_WRONG 			= SeleniumPlusException.class.getSimpleName()+":CODE_TYPE_IS_WRONG";
	public static final String CODE_NO_JS_EXECUTOR 			= SeleniumPlusException.class.getSimpleName()+":CODE_NO_JS_EXECUTOR";
	public static final String CODE_VERIFICATION_FAIL		= SeleniumPlusException.class.getSimpleName()+":CODE_VERIFICATION_FAIL";

	private String info = null;

	public SeleniumPlusException(String detailMessage) {
		super(detailMessage);
	}
	public SeleniumPlusException(String detailMessage, String code) {
		super(detailMessage, code);
	}
	public SeleniumPlusException(String detailMessage, String code, String info) {
		super(detailMessage, code);
		this.info = info;
	}

	public SeleniumPlusException(Object obj, String methodName, String msg) {
		super(obj, methodName, msg);
	}

	public SeleniumPlusException(Object obj, String msg) {
		super(obj, msg);
	}

	public SeleniumPlusException(Throwable th) {
		super(th);
	}

	public SeleniumPlusException(String message, Throwable th) {
		super(message+" Embedded Throwable: "+th.getClass().getSimpleName()+":"+th.getMessage(), th);
	}

	public synchronized String getInfo() {
		return info;
	}
	public synchronized void setInfo(String info) {
		this.info = info;
	}
}
