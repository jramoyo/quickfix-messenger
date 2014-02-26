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
 * FieldValidationLayerUI.java
 * Oct 6, 2012
 */
package com.jramoyo.qfixmessenger.ui.layers;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JLayer;
import javax.swing.plaf.LayerUI;

import com.jramoyo.qfixmessenger.ui.QFixMessengerFrame;
import com.jramoyo.qfixmessenger.ui.util.Icons;

/**
 * @author jramoyo
 */
public class FieldValidationLayerUI extends LayerUI<JFormattedTextField>
{
	private static final long serialVersionUID = 8928847899297723141L;

	private QFixMessengerFrame frame;

	public FieldValidationLayerUI(QFixMessengerFrame frame)
	{
		this.frame = frame;
	}

	@Override
	public void paint(Graphics g, JComponent c)
	{
		super.paint(g, c);

		JLayer<?> jlayer = (JLayer<?>) c;

		JFormattedTextField jFormattedTextField = (JFormattedTextField) jlayer
				.getView();
		if (jFormattedTextField.getFormatterFactory() != null
				&& !jFormattedTextField.isEditValid())
		{
			Graphics2D g2 = (Graphics2D) g.create();

			// Paint a red X
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);
			int w = c.getWidth();
			int h = c.getHeight();
			// int pad = 4;
			int s = 10;
			int pad = 10;
			int x = w - pad - s;
			int y = (h - s) / 2;
			ImageIcon icon = new ImageIcon(frame.getMessenger().getConfig()
					.getIconsLocation()
					+ Icons.INVALID_FIELD);
			g2.drawImage(icon.getImage(), x, y, null);
			g2.dispose();
		}
	}
}
