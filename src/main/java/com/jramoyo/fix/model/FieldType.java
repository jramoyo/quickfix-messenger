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
 * FieldType.java
 * 6 Jun 2011
 */
package com.jramoyo.fix.model;

import java.util.Date;

/**
 * Enumeration of known FIX field types
 * 
 * @author jamoyo
 */
public enum FieldType
{
	UNKNOWN,

	STRING,

	CHAR,

	PRICE(Double.class),

	INT(Integer.class),

	AMT(Double.class),

	QTY(Double.class),

	CURRENCY,

	MULTIPLEVALUESTRING,

	EXCHANGE,

	UTCTIMESTAMP(Date.class),

	BOOLEAN(Boolean.class),

	LOCALMKTDATE,

	DATA,

	FLOAT(Double.class),

	PRICEOFFSET(Double.class),

	MONTHYEAR,

	DAYOFMONTH(Integer.class),

	UTCDATEONLY(Date.class),

	UTCDATE(Date.class),

	UTCTIMEONLY(Date.class),

	TIME,

	NUMINGROUP(Integer.class),

	PERCENTAGE(Double.class),

	SEQNUM(Integer.class),

	LENGTH(Integer.class),

	COUNTRY;

	private Class<?> javaClass;

	private FieldType()
	{
		this.javaClass = String.class;
	}

	private FieldType(Class<?> javaClass)
	{
		this.javaClass = javaClass;
	}

	public Class<?> getJavaClass()
	{
		return javaClass;
	}
}
