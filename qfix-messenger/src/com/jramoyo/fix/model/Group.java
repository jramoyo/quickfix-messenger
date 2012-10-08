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
 * Group.java
 * 6 Jun 2011
 */
package com.jramoyo.fix.model;

import java.util.Collections;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Represents a FIX Repeating Group
 * 
 * @author jamoyo
 */
public final class Group extends AbstractMember
{
	private final Field field;
	private final Member firstMember;
	private final SortedMap<MemberOrder, Boolean> members;

	public Group(Field field, Map<MemberOrder, Boolean> members,
			Member firstMember)
	{
		this.field = field;
		this.members = new TreeMap<MemberOrder, Boolean>(members);
		this.firstMember = firstMember;
	}

	public Group(Group group)
	{
		this.field = group.field;
		this.members = new TreeMap<MemberOrder, Boolean>(group.members);
		this.firstMember = group.firstMember;
	}

	public Member getFirstMember()
	{
		return firstMember;
	}

	public Map<MemberOrder, Boolean> getMembers()
	{
		return Collections.unmodifiableMap(members);
	}

	@Override
	public String getName()
	{
		return field.getName();
	}

	public int getNumber()
	{
		return field.getNumber();
	}

	@Override
	public String toString()
	{
		return new StringBuilder(field.getName()).append("(")
				.append(field.getNumber()).append(")").append(" [GROUP]")
				.toString();
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((field == null) ? 0 : field.hashCode());
		result = prime * result
				+ ((firstMember == null) ? 0 : firstMember.hashCode());
		result = prime * result + ((members == null) ? 0 : members.hashCode());
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
		Group other = (Group) obj;
		if (field == null)
		{
			if (other.field != null)
				return false;
		} else if (!field.equals(other.field))
			return false;
		if (firstMember == null)
		{
			if (other.firstMember != null)
				return false;
		} else if (!firstMember.equals(other.firstMember))
			return false;
		if (members == null)
		{
			if (other.members != null)
				return false;
		} else if (!members.equals(other.members))
			return false;
		return true;
	}
}
