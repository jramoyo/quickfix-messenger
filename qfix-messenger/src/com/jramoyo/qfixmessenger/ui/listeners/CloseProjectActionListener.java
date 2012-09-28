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
 * CloseProjectActionListener.java
 * Sep 28, 2012
 */
package com.jramoyo.qfixmessenger.ui.listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JOptionPane;

import com.jramoyo.qfixmessenger.ui.QFixMessengerFrame;

/**
 * @author jramoyo
 */
public class CloseProjectActionListener implements ActionListener
{
	private QFixMessengerFrame frame;

	public CloseProjectActionListener(QFixMessengerFrame frame)
	{
		this.frame = frame;
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		int choice = JOptionPane.showConfirmDialog(frame,
				"Are you sure you want to close \""
						+ frame.getXmlProjectType().getName() + "\"?",
				"Close Project", JOptionPane.YES_NO_OPTION);
		if (choice == JOptionPane.YES_OPTION)
		{
			if (frame.getXmlProjectType() != null)
			{
				choice = JOptionPane.showConfirmDialog(frame,
						"Do you want to save \""
								+ frame.getXmlProjectType().getName() + "\"?",
						"Save Current Project",
						JOptionPane.YES_NO_CANCEL_OPTION);
				switch (choice)
				{
				case JOptionPane.NO_OPTION:
					break;
				case JOptionPane.YES_OPTION:
					frame.marshallXmlProjectType();
					break;
				case JOptionPane.CANCEL_OPTION:
					return;
				}
			}
			frame.setXmlProjectType(null);
		}
	}
}
