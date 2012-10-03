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
 * MessagePanel.java
 * Oct 2, 2012
 */
package com.jramoyo.qfixmessenger.ui.panels;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import quickfix.Session;
import quickfix.StringField;
import quickfix.field.ApplVerID;

import com.jramoyo.fix.model.Component;
import com.jramoyo.fix.model.Field;
import com.jramoyo.fix.model.FixDictionary;
import com.jramoyo.fix.model.Group;
import com.jramoyo.fix.model.Member;
import com.jramoyo.fix.model.Message;
import com.jramoyo.fix.xml.BodyType;
import com.jramoyo.fix.xml.ComponentType;
import com.jramoyo.fix.xml.FieldType;
import com.jramoyo.fix.xml.GroupsType;
import com.jramoyo.fix.xml.HeaderType;
import com.jramoyo.fix.xml.MessageType;
import com.jramoyo.fix.xml.ObjectFactory;
import com.jramoyo.fix.xml.SessionType;
import com.jramoyo.fix.xml.TrailerType;
import com.jramoyo.qfixmessenger.QFixMessengerConstants;
import com.jramoyo.qfixmessenger.quickfix.util.QFixUtil;
import com.jramoyo.qfixmessenger.ui.QFixMessengerFrame;

/**
 * @author jramoyo
 */
