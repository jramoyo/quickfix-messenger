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
 * QFixMessengerFrame.java
 * 6 Jun 2011
 */
package com.jramoyo.qfixmessenger.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.border.EtchedBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import quickfix.ConfigError;
import quickfix.DataDictionary;
import quickfix.InvalidMessage;
import quickfix.Session;
import quickfix.SessionID;
import quickfix.StringField;
import quickfix.field.ApplVerID;

import com.jramoyo.fix.model.Component;
import com.jramoyo.fix.model.Field;
import com.jramoyo.fix.model.FixDictionary;
import com.jramoyo.fix.model.Group;
import com.jramoyo.fix.model.Member;
import com.jramoyo.fix.model.Message;
import com.jramoyo.fix.model.parser.FixDictionaryParser;
import com.jramoyo.fix.model.parser.FixParsingException;
import com.jramoyo.qfixmessenger.QFixMessenger;
import com.jramoyo.qfixmessenger.QFixMessengerConstants;
import com.jramoyo.qfixmessenger.quickfix.QFixMessageListener;
import com.jramoyo.qfixmessenger.quickfix.util.QFixUtil;
import com.jramoyo.qfixmessenger.ui.listeners.AboutActionListener;
import com.jramoyo.qfixmessenger.ui.listeners.HelpActionListener;
import com.jramoyo.qfixmessenger.ui.listeners.LogoffAllSessionsActionListener;
import com.jramoyo.qfixmessenger.ui.listeners.LogonAllSessionsActionListener;
import com.jramoyo.qfixmessenger.ui.listeners.LogonSessionItemListener;
import com.jramoyo.qfixmessenger.ui.listeners.LogonSessionMenuItemSessionStateListener;
import com.jramoyo.qfixmessenger.ui.listeners.ResetAllSessionsActionListener;
import com.jramoyo.qfixmessenger.ui.listeners.ResetSessionActionListener;
import com.jramoyo.qfixmessenger.ui.listeners.SessionStatusActionListener;
import com.jramoyo.qfixmessenger.ui.listeners.SessionsListSessionStateListener;
import com.jramoyo.qfixmessenger.ui.model.MessagesTableModel;
import com.jramoyo.qfixmessenger.ui.model.MessagesTableModelData;
import com.jramoyo.qfixmessenger.ui.panels.ComponentPanel;
import com.jramoyo.qfixmessenger.ui.panels.FieldPanel;
import com.jramoyo.qfixmessenger.ui.panels.FreeTextMessagePanel;
import com.jramoyo.qfixmessenger.ui.panels.GroupPanel;
import com.jramoyo.qfixmessenger.ui.panels.MemberPanel;
import com.jramoyo.qfixmessenger.ui.panels.MemberPanelFactory;
import com.jramoyo.qfixmessenger.ui.renderers.MessagesListCellRenderer;
import com.jramoyo.qfixmessenger.ui.renderers.MessagesTableCellRender;
import com.jramoyo.qfixmessenger.ui.renderers.SessionsListCellRenderer;

/**
 * Main frame
 * 
 * @author jamoyo
 */
public class QFixMessengerFrame extends JFrame
{
	private static final long serialVersionUID = 7906369617506618477L;

	private static final Logger logger = LoggerFactory
			.getLogger(QFixMessengerFrame.class);

	private static final String VERSION = "2.0";

	private static final int LEFT_PANEL_WIDTH = 170;

	private static final int MIDDLE_PANEL_WIDTH = 600;

	private final Message freeTextMessage = new Message("Free Text",
			"FIX Message", null, new HashMap<Member, Boolean>());

	private final JPanel blankPanel = new JPanel();

	private final QFixMessenger messenger;

	// This will no longer suffice once we have other FIXT versions
	private final FixDictionary fixTDictionary;

	private final ReadWriteLock contentLock;

	private volatile FixDictionary activeDictionary;

	private volatile Message activeMessage;

	private volatile boolean isRequiredOnly = true;

	private volatile boolean isModifyHeader;

	private volatile boolean isModifyTrailer;

	private volatile boolean isFixTSession;

	private volatile boolean isPreviewBeforeSend;

	private JMenuBar menuBar;

	private JMenu sessionMenu;

	private JMenu helpMenu;

	private JPanel leftPanel;

	private JScrollPane mainPanelScrollPane;

	private JScrollPane bottomPanelScrollPane;

	private JPanel rightPanel;

	private JList<Session> sessionsList;

	private JList<Message> messagesList;

	private JComboBox<String> appVersionsComboBox;

	private JCheckBox requiredCheckBox;

	private JCheckBox modifyHeaderCheckBox;

	private JCheckBox modifyTrailerCheckBox;

	private JCheckBox previewBeforeSendCheckBox;

	private JButton sendButton;

	private JTable messagesTable;

	private FreeTextMessagePanel freeTextMessagePanel;

	private List<MemberPanel> headerMembers;

	private List<MemberPanel> prevHeaderMembers;

	private List<MemberPanel> bodyMembers;

	private List<MemberPanel> prevBodyMembers;

	private List<MemberPanel> trailerMembers;

	private List<MemberPanel> prevTrailerMembers;

