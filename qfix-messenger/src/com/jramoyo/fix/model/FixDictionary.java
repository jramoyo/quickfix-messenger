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
 * FIXDictionary.java
 * 7 Jun 2011
 */
package com.jramoyo.fix.model;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * Represents a dictionary of a FIX version
 * 
 * @author jamoyo
 */
public final class FixDictionary
{
	private final String majorVersion;
	private final String minorVersion;
	private final String type;

	private final ConcurrentMap<String, Component> components;
	private final ConcurrentMap<String, Field> fields;
	private final ConcurrentMap<String, Message> messages;

	private Header header;
	private Trailer trailer;

	public FixDictionary(String majorVersion, String minorVersion)
	{
		this.type = null;
		this.majorVersion = majorVersion;
		this.minorVersion = minorVersion;

		this.fields = new ConcurrentHashMap<String, Field>();
		this.components = new ConcurrentHashMap<String, Component>();
		this.messages = new ConcurrentSkipListMap<String, Message>();
	}

	public FixDictionary(String type, String majorVersion, String minorVersion)
	{
		this.type = type;
		this.majorVersion = majorVersion;
		this.minorVersion = minorVersion;

		this.fields = new ConcurrentHashMap<String, Field>();
		this.components = new ConcurrentHashMap<String, Component>();
		this.messages = new ConcurrentHashMap<String, Message>();
	}

	public ConcurrentMap<String, Component> getComponents()
	{
		return components;
	}

	public ConcurrentMap<String, Field> getFields()
	{
		return fields;
	}

	public String getFullVersion()
	{
		return new StringBuilder(majorVersion).append('.').append(minorVersion)
				.toString();
	}

	public String getMajorVersion()
	{
		return majorVersion;
	}

	public ConcurrentMap<String, Message> getMessages()
	{
		return messages;
	}

	public String getMinorVersion()
	{
		return minorVersion;
	}

	public String getType()
	{
		return type;
	}

	public Header getHeader()
	{
		return header;
	}

	public void setHeader(Header header)
	{
		this.header = header;
	}

	public Trailer getTrailer()
	{
		return trailer;
	}

	public void setTrailer(Trailer trailer)
	{
		this.trailer = trailer;
	}

	@Override
	public String toString()
	{
		return getFullVersion();
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((components == null) ? 0 : components.hashCode());
		result = prime * result + ((fields == null) ? 0 : fields.hashCode());
		result = prime * result + ((header == null) ? 0 : header.hashCode());
		result = prime * result
				+ ((majorVersion == null) ? 0 : majorVersion.hashCode());
		result = prime * result
				+ ((messages == null) ? 0 : messages.hashCode());
		result = prime * result
				+ ((minorVersion == null) ? 0 : minorVersion.hashCode());
		result = prime * result + ((trailer == null) ? 0 : trailer.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
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
		FixDictionary other = (FixDictionary) obj;
		if (components == null)
		{
			if (other.components != null)
				return false;
		} else if (!components.equals(other.components))
			return false;
		if (fields == null)
		{
			if (other.fields != null)
				return false;
		} else if (!fields.equals(other.fields))
			return false;
		if (header == null)
		{
			if (other.header != null)
				return false;
		} else if (!header.equals(other.header))
			return false;
		if (majorVersion == null)
		{
			if (other.majorVersion != null)
				return false;
		} else if (!majorVersion.equals(other.majorVersion))
			return false;
		if (messages == null)
		{
			if (other.messages != null)
				return false;
		} else if (!messages.equals(other.messages))
			return false;
		if (minorVersion == null)
		{
			if (other.minorVersion != null)
				return false;
		} else if (!minorVersion.equals(other.minorVersion))
			return false;
		if (trailer == null)
		{
			if (other.trailer != null)
				return false;
		} else if (!trailer.equals(other.trailer))
			return false;
		if (type == null)
		{
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}
}
