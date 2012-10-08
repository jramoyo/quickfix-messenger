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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JLayer;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.LayerUI;

import quickfix.StringField;

import com.jramoyo.fix.model.Component;
import com.jramoyo.fix.model.Field;
import com.jramoyo.fix.model.Group;
import com.jramoyo.fix.model.Member;
import com.jramoyo.fix.model.MemberOrder;
import com.jramoyo.fix.xml.ComponentType;
import com.jramoyo.fix.xml.FieldType;
import com.jramoyo.fix.xml.GroupType;
import com.jramoyo.fix.xml.GroupsType;
import com.jramoyo.fix.xml.ObjectFactory;
import com.jramoyo.qfixmessenger.QFixMessengerConstants;
import com.jramoyo.qfixmessenger.ui.QFixMessengerFrame;
import com.jramoyo.qfixmessenger.ui.layers.FieldValidationLayerUI;
import com.jramoyo.qfixmessenger.ui.util.TitledBorderUtil;

/**
 * @author jamoyo
 */
public class GroupPanel extends
		AbstractMemberPanel<Group, List<quickfix.Group>, GroupsType>
{
	private static final long serialVersionUID = -1327365939623841550L;

	private final boolean isRequired;

	private final boolean isRequiredOnly;

	private final List<List<MemberPanel<?, ?, ?>>> groups;

	private JLabel groupLabel;

	private LayerUI<JFormattedTextField> layerUI;

	private JFormattedTextField groupTextField;

	private JButton setButton;

	private JPanel groupPanels;

	private int initialNoOfGroups;

	public GroupPanel(QFixMessengerFrame frame, Group group,
			boolean isRequiredOnly, boolean isRequired, int initialNoOfGroups)
	{
		super(frame, group);
		this.isRequired = isRequired;
		this.isRequiredOnly = isRequiredOnly;
		this.initialNoOfGroups = initialNoOfGroups;

		this.groups = new ArrayList<List<MemberPanel<?, ?, ?>>>();

		initComponents();
	}

	@Override
	public String getFixString()
	{
		StringBuilder sb = new StringBuilder("" + getMember().getNumber())
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

	public List<quickfix.Group> getQuickFixMember()
	{
		List<quickfix.Group> qfixGroups = new ArrayList<quickfix.Group>();

		for (List<MemberPanel<?, ?, ?>> groupMembers : groups)
		{
			quickfix.Group qfixGroup;
			Member firstMember = getMember().getFirstMember();
			if (firstMember instanceof Component)
			{
				Component firstComponent = (Component) firstMember;
				Field firstField = firstComponent.getFirstField();
				qfixGroup = new quickfix.Group(getMember().getNumber(),
						firstField.getNumber());
			} else
			{
				Field firstField = (Field) firstMember;
				qfixGroup = new quickfix.Group(getMember().getNumber(),
						firstField.getNumber());
			}

			int i = 0;
			for (MemberPanel<?, ?, ?> memberPanel : groupMembers)
			{
				if (memberPanel instanceof FieldPanel)
				{
					FieldPanel fieldPanel = (FieldPanel) memberPanel;
					if (fieldPanel.getQuickFixMember() != null)
					{
						qfixGroup.setField(++i, fieldPanel.getQuickFixMember());
					}
				}

				if (memberPanel instanceof ComponentPanel)
				{
					ComponentPanel componentPanel = (ComponentPanel) memberPanel;
					for (StringField qfixField : componentPanel
							.getQuickFixMember().getFields())
					{
						qfixGroup.setField(++i, qfixField);
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
		xmlGroupsType.setId(getMember().getNumber());
		xmlGroupsType.setName(getMember().getName());
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

	@Override
	public boolean hasValidFormat()
	{
		boolean hasValidContent = true;

		for (List<MemberPanel<?, ?, ?>> groupMembers : groups)
		{
			for (MemberPanel<?, ?, ?> memberPanel : groupMembers)
			{
				hasValidContent = hasValidContent
						&& memberPanel.hasValidFormat();
			}
		}

		return hasValidContent;
	}

	public void populateXml(GroupsType xmlGroupsType)
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
					fieldPanel.populateXml(xmlFieldType);
				}

				if (xmlMember instanceof GroupsType)
				{
					GroupsType xmlGroupsTypeMember = (GroupsType) xmlMember;
					GroupPanel groupPanel = (GroupPanel) MemberPanelUtil
							.findMemberPanelByName(
									xmlGroupsTypeMember.getName(), groupMembers);
					groupPanel.populateXml(xmlGroupsTypeMember);
				}

				if (xmlMember instanceof ComponentType)
				{
					ComponentType xmlComponentType = (ComponentType) xmlMember;
					ComponentPanel componentPanel = (ComponentPanel) MemberPanelUtil
							.findMemberPanelByName(xmlComponentType.getName(),
									groupMembers);
					componentPanel.populateXml(xmlComponentType);
				}
			}
		}
	}

	List<List<MemberPanel<?, ?, ?>>> getGroups()
	{
		return groups;
	}

	int getNoOfGroups()
	{
		String noOfGroupsString = groupTextField.getText().trim();
		if (noOfGroupsString != null && !noOfGroupsString.equals(""))
		{
			try
			{
				return Integer.parseInt(noOfGroupsString);
			} catch (NumberFormatException ex)
			{
				// JFormattedTextField should prevent this
			}
		}

		return -1;
	}

	private GridBagConstraints createGridBagConstraints()
	{
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;

		c.weightx = 0.5;
		c.weighty = 0.0;

		c.gridx = 0;
		c.gridy = GridBagConstraints.RELATIVE;

		return c;
	}

	private void initComponents()
	{
		setLayout(new GridBagLayout());

		layerUI = new FieldValidationLayerUI(getFrame());

		groupLabel = new JLabel(getMember().toString());
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

		groupTextField = new JFormattedTextField(
				NumberFormat.getIntegerInstance());
		groupTextField.setFocusLostBehavior(JFormattedTextField.COMMIT);
		if (initialNoOfGroups > 0)
		{
			groupTextField.setText(String.valueOf(initialNoOfGroups));
		}
		setButton = new JButton("Set");
		setButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				getFrame().displayMainPanel();
			}
		});

		groupValuePanel.add(new JLayer<JFormattedTextField>(groupTextField,
				layerUI));
		groupValuePanel.add(setButton);

		groupPanels = new JPanel();
		groupPanels.setLayout(new GridBagLayout());

		loadMembers();

		add(groupLabel, createGridBagConstraints());
		add(groupValuePanel, createGridBagConstraints());
		add(groupPanels, createGridBagConstraints());
	}

	private void loadMembers()
	{
		if (initialNoOfGroups > 0)
		{
			for (int i = 0; i < initialNoOfGroups; i++)
			{
				List<MemberPanel<?, ?, ?>> groupMembers = new ArrayList<MemberPanel<?, ?, ?>>();

				JPanel groupPanel = new JPanel();
				groupPanel.setLayout(new GridBagLayout());

				TitledBorder titledBoarder = new TitledBorder(new LineBorder(
						Color.BLACK), getMember().getName() + " [" + (i + 1)
						+ "]");
				groupPanel.setBorder(TitledBorderUtil
						.formatTitle(titledBoarder));

				Member firstMember = getMember().getFirstMember();
				if (firstMember != null)
				{
					if (firstMember instanceof Field)
					{
						FieldPanel fieldPanel = GroupMemberPanelFactory
								.createFieldPanel(getFrame(),
										(Field) firstMember, i, true);
						fieldPanel.setMaximumSize(new Dimension(
								getPreferredSize().width, fieldPanel
										.getPreferredSize().height));

						groupPanel.add(fieldPanel, createGridBagConstraints());
						groupMembers.add(fieldPanel);
					}

					else if (firstMember instanceof Component)
					{
						ComponentPanel componentPanel = GroupMemberPanelFactory
								.createComponentPanel(getFrame(),
										(Component) firstMember, i,
										isRequiredOnly, true);
						componentPanel.setMaximumSize(new Dimension(
								getPreferredSize().width, componentPanel
										.getPreferredSize().height));

						groupPanel.add(componentPanel,
								createGridBagConstraints());
						groupMembers.add(componentPanel);
					}
				}

				for (Entry<MemberOrder, Boolean> entry : getMember()
						.getMembers().entrySet())
				{
					if (isRequiredOnly && !entry.getValue())
					{
						continue;
					}

					if (entry.getKey().getMember() instanceof Field)
					{
						Field field = (Field) entry.getKey().getMember();
						if (!field.equals(firstMember))
						{
							FieldPanel fieldPanel = GroupMemberPanelFactory
									.createFieldPanel(getFrame(), field, i,
											entry.getValue());
							fieldPanel.setMaximumSize(new Dimension(
									getPreferredSize().width, fieldPanel
											.getPreferredSize().height));

							groupPanel.add(fieldPanel,
									createGridBagConstraints());
							groupMembers.add(fieldPanel);
						}
					}

					else if (entry.getKey().getMember() instanceof Group)
					{
						Group group = (Group) entry.getKey().getMember();

						GroupPanel memberGroupPanel = GroupMemberPanelFactory
								.createGroupPanel(getFrame(), group, i,
										isRequiredOnly, entry.getValue());
						groupPanel.setMaximumSize(new Dimension(
								getPreferredSize().width, groupPanel
										.getPreferredSize().height));

						groupPanel.add(memberGroupPanel,
								createGridBagConstraints());
						groupMembers.add(memberGroupPanel);
					}

					else if (entry.getKey().getMember() instanceof Component)
					{
						Component component = (Component) entry.getKey()
								.getMember();
						if (!component.equals(firstMember))
						{
							ComponentPanel componentPanel = GroupMemberPanelFactory
									.createComponentPanel(getFrame(),
											component, i, isRequiredOnly,
											entry.getValue());
							componentPanel.setMaximumSize(new Dimension(
									getPreferredSize().width, componentPanel
											.getPreferredSize().height));

							groupPanel.add(componentPanel,
									createGridBagConstraints());
							groupMembers.add(componentPanel);
						}
					}
				}

				groups.add(groupMembers);
				groupPanels.add(groupPanel, createGridBagConstraints());
			}
		}

		else
		{
			groupPanels.removeAll();
		}
	}
}
