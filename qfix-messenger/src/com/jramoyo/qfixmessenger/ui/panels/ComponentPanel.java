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
 * ComponentPanel.java
 * 13 Jun 2011
 */
package com.jramoyo.qfixmessenger.ui.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

import quickfix.StringField;

import com.jramoyo.fix.model.Component;
import com.jramoyo.fix.model.Field;
import com.jramoyo.fix.model.Group;
import com.jramoyo.fix.model.Member;
import com.jramoyo.fix.xml.ComponentType;
import com.jramoyo.fix.xml.FieldType;
import com.jramoyo.fix.xml.GroupsType;
import com.jramoyo.fix.xml.ObjectFactory;
import com.jramoyo.qfixmessenger.QFixMessengerConstants;

/**
 * @author jamoyo
 */
public class ComponentPanel extends AbstractMemberPanel
{
	private static final long serialVersionUID = 1982089310942186498L;

	private final Component component;

	private final boolean isRequired;

	private final boolean isRequiredOnly;

	private List<MemberPanel> members;

	private List<MemberPanel> prevMembers;

	private JLabel componentLabel;

	private JPanel membersPanel;

	public ComponentPanel(Component component, boolean isRequiredOnly,
			boolean isRequired)
	{
		this.component = component;
		this.isRequiredOnly = isRequiredOnly;
		this.isRequired = isRequired;

		this.members = new ArrayList<MemberPanel>();
		this.prevMembers = new ArrayList<MemberPanel>();

		initComponents();
	}

	public ComponentType getXmlComponent()
	{
		ObjectFactory xmlObjectFactory = new ObjectFactory();
		ComponentType xmlComponentType = xmlObjectFactory.createComponentType();
		xmlComponentType.setName(component.getName());

		for (MemberPanel memberPanel : members)
		{
			if (memberPanel instanceof FieldPanel)
			{
				FieldType xmlFieldType = ((FieldPanel) memberPanel)
						.getXmlField();
				if (xmlFieldType != null)
				{
					xmlComponentType.getFieldOrGroupsOrComponent().add(
							xmlFieldType);
				}
			}

			if (memberPanel instanceof GroupPanel)
			{
				GroupsType xmlGroupsTypeMember = ((GroupPanel) memberPanel)
						.getXmlGroups();
				xmlComponentType.getFieldOrGroupsOrComponent().add(
						xmlGroupsTypeMember);
			}
		}

		return xmlComponentType;
	}

	/*
	 * This is a work-around because QuickFIX does not provide a non type-safe
	 * representation message components.
	 */
	public List<StringField> getQuickFixFields()
	{
		List<StringField> qfixFields = new ArrayList<StringField>();

		for (MemberPanel memberPanel : members)
		{
			if (memberPanel instanceof FieldPanel)
			{
				FieldPanel fieldPanel = (FieldPanel) memberPanel;
				if (fieldPanel.getQuickFixField() != null)
				{
					qfixFields.add(fieldPanel.getQuickFixField());
				}
			}
		}

		return qfixFields;
	}

	/*
	 * This is a work-around because QuickFIX does not provide a non type-safe
	 * representation message components.
	 */
	public List<quickfix.Group> getQuickFixGroups()
	{
		List<quickfix.Group> qfixGroups = new ArrayList<quickfix.Group>();

		for (MemberPanel memberPanel : members)
		{
			if (memberPanel instanceof GroupPanel)
			{
				GroupPanel groupPanel = (GroupPanel) memberPanel;
				qfixGroups.addAll(groupPanel.getQuickFixGroups());
			}
		}

		return qfixGroups;
	}

	@Override
	public String getFixString()
	{
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < members.size(); i++)
		{
			sb.append(members.get(i).getFixString());
			sb.append(QFixMessengerConstants.SOH);
		}

