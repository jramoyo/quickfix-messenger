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
 * MemberPanelFactory.java
 * 14 Jun 2011
 */
package com.jramoyo.qfixmessenger.ui.panels;

import java.util.List;

import com.jramoyo.fix.model.Component;
import com.jramoyo.fix.model.Field;
import com.jramoyo.fix.model.Group;
import com.jramoyo.fix.model.Member;

/**
 * @author jamoyo
 */
public class MemberPanelFactory
{
	public static ComponentPanel createComponentPanel(
			List<MemberPanel<?, ?, ?>> prevMembers, Component component,
			boolean isRequiredOnly, boolean isRequired)
	{
		MemberPanel<?, ?, ?> prevMemberPanel = findPreviousPanelByMember(
				prevMembers, component);
		ComponentPanel componentPanel;
		if (prevMemberPanel != null)
		{
			componentPanel = (ComponentPanel) prevMemberPanel;
		} else
		{
			componentPanel = new ComponentPanel(component, isRequiredOnly,
					isRequired);
		}

		return componentPanel;
	}

	public static FieldPanel createFieldPanel(
			List<MemberPanel<?, ?, ?>> prevMembers, Field field,
			boolean isRequired)
	{
		MemberPanel<?, ?, ?> prevMemberPanel = findPreviousPanelByMember(
				prevMembers, field);
		FieldPanel fieldPanel;
		if (prevMemberPanel != null)
		{
			fieldPanel = (FieldPanel) prevMemberPanel;
		} else
		{
			fieldPanel = new FieldPanel(field, isRequired);
		}

		return fieldPanel;
	}

	public static GroupPanel createGroupPanel(
			List<MemberPanel<?, ?, ?>> prevMembers, Group group,
			boolean isRequiredOnly, boolean isRequired)
	{
		MemberPanel<?, ?, ?> prevMemberPanel = findPreviousPanelByMember(
				prevMembers, group);
		GroupPanel groupPanel;
		if (prevMemberPanel != null)
		{
			groupPanel = (GroupPanel) prevMemberPanel;
		} else
		{
			groupPanel = new GroupPanel(group, isRequiredOnly, isRequired);
		}

		return groupPanel;
	}

	private static MemberPanel<?, ?, ?> findPreviousPanelByMember(
			List<MemberPanel<?, ?, ?>> prevMembers, Member member)
	{
		for (MemberPanel<?, ?, ?> memberPanel : prevMembers)
		{
			if (memberPanel.getMember().equals(member))
			{
				return memberPanel;
			}
		}

		return null;
	}
}