public class MessagePanel extends JPanel implements
		MemberPanel<Message, quickfix.Message, MessageType>
{
	private static final long serialVersionUID = 7937359075224178112L;

	private final Session session;

	private final String appVersion;

	private final Message message;

	private final boolean isRequiredOnly;

	private final boolean isModifyHeader;

	private final boolean isModifyTrailer;

	private final boolean isFixTSession;

	private final MemberPanelCache memberPanelCache;

	private final List<MemberPanel<?, ?, ?>> headerMembers;

	private final List<MemberPanel<?, ?, ?>> bodyMembers;

	private final List<MemberPanel<?, ?, ?>> trailerMembers;

	private final FixDictionary dictionary;

	private final FixDictionary fixTDictionary;

	private MessagePanel(MessagePanelBuilder builder)
	{
		this.session = builder.session;
		this.appVersion = builder.appVersion;

		this.message = builder.message;

		this.isRequiredOnly = builder.isRequiredOnly;
		this.isModifyHeader = builder.isModifyHeader;
		this.isModifyTrailer = builder.isModifyTrailer;
		this.isFixTSession = builder.isFixTSession;

		this.headerMembers = new ArrayList<MemberPanel<?, ?, ?>>();
		this.bodyMembers = new ArrayList<MemberPanel<?, ?, ?>>();
		this.trailerMembers = new ArrayList<MemberPanel<?, ?, ?>>();

		this.memberPanelCache = builder.memberPanelCache;

		this.dictionary = builder.dictionary;
		this.fixTDictionary = builder.fixTDictionary;

		initComponents();
	}

	public List<MemberPanel<?, ?, ?>> getHeaderMembers()
	{
		return Collections.unmodifiableList(headerMembers);
	}

	public List<MemberPanel<?, ?, ?>> getBodyMembers()
	{
		return Collections.unmodifiableList(bodyMembers);
	}

	public List<MemberPanel<?, ?, ?>> getTrailerMembers()
	{
		return Collections.unmodifiableList(trailerMembers);
	}

	@Override
	public String getFixString()
	{
		// TODO
		return null;
	}

	@Override
	public Message getMember()
	{
		return message;
	}

	@Override
	public quickfix.Message getQuickFixMember()
	{
		quickfix.Message qfixMessage = session.getMessageFactory().create(
				session.getSessionID().getBeginString(), message.getMsgType());

		if (isFixTSession)
		{
			ApplVerID applVerID = new ApplVerID(
					QFixMessengerConstants.APPVER_ID_MAP.get(appVersion));
			qfixMessage.getHeader().setField(applVerID);
		}

		if (isModifyHeader)
		{
			for (MemberPanel<?, ?, ?> memberPanel : headerMembers)
			{
				if (memberPanel instanceof FieldPanel)
				{
					FieldPanel fieldPanel = (FieldPanel) memberPanel;
					if (fieldPanel.getQuickFixMember() != null)
					{
						qfixMessage.getHeader().setField(
								fieldPanel.getQuickFixMember());
					}
				}
			}
		}

		for (MemberPanel<?, ?, ?> memberPanel : bodyMembers)
		{
			if (memberPanel instanceof FieldPanel)
			{
				FieldPanel fieldPanel = (FieldPanel) memberPanel;
				if (fieldPanel.getQuickFixMember() != null)
				{
					qfixMessage.setField(fieldPanel.getQuickFixMember());
				}
			}

			if (memberPanel instanceof GroupPanel)
			{
				GroupPanel groupPanel = (GroupPanel) memberPanel;
				for (quickfix.Group group : groupPanel.getQuickFixMember())
				{
					qfixMessage.addGroup(group);
				}
			}

			if (memberPanel instanceof ComponentPanel)
			{
				ComponentPanel componentPanel = (ComponentPanel) memberPanel;
				for (quickfix.StringField field : componentPanel
						.getQuickFixMember().getFields())
				{
					qfixMessage.setField(field);
				}

				for (quickfix.Group group : componentPanel.getQuickFixMember()
						.getGroups())
				{
					qfixMessage.addGroup(group);
				}
			}
		}

		if (isModifyTrailer)
		{
			for (MemberPanel<?, ?, ?> memberPanel : trailerMembers)
			{
				if (memberPanel instanceof FieldPanel)
				{
					FieldPanel fieldPanel = (FieldPanel) memberPanel;
					if (fieldPanel.getQuickFixMember() != null)
					{
						StringField field = fieldPanel.getQuickFixMember();
						qfixMessage.getTrailer().setField(field);
					}
				}
			}
		}

		return qfixMessage;
	}

	@Override
	public MessageType getXmlMember()
	{
		ObjectFactory xmlObjectFactory = new ObjectFactory();

		MessageType xmlMessageType = xmlObjectFactory.createMessageType();
		xmlMessageType.setName(message.getName());
		xmlMessageType.setMsgType(message.getMsgType());
		xmlMessageType.setIsRequiredOnly(isRequiredOnly);

		SessionType xmlSessionType = xmlObjectFactory.createSessionType();
		xmlSessionType.setName(QFixUtil.getSessionName(session.getSessionID()));
		if (isFixTSession)
		{
			xmlSessionType.setAppVersionId(appVersion);
		}

		HeaderType xmlHeaderType = null;
		BodyType xmlBodyType = null;
		TrailerType xmlTrailerType = null;

		if (isModifyHeader)
		{
			xmlHeaderType = xmlObjectFactory.createHeaderType();
			for (MemberPanel<?, ?, ?> memberPanel : headerMembers)
			{
				if (memberPanel instanceof FieldPanel)
				{
					FieldPanel fieldPanel = (FieldPanel) memberPanel;
					FieldType xmlFieldType = fieldPanel.getXmlMember();
					if (xmlFieldType != null)
					{
						xmlHeaderType.getField().add(xmlFieldType);
					}
				}
			}
		}

		xmlBodyType = xmlObjectFactory.createBodyType();
		for (MemberPanel<?, ?, ?> memberPanel : bodyMembers)
		{
			if (memberPanel instanceof FieldPanel)
			{
				FieldPanel fieldPanel = (FieldPanel) memberPanel;
				FieldType xmlFieldType = fieldPanel.getXmlMember();
				if (xmlFieldType != null)
				{
					xmlBodyType.getFieldOrGroupsOrComponent().add(xmlFieldType);
				}
			}

			if (memberPanel instanceof GroupPanel)
			{
				GroupPanel groupPanel = (GroupPanel) memberPanel;
				GroupsType xmlGroupsType = groupPanel.getXmlMember();
				if (xmlGroupsType != null)
				{
					xmlBodyType.getFieldOrGroupsOrComponent()
							.add(xmlGroupsType);
				}
			}

			if (memberPanel instanceof ComponentPanel)
			{
				ComponentPanel componentPanel = (ComponentPanel) memberPanel;
				ComponentType xmlComponentType = componentPanel.getXmlMember();
				if (xmlComponentType != null)
				{
					xmlBodyType.getFieldOrGroupsOrComponent().add(
							componentPanel.getXmlMember());
				}
			}
		}

		if (isModifyTrailer)
		{
			xmlTrailerType = xmlObjectFactory.createTrailerType();
			for (MemberPanel<?, ?, ?> memberPanel : trailerMembers)
			{
				if (memberPanel instanceof FieldPanel)
				{
					FieldPanel fieldPanel = (FieldPanel) memberPanel;
					FieldType xmlFieldType = fieldPanel.getXmlMember();
					if (xmlFieldType != null)
					{
						xmlTrailerType.getField().add(xmlFieldType);
					}
				}
			}
		}

		xmlMessageType.setSession(xmlSessionType);
		xmlMessageType.setHeader(xmlHeaderType);
		xmlMessageType.setBody(xmlBodyType);
		xmlMessageType.setTrailer(xmlTrailerType);

		return xmlMessageType;
	}

	public void populate(MessageType xmlMessageType)
	{
		HeaderType xmlHeaderType = xmlMessageType.getHeader();
		if (xmlHeaderType != null)
		{
			for (Object xmlMember : xmlHeaderType.getField())
			{
				if (xmlMember instanceof FieldType)
				{
					FieldType xmlFieldType = (FieldType) xmlMember;
					FieldPanel fieldPanel = (FieldPanel) MemberPanelUtil
							.findMemberPanelByName(xmlFieldType.getName(),
									headerMembers);
					fieldPanel.populate(xmlFieldType);
				}
			}
		}

		BodyType xmlBodyType = xmlMessageType.getBody();
		for (Object xmlMember : xmlBodyType.getFieldOrGroupsOrComponent())
		{
			if (xmlMember instanceof FieldType)
			{
				FieldType xmlFieldType = (FieldType) xmlMember;
				FieldPanel fieldPanel = (FieldPanel) MemberPanelUtil
						.findMemberPanelByName(xmlFieldType.getName(),
								bodyMembers);
				fieldPanel.populate(xmlFieldType);
			}

			if (xmlMember instanceof GroupsType)
			{
				GroupsType xmlGroupsType = (GroupsType) xmlMember;
				GroupPanel groupPanel = (GroupPanel) MemberPanelUtil
						.findMemberPanelByName(xmlGroupsType.getName(),
								bodyMembers);
				groupPanel.populate(xmlGroupsType);
			}

			if (xmlMember instanceof ComponentType)
			{
				ComponentType xmlComponentTypeMember = (ComponentType) xmlMember;
				ComponentPanel componentPanel = (ComponentPanel) MemberPanelUtil
						.findMemberPanelByName(
								xmlComponentTypeMember.getName(), bodyMembers);
				componentPanel.populate(xmlComponentTypeMember);
			}
		}

		TrailerType xmlTrailerType = xmlMessageType.getTrailer();
		if (xmlTrailerType != null)
		{
			for (Object xmlMember : xmlTrailerType.getField())
			{
				if (xmlMember instanceof FieldType)
				{
					FieldType xmlFieldType = (FieldType) xmlMember;
					FieldPanel fieldPanel = (FieldPanel) MemberPanelUtil
							.findMemberPanelByName(xmlFieldType.getName(),
									trailerMembers);
					fieldPanel.populate(xmlFieldType);
				}
			}
		}
	}

	private void initComponents()
	{
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		// Load the header
		if (isModifyHeader)
		{
			JPanel headerPanel = new JPanel();
			headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));

			TitledBorder headerBorder = new TitledBorder(new LineBorder(
					Color.BLACK), "Message Header");
			// TODO Workaround for Java Bug ID: 7022041
			Font headerTitleBorderFont = UIManager.getDefaults().getFont(
					"TitledBorder.font");
			if (headerTitleBorderFont != null)
			{
				headerBorder.setTitleFont(new Font(headerTitleBorderFont
						.getName(), Font.BOLD, 15));
			}
			headerPanel.setBorder(headerBorder);

			if (!isFixTSession)
			{
				for (Entry<Member, Boolean> entry : dictionary.getHeader()
						.getMembers().entrySet())
				{
					loadMember(headerPanel,
							memberPanelCache.getHeaderMembers(), headerMembers,
							entry);
				}
			} else
			{
				for (Entry<Member, Boolean> entry : fixTDictionary.getHeader()
						.getMembers().entrySet())
				{
					loadMember(headerPanel,
							memberPanelCache.getHeaderMembers(), headerMembers,
							entry);
				}
			}

			add(headerPanel);
			add(Box.createRigidArea(new Dimension(0, 20)));
		}

		// Load the body
		JPanel bodyPanel = new JPanel();
		bodyPanel.setLayout(new BoxLayout(bodyPanel, BoxLayout.Y_AXIS));

		TitledBorder bodyBorder = new TitledBorder(new LineBorder(Color.BLACK),
				"Message Body");
		// TODO Workaround for Java Bug ID: 7022041
		Font bodyTitleBorderFont = UIManager.getDefaults().getFont(
				"TitledBorder.font");
		if (bodyTitleBorderFont != null)
		{
			bodyBorder.setTitleFont(new Font(bodyTitleBorderFont.getName(),
					Font.BOLD, 15));
		}
		bodyPanel.setBorder(bodyBorder);

		for (Entry<Member, Boolean> entry : message.getMembers().entrySet())
		{
			loadMember(bodyPanel, memberPanelCache.getBodyMembers(),
					bodyMembers, entry);
		}

		add(bodyPanel);

		// Load the trailer
		if (isModifyTrailer)
		{
			JPanel trailerPanel = new JPanel();
			trailerPanel
					.setLayout(new BoxLayout(trailerPanel, BoxLayout.Y_AXIS));

			TitledBorder trailerBorder = new TitledBorder(new LineBorder(
					Color.BLACK), "Message Trailer");
			// TODO Workaround for Java Bug ID: 7022041
			Font trailerTitleBorderFont = UIManager.getDefaults().getFont(
					"TitledBorder.font");
			if (trailerTitleBorderFont != null)
			{
				trailerBorder.setTitleFont(new Font(trailerTitleBorderFont
						.getName(), Font.BOLD, 15));
			}
			trailerPanel.setBorder(trailerBorder);

			if (!isFixTSession)
			{
				for (Entry<Member, Boolean> entry : dictionary.getTrailer()
						.getMembers().entrySet())
				{
					loadMember(trailerPanel,
							memberPanelCache.getTrailerMembers(),
							trailerMembers, entry);
				}
			} else
			{
				for (Entry<Member, Boolean> entry : fixTDictionary.getTrailer()
						.getMembers().entrySet())
				{
					loadMember(trailerPanel,
							memberPanelCache.getTrailerMembers(),
							trailerMembers, entry);
				}
			}

			add(Box.createRigidArea(new Dimension(0, 20)));
			add(trailerPanel);
		}
	}

	private void loadMember(JPanel mainPanel,
			List<MemberPanel<?, ?, ?>> previousMemberList,
			List<MemberPanel<?, ?, ?>> memberList, Entry<Member, Boolean> entry)
	{
		if (isRequiredOnly && !entry.getValue())
		{
			return;
		}

		if (entry.getKey() instanceof Field)
		{
			Field field = (Field) entry.getKey();

			FieldPanel fieldPanel = MemberPanelFactory.createFieldPanel(
					previousMemberList, field, entry.getValue());
			fieldPanel.setMaximumSize(new Dimension(
					QFixMessengerFrame.MIDDLE_PANEL_WIDTH, fieldPanel
							.getPreferredSize().height));

			mainPanel.add(fieldPanel);
			memberList.add(fieldPanel);
		}

		if (entry.getKey() instanceof Group)
		{
			Group group = (Group) entry.getKey();

			GroupPanel groupPanel = MemberPanelFactory
					.createGroupPanel(previousMemberList, group,
							isRequiredOnly, entry.getValue());
			groupPanel.setMaximumSize(new Dimension(
					QFixMessengerFrame.MIDDLE_PANEL_WIDTH, groupPanel
							.getPreferredSize().height));

			mainPanel.add(groupPanel);
			memberList.add(groupPanel);
		}

		if (entry.getKey() instanceof Component)
		{
			Component component = (Component) entry.getKey();

			ComponentPanel componentPanel = MemberPanelFactory
					.createComponentPanel(previousMemberList, component,
							isRequiredOnly, entry.getValue());
			componentPanel.setMaximumSize(new Dimension(
					QFixMessengerFrame.MIDDLE_PANEL_WIDTH, componentPanel
							.getPreferredSize().height));

			mainPanel.add(componentPanel);
			memberList.add(componentPanel);
		}
	}

	public static class MessagePanelBuilder
	{
		private Session session;

		private String appVersion;

		private Message message;

		private boolean isRequiredOnly;

		private boolean isModifyHeader;

		private boolean isModifyTrailer;

		private boolean isFixTSession;

		private MemberPanelCache memberPanelCache;

		private List<MemberPanel<?, ?, ?>> prevHeaderMembers;

		private List<MemberPanel<?, ?, ?>> prevBodyMembers;

		private List<MemberPanel<?, ?, ?>> prevTrailerMembers;

		private FixDictionary dictionary;

		private FixDictionary fixTDictionary;

		public MessagePanel build()
		{
			return new MessagePanel(this);
		}

		public Session getSession()
		{
			return session;
		}

		public void setSession(Session session)
		{
			this.session = session;
		}

		public String getAppVersion()
		{
			return appVersion;
		}

		public void setAppVersion(String appVersion)
		{
			this.appVersion = appVersion;
		}

		public Message getMessage()
		{
			return message;
		}

		public void setMessage(Message message)
		{
			this.message = message;
		}

		public boolean isRequiredOnly()
		{
			return isRequiredOnly;
		}

		public void setIsRequiredOnly(boolean isRequiredOnly)
		{
			this.isRequiredOnly = isRequiredOnly;
		}

		public boolean isModifyHeader()
		{
			return isModifyHeader;
		}

		public void setIsModifyHeader(boolean isModifyHeader)
		{
			this.isModifyHeader = isModifyHeader;
		}

		public boolean isModifyTrailer()
		{
			return isModifyTrailer;
		}

		public void setIsModifyTrailer(boolean isModifyTrailer)
		{
			this.isModifyTrailer = isModifyTrailer;
		}

		public boolean isFixTSession()
		{
			return isFixTSession;
		}

		public void setIsFixTSession(boolean isFixTSession)
		{
			this.isFixTSession = isFixTSession;
		}

		public MemberPanelCache getMemberPanelCache()
		{
			return memberPanelCache;
		}

		public void setMemberPanelCache(MemberPanelCache memberPanelCache)
		{
			this.memberPanelCache = memberPanelCache;
		}

		public List<MemberPanel<?, ?, ?>> getPrevHeaderMembers()
		{
			return prevHeaderMembers;
		}

		public void setPrevHeaderMembers(
				List<MemberPanel<?, ?, ?>> prevHeaderMembers)
		{
			this.prevHeaderMembers = prevHeaderMembers;
		}

		public List<MemberPanel<?, ?, ?>> getPrevBodyMembers()
		{
			return prevBodyMembers;
		}

		public void setPrevBodyMembers(
				List<MemberPanel<?, ?, ?>> prevBodyMembers)
		{
			this.prevBodyMembers = prevBodyMembers;
		}

		public List<MemberPanel<?, ?, ?>> getPrevTrailerMembers()
		{
			return prevTrailerMembers;
		}

		public void setPrevTrailerMembers(
				List<MemberPanel<?, ?, ?>> prevTrailerMembers)
		{
			this.prevTrailerMembers = prevTrailerMembers;
		}

		public FixDictionary getDictionary()
		{
			return dictionary;
		}

		public void setDictionary(FixDictionary dictionary)
		{
			this.dictionary = dictionary;
		}

		public FixDictionary getFixTDictionary()
		{
			return fixTDictionary;
		}

		public void setFixTDictionary(FixDictionary fixTDictionary)
		{
			this.fixTDictionary = fixTDictionary;
		}
	}
}
