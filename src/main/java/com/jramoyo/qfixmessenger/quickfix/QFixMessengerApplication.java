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
 * QFixMessengerApplication.java
 * 6 Jun 2011
 */
package com.jramoyo.qfixmessenger.quickfix;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import quickfix.Application;
import quickfix.ConfigError;
import quickfix.DoNotSend;
import quickfix.FieldConvertError;
import quickfix.FieldNotFound;
import quickfix.IncorrectDataFormat;
import quickfix.IncorrectTagValue;
import quickfix.Message;
import quickfix.RejectLogon;
import quickfix.SessionID;
import quickfix.SessionSettings;
import quickfix.UnsupportedMessageType;
import quickfix.field.MsgType;
import quickfix.field.Password;
import quickfix.field.RawData;
import quickfix.field.RawDataLength;
import quickfix.field.TestMessageIndicator;
import quickfix.field.Username;

import com.jramoyo.qfixmessenger.util.StringUtil;

/**
 * Implementation of QuickFIX application
 * 
 * @author jamoyo
 */
public class QFixMessengerApplication implements Application
{
	private static final Logger logger = LoggerFactory
			.getLogger(QFixMessengerApplication.class);

	private final SessionSettings sessionSettings;

	private final List<QFixMessageListener> messageListeners = new ArrayList<QFixMessageListener>();

	public QFixMessengerApplication(SessionSettings sessionSettings)
	{
		this.sessionSettings = sessionSettings;
	}

	public void addMessageListener(QFixMessageListener messageListener)
	{
		messageListeners.add(messageListener);
	}

	public void removeMessageListener(QFixMessageListener messageListener)
	{
		messageListeners.remove(messageListener);
	}

	@Override
	public void fromAdmin(Message message, SessionID sessionId)
			throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue,
			RejectLogon
	{
		// Listen for Session Reject messages
		MsgType msgType = (MsgType) message.getHeader().getField(new MsgType());
		if (msgType.getValue().equals("3"))
		{
			for (QFixMessageListener messageListener : messageListeners)
			{
				messageListener.onMessage(QFixMessageListener.RECV, message,
						sessionId);
			}
		}
	}

	@Override
	public void fromApp(Message message, SessionID sessionId)
			throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue,
			UnsupportedMessageType
	{
		for (QFixMessageListener messageListener : messageListeners)
		{
			messageListener.onMessage(QFixMessageListener.RECV, message,
					sessionId);
		}
	}

	@Override
	public void onCreate(SessionID sessionId)
	{
	}

	@Override
	public void onLogon(SessionID sessionId)
	{
	}

	@Override
	public void onLogout(SessionID sessionId)
	{
	}

	@Override
	public void toAdmin(Message message, SessionID sessionId)
	{
		// Customize logon
		if (isMessageOfType(message, MsgType.LOGON))
		{
			try
			{
				try
				{
					String usernameSetting = sessionSettings.getString(
							sessionId, "Username");
					if (!StringUtil.isNullOrEmpty(usernameSetting))
					{
						message.setField(new Username(usernameSetting));
					}
				} catch (ConfigError ex)
				{
					logger.debug(ex.getMessage());
				}

				try
				{
					String passwordSetting = sessionSettings.getString(
							sessionId, "Password");
					if (!StringUtil.isNullOrEmpty(passwordSetting))
					{
						message.setField(new Password(passwordSetting));
					}
				} catch (ConfigError ex)
				{
					logger.debug(ex.getMessage());
				}

				try
				{
					String rawDataSetting = sessionSettings.getString(
							sessionId, "RawData");
					if (!StringUtil.isNullOrEmpty(rawDataSetting))
					{
						message.setField(new RawData(rawDataSetting));
					}
				} catch (ConfigError ex)
				{
					logger.debug(ex.getMessage());
				}

				try
				{
					long rawDataLengthSetting = sessionSettings.getLong(
							sessionId, "RawDataLength");
					if (rawDataLengthSetting > 0)
					{
						message.setField(new RawDataLength(
								(int) rawDataLengthSetting));
					}
				} catch (ConfigError ex)
				{
					logger.debug(ex.getMessage());
				}

				try
				{
					String testMessageIndicatorSetting = sessionSettings
							.getString(sessionId, "TestMessageIndicator");
					if (!StringUtil.isNullOrEmpty(testMessageIndicatorSetting))
					{
						message.setField(new TestMessageIndicator(Boolean
								.valueOf(testMessageIndicatorSetting)));
					}
				} catch (ConfigError ex)
				{
					logger.debug(ex.getMessage());
				}
			} catch (FieldConvertError ex)
			{
				logger.error("An error occured while "
						+ "fetching custom logon settings!", ex);
			}
		}
	}

	@Override
	public void toApp(Message message, SessionID sessionId) throws DoNotSend
	{
		for (QFixMessageListener messageListener : messageListeners)
		{
			messageListener.onMessage(QFixMessageListener.SENT, message,
					sessionId);
		}
	}

	private boolean isMessageOfType(Message message, String type)
	{
		try
		{
			return type.equals(message.getHeader().getField(new MsgType())
					.getValue());
		} catch (FieldNotFound ex)
		{
			logger.error("Unable to find MsgType from Message!", ex);
			return false;
		}
	}
}
