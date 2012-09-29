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
 * ImportMessageActionListener.java
 * Sep 26, 2012
 */
package com.jramoyo.qfixmessenger.ui.listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jramoyo.fix.xml.MessageType;
import com.jramoyo.qfixmessenger.ui.QFixMessengerFrame;
import com.jramoyo.qfixmessenger.ui.QFixMessengerFrame.XmlFileFilter;

/**
 * @author jramoyo
 */
public class ImportMessageActionListener implements ActionListener
{
	private static final Logger logger = LoggerFactory
			.getLogger(ImportMessageActionListener.class);

	private QFixMessengerFrame frame;

	public ImportMessageActionListener(QFixMessengerFrame frame)
	{
		this.frame = frame;
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		JFileChooser jFileChooser = new JFileChooser();
		jFileChooser.setFileFilter(XmlFileFilter.INSTANCE);
		jFileChooser.setDialogTitle("Import Message");

		int choice = jFileChooser.showOpenDialog(frame);
		if (choice == JFileChooser.APPROVE_OPTION)
		{
			File file = jFileChooser.getSelectedFile();
			try
			{
				Unmarshaller unmarshaller = frame.getJaxbContext()
						.createUnmarshaller();
				@SuppressWarnings("unchecked")
				JAXBElement<MessageType> rootElement = (JAXBElement<MessageType>) unmarshaller
						.unmarshal(file);
				MessageType xmlMessageType = rootElement.getValue();

				frame.loadXmlMessage(xmlMessageType);
			} catch (Exception ex)
			{
				logger.error(
						"A JAXBException occurred while importing message.", ex);
				JOptionPane.showMessageDialog(frame, "Unable to open file!",
						"Error", JOptionPane.ERROR_MESSAGE);
			}
		}
	}
}