	public QFixMessengerFrame(QFixMessenger messenger)
	{
		super();
		this.messenger = messenger;

		this.headerMembers = new ArrayList<MemberPanel>();
		this.prevHeaderMembers = new ArrayList<MemberPanel>();

		this.bodyMembers = new ArrayList<MemberPanel>();
		this.prevBodyMembers = new ArrayList<MemberPanel>();

		this.trailerMembers = new ArrayList<MemberPanel>();
		this.prevTrailerMembers = new ArrayList<MemberPanel>();

		FixDictionaryParser parser = messenger.getParser();
		String fixTDictionaryFile = messenger.getConfig()
				.getFixT11DictionaryLocation();

		FixDictionary dictionary = null;
		try
		{
			dictionary = parser.parse(fixTDictionaryFile);
		} catch (FixParsingException ex)
		{
			logger.error("Unable to parse FIXT 1.1 Dictionary!", ex);
		}

		fixTDictionary = dictionary;

		contentLock = new ReentrantReadWriteLock();
	}

	public void launch()
	{
		setIconImage(new ImageIcon(messenger.getConfig().getAppIconLocation())
				.getImage());

		if (messenger.getConfig().isInitiator())
		{
			setTitle("QuickFIX Messenger " + VERSION + " (Initiator)");
		} else
		{
			setTitle("QuickFIX Messenger " + VERSION + " (Acceptor)");
		}
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addWindowListener(new FrameWindowAdapter(this));

		initComponents();

		setVisible(true);
	}

	public QFixMessenger getMessenger()
	{
		return messenger;
	}

	private void exit()
	{
		int choice = JOptionPane.showConfirmDialog(this,
				"Exit QuickFIX Messenger?", "Quit", JOptionPane.YES_NO_OPTION);
		if (choice == JOptionPane.YES_OPTION)
		{
			setVisible(false);
			messenger.exit();
		}
	}

	private void initAppVersionsComboBox()
	{
		appVersionsComboBox.setEnabled(false);
		appVersionsComboBox
				.addActionListener(new AppVersionsComboBoxActionListener(this));
	}

	private void initBottomPanel()
	{
		MessagesTableModel messagesTableModel = new MessagesTableModel();

		messagesTable = new JTable(messagesTableModel);
		messagesTable.getColumnModel().getColumn(0).setPreferredWidth(90);
		messagesTable.getColumnModel().getColumn(1).setPreferredWidth(5);
		messagesTable.getColumnModel().getColumn(2).setPreferredWidth(70);
		messagesTable.getColumnModel().getColumn(3).setPreferredWidth(520);

		messagesTable.setDefaultRenderer(String.class,
				new MessagesTableCellRender());
		messagesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		messagesTable.addMouseListener(new MessagesTableMouseAdapter(this));

		messenger.getApplication().addMessageListener(messagesTableModel);

		bottomPanelScrollPane = new JScrollPane(messagesTable);
		bottomPanelScrollPane.setPreferredSize(new Dimension(
				MIDDLE_PANEL_WIDTH, 120));
		bottomPanelScrollPane.getViewport().add(messagesTable);
		add(bottomPanelScrollPane, BorderLayout.SOUTH);
	}

	private void initComponents()
	{
		setLayout(new BorderLayout());

		initMenuBar();
		initLeftPanel();
		initMainPanel();
		initBottomPanel();
		initRightPanel();

		pack();
	}

	private void initHelpMenu()
	{
		helpMenu = new JMenu("Help");
		helpMenu.setMnemonic('H');

		JMenuItem aboutMenuItem = new JMenuItem("About");
		aboutMenuItem.setMnemonic('A');
		aboutMenuItem.addActionListener(new AboutActionListener(this));

		JMenuItem helpMenuItem = new JMenuItem("Help");
		helpMenuItem.setMnemonic('H');
		helpMenuItem.addActionListener(new HelpActionListener(this));

		helpMenu.add(helpMenuItem);
		helpMenu.add(aboutMenuItem);
	}

