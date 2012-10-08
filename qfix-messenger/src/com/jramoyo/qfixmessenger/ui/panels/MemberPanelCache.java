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
 * MemberPanelCache.java
 * Oct 4, 2012
 */
package com.jramoyo.qfixmessenger.ui.panels;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jramoyo.fix.model.Member;

/**
 * Cache for MemberPanels
 * 
 * @author jramoyo
 */
public class MemberPanelCache
{
	private final Map<Member, MemberPanel<?, ?, ?>> cache;

	private final Map<GroupMemberKey, MemberPanel<?, ?, ?>> groupCache;

	public MemberPanelCache()
	{
		cache = new HashMap<>();
		groupCache = new HashMap<>();
	}

	public void clear()
	{
		cache.clear();
		groupCache.clear();
	}

	public void encacheGroupMember(int index, MemberPanel<?, ?, ?> memberPanel)
	{
		groupCache.put(new GroupMemberKey(index, memberPanel.getMember()),
				memberPanel);
	}

	public void encacheGroupMembers(int index,
			List<MemberPanel<?, ?, ?>> memberPanels)
	{
		for (MemberPanel<?, ?, ?> memberPanel : memberPanels)
		{
			groupCache.put(new GroupMemberKey(index, memberPanel.getMember()),
					memberPanel);
		}
	}

	public void encacheMember(MemberPanel<?, ?, ?> memberPanel)
	{
		cache.put(memberPanel.getMember(), memberPanel);
	}

	public void encacheMembers(List<MemberPanel<?, ?, ?>> memberPanels)
	{
		for (MemberPanel<?, ?, ?> memberPanel : memberPanels)
		{
			cache.put(memberPanel.getMember(), memberPanel);
		}
	}

	public MemberPanel<?, ?, ?> getGroupMemberPanel(int index, Member member)
	{
		return groupCache.get(new GroupMemberKey(index, member));
	}

	public MemberPanel<?, ?, ?> getMemberPanel(Member member)
	{
		return cache.get(member);
	}

	private static class GroupMemberKey
	{
		private final int index;
		private final Member member;

		private GroupMemberKey(int index, Member member)
		{
			this.index = index;
			this.member = member;
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
			GroupMemberKey other = (GroupMemberKey) obj;
			if (index != other.index)
				return false;
			if (member == null)
			{
				if (other.member != null)
					return false;
			} else if (!member.equals(other.member))
				return false;
			return true;
		}

		@Override
		public int hashCode()
		{
			final int prime = 31;
			int result = 1;
			result = prime * result + index;
			result = prime * result
					+ ((member == null) ? 0 : member.hashCode());
			return result;
		}
	}
}