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
 * Field.java
 * 6 Jun 2011
 */
package com.jramoyo.fix.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a FIX field (tag)
 * 
 * @author jamoyo
 */
public final class Field extends AbstractMember
{
	private final int number;
	private final String name;
	private final FieldType type;
	private final List<FieldValue> values;

	public Field(int number, String name, FieldType type,
			List<FieldValue> values)
	{
		this.number = number;
		this.name = name;
		this.type = type;
		if (values != null)
		{
			this.values = new ArrayList<FieldValue>(values);
		} else
		{
			this.values = null;
		}
	}

	public Field(Field field)
	{
		this.number = field.number;
		this.name = field.name;
		this.type = field.type;
		if (field.values != null)
		{
			this.values = new ArrayList<FieldValue>(field.values);
		} else
		{
			this.values = null;
		}
	}

	public int getNumber()
	{
		return number;
	}

	@Override
	public String getName()
	{
		return name;
	}

	public FieldType getType()
	{
		return type;
	}

	public List<FieldValue> getValues()
	{
		if (values != null)
		{
			return Collections.unmodifiableList(values);
		} else
		{
			return null;
		}
	}

	@Override
	public String toString()
	{
		return new StringBuilder(name).append(" (").append(number).append(")")
				.toString();
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + number;
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		result = prime * result + ((values == null) ? 0 : values.hashCode());
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
		Field other = (Field) obj;
		if (name == null)
		{
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (number != other.number)
			return false;
		if (type == null)
		{
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		if (values == null)
		{
			if (other.values != null)
				return false;
		} else if (!values.equals(other.values))
			return false;
		return true;
	}

}
