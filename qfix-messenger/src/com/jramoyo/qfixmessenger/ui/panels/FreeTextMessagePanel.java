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

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EtchedBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

/**
 * @author jamoyo
 */
public class FreeTextMessagePanel extends JPanel
{
	private static final long serialVersionUID = -7614167852761624847L;

	private JTextArea messageTextArea;

	public FreeTextMessagePanel()
	{
		initComponents();
	}

	public String getFixString()
	{
		return messageTextArea.getText();
	}

	private void initComponents()
	{
		setLayout(new BorderLayout());

		TitledBorder trailerBorder = new TitledBorder(new LineBorder(
				Color.BLACK), "Free Text");
		trailerBorder.setTitleFont(new Font(trailerBorder.getTitleFont()
				.getName(), Font.BOLD, 15));
		setBorder(trailerBorder);

		messageTextArea = new JTextArea(5, 60);
		messageTextArea.setLineWrap(true);

		JScrollPane messageTextScrollPane = new JScrollPane(messageTextArea);
		messageTextScrollPane.setBorder(new EtchedBorder());

		add(messageTextScrollPane, BorderLayout.NORTH);
	}

}
