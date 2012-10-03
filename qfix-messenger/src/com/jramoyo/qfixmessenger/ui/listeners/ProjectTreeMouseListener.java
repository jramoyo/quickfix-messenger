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
 * ProjectTreeMouseListener.java
 * Sep 27, 2012
 */
package com.jramoyo.qfixmessenger.ui.listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTree;

import com.jramoyo.fix.xml.MessageType;
import com.jramoyo.fix.xml.ProjectType;
import com.jramoyo.qfixmessenger.QFixMessengerException;
import com.jramoyo.qfixmessenger.ui.Icons;
import com.jramoyo.qfixmessenger.ui.QFixMessengerFrame;
import com.jramoyo.qfixmessenger.ui.models.ProjectTreeModel;

/**
 * @author jramoyo
 */
public class ProjectTreeMouseListener extends MouseAdapter
{
	private final QFixMessengerFrame frame;

	private final JTree projectTree;

	public ProjectTreeMouseListener(QFixMessengerFrame frame, JTree projectTree)
	{
		this.frame = frame;
		this.projectTree = projectTree;
	}

	public void mouseClicked(MouseEvent event)
	{
		if (event.getButton() == MouseEvent.BUTTON3)
		{
			JPopupMenu popUpMenu = new JPopupMenu();

			JMenuItem sendAllMenuItem = new JMenuItem("Send All Messages");
			sendAllMenuItem.setIcon(new ImageIcon(frame.getMessenger()
					.getConfig().getIconsLocation()
					+ Icons.SEND_ALL_ICON));
			sendAllMenuItem.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					for (MessageType xmlMessageType : frame.getXmlProjectType()
							.getMessages().getMessage())
					{
						try
						{
							frame.getMessenger().sendXmlMessage(xmlMessageType);
						} catch (QFixMessengerException ex)
						{
							JOptionPane.showMessageDialog(
									frame.getProjectFrame(),
									"Unable to send message:\n"
											+ ex.getMessage(), "Error",
									JOptionPane.ERROR_MESSAGE);
							break;
						}
					}
				}
			});

			JMenuItem collapseAllMenuItem = new JMenuItem("Collapse All");
			collapseAllMenuItem.setIcon(new ImageIcon(frame.getMessenger()
					.getConfig().getIconsLocation()
					+ Icons.COLLAPSE_ICON));
			collapseAllMenuItem.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					synchronized (projectTree)
					{
						int row = projectTree.getRowCount() - 1;
						while (row > 1)
						{
							projectTree.collapseRow(row);
							row--;
						}
					}
				}
			});

			JMenuItem expandAllMenuItem = new JMenuItem("Expand All");
			expandAllMenuItem.setIcon(new ImageIcon(frame.getMessenger()
					.getConfig().getIconsLocation()
					+ Icons.EXPAND_ICON));
			expandAllMenuItem.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					synchronized (projectTree)
					{
						int row = 0;
						while (row < projectTree.getRowCount())
						{
							projectTree.expandRow(row);
							row++;
						}
					}
				}
			});

			if (projectTree.getLastSelectedPathComponent() != null
					&& projectTree.getLastSelectedPathComponent() instanceof MessageType)
			{
				JMenuItem loadMessageMenuItem = new JMenuItem("Load Message");
				loadMessageMenuItem.setIcon(new ImageIcon(frame.getMessenger()
						.getConfig().getIconsLocation()
						+ Icons.LOAD_ICON));
				loadMessageMenuItem.addActionListener(new ActionListener()
				{
					@Override
					public void actionPerformed(ActionEvent e)
					{
						MessageType xmlMessageType = (MessageType) projectTree
								.getLastSelectedPathComponent();
						frame.loadXmlMessage(xmlMessageType);
					}
				});

				JMenuItem sendMenuItem = new JMenuItem("Send Message");
				sendMenuItem.setIcon(new ImageIcon(frame.getMessenger()
						.getConfig().getIconsLocation()
						+ Icons.SEND_SMALL_ICON));
				sendMenuItem.addActionListener(new ActionListener()
				{
					@Override
					public void actionPerformed(ActionEvent e)
					{
						MessageType xmlMessageType = (MessageType) projectTree
								.getLastSelectedPathComponent();
						try
						{
							frame.getMessenger().sendXmlMessage(xmlMessageType);
						} catch (QFixMessengerException ex)
						{
							JOptionPane.showMessageDialog(
									frame.getProjectFrame(),
									"Unable to send message:\n"
											+ ex.getMessage(), "Error",
									JOptionPane.ERROR_MESSAGE);
						}
					}
				});

				JMenuItem exportMenuItem = new JMenuItem("Export Message");
				exportMenuItem.setIcon(new ImageIcon(frame.getMessenger()
						.getConfig().getIconsLocation()
						+ Icons.EXPORT_ICON));
				exportMenuItem.addActionListener(new ActionListener()
				{
					@Override
					public void actionPerformed(ActionEvent e)
					{
						MessageType xmlMessageType = (MessageType) projectTree
								.getLastSelectedPathComponent();
						frame.marshallXmlMessage(xmlMessageType);
					}
				});

				JMenuItem deleteMessageMenuItem = new JMenuItem(
						"Delete Message");
				deleteMessageMenuItem.setIcon(new ImageIcon(frame
						.getMessenger().getConfig().getIconsLocation()
						+ Icons.DELETE_ICON));
				deleteMessageMenuItem.addActionListener(new ActionListener()
				{
					@Override
					public void actionPerformed(ActionEvent e)
					{
						MessageType xmlMessageType = (MessageType) projectTree
								.getLastSelectedPathComponent();

						int choice = JOptionPane.showConfirmDialog(
								frame.getProjectFrame(), "Delete \""
										+ xmlMessageType.getName() + "\"?",
								"Delete Message", JOptionPane.YES_NO_OPTION);
						if (choice == JOptionPane.YES_OPTION)
						{
							ProjectTreeModel projectTreeModel = (ProjectTreeModel) projectTree
									.getModel();
							ProjectType xmlProjectType = (ProjectType) projectTreeModel
									.getRoot();
							int index = 0;
							for (MessageType currentXmlMessageType : xmlProjectType
									.getMessages().getMessage())
							{
								if (currentXmlMessageType
										.equals(xmlMessageType))
								{
									break;
								}
								index++;
							}

							xmlProjectType.getMessages().getMessage()
									.remove(index);
							projectTreeModel.updateMessageRemoved(
									xmlMessageType, index);
						}
					}
				});

				popUpMenu.add(loadMessageMenuItem);
				popUpMenu.add(sendMenuItem);
				popUpMenu.add(exportMenuItem);
				popUpMenu.add(deleteMessageMenuItem);
				popUpMenu.addSeparator();
				popUpMenu.add(sendAllMenuItem);
				popUpMenu.addSeparator();
				popUpMenu.add(collapseAllMenuItem);
				popUpMenu.add(expandAllMenuItem);
			} else
			{
				popUpMenu.add(sendAllMenuItem);
				popUpMenu.addSeparator();
				popUpMenu.add(collapseAllMenuItem);
				popUpMenu.add(expandAllMenuItem);
			}

			popUpMenu.show(projectTree, event.getX(), event.getY());
		}
	}
}
