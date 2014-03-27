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
 * - Neither the name of the authors nor the names of its contributors 
 *   may be used to endorse or promote products derived from this software 
 *   without specific prior written permission.
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
 * LogFileDialog.java
 * Aug 5, 2013
 */
package com.jramoyo.qfixmessenger.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;

import org.apache.log4j.Logger;

/**
 * LogFileDialog
 * 
 * @author derek.yang
 */
public class LogfileDialog extends JDialog
{

	private static final long serialVersionUID = -1373316797900491533L;

	private static final Logger logger = Logger.getLogger(LogfileDialog.class);

	private final QFixMessengerFrame frame;

	private JTabbedPane logTabPane;

	private JTextArea eventTextArea;

	private JTextArea messageTextArea;

	public LogfileDialog(QFixMessengerFrame frame)
	{
		this.frame = frame;
	}

	public void launch(String beginStr)
	{
		initDialog();
		initComponents();
		setVisible(true);
		loadLogfile(beginStr);
	}

	public void loadLogfile(String beginStr)
	{
		if (beginStr == null || beginStr.isEmpty())
		{
			return;
		}

		String path = frame.getMessenger().getConfig().getLogFilePath();
		String eventFileName = path + beginStr;
		String messengeFileName = path + beginStr;

		if (frame.getMessenger().getConfig().isInitiator())
		{
			eventFileName += "-INIT-ACCEPT.event.log";
			messengeFileName += "-INIT-ACCEPT.messages.log";
		} else
		{
			eventFileName += "-ACCEPT-INIT.event.log";
			messengeFileName += "-ACCEPT-INIT.messages.log";
		}

		Path eventPath = Paths.get(eventFileName);
		Path messagePath = Paths.get(messengeFileName);

		try
		{
			List<String> eventLines = Files.readAllLines(eventPath,
					Charset.defaultCharset());
			List<String> messageLines = Files.readAllLines(messagePath,
					Charset.defaultCharset());

			for (String l : eventLines)
			{
				eventTextArea.append(l);
				eventTextArea.append("\n");
			}

			for (String l : messageLines)
			{
				messageTextArea.append(l);
				messageTextArea.append("\n");
			}
		} catch (IOException e)
		{
			logger.error("Exception in LogfileDialog: " + e.getMessage());
		}

	}

	private void initDialog()
	{

		String title = "Logfile";
		if (frame.getMessenger().getConfig().isInitiator())
		{
			title = title + " (Initiator)";
		} else
		{
			title = title + " (Acceptor)";
		}
		setTitle(title);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	}

	private void initComponents()
	{

		setLayout(new BorderLayout());

		eventTextArea = new JTextArea();
		messageTextArea = new JTextArea();

		JScrollPane eventPane = new JScrollPane(eventTextArea);
		JScrollPane messagePane = new JScrollPane(messageTextArea);

		logTabPane = new JTabbedPane();
		logTabPane.setPreferredSize(new Dimension(500, 500));
		logTabPane.setTabPlacement(JTabbedPane.BOTTOM);
		logTabPane.addTab("Event", eventPane);
		logTabPane.addTab("Message", messagePane);

		add(logTabPane);
		pack();
	}

}
