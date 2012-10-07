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

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.jramoyo.fix.model.Member;

/**
 * Cache for MemberPanels
 * 
 * @author jramoyo
 */
public class MemberPanelCache
{
	private final ConcurrentMap<Member, MemberPanel<?, ?, ?>> cache;

	public MemberPanelCache()
	{
		cache = new ConcurrentHashMap<>();
	}

	public void encache(MemberPanel<?, ?, ?> memberPanel)
	{
		cache.put(memberPanel.getMember(), memberPanel);
	}

	public void encacheAll(List<MemberPanel<?, ?, ?>> memberPanels)
	{
		for (MemberPanel<?, ?, ?> memberPanel : memberPanels)
		{
			cache.put(memberPanel.getMember(), memberPanel);
		}
	}

	public MemberPanel<?, ?, ?> getMemberPanel(Member member)
	{
		return cache.get(member);
	}
}