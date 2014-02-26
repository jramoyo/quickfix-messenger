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
 * FrameAboutActionListener.java
 * 8 Jun 2011
 */
package com.jramoyo.qfixmessenger.ui.listeners;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EtchedBorder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jramoyo.qfixmessenger.ui.QFixMessengerFrame;

/**
 * @author jamoyo
 */
public class AboutActionListener implements ActionListener
{
	private static final Logger logger = LoggerFactory
			.getLogger(AboutActionListener.class);

	private QFixMessengerFrame frame;

	public AboutActionListener(QFixMessengerFrame frame)
	{
		this.frame = frame;
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 0.5;
		c.weighty = 0.0;
		c.ipadx = 2;
		c.ipady = 2;

		JLabel titleLabel = new JLabel("<html><b>QuickFIX Messenger</b></html>");
		JLabel nameLabel = new JLabel("<html><i>by Jan Amoyo</i></html>");
		JLabel emailLabel = new JLabel("<html>jramoyo@gmail.com</html>");
		JLabel webpageLabel = new JLabel(
				"<html><a href=''>quickfix-messenger</a></html>");
		webpageLabel.addMouseListener(new LinkMouseAdapter(this, frame
				.getMessenger().getConfig().getHomeUrl()));
		webpageLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

		JLabel licenseLabel = new JLabel("<html><b>License</b></html>");

		JTextArea licenseText = new JTextArea(readLicenseFile(), 15, 60);
		licenseText.setWrapStyleWord(true);
		licenseText.setLineWrap(true);
		licenseText.setEditable(false);

		JScrollPane licenseTextScrollPane = new JScrollPane(licenseText);
		licenseText.setPreferredSize(new Dimension(400, 400));
		licenseTextScrollPane.setBorder(new EtchedBorder());

		c.gridx = 0;
		c.gridy = 0;
		panel.add(titleLabel, c);

		c.gridx = 0;
		c.gridy = 1;
		panel.add(nameLabel, c);

		c.gridx = 0;
		c.gridy = 2;
		panel.add(emailLabel, c);

		c.gridx = 0;
		c.gridy = 3;
		panel.add(webpageLabel, c);

		c.gridx = 0;
		c.gridy = 4;
		panel.add(Box.createRigidArea(new Dimension(50, 10)), c);

		c.gridx = 0;
		c.gridy = 5;
		panel.add(licenseLabel, c);

		c.gridx = 0;
		c.gridy = 6;
		panel.add(licenseTextScrollPane, c);

		JOptionPane.showMessageDialog(frame, panel, "About QuickFIX Messenger",
				JOptionPane.PLAIN_MESSAGE);
	}

	private void goToWikiPage(String url) throws IOException
	{
		java.awt.Desktop.getDesktop().browse(java.net.URI.create(url));
	}

	private String readLicenseFile()
	{
		StringBuilder license = new StringBuilder();

		BufferedReader reader = null;
		try
		{
			reader = new BufferedReader(new FileReader(frame.getMessenger()
					.getConfig().getLicenseFileLocation()));

			String line = null;
			while ((line = reader.readLine()) != null)
			{
				license.append(line).append("\n");
			}
		} catch (IOException ex)
		{
			logger.error("An error occured while reading license file!", ex);
			license.append("An error occured while reading license file!\n");
			license.append("Please refer to log file.");
		} finally
		{
			if (reader != null)
			{
				try
				{
					reader.close();
				} catch (IOException ex)
				{
					logger.error("An error occured"
							+ " while closing license file!", ex);
				}
			}
		}

		return license.toString();
	}

	private static class LinkMouseAdapter extends MouseAdapter
	{
		private AboutActionListener listener;

		private String url;

		public LinkMouseAdapter(AboutActionListener listener, String url)
		{
			this.listener = listener;
			this.url = url;
		}

		@Override
		public void mouseClicked(MouseEvent e)
		{
			if (e.getClickCount() == 2)
			{
				try
				{
					listener.goToWikiPage(url);
				} catch (IOException ex)
				{
					logger.error("An exception occured!", ex);
					JOptionPane.showMessageDialog(listener.frame,
							"An exception occured:\n"
									+ Arrays.toString(ex.getStackTrace()),
							"Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		}
	}

}