	private void initLeftPanel()
	{
		leftPanel = new JPanel();
		leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));

		// Sessions Panel
		sessionsList = new JList<Session>();
		appVersionsComboBox = new JComboBox<String>(
				QFixMessengerConstants.FIXT_APP_VERSIONS);
		JPanel sessionsPanel = new JPanel();
		sessionsPanel.setBorder(new TitledBorder("Current Sessions"));
		sessionsPanel.setLayout(new BorderLayout());

		initSessionsList();
		JScrollPane sessionsListScrollPane = new JScrollPane(sessionsList);
		sessionsListScrollPane.setPreferredSize(new Dimension(LEFT_PANEL_WIDTH,
				120));
		sessionsListScrollPane.setMaximumSize(sessionsListScrollPane
				.getPreferredSize());

		initAppVersionsComboBox();
		JPanel appVersionsPanel = new JPanel();
		appVersionsPanel.setLayout(new GridLayout(2, 1));
		appVersionsPanel.add(new JLabel("ApplVerID: "));
		appVersionsPanel.add(appVersionsComboBox);

		sessionsPanel.add(sessionsListScrollPane, BorderLayout.CENTER);
		sessionsPanel.add(appVersionsPanel, BorderLayout.SOUTH);

		// Prevent resize
		sessionsPanel.setMinimumSize(sessionsPanel.getPreferredSize());
		sessionsPanel.setMaximumSize(sessionsPanel.getPreferredSize());

		// Messages Panel
		messagesList = new JList<Message>();
		JPanel messagesPanel = new JPanel();
		messagesPanel.setBorder(new TitledBorder("Available Messages"));
		messagesPanel.setLayout(new BoxLayout(messagesPanel, BoxLayout.Y_AXIS));

		initMessagesList();
		JScrollPane messagesListScrollPane = new JScrollPane(messagesList);
		messagesListScrollPane.setPreferredSize(new Dimension(LEFT_PANEL_WIDTH,
				300));

		messagesPanel.add(messagesListScrollPane);

		leftPanel.add(sessionsPanel);
		leftPanel.add(messagesPanel);
		add(leftPanel, BorderLayout.WEST);
	}

	private void initMainPanel()
	{
		mainPanelScrollPane = new JScrollPane();
		mainPanelScrollPane.setPreferredSize(new Dimension(MIDDLE_PANEL_WIDTH,
				400));

		add(mainPanelScrollPane, BorderLayout.CENTER);
	}

	private void initMenuBar()
	{
		menuBar = new JMenuBar();

		initSessionMenu();
		initHelpMenu();

		menuBar.add(sessionMenu);
		menuBar.add(helpMenu);

		setJMenuBar(menuBar);
	}

	private void initMessagesList()
	{
		messagesList.setCellRenderer(new MessagesListCellRenderer());
		messagesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		messagesList.getSelectionModel().addListSelectionListener(
				new MessagesListSelectionListener(this));
		messagesList.addMouseListener(new MessagesListMouseAdapter(this));
	}

	private void initRightPanel()
	{
		rightPanel = new JPanel();
		rightPanel.setLayout(new BorderLayout());

		JPanel optionsPanel = new JPanel();
		optionsPanel.setBorder(new TitledBorder("Options"));
		optionsPanel.setLayout(new BoxLayout(optionsPanel, BoxLayout.Y_AXIS));

		requiredCheckBox = new JCheckBox("Required Only", true);
		requiredCheckBox.addItemListener(new ItemListener()
		{
			@Override
			public void itemStateChanged(ItemEvent e)
			{
				if (e.getStateChange() == ItemEvent.SELECTED)
				{
					isRequiredOnly = true;
				} else
				{
					isRequiredOnly = false;
				}

				if (logger.isDebugEnabled())
				{
					logger.debug("Selected isRequiredOnly = " + isRequiredOnly);
				}
				loadMainPanel();
			}
		});

		modifyHeaderCheckBox = new JCheckBox("Modify Header", false);
		modifyHeaderCheckBox.addItemListener(new ItemListener()
		{
			@Override
			public void itemStateChanged(ItemEvent e)
			{
				if (e.getStateChange() == ItemEvent.SELECTED)
				{
					isModifyHeader = true;
				} else
				{
					isModifyHeader = false;
				}

				if (logger.isDebugEnabled())
				{
					logger.debug("Selected isModifyHeader = " + isModifyHeader);
				}
				loadMainPanel();
			}
		});

		modifyTrailerCheckBox = new JCheckBox("Modify Trailer", false);
		modifyTrailerCheckBox.addItemListener(new ItemListener()
		{
			@Override
			public void itemStateChanged(ItemEvent e)
			{
				if (e.getStateChange() == ItemEvent.SELECTED)
				{
					isModifyTrailer = true;
				} else
				{
					isModifyTrailer = false;
				}

				if (logger.isDebugEnabled())
				{
					logger.debug("Selected isModifyTrailer = "
							+ isModifyTrailer);
				}
				loadMainPanel();
			}
		});

		optionsPanel.add(requiredCheckBox);
		optionsPanel.add(modifyHeaderCheckBox);
		optionsPanel.add(modifyTrailerCheckBox);

		JPanel sendPanel = new JPanel();
		sendPanel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 0.5;
		c.weighty = 0.0;
		c.ipadx = 2;
		c.ipady = 2;

		previewBeforeSendCheckBox = new JCheckBox("Preview Before Sending",
				false);
		previewBeforeSendCheckBox.addItemListener(new ItemListener()
		{
			@Override
			public void itemStateChanged(ItemEvent e)
			{
				if (e.getStateChange() == ItemEvent.SELECTED)
				{
					isPreviewBeforeSend = true;
				} else
				{
					isPreviewBeforeSend = false;
				}

				if (logger.isDebugEnabled())
				{
					logger.debug("Selected isPreviewBeforeSend = "
							+ isPreviewBeforeSend);
				}
			}
		});

		ImageIcon imageIcon = new ImageIcon(messenger.getConfig()
				.getSendIconLocation());
		Image img = imageIcon.getImage();
		Image scaledImg = img.getScaledInstance(80, 80,
				java.awt.Image.SCALE_SMOOTH);
		ImageIcon scaledImageIcon = new ImageIcon(scaledImg);
		sendButton = new JButton(scaledImageIcon);
		sendButton.addActionListener(new SendActionListener(this));
		sendButton.setToolTipText("Click to send the FIX message");

		c.gridx = 0;
		c.gridy = 0;
		sendPanel.add(previewBeforeSendCheckBox, c);

		c.gridx = 0;
		c.gridy = 1;
		sendPanel.add(sendButton, c);

		rightPanel.add(optionsPanel, BorderLayout.NORTH);
		rightPanel.add(sendPanel, BorderLayout.SOUTH);
		add(rightPanel, BorderLayout.EAST);
	}

	private void initSessionMenu()
	{
		sessionMenu = new JMenu("Session");
		sessionMenu.setMnemonic('S');

		JMenu currentSessionMenu;
		JCheckBoxMenuItem logonMenuItem;
		JMenuItem resetMenuItem;
		JMenuItem statusMenuItem;
		for (SessionID sessionId : messenger.getConnector().getSessions())
		{
			Session session = Session.lookupSession(sessionId);
			currentSessionMenu = new JMenu(QFixUtil.getSessionName(sessionId));

			logonMenuItem = new JCheckBoxMenuItem("Logon");
			logonMenuItem
					.addItemListener(new LogonSessionItemListener(session));
			if (session.isLoggedOn())
			{
				logonMenuItem.setSelected(true);
			} else
			{
				logonMenuItem.setSelected(false);
			}
			session.addStateListener(new LogonSessionMenuItemSessionStateListener(
					logonMenuItem));

			resetMenuItem = new JMenuItem("Reset");
			resetMenuItem.addActionListener(new ResetSessionActionListener(
					this, session));

			statusMenuItem = new JMenuItem("Status");
			statusMenuItem.addActionListener(new SessionStatusActionListener(
					this, session));

			currentSessionMenu.add(logonMenuItem);
			currentSessionMenu.add(resetMenuItem);
			currentSessionMenu.add(statusMenuItem);
			sessionMenu.add(currentSessionMenu);
		}

		JMenuItem logonAllSessionsMenuItem = new JMenuItem(
				"All Sessions - Logon");
		logonAllSessionsMenuItem.setMnemonic('n');
		logonAllSessionsMenuItem
				.addActionListener(new LogonAllSessionsActionListener(this));

		JMenuItem logoffAllSessionsMenuItem = new JMenuItem(
				"All Sessions - Logoff");
		logoffAllSessionsMenuItem.setMnemonic('f');
		logoffAllSessionsMenuItem
				.addActionListener(new LogoffAllSessionsActionListener(this));

		JMenuItem resetAllSessionsMenuItem = new JMenuItem(
				"All Sessions - Reset");
		resetAllSessionsMenuItem.setMnemonic('R');
		resetAllSessionsMenuItem
				.addActionListener(new ResetAllSessionsActionListener(this));

		JMenuItem exitMenuItem = new JMenuItem("Exit");
		exitMenuItem.setMnemonic('x');
		exitMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X,
				InputEvent.ALT_MASK));
		exitMenuItem.addActionListener(new FrameExitActionListener(this));

		sessionMenu.addSeparator();
		sessionMenu.add(logonAllSessionsMenuItem);
		sessionMenu.add(logoffAllSessionsMenuItem);
		sessionMenu.add(resetAllSessionsMenuItem);
		sessionMenu.addSeparator();
		sessionMenu.add(exitMenuItem);
	}

	private void initSessionsList()
	{
		sessionsList = new JList<Session>();

		List<SessionID> sessionIds = messenger.getConnector().getSessions();
		List<Session> sessions = new ArrayList<Session>(sessionIds.size());
		for (SessionID sessionId : sessionIds)
		{
			Session session = Session.lookupSession(sessionId);
			session.addStateListener(new SessionsListSessionStateListener(
					sessionsList));
			sessions.add(session);
		}

		Collections.sort(sessions, new Comparator<Session>()
		{
			@Override
			public int compare(Session o1, Session o2)
			{
				return o1.getSessionID().getBeginString()
						.compareTo(o2.getSessionID().getBeginString());
			}
		});

		sessionsList.setListData(sessions.toArray(new Session[] {}));
		sessionsList.setCellRenderer(new SessionsListCellRenderer());
		sessionsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		sessionsList.getSelectionModel().addListSelectionListener(
				new SessionsListSelectionListener(this));
		sessionsList.addMouseListener(new SessionsListMouseAdapter(this));
	}

	private void loadMainPanel()
	{
		try
		{
			contentLock.writeLock().lock();

			prevHeaderMembers.clear();
			prevHeaderMembers.addAll(bodyMembers);

			prevBodyMembers.clear();
			prevBodyMembers.addAll(bodyMembers);

			prevTrailerMembers.clear();
			prevTrailerMembers.addAll(bodyMembers);

			headerMembers.clear();
			bodyMembers.clear();
			trailerMembers.clear();

			if (activeMessage != null)
			{
				JPanel mainPanel = new JPanel();
				mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

				if (!activeMessage.equals(freeTextMessage))
				{
					// Load the header
					if (isModifyHeader)
					{
						JPanel headerPanel = new JPanel();
						headerPanel.setLayout(new BoxLayout(headerPanel,
								BoxLayout.Y_AXIS));

						TitledBorder headerBorder = new TitledBorder(
								new LineBorder(Color.BLACK), "Message Header");
						// TODO Workaround for Java Bug ID: 7022041
						Font headerTitleBorderFont = UIManager.getDefaults()
								.getFont("TitledBorder.font");
						if (headerTitleBorderFont != null)
						{
							headerBorder.setTitleFont(new Font(
									headerTitleBorderFont.getName(), Font.BOLD,
									15));
						}
						headerPanel.setBorder(headerBorder);

						if (!isFixTSession)
						{
							for (Entry<Member, Boolean> entry : activeDictionary
									.getHeader().getMembers().entrySet())
							{
								loadMember(headerPanel, prevHeaderMembers,
										headerMembers, entry);
							}
						} else
						{
							for (Entry<Member, Boolean> entry : fixTDictionary
									.getHeader().getMembers().entrySet())
							{
								loadMember(headerPanel, prevHeaderMembers,
										headerMembers, entry);
							}
						}

						mainPanel.add(headerPanel);
						mainPanel
								.add(Box.createRigidArea(new Dimension(0, 20)));
					}

					// Load the body
					JPanel bodyPanel = new JPanel();
					bodyPanel.setLayout(new BoxLayout(bodyPanel,
							BoxLayout.Y_AXIS));

					TitledBorder bodyBorder = new TitledBorder(new LineBorder(
							Color.BLACK), "Message Body");
					// TODO Workaround for Java Bug ID: 7022041
					Font bodyTitleBorderFont = UIManager.getDefaults().getFont(
							"TitledBorder.font");
					if (bodyTitleBorderFont != null)
					{
						bodyBorder.setTitleFont(new Font(bodyTitleBorderFont
								.getName(), Font.BOLD, 15));
					}
					bodyPanel.setBorder(bodyBorder);

					for (Entry<Member, Boolean> entry : activeMessage
							.getMembers().entrySet())
					{
						loadMember(bodyPanel, prevBodyMembers, bodyMembers,
								entry);
					}

					mainPanel.add(bodyPanel);

					// Load the trailer
					if (isModifyTrailer)
					{
						JPanel trailerPanel = new JPanel();
						trailerPanel.setLayout(new BoxLayout(trailerPanel,
								BoxLayout.Y_AXIS));

						TitledBorder trailerBorder = new TitledBorder(
								new LineBorder(Color.BLACK), "Message Trailer");
						// TODO Workaround for Java Bug ID: 7022041
						Font trailerTitleBorderFont = UIManager.getDefaults()
								.getFont("TitledBorder.font");
						if (trailerTitleBorderFont != null)
						{
							trailerBorder.setTitleFont(new Font(
									trailerTitleBorderFont.getName(),
									Font.BOLD, 15));
						}
						trailerPanel.setBorder(trailerBorder);

						if (!isFixTSession)
						{
							for (Entry<Member, Boolean> entry : activeDictionary
									.getTrailer().getMembers().entrySet())
							{
								loadMember(trailerPanel, prevTrailerMembers,
										trailerMembers, entry);
							}
						} else
						{
							for (Entry<Member, Boolean> entry : fixTDictionary
									.getTrailer().getMembers().entrySet())
							{
								loadMember(trailerPanel, prevTrailerMembers,
										trailerMembers, entry);
							}
						}

						mainPanel
								.add(Box.createRigidArea(new Dimension(0, 20)));
						mainPanel.add(trailerPanel);
					}
				} else
				{
					freeTextMessagePanel = new FreeTextMessagePanel();
					freeTextMessagePanel.setMaximumSize(new Dimension(
							MIDDLE_PANEL_WIDTH, freeTextMessagePanel
									.getPreferredSize().height));

					mainPanel.add(freeTextMessagePanel);

					prevHeaderMembers.clear();
					prevBodyMembers.clear();
					trailerMembers.clear();
				}

				mainPanelScrollPane.getViewport().add(mainPanel);
			} else
			{
				prevHeaderMembers.clear();
				prevBodyMembers.clear();
				trailerMembers.clear();

				mainPanelScrollPane.getViewport().add(blankPanel);
			}
		} finally
		{
			contentLock.writeLock().unlock();
		}
	}

	private void loadMember(JPanel mainPanel,
			List<MemberPanel> previousMemberList, List<MemberPanel> memberList,
			Entry<Member, Boolean> entry)
	{
		if (isRequiredOnly && !entry.getValue())
		{
			return;
		}

		if (entry.getKey() instanceof Field)
		{
			Field field = (Field) entry.getKey();

			FieldPanel fieldPanel = MemberPanelFactory.createFieldPanel(
					previousMemberList, field, entry.getValue());
			fieldPanel.setMaximumSize(new Dimension(MIDDLE_PANEL_WIDTH,
					fieldPanel.getPreferredSize().height));

			mainPanel.add(fieldPanel);
			memberList.add(fieldPanel);
		}

		if (entry.getKey() instanceof Group)
		{
			Group group = (Group) entry.getKey();

			GroupPanel groupPanel = MemberPanelFactory
					.createGroupPanel(previousMemberList, group,
							isRequiredOnly, entry.getValue());
			groupPanel.setMaximumSize(new Dimension(MIDDLE_PANEL_WIDTH,
					groupPanel.getPreferredSize().height));

			mainPanel.add(groupPanel);
			memberList.add(groupPanel);
		}

		if (entry.getKey() instanceof Component)
		{
			Component component = (Component) entry.getKey();

			ComponentPanel componentPanel = MemberPanelFactory
					.createComponentPanel(previousMemberList, component,
							isRequiredOnly, entry.getValue());
			componentPanel.setMaximumSize(new Dimension(MIDDLE_PANEL_WIDTH,
					componentPanel.getPreferredSize().height));

			mainPanel.add(componentPanel);
			memberList.add(componentPanel);
		}
	}

	private void loadMessagesList()
	{
		if (activeDictionary != null)
		{
			List<Message> messages = new ArrayList<Message>();
			messages.addAll(activeDictionary.getMessages().values());
			Collections.sort(messages);

			messages.add(0, freeTextMessage);

			messagesList.setListData(messages.toArray(new Message[] {}));
		} else
		{
			messagesList.setListData(new Message[] {});
		}
	}

	private static class AppVersionsComboBoxActionListener implements
			ActionListener
	{
		private QFixMessengerFrame frame;

		public AppVersionsComboBoxActionListener(QFixMessengerFrame frame)
		{
			this.frame = frame;
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			String appVersion = (String) frame.appVersionsComboBox
					.getSelectedItem();
			try
			{
				frame.activeDictionary = frame.messenger.getParser().parse(
						frame.messenger.getConfig().getFixDictionaryLocation(
								appVersion));
			} catch (FixParsingException ex)
			{
				JOptionPane.showMessageDialog(frame, "An error occured while"
						+ " parsing the FIX dictionary: " + ex, "Error",
						JOptionPane.ERROR_MESSAGE);
			}

			if (logger.isDebugEnabled())
			{
				logger.debug("Selected dictionary " + frame.activeDictionary);
			}
			frame.loadMessagesList();
		}
	}

	private static class FrameExitActionListener implements ActionListener
	{
		private QFixMessengerFrame frame;

		public FrameExitActionListener(QFixMessengerFrame frame)
		{
			this.frame = frame;
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			frame.exit();
		}
	}

	private static class FrameWindowAdapter extends WindowAdapter
	{
		private QFixMessengerFrame frame;

		public FrameWindowAdapter(QFixMessengerFrame frame)
		{
			this.frame = frame;
		}

		@Override
		public void windowClosing(WindowEvent e)
		{
			frame.exit();
		}
	}

	private class MessagesListMouseAdapter extends MouseAdapter
	{
		private QFixMessengerFrame frame;

		public MessagesListMouseAdapter(QFixMessengerFrame frame)
		{
			this.frame = frame;
		}

		@Override
		public void mouseClicked(MouseEvent e)
		{
			if (e.getClickCount() == 2)
			{
				int index = frame.messagesList.locationToIndex(e.getPoint());
				Message message = (Message) frame.messagesList.getModel()
						.getElementAt(index);

				Message selectedMessage = (Message) frame.messagesList
						.getSelectedValue();
				if (message == selectedMessage && message.getCategory() != null)
				{
					try
					{
						String url = QFixMessengerConstants.FIX_WIKI_URL
								+ message.getName();
						java.awt.Desktop.getDesktop().browse(
								java.net.URI.create(url));
					} catch (IOException ex)
					{
						JOptionPane.showMessageDialog(
								frame,
								"An exception occured:\n"
										+ Arrays.toString(ex.getStackTrace()),
								"Error", JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		}
	}

	private static class MessagesListSelectionListener implements
			ListSelectionListener
	{
		private QFixMessengerFrame frame;

		public MessagesListSelectionListener(QFixMessengerFrame frame)
		{
			this.frame = frame;
		}

		@Override
		public void valueChanged(ListSelectionEvent e)
		{
			if (!e.getValueIsAdjusting())
			{
				frame.activeMessage = (Message) frame.messagesList
						.getSelectedValue();

				if (logger.isDebugEnabled())
				{
					logger.debug("Selected message " + frame.activeMessage);
				}
				frame.loadMainPanel();
			}
		}
	}

	private class MessagesTableMouseAdapter extends MouseAdapter
	{
		private QFixMessengerFrame frame;

		public MessagesTableMouseAdapter(QFixMessengerFrame frame)
		{
			this.frame = frame;
		}

		@Override
		public void mouseClicked(MouseEvent e)
		{
			if (e.getClickCount() == 2)
			{
				int viewRow = frame.messagesTable.rowAtPoint(e.getPoint());
				int modelRow = frame.messagesTable
						.convertRowIndexToModel(viewRow);

				MessagesTableModel model = (MessagesTableModel) frame.messagesTable
						.getModel();
				MessagesTableModelData data = model.getData(modelRow);

				JPanel panel = new JPanel();
				panel.setLayout(new GridBagLayout());
				GridBagConstraints c = new GridBagConstraints();
				c.fill = GridBagConstraints.HORIZONTAL;
				c.weightx = 0.5;
				c.weighty = 0.0;
				c.ipadx = 2;
				c.ipady = 2;

				JLabel timeLabel = new JLabel(
						"<html><b>Time Stamp:</b> <i><font color = 'blue'>"
								+ data.getDate() + "</font></i></html>");

				c.gridx = 0;
				c.gridy = 3;
				panel.add(Box.createRigidArea(new Dimension(50, 10)), c);

				JLabel directionLabel;
				if (data.getDirection().equals(QFixMessageListener.RECV))
				{
					directionLabel = new JLabel(
							"<html><b>Direction:</b> <i><b><font color = '#FF8040'>"
									+ data.getDirection()
									+ "</font></b></i></html>");
				} else
				{
					directionLabel = new JLabel(
							"<html><b>Direction:</b> <i><b><font color = '#669900'>"
									+ data.getDirection()
									+ "</font></b></i></html>");
				}

				JLabel sessionIdLabel = new JLabel(
						"<html><b>Session ID:</b> <i><font color = 'blue'>"
								+ data.getSessionName() + "</font></i></html>");

				JLabel messageLabel = new JLabel("<html><b>Message:</b></html>");

				c.gridx = 0;
				c.gridy = 0;
				panel.add(timeLabel, c);

				c.gridx = 0;
				c.gridy = 1;
				panel.add(directionLabel, c);

				c.gridx = 0;
				c.gridy = 2;
				panel.add(sessionIdLabel, c);

				c.gridx = 0;
				c.gridy = 2;
				panel.add(sessionIdLabel, c);

				c.gridx = 0;
				c.gridy = 4;
				panel.add(messageLabel, c);

				JTextArea messageText = new JTextArea(data.getMessage(), 5, 60);
				messageText.setLineWrap(true);
				messageText.setEditable(false);

				JScrollPane messageTextScrollPane = new JScrollPane(messageText);
				messageText.setPreferredSize(new Dimension(300, 200));
				messageTextScrollPane.setBorder(new EtchedBorder());

				c.gridx = 0;
				c.gridy = 5;
				panel.add(messageTextScrollPane, c);

				JOptionPane.showMessageDialog(frame, panel, "Message Details",
						JOptionPane.PLAIN_MESSAGE);
			}
		}
	}

	private static class SendActionListener implements ActionListener
	{
		private QFixMessengerFrame frame;

		public SendActionListener(QFixMessengerFrame frame)
		{
			this.frame = frame;
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			Session session = (Session) frame.sessionsList.getSelectedValue();
			if (session.isLoggedOn())
			{
				if (frame.activeMessage != null)
				{
					quickfix.Message message = null;
					if (!frame.activeMessage.equals(frame.freeTextMessage))
					{
						message = getQFixMessageFromForm(session);
					} else
					{
						message = getQFixMessageFromFreeText(session);
					}

					if (message != null)
					{
						if (frame.isPreviewBeforeSend)
						{
							int choice = JOptionPane.showConfirmDialog(frame,
									message.toString(), "Send FIX Message?",
									JOptionPane.YES_NO_OPTION,
									JOptionPane.QUESTION_MESSAGE);
							if (choice == JOptionPane.YES_OPTION)
							{
								logger.info("Sending message "
										+ message.toString());
								session.send(message);
							}
						} else
						{
							logger.info("Sending message " + message.toString());
							session.send(message);
						}
					}
				} else
				{
					JOptionPane.showMessageDialog(frame,
							"Please select a message!", "Error",
							JOptionPane.WARNING_MESSAGE);
				}
			} else
			{
				JOptionPane.showMessageDialog(frame,
						QFixUtil.getSessionName(session.getSessionID())
								+ " is not logged on!", "Error",
						JOptionPane.ERROR_MESSAGE);
			}
		}

		/*
		 * Note: As mentioned in the QuickFIX/J documentation, the below method
		 * is not the ideal way of constructing messages in QuickFIX. However,
		 * this method is necessary given the use case.
		 */
		private quickfix.Message getQFixMessageFromForm(Session session)
		{
			quickfix.Message message = session.getMessageFactory().create(
					session.getSessionID().getBeginString(),
					frame.activeMessage.getMsgType());

			// For FIXT 1.1 sessions, add the ApplVerID field (1128)
			if (frame.isFixTSession)
			{
				String appVersion = (String) frame.appVersionsComboBox
						.getSelectedItem();

				ApplVerID applVerID = new ApplVerID(
						QFixMessengerConstants.APPVER_ID_MAP.get(appVersion));
				message.getHeader().setField(applVerID);
			}

			if (frame.isModifyHeader)
			{
				synchronized (frame.headerMembers)
				{
					for (MemberPanel memberPanel : frame.headerMembers)
					{
						if (memberPanel instanceof FieldPanel)
						{
							FieldPanel fieldPanel = (FieldPanel) memberPanel;
							if (fieldPanel.getQuickFixField() != null)
							{
								message.getHeader().setField(
										fieldPanel.getQuickFixField());
							}
						}
					}
				}
			}

			try
			{
				frame.contentLock.readLock().lock();

				for (MemberPanel memberPanel : frame.bodyMembers)
				{
					if (memberPanel instanceof FieldPanel)
					{
						FieldPanel fieldPanel = (FieldPanel) memberPanel;
						if (fieldPanel.getQuickFixField() != null)
						{
							message.setField(fieldPanel.getQuickFixField());
						}
					}

					if (memberPanel instanceof GroupPanel)
					{
						GroupPanel groupPanel = (GroupPanel) memberPanel;
						for (quickfix.Group group : groupPanel
								.getQuickFixGroups())
						{
							message.addGroup(group);
						}
					}

					if (memberPanel instanceof ComponentPanel)
					{
						ComponentPanel componentPanel = (ComponentPanel) memberPanel;
						for (quickfix.StringField field : componentPanel
								.getQuickFixFields())
						{
							message.setField(field);
						}

						for (quickfix.Group group : componentPanel
								.getQuickFixGroups())
						{
							message.addGroup(group);
						}
					}
				}

				if (frame.isModifyTrailer)
				{
					synchronized (frame.trailerMembers)
					{
						for (MemberPanel memberPanel : frame.trailerMembers)
						{
							if (memberPanel instanceof FieldPanel)
							{
								FieldPanel fieldPanel = (FieldPanel) memberPanel;
								if (fieldPanel.getQuickFixField() != null)
								{
									StringField field = fieldPanel
											.getQuickFixField();
									message.getTrailer().setField(field);
								}
							}
						}
					}
				}
			} finally
			{
				frame.contentLock.readLock().unlock();
			}

			return message;
		}

		private quickfix.Message getQFixMessageFromFreeText(Session session)
		{
			quickfix.Message message = null;
			try
			{
				message = new quickfix.Message();

				if (!frame.isFixTSession)
				{
					message.fromString(
							frame.freeTextMessagePanel.getFixString(),
							session.getDataDictionary(), false);
				} else
				{
					/*
					 * FIXT sessions require the data dictionary of both the
					 * session and the application version
					 */
					DataDictionary appDictionary = null;
					DataDictionary sessionDictionary = null;
					String appVersion = (String) frame.appVersionsComboBox
							.getSelectedItem();
					try
					{
						sessionDictionary = new DataDictionary(frame
								.getMessenger().getConfig()
								.getFixT11DictionaryLocation());
						appDictionary = new DataDictionary(frame.getMessenger()
								.getConfig()
								.getFixDictionaryLocation(appVersion));
						message.fromString(
								frame.freeTextMessagePanel.getFixString(),
								sessionDictionary, appDictionary, false);
					} catch (ConfigError ex)
					{
						message = null;
						logger.error("Unable to load Application "
								+ "version dictionary!", ex);
						JOptionPane.showMessageDialog(frame,
								"Unable to load Application "
										+ "version dictionary!", "Error",
								JOptionPane.ERROR_MESSAGE);
					}
				}
			} catch (InvalidMessage ex)
			{
				message = null;
				logger.error("Attempt to send an invalid mesage!", ex);
				JOptionPane.showMessageDialog(frame, "Message is invalid!",
						"Error", JOptionPane.ERROR_MESSAGE);
			}

			return message;
		}
	}

	private class SessionsListMouseAdapter extends MouseAdapter
	{
		private QFixMessengerFrame frame;

		public SessionsListMouseAdapter(QFixMessengerFrame frame)
		{
			this.frame = frame;
		}

		@Override
		public void mouseClicked(MouseEvent e)
		{
			if (e.getButton() == MouseEvent.BUTTON3)
			{
				int index = frame.sessionsList.locationToIndex(e.getPoint());
				Session session = (Session) frame.sessionsList.getModel()
						.getElementAt(index);

				Session selectedSession = (Session) frame.sessionsList
						.getSelectedValue();
				if (session == selectedSession)
				{
					JPopupMenu sessionMenuPopup;
					JCheckBoxMenuItem logonMenuItem;
					JMenuItem resetMenuItem;
					JMenuItem statusMenuItem;

					sessionMenuPopup = new JPopupMenu();

					logonMenuItem = new JCheckBoxMenuItem("Logon");
					logonMenuItem.addItemListener(new LogonSessionItemListener(
							session));
					if (session.isLoggedOn())
					{
						logonMenuItem.setSelected(true);
					} else
					{
						logonMenuItem.setSelected(false);
					}
					session.addStateListener(new LogonSessionMenuItemSessionStateListener(
							logonMenuItem));

					resetMenuItem = new JMenuItem("Reset");
					resetMenuItem
							.addActionListener(new ResetSessionActionListener(
									frame, session));

					statusMenuItem = new JMenuItem("Status");
					statusMenuItem
							.addActionListener(new SessionStatusActionListener(
									frame, session));

					sessionMenuPopup.add(logonMenuItem);
					sessionMenuPopup.add(resetMenuItem);
					sessionMenuPopup.add(statusMenuItem);

					sessionMenuPopup.show(frame.sessionsList, e.getX(),
							e.getY());
				}
			}
		}
	}

	private static class SessionsListSelectionListener implements
			ListSelectionListener
	{
		private QFixMessengerFrame frame;

		public SessionsListSelectionListener(QFixMessengerFrame frame)
		{
			this.frame = frame;
		}

		@Override
		public void valueChanged(ListSelectionEvent e)
		{
			if (!e.getValueIsAdjusting())
			{
				Session session = (Session) frame.sessionsList
						.getSelectedValue();
				SessionID sessionId = session.getSessionID();

				if (sessionId.getBeginString().equals(
						QFixMessengerConstants.BEGIN_STRING_FIXT11))
				{
					frame.appVersionsComboBox.setEnabled(true);
					frame.activeDictionary = null;
					frame.isFixTSession = true;
				} else
				{
					frame.appVersionsComboBox.setEnabled(false);
					frame.isFixTSession = false;

					try
					{
						FixDictionaryParser parser = frame.messenger
								.getParser();
						frame.activeDictionary = parser.parse(frame.messenger
								.getConfig().getFixDictionaryLocation(
										sessionId.getBeginString()));
					} catch (FixParsingException ex)
					{
						JOptionPane.showMessageDialog(frame,
								"An error occured while"
										+ " parsing the FIX dictionary: " + ex,
								"Error", JOptionPane.ERROR_MESSAGE);
					}
				}

				if (logger.isDebugEnabled())
				{
					logger.debug("Selected dictionary "
							+ frame.activeDictionary);
				}

				frame.activeMessage = null;
				frame.loadMainPanel();
				frame.loadMessagesList();
			}
		}
	}
}
