/*
 * Copyright (c) 2011, Jan Amoyo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions 
 * are met:
 *
 * - Redistributions of source code must retain the above copyright 
 *   notice, this list of conditions and the following disclaimer.
 * 
 * - Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer 
 *   in the documentation and/or other materials provided with the
 *   distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS 
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT 
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS 
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE 
 * COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, 
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, 
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS 
 * OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED 
 * AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, 
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF 
 * THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH 
 * DAMAGE.
 *
 * QFixMessengerConstants.java
 * 9 Jun 2011
 */
package com.jramoyo.qfixmessenger;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Constants used by QuickFIX Messenger
 * <p>
 * TODO These values need to be configurable
 * </p>
 * 
 * @author jamoyo
 */
public class QFixMessengerConstants
{
	public static final String UTC_DATE_FORMAT = "yyyyMMdd-HH:mm:ss.SSS";

	public static final char SOH = '\001';

	public static final String BEGIN_STRING_FIX40 = "FIX.4.0";
	public static final String BEGIN_STRING_FIX41 = "FIX.4.1";
	public static final String BEGIN_STRING_FIX42 = "FIX.4.2";
	public static final String BEGIN_STRING_FIX43 = "FIX.4.3";
	public static final String BEGIN_STRING_FIX44 = "FIX.4.4";
	public static final String BEGIN_STRING_FIX50 = "FIX.5.0";
	public static final String BEGIN_STRING_FIX50SP1 = "FIX.5.0SP1";
	public static final String BEGIN_STRING_FIX50SP2 = "FIX.5.0SP2";
	public static final String BEGIN_STRING_FIXT11 = "FIXT.1.1";

	public static final Map<String, String> APPVER_ID_MAP;
	static
	{
		Map<String, String> appVerIDMap = new HashMap<String, String>();

		appVerIDMap.put(BEGIN_STRING_FIX40, "2");
		appVerIDMap.put(BEGIN_STRING_FIX41, "3");
		appVerIDMap.put(BEGIN_STRING_FIX42, "4");
		appVerIDMap.put(BEGIN_STRING_FIX43, "5");
		appVerIDMap.put(BEGIN_STRING_FIX44, "6");
		appVerIDMap.put(BEGIN_STRING_FIX50, "7");
		appVerIDMap.put(BEGIN_STRING_FIX50SP1, "8");
		appVerIDMap.put(BEGIN_STRING_FIX50SP2, "9");

		APPVER_ID_MAP = Collections.unmodifiableMap(appVerIDMap);
	}

	public static final String[] FIXT_APP_VERSIONS = { BEGIN_STRING_FIX40,
			BEGIN_STRING_FIX41, BEGIN_STRING_FIX42, BEGIN_STRING_FIX43,
			BEGIN_STRING_FIX44, BEGIN_STRING_FIX50 };

	// TODO Find a way to be configurable
	public static final String FIX_WIKI_URL = "http://fixwiki.org/fixwiki/";
}
