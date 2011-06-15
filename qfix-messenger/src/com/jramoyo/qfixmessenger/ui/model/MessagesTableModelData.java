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
 * MessagesTableModelData.java
 * 14 Jun 2011
 */
package com.jramoyo.qfixmessenger.ui.model;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.jramoyo.qfixmessenger.QFixMessengerConstants;

/**
 * @author jamoyo
 */
public final class MessagesTableModelData
{
	public static final String DATE_COLUMN = "Time Stamp";
	public static final String DIRECTION_COLUMN = "Direction";
	public static final String SESSION_COLUMN = "Session ID";
	public static final String MESSAGE_COLUMN = "Message";

	public static final String[] COLUMN_NAMES =
	{ DATE_COLUMN, DIRECTION_COLUMN, SESSION_COLUMN, MESSAGE_COLUMN };

	private final Date date;
	private final String direction;
	private final String sessionName;
	private final String message;

	public MessagesTableModelData(Date date, String direction,
			String sessionName, String message)
	{
		this.date = date;
		this.direction = direction;
		this.sessionName = sessionName;
		this.message = message;
	}

	public Date getDate()
	{
		return date;
	}

	public String getDirection()
	{
		return direction;
	}

	public String getSessionName()
	{
		return sessionName;
	}

	public String getMessage()
	{
		return message;
	}

	public String getColumData(String columnName)
	{
		if (columnName.equals(DATE_COLUMN))
		{
			return new SimpleDateFormat(QFixMessengerConstants.UTC_DATE_FORMAT)
					.format(getDate());
		}

		else if (columnName.equals(DIRECTION_COLUMN))
		{
			return getDirection();
		}

		else if (columnName.equals(SESSION_COLUMN))
		{
			return getSessionName();
		}

		else if (columnName.equals(MESSAGE_COLUMN))
		{
			return getMessage();
		}

		return "";
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((date == null) ? 0 : date.hashCode());
		result = prime * result
				+ ((direction == null) ? 0 : direction.hashCode());
		result = prime * result + ((message == null) ? 0 : message.hashCode());
		result = prime * result
				+ ((sessionName == null) ? 0 : sessionName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MessagesTableModelData other = (MessagesTableModelData) obj;
		if (date == null)
		{
			if (other.date != null)
				return false;
		} else if (!date.equals(other.date))
			return false;
		if (direction == null)
		{
			if (other.direction != null)
				return false;
		} else if (!direction.equals(other.direction))
			return false;
		if (message == null)
		{
			if (other.message != null)
				return false;
		} else if (!message.equals(other.message))
			return false;
		if (sessionName == null)
		{
			if (other.sessionName != null)
				return false;
		} else if (!sessionName.equals(other.sessionName))
			return false;
		return true;
	}
}