		return sb.toString();
	}

	@Override
	public Member getMember()
	{
		return component;
	}

	public void populate(ComponentType xmlComponentType)
	{
		for (Object xmlMember : xmlComponentType.getFieldOrGroupsOrComponent())
		{
			if (xmlMember instanceof FieldType)
			{
				FieldType xmlFieldType = (FieldType) xmlMember;
				FieldPanel fieldPanel = (FieldPanel) MemberPanelUtil
						.findMemberPanelByName(xmlFieldType.getName(), members);
				fieldPanel.populate(xmlFieldType);
			}

			if (xmlMember instanceof GroupsType)
			{
				GroupsType xmlGroupsType = (GroupsType) xmlMember;
				GroupPanel groupPanel = (GroupPanel) MemberPanelUtil
						.findMemberPanelByName(xmlGroupsType.getName(), members);
				groupPanel.populate(xmlGroupsType);
			}

			if (xmlMember instanceof ComponentType)
			{
				ComponentType xmlComponentTypeMember = (ComponentType) xmlMember;
				ComponentPanel componentPanel = (ComponentPanel) MemberPanelUtil
						.findMemberPanelByName(
								xmlComponentTypeMember.getName(), members);
				componentPanel.populate(xmlComponentTypeMember);
			}
		}
	}

	private void initComponents()
	{
		setLayout(new BorderLayout());

		componentLabel = new JLabel(component.toString());
		componentLabel
				.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		componentLabel.addMouseListener(new LinkMouseAdapter(this));
		componentLabel.setToolTipText("Double-click to look-up in FIXwiki");
		if (isRequired)
		{
			componentLabel.setForeground(Color.BLUE);
		}

		componentLabel.setFont(new Font(componentLabel.getFont().getName(),
				Font.BOLD, 15));

		membersPanel = new JPanel();
		if (isRequired)
		{
			membersPanel.setBorder(new LineBorder(Color.BLUE));
		} else
		{
			membersPanel.setBorder(new LineBorder(Color.BLACK));
		}
		loadMembers();

		add(componentLabel, BorderLayout.NORTH);
		add(membersPanel, BorderLayout.SOUTH);
	}

	private void loadMembers()
	{
		int index = 0;

		membersPanel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 0.5;
		c.weighty = 0.0;

		Field firstTag = component.getFirstField();
		if (firstTag != null)
		{
			FieldPanel fieldPanel = MemberPanelFactory.createFieldPanel(
					prevMembers, firstTag, true);
			fieldPanel.setMaximumSize(new Dimension(getPreferredSize().width,
					fieldPanel.getPreferredSize().height));

			c.gridx = 0;
			c.gridy = index++;
			membersPanel.add(fieldPanel, c);
			members.add(fieldPanel);
		}

		for (Entry<Member, Boolean> entry : component.getMembers().entrySet())
		{
			if (isRequiredOnly && !entry.getValue())
			{
				continue;
			}

			if (entry.getKey() instanceof Field)
			{
				Field field = (Field) entry.getKey();
				if (!field.equals(firstTag))
				{
					FieldPanel fieldPanel = MemberPanelFactory
							.createFieldPanel(prevMembers, field,
									entry.getValue());
					fieldPanel.setMaximumSize(new Dimension(
							getPreferredSize().width, fieldPanel
									.getPreferredSize().height));

					c.gridx = 0;
					c.gridy = index++;
					membersPanel.add(fieldPanel, c);
					members.add(fieldPanel);
				}
			}

			if (entry.getKey() instanceof Group)
			{
				Group group = (Group) entry.getKey();

				GroupPanel groupPanel = MemberPanelFactory.createGroupPanel(
						prevMembers, group, isRequiredOnly, entry.getValue());
				groupPanel.setMaximumSize(new Dimension(
						getPreferredSize().width,
						groupPanel.getPreferredSize().height));

				c.gridx = 0;
				c.gridy = index++;
				membersPanel.add(groupPanel, c);
				members.add(groupPanel);
			}

			if (entry.getKey() instanceof Component)
			{
				Component component = (Component) entry.getKey();

				ComponentPanel componentPanel = MemberPanelFactory
						.createComponentPanel(prevMembers, component,
								isRequiredOnly, entry.getValue());

				componentPanel.setMaximumSize(new Dimension(
						getPreferredSize().width, componentPanel
								.getPreferredSize().height));

				c.gridx = 0;
				c.gridy = index++;
				membersPanel.add(componentPanel, c);
				members.add(componentPanel);
			}
		}

		// Cleanup
		prevMembers.clear();
	}
}
