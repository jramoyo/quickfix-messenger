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
 * SessionStatusMIActionListener.java
 * 6 Jun 2011
 */
package com.jramoyo.qfixmessenger.ui.listeners;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import quickfix.Session;

import com.jramoyo.qfixmessenger.ui.QFixMessengerFrame;

/**
 * @author jamoyo
 */
public class SessionStatusActionListener implements ActionListener
{
	private QFixMessengerFrame frame;
	private Session session;

	public SessionStatusActionListener(QFixMessengerFrame frame, Session session)
	{
		this.frame = frame;
		this.session = session;
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(7, 1));

		JLabel versionLabel = new JLabel(
				"<html><b>Version:</b> <i><font color='blue'>"
						+ session.getSessionID().getBeginString()
						+ "</font></i></html>");

		JLabel senderCompIdLabel = new JLabel(
				"<html><b>SenderCompID:</b> <i><font color='blue'>"
						+ session.getSessionID().getSenderCompID()
						+ "</font></i></html>");

		JLabel targetCompIdLabel = new JLabel(
				"<html><b>TargetCompID:</b> <i><font color='blue'>"
						+ session.getSessionID().getTargetCompID()
						+ "</font></i></html>");

		JLabel isLoggedOnLabel;
		if (session.isLoggedOn())
		{
			isLoggedOnLabel = new JLabel(
					"<html><b>Logged On:</b> <i><font color='green'>Yes</font></i></html>");
		} else
		{
			isLoggedOnLabel = new JLabel(
					"<html><b>Logged On:</b> <i><font color='red'>NO</font></i></html>");
		}

		JLabel expectedInSeqLabel = new JLabel(
				"<html><b>Expected IN Seq:</b> <i><font color='blue'>"
						+ session.getExpectedTargetNum() + "</font></i></html>");

		JLabel expectedOutSeqLabel = new JLabel(
				"<html><b>Expected OUT Seq:</b> <i><font color='blue'>"
						+ session.getExpectedSenderNum() + "</font></i></html>");

		panel.add(versionLabel);
		panel.add(senderCompIdLabel);
		panel.add(targetCompIdLabel);
		panel.add(Box.createRigidArea(new Dimension(50, 10)));
		panel.add(isLoggedOnLabel);
		panel.add(expectedInSeqLabel);
		panel.add(expectedOutSeqLabel);

		JOptionPane.showMessageDialog(frame, panel, "Session Details",
				JOptionPane.PLAIN_MESSAGE);
	}

}
