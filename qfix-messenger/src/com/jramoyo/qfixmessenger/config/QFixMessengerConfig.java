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
 * QFixMessengerConfig.java
 * 8 Jun 2011
 */
package com.jramoyo.qfixmessenger.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import com.jramoyo.qfixmessenger.QFixMessengerConstants;

/**
 * Represents a QuickFIX Messenger configuration
 * 
 * @author jamoyo
 */
public class QFixMessengerConfig
{
	private static final String PARSER_THREADS_PROP = "messenger.parser.threads";
	private static final String LICENSE_PROP = "messenger.license";
	private static final String ICON_SEND_PROP = "messenger.icon.send";
	private static final String HOME_URL_PROP = "messenger.home.url";

	private static final String DICT_FIX40_PROP = "messenger.dict.fix40";
	private static final String DICT_FIX41_PROP = "messenger.dict.fix41";
	private static final String DICT_FIX42_PROP = "messenger.dict.fix42";
	private static final String DICT_FIX43_PROP = "messenger.dict.fix43";
	private static final String DICT_FIX44_PROP = "messenger.dict.fix44";
	private static final String DICT_FIX50_PROP = "messenger.dict.fix50";
	private static final String DICT_FIXT11_PROP = "messenger.dict.fixt11";

	private final Properties properties;

	public QFixMessengerConfig(String configFileName) throws IOException
	{
		properties = new Properties();
		properties.load(new FileInputStream(configFileName));
	}

	public String getFix40DictionaryLocation()
	{
		return properties.getProperty(DICT_FIX40_PROP, "resources/FIX40.xml");
	}

	public String getFix41DictionaryLocation()
	{
		return properties.getProperty(DICT_FIX41_PROP, "resources/FIX41.xml");
	}

	public String getFix42DictionaryLocation()
	{
		return properties.getProperty(DICT_FIX42_PROP, "resources/FIX42.xml");
	}

	public String getFix43DictionaryLocation()
	{
		return properties.getProperty(DICT_FIX43_PROP, "resources/FIX43.xml");
	}

	public String getFix44DictionaryLocation()
	{
		return properties.getProperty(DICT_FIX44_PROP, "resources/FIX44.xml");
	}

	public String getFix50DictionaryLocation()
	{
		return properties.getProperty(DICT_FIX50_PROP, "resources/FIX50.xml");
	}

	public String getFixDictionaryLocation(String beginString)
	{
		if (beginString.equals(QFixMessengerConstants.BEGIN_STRING_FIX40))
		{
			return getFix40DictionaryLocation();
		}

		else if (beginString.equals(QFixMessengerConstants.BEGIN_STRING_FIX41))
		{
			return getFix41DictionaryLocation();
		}

		else if (beginString.equals(QFixMessengerConstants.BEGIN_STRING_FIX42))
		{
			return getFix42DictionaryLocation();
		}

		else if (beginString.equals(QFixMessengerConstants.BEGIN_STRING_FIX43))
		{
			return getFix43DictionaryLocation();
		}

		else if (beginString.equals(QFixMessengerConstants.BEGIN_STRING_FIX44))
		{
			return getFix44DictionaryLocation();
		}

		else if (beginString.equals(QFixMessengerConstants.BEGIN_STRING_FIX50))
		{
			return getFix50DictionaryLocation();
		}

		else if (beginString.equals(QFixMessengerConstants.BEGIN_STRING_FIXT11))
		{
			return getFixT11DictionaryLocation();
		}

		return "";
	}

	public String getFixT11DictionaryLocation()
	{
		return properties.getProperty(DICT_FIXT11_PROP, "resources/FIXT11.xml");
	}

	public String getSendIconLocation()
	{
		return properties.getProperty(ICON_SEND_PROP, "icons/send.png");
	}

	public String getLicenseFileLocation()
	{
		return properties.getProperty(LICENSE_PROP, "resources/license.txt");
	}

	public String getHomeURL()
	{
		return properties.getProperty(HOME_URL_PROP,
				"https://code.google.com/p/quickfix-messenger/");
	}

	public int getNoOfParserThreads()
	{
		return Integer.parseInt(properties.getProperty(PARSER_THREADS_PROP,
				"20"));
	}
}
