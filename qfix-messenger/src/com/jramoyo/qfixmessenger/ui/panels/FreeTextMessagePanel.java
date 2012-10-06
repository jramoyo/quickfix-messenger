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
 * FreeTextMessagePanel.java
 * 17 Jun 2011
 */
package com.jramoyo.qfixmessenger.ui.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.border.EtchedBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import quickfix.ConfigError;
import quickfix.DataDictionary;
import quickfix.InvalidMessage;
import quickfix.Session;

import com.jramoyo.fix.model.Message;
import com.jramoyo.fix.xml.MessageType;
import com.jramoyo.qfixmessenger.QFixMessenger;

/**
 * @author jamoyo
 */
public class FreeTextMessagePanel extends JPanel implements
		MemberPanel<Message, quickfix.Message, MessageType>
{
	private static final long serialVersionUID = -7614167852761624847L;

	private final QFixMessenger messenger;

	private final Session session;

	private final String appVersion;

	private final boolean isFixTSession;

	private JTextArea messageTextArea;

	public FreeTextMessagePanel(QFixMessenger messenger, Session session,
			String appVersion, boolean isFixTSession)
	{
		this.messenger = messenger;
		this.session = session;
		this.appVersion = appVersion;
		this.isFixTSession = isFixTSession;

		initComponents();
	}

	@Override
	public String getFixString()
	{
		return messageTextArea.getText();
	}

	@Override
	public Message getMember()
	{
		return null;
	}

	@Override
	public quickfix.Message getQuickFixMember()
	{
		quickfix.Message message = null;
		try
		{
			message = new quickfix.Message();

			if (!isFixTSession)
			{
				message.fromString(getFixString(), session.getDataDictionary(),
						false);
			} else
			{
				/*
				 * FIXT sessions require the data dictionary of both the session
				 * and the application version
				 */
				DataDictionary appDictionary = null;
				DataDictionary sessionDictionary = null;
				try
				{
					sessionDictionary = new DataDictionary(messenger
							.getConfig().getFixT11DictionaryLocation());
					appDictionary = new DataDictionary(messenger.getConfig()
							.getFixDictionaryLocation(appVersion));
					message.fromString(getFixString(), sessionDictionary,
							appDictionary, false);
				} catch (ConfigError ex)
				{
					message = null;
					JOptionPane.showMessageDialog(getParent(),
							"Unable to load Application "
									+ "version dictionary!", "Error",
							JOptionPane.ERROR_MESSAGE);
				}
			}
		} catch (InvalidMessage ex)
		{
			message = null;
			JOptionPane.showMessageDialog(getParent(), "Message is invalid!",
					"Error", JOptionPane.ERROR_MESSAGE);
		}

		return message;
	}

	@Override
	public MessageType getXmlMember()
	{
		throw new IllegalStateException(
				"FreeTextMessagePanel does not support XML Messages!");
	}

	@Override
	public boolean hasValidContent()
	{
		return true;
	}

	public void populateXml(MessageType xmlMessageType)
	{
		throw new IllegalStateException(
				"FreeTextMessagePanel does not support XML Messages!");
	}

	private void initComponents()
	{
		setLayout(new BorderLayout());

		TitledBorder titledBorder = new TitledBorder(
				new LineBorder(Color.BLACK), "Free Text");
		// TODO Workaround for Java Bug ID: 7022041
		Font trailerTitleBorderFont = UIManager.getDefaults().getFont(
				"TitledBorder.font");
		if (trailerTitleBorderFont != null)
		{
			titledBorder.setTitleFont(new Font(
					trailerTitleBorderFont.getName(), Font.BOLD, 15));
		}
		setBorder(titledBorder);

		messageTextArea = new JTextArea(5, 60);
		messageTextArea.setLineWrap(true);

		JScrollPane messageTextScrollPane = new JScrollPane(messageTextArea);
		messageTextScrollPane.setBorder(new EtchedBorder());

		add(messageTextScrollPane, BorderLayout.NORTH);
	}
}