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
 * AbstractMemberPanel.java
 * 14 Jun 2011
 */
package com.jramoyo.qfixmessenger.ui.panels;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.Arrays;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jramoyo.qfixmessenger.QFixMessengerConstants;

/**
 * @author jamoyo
 */
public abstract class AbstractMemberPanel extends JPanel implements MemberPanel
{
	private static final Logger logger = LoggerFactory
			.getLogger(AbstractMemberPanel.class);

	private static final long serialVersionUID = 6904389112190383945L;

	public AbstractMemberPanel()
	{
		setOpaque(true);
	}

	private void goToWikiPage() throws IOException
	{
		String url = QFixMessengerConstants.FIX_WIKI_URL
				+ getMember().getName();
		java.awt.Desktop.getDesktop().browse(java.net.URI.create(url));
	}

	protected static class LinkMouseAdapter extends MouseAdapter
	{
		private AbstractMemberPanel memberPanel;

		public LinkMouseAdapter(AbstractMemberPanel memberPanel)
		{
			this.memberPanel = memberPanel;
		}

		@Override
		public void mouseClicked(MouseEvent e)
		{
			if (e.getClickCount() == 2)
			{
				try
				{
					memberPanel.goToWikiPage();
				} catch (IOException ex)
				{
					logger.error("An exception occured!", ex);
					JOptionPane.showMessageDialog(memberPanel,
							"An exception occured:\n"
									+ Arrays.toString(ex.getStackTrace()),
							"Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		}
	}
}
