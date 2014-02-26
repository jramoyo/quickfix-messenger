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
 * OpenProjectActionListener.java
 * Sep 25, 2012
 */
package com.jramoyo.qfixmessenger.ui.listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jramoyo.fix.xml.ProjectType;
import com.jramoyo.qfixmessenger.ui.QFixMessengerFrame;
import com.jramoyo.qfixmessenger.ui.QFixMessengerFrame.XmlFileFilter;

/**
 * @author jramoyo
 */
public class OpenProjectActionListener implements ActionListener
{
	private static final Logger logger = LoggerFactory
			.getLogger(OpenProjectActionListener.class);

	private QFixMessengerFrame frame;

	public OpenProjectActionListener(QFixMessengerFrame frame)
	{
		this.frame = frame;
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		if (frame.getActiveXmlProject() != null)
		{
			int choice = JOptionPane.showConfirmDialog(frame,
					"Do you want to save \""
							+ frame.getActiveXmlProject().getName() + "\"?",
					"Save Current Project", JOptionPane.YES_NO_CANCEL_OPTION);
			switch (choice)
			{
			case JOptionPane.NO_OPTION:
				break;
			case JOptionPane.YES_OPTION:
				frame.marshallActiveXmlProject();
				break;
			case JOptionPane.CANCEL_OPTION:
				return;
			}
		}

		JFileChooser jFileChooser = new JFileChooser();
		jFileChooser.setFileFilter(XmlFileFilter.INSTANCE);
		jFileChooser.setDialogTitle("Open Project");

		int choice = jFileChooser.showOpenDialog(frame);
		if (choice == JFileChooser.APPROVE_OPTION)
		{
			File file = jFileChooser.getSelectedFile();
			try
			{
				new UnmarshallWorker(frame, file).execute();
			} catch (Exception ex)
			{
				logger.error(
						"A JAXBException occurred while importing message.", ex);
				JOptionPane.showMessageDialog(frame, "Unable to open file!",
						"Error", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	private static class UnmarshallWorker extends
			SwingWorker<ProjectType, Void>
	{
		private QFixMessengerFrame frame;
		private File file;

		public UnmarshallWorker(QFixMessengerFrame frame, File file)
		{
			this.frame = frame;
			this.file = file;
		}

		@Override
		protected ProjectType doInBackground() throws Exception
		{
			Unmarshaller unmarshaller = frame.getMessenger().getJaxbContext()
					.createUnmarshaller();
			@SuppressWarnings("unchecked")
			JAXBElement<ProjectType> rootElement = (JAXBElement<ProjectType>) unmarshaller
					.unmarshal(file);
			return rootElement.getValue();
		}

		@Override
		protected void done()
		{
			ProjectType xmlProjectType;
			try
			{
				xmlProjectType = get();
				frame.setActiveXmlProject(xmlProjectType, file);
			} catch (Exception ex)
			{
				logger.error("An Exception occurred while importing message.",
						ex);
				JOptionPane.showMessageDialog(frame, "Unable to open file!",
						"Error", JOptionPane.ERROR_MESSAGE);
			}
		}
	}
}
