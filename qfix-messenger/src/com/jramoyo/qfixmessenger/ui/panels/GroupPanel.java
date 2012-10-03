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
 * GroupPanel.java
 * 9 Jun 2011
 */
package com.jramoyo.qfixmessenger.ui.panels;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import quickfix.StringField;

import com.jramoyo.fix.model.Component;
import com.jramoyo.fix.model.Field;
import com.jramoyo.fix.model.Group;
import com.jramoyo.fix.model.Member;
import com.jramoyo.fix.xml.ComponentType;
import com.jramoyo.fix.xml.FieldType;
import com.jramoyo.fix.xml.GroupType;
import com.jramoyo.fix.xml.GroupsType;
import com.jramoyo.fix.xml.ObjectFactory;
import com.jramoyo.qfixmessenger.QFixMessengerConstants;

/**
 * @author jamoyo
 */
public class GroupPanel extends
		AbstractMemberPanel<Group, List<quickfix.Group>, GroupsType>
{
	private static final long serialVersionUID = -1327365939623841550L;

	private final JPanel blankPanel = new JPanel();

	private final Group group;

	private final boolean isRequired;

	private final boolean isRequiredOnly;

	private JLabel groupLabel;

	private JTextField groupTextField;

	private JButton setButton;

	private JScrollPane membersScrollPane;

	private List<List<MemberPanel<?, ?, ?>>> groups;

	public GroupPanel(Group group, boolean isRequiredOnly, boolean isRequired)
	{
		this.group = group;
		this.isRequired = isRequired;
		this.isRequiredOnly = isRequiredOnly;

		this.groups = new ArrayList<List<MemberPanel<?, ?, ?>>>();

		initComponents();
	}

	@Override
	public String getFixString()
	{
		StringBuilder sb = new StringBuilder("" + group.getNumber())
				.append('=').append(groupTextField.getText().trim())
				.append(QFixMessengerConstants.SOH);

		for (List<MemberPanel<?, ?, ?>> groupMembers : groups)
		{
			for (MemberPanel<?, ?, ?> memberPanel : groupMembers)
			{
				sb.append(memberPanel.getFixString());
				sb.append(QFixMessengerConstants.SOH);
			}
		}

		return sb.toString();
	}

	@Override
	public Group getMember()
	{
		return group;
	}

	public List<quickfix.Group> getQuickFixMember()
	{
		List<quickfix.Group> qfixGroups = new ArrayList<quickfix.Group>();

		for (List<MemberPanel<?, ?, ?>> groupMembers : groups)
		{
			quickfix.Group qfixGroup;
			Member firstMember = group.getFirstMember();
			if (firstMember instanceof Component)
			{
				Component firstComponent = (Component) firstMember;
				Field firstField = firstComponent.getFirstField();
				qfixGroup = new quickfix.Group(group.getNumber(),
						firstField.getNumber());
			} else
			{
				Field firstField = (Field) firstMember;
				qfixGroup = new quickfix.Group(group.getNumber(),
						firstField.getNumber());
			}

			for (MemberPanel<?, ?, ?> memberPanel : groupMembers)
			{
				if (memberPanel instanceof FieldPanel)
				{
					FieldPanel fieldPanel = (FieldPanel) memberPanel;
					if (fieldPanel.getQuickFixMember() != null)
					{
						qfixGroup.setField(fieldPanel.getQuickFixMember());
					}
				}

				if (memberPanel instanceof ComponentPanel)
				{
					ComponentPanel componentPanel = (ComponentPanel) memberPanel;
					for (StringField qfixField : componentPanel
							.getQuickFixMember().getFields())
					{
						qfixGroup.setField(qfixField);
					}

					for (quickfix.Group group : componentPanel
							.getQuickFixMember().getGroups())
					{
						qfixGroup.addGroup(group);
					}
				}

				if (memberPanel instanceof GroupPanel)
				{
					GroupPanel groupPanel = (GroupPanel) memberPanel;
					qfixGroups.addAll(groupPanel.getQuickFixMember());
				}
			}

			qfixGroups.add(qfixGroup);
		}

		return qfixGroups;
	}

	@Override
	public GroupsType getXmlMember()
	{
		ObjectFactory xmlObjectFactory = new ObjectFactory();
		GroupsType xmlGroupsType = xmlObjectFactory.createGroupsType();
		xmlGroupsType.setId(group.getNumber());
		xmlGroupsType.setName(group.getName());
		xmlGroupsType.setCount(groups.size());

		for (List<MemberPanel<?, ?, ?>> groupMembers : groups)
		{
			GroupType xmlGroupType = xmlObjectFactory.createGroupType();

			for (MemberPanel<?, ?, ?> memberPanel : groupMembers)
			{
				if (memberPanel instanceof FieldPanel)
				{
					FieldType xmlFieldType = ((FieldPanel) memberPanel)
							.getXmlMember();
					if (xmlFieldType != null)
					{
						xmlGroupType.getFieldOrGroupsOrComponent().add(
								xmlFieldType);
					}
				}

				if (memberPanel instanceof ComponentPanel)
				{
					ComponentType xmlComponentType = ((ComponentPanel) memberPanel)
							.getXmlMember();
					if (xmlComponentType != null)
					{
						xmlGroupType.getFieldOrGroupsOrComponent().add(
								xmlComponentType);
					}
				}

				if (memberPanel instanceof GroupPanel)
				{
					GroupsType xmlGroupsTypeMember = ((GroupPanel) memberPanel)
							.getXmlMember();
					if (xmlGroupsTypeMember != null)
					{
						xmlGroupType.getFieldOrGroupsOrComponent().add(
								xmlGroupsTypeMember);
					}
				}
			}

			xmlGroupsType.getGroup().add(xmlGroupType);
		}

		return xmlGroupsType;
	}

	public void populate(GroupsType xmlGroupsType)
	{
		groupTextField.setText(String.valueOf(xmlGroupsType.getCount()));
		loadMembers();

		for (int i = 0; i < xmlGroupsType.getGroup().size(); i++)
		{
			GroupType xmlGroupType = xmlGroupsType.getGroup().get(i);
			List<MemberPanel<?, ?, ?>> groupMembers = groups.get(i);

			for (Object xmlMember : xmlGroupType.getFieldOrGroupsOrComponent())
			{
				if (xmlMember instanceof FieldType)
				{
					FieldType xmlFieldType = (FieldType) xmlMember;
					FieldPanel fieldPanel = (FieldPanel) MemberPanelUtil
							.findMemberPanelByName(xmlFieldType.getName(),
									groupMembers);
					fieldPanel.populate(xmlFieldType);
				}

				if (xmlMember instanceof GroupsType)
				{
					GroupsType xmlGroupsTypeMember = (GroupsType) xmlMember;
					GroupPanel groupPanel = (GroupPanel) MemberPanelUtil
							.findMemberPanelByName(
									xmlGroupsTypeMember.getName(), groupMembers);
					groupPanel.populate(xmlGroupsTypeMember);
				}

				if (xmlMember instanceof ComponentType)
				{
					ComponentType xmlComponentType = (ComponentType) xmlMember;
					ComponentPanel componentPanel = (ComponentPanel) MemberPanelUtil
							.findMemberPanelByName(xmlComponentType.getName(),
									groupMembers);
					componentPanel.populate(xmlComponentType);
				}
			}
		}
	}

	private void initComponents()
	{
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		groupLabel = new JLabel(group.toString());
		groupLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		groupLabel.addMouseListener(new LinkMouseAdapter(this));
		groupLabel.setToolTipText("Double-click to look-up in FIXwiki");
		if (isRequired)
		{
			groupLabel.setForeground(Color.BLUE);
		}

		JPanel groupValuePanel = new JPanel();
		groupValuePanel.setLayout(new BoxLayout(groupValuePanel,
				BoxLayout.X_AXIS));

		groupTextField = new JTextField();
		setButton = new JButton("Set");
		setButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				loadMembers();
			}
		});

		groupValuePanel.add(groupTextField);
		groupValuePanel.add(setButton);

		membersScrollPane = new JScrollPane();

		c.fill = GridBagConstraints.BOTH;
		c.weightx = 0.5;
		c.weighty = 0.0;

		c.gridx = 0;
		c.gridy = 0;
		add(groupLabel, c);

		c.gridx = 0;
		c.gridy = 1;
		add(groupValuePanel, c);

		c.gridx = 0;
		c.gridy = 2;
		c.ipady = 150;
		add(membersScrollPane, c);
	}

	private void loadMembers()
	{
		// Clear the members
		groups.clear();

		// Reload the members
		String noOfGroupsString = groupTextField.getText().trim();
		if (noOfGroupsString != null && !noOfGroupsString.equals(""))
		{
			try
			{
				int noOfGroups = Integer.parseInt(noOfGroupsString);

				JPanel membersPanel = new JPanel();
				membersPanel.setLayout(new GridLayout(noOfGroups, 1));

				for (int i = 0; i < noOfGroups; i++)
				{
					List<MemberPanel<?, ?, ?>> groupMembers = new ArrayList<MemberPanel<?, ?, ?>>();

					int rows = 0;
					JPanel groupPanel = new JPanel();
					groupPanel.setLayout(new GridBagLayout());
					GridBagConstraints c = new GridBagConstraints();
					c.fill = GridBagConstraints.BOTH;
					c.weightx = 0.5;
					c.weighty = 0.0;

					JLabel groupLabel = new JLabel("Group #" + (i + 1));
					groupLabel.setFont(new Font(groupLabel.getFont().getName(),
							Font.BOLD, groupLabel.getFont().getSize()));

					c.gridx = 0;
					c.gridy = rows;
					groupPanel.add(groupLabel, c);
					rows++;

					Member firstMember = group.getFirstMember();
					if (firstMember != null)
					{
						if (firstMember instanceof Field)
						{
							FieldPanel fieldPanel = new FieldPanel(
									(Field) firstMember, true);

							fieldPanel.setMaximumSize(new Dimension(
									getPreferredSize().width, fieldPanel
											.getPreferredSize().height));

							c.gridx = 0;
							c.gridy = rows;
							groupPanel.add(fieldPanel, c);
							groupMembers.add(fieldPanel);
							rows++;
						}

						else if (firstMember instanceof Component)
						{
							ComponentPanel componentPanel = new ComponentPanel(
									(Component) firstMember, isRequiredOnly,
									true);

							componentPanel.setMaximumSize(new Dimension(
									getPreferredSize().width, componentPanel
											.getPreferredSize().height));

							c.gridx = 0;
							c.gridy = rows;
							groupPanel.add(componentPanel, c);
							groupMembers.add(componentPanel);
							rows++;
						}
					}

					for (Entry<Member, Boolean> entry : group.getMembers()
							.entrySet())
					{
						if (isRequiredOnly && !entry.getValue())
						{
							continue;
						}

						if (entry.getKey() instanceof Field)
						{
							Field field = (Field) entry.getKey();
							if (!field.equals(firstMember))
							{
								FieldPanel fieldPanel = new FieldPanel(field,
										entry.getValue());

								fieldPanel.setMaximumSize(new Dimension(
										getPreferredSize().width, fieldPanel
												.getPreferredSize().height));

								c.gridx = 0;
								c.gridy = rows;
								groupPanel.add(fieldPanel, c);
								groupMembers.add(fieldPanel);
								rows++;
							}
						}

						if (entry.getKey() instanceof Group)
						{
							Group group = (Group) entry.getKey();
							GroupPanel memberGroupPanel = new GroupPanel(group,
									isRequiredOnly, entry.getValue());

							groupPanel.setMaximumSize(new Dimension(
									getPreferredSize().width, groupPanel
											.getPreferredSize().height));

							c.gridx = 0;
							c.gridy = rows;
							groupPanel.add(memberGroupPanel, c);
							groupMembers.add(memberGroupPanel);
							rows++;
						}

						if (entry.getKey() instanceof Component)
						{
							Component component = (Component) entry.getKey();
							if (!component.equals(firstMember))
							{
								ComponentPanel componentPanel = new ComponentPanel(
										component, isRequiredOnly,
										entry.getValue());

								componentPanel
										.setMaximumSize(new Dimension(
												getPreferredSize().width,
												componentPanel
														.getPreferredSize().height));

								c.gridx = 0;
								c.gridy = rows;
								groupPanel.add(componentPanel, c);
								groupMembers.add(componentPanel);
								rows++;
							}
						}
					}

					groups.add(groupMembers);
					membersPanel.add(groupPanel);
				}

				membersScrollPane.getViewport().add(membersPanel);
			} catch (NumberFormatException ex)
			{
				JOptionPane.showMessageDialog(this, "Invalid number of group!",
						"Error", JOptionPane.ERROR_MESSAGE);
			}
		} else
		{
			membersScrollPane.getViewport().add(blankPanel);
		}
	}
}
