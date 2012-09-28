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
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
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
import java.io.File;
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
import javax.swing.ComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
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
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.border.EtchedBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileFilter;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;

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
import com.jramoyo.fix.xml.BodyType;
import com.jramoyo.fix.xml.ComponentType;
import com.jramoyo.fix.xml.FieldType;
import com.jramoyo.fix.xml.GroupsType;
import com.jramoyo.fix.xml.HeaderType;
import com.jramoyo.fix.xml.MessageType;
import com.jramoyo.fix.xml.ObjectFactory;
import com.jramoyo.fix.xml.ProjectType;
import com.jramoyo.fix.xml.SessionType;
import com.jramoyo.fix.xml.TrailerType;
import com.jramoyo.qfixmessenger.QFixMessenger;
import com.jramoyo.qfixmessenger.QFixMessengerConstants;
import com.jramoyo.qfixmessenger.quickfix.QFixMessageListener;
import com.jramoyo.qfixmessenger.quickfix.util.QFixUtil;
import com.jramoyo.qfixmessenger.ui.listeners.AboutActionListener;
import com.jramoyo.qfixmessenger.ui.listeners.CloseProjectActionListener;
import com.jramoyo.qfixmessenger.ui.listeners.FrameExitActionListener;
import com.jramoyo.qfixmessenger.ui.listeners.HelpActionListener;
import com.jramoyo.qfixmessenger.ui.listeners.ImportMessageActionListener;
import com.jramoyo.qfixmessenger.ui.listeners.LogoffAllSessionsActionListener;
import com.jramoyo.qfixmessenger.ui.listeners.LogonAllSessionsActionListener;
import com.jramoyo.qfixmessenger.ui.listeners.LogonSessionItemListener;
import com.jramoyo.qfixmessenger.ui.listeners.LogonSessionMenuItemSessionStateListener;
import com.jramoyo.qfixmessenger.ui.listeners.NewProjectActionListener;
import com.jramoyo.qfixmessenger.ui.listeners.OpenProjectActionListener;
import com.jramoyo.qfixmessenger.ui.listeners.ResetAllSessionsActionListener;
import com.jramoyo.qfixmessenger.ui.listeners.ResetSessionActionListener;
import com.jramoyo.qfixmessenger.ui.listeners.SaveProjectActionListener;
import com.jramoyo.qfixmessenger.ui.listeners.SessionStatusActionListener;
import com.jramoyo.qfixmessenger.ui.listeners.SessionsListSessionStateListener;
import com.jramoyo.qfixmessenger.ui.models.MessagesTableModel;
import com.jramoyo.qfixmessenger.ui.models.data.MessagesTableModelData;
import com.jramoyo.qfixmessenger.ui.panels.ComponentPanel;
import com.jramoyo.qfixmessenger.ui.panels.FieldPanel;
import com.jramoyo.qfixmessenger.ui.panels.FreeTextMessagePanel;
import com.jramoyo.qfixmessenger.ui.panels.GroupPanel;
import com.jramoyo.qfixmessenger.ui.panels.MemberPanel;
import com.jramoyo.qfixmessenger.ui.panels.MemberPanelFactory;
import com.jramoyo.qfixmessenger.ui.panels.MemberPanelUtil;
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

	private static final String EMPTY_PROJECT = "None";

	private static final int LEFT_PANEL_WIDTH = 170;

	private static final int MIDDLE_PANEL_WIDTH = 600;

	private final Message freeTextMessage = new Message("Free Text",
			"FIX Message", null, new HashMap<Member, Boolean>());

	private final JPanel blankPanel = new JPanel();

	private final QFixMessenger messenger;

	// This will no longer suffice once we have other FIXT versions
	private final FixDictionary fixTDictionary;

	private final ReadWriteLock contentLock;

	private JAXBContext jaxbContext;

	private String frameTitle;

	private String projectTitle = EMPTY_PROJECT;

	private ProjectType xmlProjectType;

	private File xmlProjectFile;

	private volatile FixDictionary activeDictionary;

	private volatile Message activeMessage;

	private volatile boolean isRequiredOnly = true;

	private volatile boolean isModifyHeader;

	private volatile boolean isModifyTrailer;

	private volatile boolean isFixTSession;

	private volatile boolean isPreviewBeforeSend;

	private JMenuBar menuBar;

	private JMenu fileMenu;

	private JMenu sessionMenu;

	private JMenu helpMenu;

	private JMenu windowMenu;

	private JMenuItem closeProjectMenuItem;
	private JMenuItem saveProjectMenuItem;

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

	private JButton addButton;

	private JButton sendButton;

	private JTable messagesTable;

	private FreeTextMessagePanel freeTextMessagePanel;

	private ProjectFrame projectFrame;

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
			System.exit(1);
		}

		fixTDictionary = dictionary;

		contentLock = new ReentrantReadWriteLock();

		try
		{
			jaxbContext = JAXBContext.newInstance("com.jramoyo.fix.xml");
		} catch (JAXBException ex)
		{
			logger.error("Unable to create JAXB context for com.jramoyo.fix.xml");
			System.exit(1);
		}
	}

	/**
	 * Gracefully exits the application
	 */
	public void exit()
	{
		int choice = JOptionPane.showConfirmDialog(this,
				"Exit QuickFIX Messenger?", "Quit", JOptionPane.YES_NO_OPTION);
		if (choice == JOptionPane.YES_OPTION)
		{
			if (xmlProjectType != null)
			{
				choice = JOptionPane.showConfirmDialog(this,
						"Do you want to save \"" + xmlProjectType.getName()
								+ "\"?", "Save Current Project",
						JOptionPane.YES_NO_CANCEL_OPTION);
				switch (choice)
				{
				case JOptionPane.NO_OPTION:
					break;
				case JOptionPane.YES_OPTION:
					marshallXmlProjectType();
					break;
				case JOptionPane.CANCEL_OPTION:
					return;
				}
			}
			setVisible(false);
			messenger.exit();
		}
	}

	/**
	 * Returns the JAXB context
	 * 
	 * @return the JAXB context
	 */
	public JAXBContext getJaxbContext()
	{
		return jaxbContext;
	}

	/**
	 * Returns the application instance
	 * 
	 * @return the application instance
	 */
	public QFixMessenger getMessenger()
	{
		return messenger;
	}

	/**
	 * Returns the active XML ProjectType
	 * 
	 * @return the active XML ProjectType
	 */
	public ProjectType getXmlProjectType()
	{
		return xmlProjectType;
	}

	/**
	 * Launches the frame
	 */
	public void launch()
	{
		setIconImage(new ImageIcon(messenger.getConfig().getIconsLocation()
				+ Icons.APP_ICON).getImage());

		if (messenger.getConfig().isInitiator())
		{
			frameTitle = "QuickFIX Messenger " + VERSION + " (Initiator)";
		} else
		{
			frameTitle = "QuickFIX Messenger " + VERSION + " (Acceptor)";
		}
		loadFrameTitle();
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addWindowListener(new FrameWindowAdapter(this));

		initComponents();
		setVisible(true);
	}

	/**
	 * Loads an XML MessageType to the UI
	 * 
	 * @param xmlMessageType
	 *            an XML MessageType
	 */
	public void loadXmlMessage(MessageType xmlMessageType)
	{
		if (selectSession(xmlMessageType.getSession()))
		{
			if (selectMessage(xmlMessageType))
			{
				populateMembers(xmlMessageType);
			}
		}
	}

	/**
	 * Saves an XML MessageType to a file
	 * 
	 * @param xmlMessageType
	 *            an XML MessageType
	 */
	public void marshallXmlMessage(MessageType xmlMessageType)
	{
		JFileChooser jFileChooser = new JFileChooser();
		jFileChooser.setFileFilter(XmlFileFilter.INSTANCE);

		int choice = jFileChooser.showSaveDialog(this);
		if (choice == JFileChooser.APPROVE_OPTION)
		{
			File file = jFileChooser.getSelectedFile();
			try
			{
				JAXBElement<MessageType> rootElement = new JAXBElement<MessageType>(
						new QName("http://xml.fix.jramoyo.com", "message"),
						MessageType.class, xmlMessageType);
				Marshaller marshaller = jaxbContext.createMarshaller();
				marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,
						Boolean.TRUE);
				marshaller.marshal(rootElement, file);
				logger.debug("Message exported to " + file.getName());
				JOptionPane.showMessageDialog(this, "Message exported to "
						+ file.getName(), "Export",
						JOptionPane.INFORMATION_MESSAGE);
			} catch (JAXBException ex)
			{
				logger.error(
						"A JAXBException occurred while exporting message.", ex);
				JOptionPane.showMessageDialog(this,
						"An error occurred while exporting message!", "Error",
						JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	/**
	 * Saves the active XML ProjectType to a file
	 */
	public void marshallXmlProjectType()
	{
		if (xmlProjectFile == null)
		{
			JFileChooser jFileChooser = new JFileChooser();
			jFileChooser.setFileFilter(XmlFileFilter.INSTANCE);

			int choice = jFileChooser.showSaveDialog(this);
			if (choice == JFileChooser.APPROVE_OPTION)
			{
				xmlProjectFile = jFileChooser.getSelectedFile();
			}

			else if (choice == JOptionPane.CANCEL_OPTION)
			{
				return;
			}
		}

		try
		{
			JAXBElement<ProjectType> rootElement = new JAXBElement<ProjectType>(
					new QName("http://xml.fix.jramoyo.com", "project"),
					ProjectType.class, xmlProjectType);
			Marshaller marshaller = jaxbContext.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,
					Boolean.TRUE);
			marshaller.marshal(rootElement, xmlProjectFile);
			logger.debug("Message exported to " + xmlProjectFile.getName());
			JOptionPane.showMessageDialog(this, "Project saved to "
					+ xmlProjectFile.getName(), "Export",
					JOptionPane.INFORMATION_MESSAGE);
		} catch (JAXBException ex)
		{
			logger.error("A JAXBException occurred while exporting message.",
					ex);
			JOptionPane.showMessageDialog(this,
					"An error occurred while saving project!", "Error",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * Serializes the message form to an XML MessageType
	 * 
	 * @param session
	 *            the current session
	 * @return an XML MessageType
	 */
	public MessageType serializeFormAsXmlMessage(Session session)
	{
		ObjectFactory xmlObjectFactory = new ObjectFactory();

		MessageType xmlMessageType = xmlObjectFactory.createMessageType();
		xmlMessageType.setName(activeMessage.getName());
		xmlMessageType.setMsgType(activeMessage.getMsgType());
		xmlMessageType.setIsRequiredOnly(requiredCheckBox.isSelected());

		SessionType xmlSessionType = xmlObjectFactory.createSessionType();
		xmlSessionType.setName(QFixUtil.getSessionName(session.getSessionID()));
		if (isFixTSession)
		{
			String appVersionId = (String) appVersionsComboBox
					.getSelectedItem();
			xmlSessionType.setAppVersionId(appVersionId);
		}

		HeaderType xmlHeaderType = null;
		BodyType xmlBodyType = null;
		TrailerType xmlTrailerType = null;
		try
		{
			contentLock.readLock().lock();

			if (isModifyHeader)
			{
				xmlHeaderType = xmlObjectFactory.createHeaderType();
				for (MemberPanel memberPanel : headerMembers)
				{
					if (memberPanel instanceof FieldPanel)
					{
						FieldType xmlFieldType = ((FieldPanel) memberPanel)
								.getXmlField();
						xmlHeaderType.getField().add(xmlFieldType);
					}
				}
			}

			xmlBodyType = xmlObjectFactory.createBodyType();
			for (MemberPanel memberPanel : bodyMembers)
			{
				if (memberPanel instanceof FieldPanel)
				{
					FieldPanel fieldPanel = (FieldPanel) memberPanel;
					xmlBodyType.getFieldOrGroupsOrComponent().add(
							fieldPanel.getXmlField());
				}

				if (memberPanel instanceof GroupPanel)
				{
					GroupPanel groupPanel = (GroupPanel) memberPanel;
					xmlBodyType.getFieldOrGroupsOrComponent().add(
							groupPanel.getXmlGroups());
				}

				if (memberPanel instanceof ComponentPanel)
				{
					ComponentPanel componentPanel = (ComponentPanel) memberPanel;
					xmlBodyType.getFieldOrGroupsOrComponent().add(
							componentPanel.getXmlComponent());
				}
			}

			if (isModifyTrailer)
			{
				xmlTrailerType = xmlObjectFactory.createTrailerType();
				for (MemberPanel memberPanel : trailerMembers)
				{
					if (memberPanel instanceof FieldPanel)
					{
						FieldType xmlFieldType = ((FieldPanel) memberPanel)
								.getXmlField();
						xmlTrailerType.getField().add(xmlFieldType);
					}
				}
			}
		} finally
		{
			contentLock.readLock().unlock();
		}

		xmlMessageType.setSession(xmlSessionType);
		xmlMessageType.setHeader(xmlHeaderType);
		xmlMessageType.setBody(xmlBodyType);
		xmlMessageType.setTrailer(xmlTrailerType);

		return xmlMessageType;
	}

	/**
	 * Sets the active XML ProjectType
	 * 
	 * @param xmlProjectType
	 *            an XML ProjectType
	 */
	public void setXmlProjectType(ProjectType xmlProjectType)
	{
		this.xmlProjectType = xmlProjectType;
		if (projectFrame != null)
		{
			projectFrame.dispose();
			projectFrame = null;
		}

		if (xmlProjectType != null)
		{
			projectTitle = xmlProjectType.getName();
			loadFrameTitle();
			addButton.setEnabled(true);
			saveProjectMenuItem.setEnabled(true);
			closeProjectMenuItem.setEnabled(true);
			launchProjectFrame();
		} else
		{
			projectTitle = EMPTY_PROJECT;
			loadFrameTitle();
			saveProjectMenuItem.setEnabled(false);
			closeProjectMenuItem.setEnabled(false);
			addButton.setEnabled(false);
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

	private void initFileMenu()
	{
		fileMenu = new JMenu("File");
		fileMenu.setMnemonic('F');

		JMenuItem newProjectMenuItem = new JMenuItem("New Project");
		newProjectMenuItem.setIcon(new ImageIcon(messenger.getConfig()
				.getIconsLocation() + Icons.NEW_ICON));
		newProjectMenuItem.setMnemonic('N');
		newProjectMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N,
				InputEvent.CTRL_DOWN_MASK));
		newProjectMenuItem
				.addActionListener(new NewProjectActionListener(this));

		saveProjectMenuItem = new JMenuItem("Save Project");
		saveProjectMenuItem.setIcon(new ImageIcon(messenger.getConfig()
				.getIconsLocation() + Icons.SAVE_ICON));
		saveProjectMenuItem.setMnemonic('S');
		saveProjectMenuItem.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK));
		saveProjectMenuItem.addActionListener(new SaveProjectActionListener(
				this));
		saveProjectMenuItem.setEnabled(false);

		JMenuItem openProjectMenuItem = new JMenuItem("Open Project");
		openProjectMenuItem.setIcon(new ImageIcon(messenger.getConfig()
				.getIconsLocation() + Icons.OPEN_ICON));
		openProjectMenuItem.setMnemonic('O');
		openProjectMenuItem.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK));
		openProjectMenuItem.addActionListener(new OpenProjectActionListener(
				this));

		closeProjectMenuItem = new JMenuItem("Close Project");
		closeProjectMenuItem.setIcon(new ImageIcon(messenger.getConfig()
				.getIconsLocation() + Icons.CLOSE_ICON));
		closeProjectMenuItem.setMnemonic('C');
		closeProjectMenuItem.addActionListener(new CloseProjectActionListener(
				this));
		closeProjectMenuItem.setEnabled(false);

		JMenuItem importMessageMenuItem = new JMenuItem("Import Message");
		importMessageMenuItem.setIcon(new ImageIcon(messenger.getConfig()
				.getIconsLocation() + Icons.IMPORT_ICON));
		importMessageMenuItem.setMnemonic('I');
		importMessageMenuItem.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_I, InputEvent.CTRL_DOWN_MASK));
		importMessageMenuItem
				.addActionListener(new ImportMessageActionListener(this));

		JMenuItem exportMessageMenuItem = new JMenuItem("Export Message");
		exportMessageMenuItem.setIcon(new ImageIcon(messenger.getConfig()
				.getIconsLocation() + Icons.EXPORT_ICON));
		exportMessageMenuItem
				.addActionListener(new ExportMessageActionListener(this));
		exportMessageMenuItem.setMnemonic('X');
		exportMessageMenuItem.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_E, InputEvent.CTRL_DOWN_MASK));

		JMenuItem exitMenuItem = new JMenuItem("Exit");
		exitMenuItem.setIcon(new ImageIcon(messenger.getConfig()
				.getIconsLocation() + Icons.EXIT_ICON));
		exitMenuItem.setMnemonic('x');
		exitMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X,
				InputEvent.ALT_DOWN_MASK));
		exitMenuItem.addActionListener(new FrameExitActionListener(this));

		fileMenu.add(newProjectMenuItem);
		fileMenu.add(saveProjectMenuItem);
		fileMenu.add(openProjectMenuItem);
		fileMenu.add(closeProjectMenuItem);
		fileMenu.addSeparator();
		fileMenu.add(importMessageMenuItem);
		fileMenu.add(exportMessageMenuItem);
		fileMenu.addSeparator();
		fileMenu.add(exitMenuItem);
	}

	private void initHelpMenu()
	{
		helpMenu = new JMenu("Help");
		helpMenu.setMnemonic('H');

		JMenuItem helpMenuItem = new JMenuItem("Help");
		helpMenuItem.setIcon(new ImageIcon(messenger.getConfig()
				.getIconsLocation() + Icons.HELP_ICON));
		helpMenuItem.setMnemonic('H');
		helpMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F2,
				InputEvent.SHIFT_DOWN_MASK));
		helpMenuItem.addActionListener(new HelpActionListener(this));

		JMenuItem aboutMenuItem = new JMenuItem("About");
		aboutMenuItem.setMnemonic('A');
		aboutMenuItem.addActionListener(new AboutActionListener(this));

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

		initFileMenu();
		initSessionMenu();
		initHelpMenu();
		initWindowMenu();

		menuBar.add(fileMenu);
		menuBar.add(sessionMenu);
		menuBar.add(helpMenu);
		menuBar.add(windowMenu);

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

		ImageIcon addImageIcon = new ImageIcon(messenger.getConfig()
				.getIconsLocation() + Icons.ADD_ICON);
		addButton = new JButton(addImageIcon);
		addButton.addActionListener(new AddMessageActionListener(this));
		addButton.setToolTipText("Adds the message to the current project");
		addButton.setEnabled(false);

		ImageIcon sendImageIcon = new ImageIcon(messenger.getConfig()
				.getIconsLocation() + Icons.SEND_ICON);
		sendButton = new JButton(sendImageIcon);
		sendButton.addActionListener(new SendActionListener(this));
		sendButton.setToolTipText("Sends the message across the session");

		c.gridx = 0;
		c.gridy = 0;
		sendPanel.add(addButton, c);

		c.gridx = 0;
		c.gridy = 1;
		sendPanel.add(sendButton, c);

		c.gridx = 0;
		c.gridy = 2;
		sendPanel.add(previewBeforeSendCheckBox, c);

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
		logonAllSessionsMenuItem.setIcon(new ImageIcon(messenger.getConfig()
				.getIconsLocation() + Icons.LOGON_ICON));
		logonAllSessionsMenuItem.setMnemonic('n');
		logonAllSessionsMenuItem.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK));
		logonAllSessionsMenuItem
				.addActionListener(new LogonAllSessionsActionListener(this));

		JMenuItem logoffAllSessionsMenuItem = new JMenuItem(
				"All Sessions - Logoff");
		logoffAllSessionsMenuItem.setIcon(new ImageIcon(messenger.getConfig()
				.getIconsLocation() + Icons.LOGOFF_ICON));
		logoffAllSessionsMenuItem.setMnemonic('f');
		logoffAllSessionsMenuItem.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_F, InputEvent.CTRL_DOWN_MASK));
		logoffAllSessionsMenuItem
				.addActionListener(new LogoffAllSessionsActionListener(this));

		JMenuItem resetAllSessionsMenuItem = new JMenuItem(
				"All Sessions - Reset");
		resetAllSessionsMenuItem.setIcon(new ImageIcon(messenger.getConfig()
				.getIconsLocation() + Icons.RESET_ICON));
		resetAllSessionsMenuItem.setMnemonic('R');
		resetAllSessionsMenuItem.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_R, InputEvent.CTRL_DOWN_MASK));
		resetAllSessionsMenuItem
				.addActionListener(new ResetAllSessionsActionListener(this));

		sessionMenu.addSeparator();
		sessionMenu.add(logonAllSessionsMenuItem);
		sessionMenu.add(logoffAllSessionsMenuItem);
		sessionMenu.add(resetAllSessionsMenuItem);
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

	private void initWindowMenu()
	{
		windowMenu = new JMenu("Window");
		windowMenu.setMnemonic('W');

		JMenuItem projectWindowMenuItem = new JMenuItem("Project Window");
		projectWindowMenuItem.setIcon(new ImageIcon(messenger.getConfig()
				.getIconsLocation() + Icons.WINDOW_ICON));
		projectWindowMenuItem.setMnemonic('P');
		projectWindowMenuItem.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_P, InputEvent.CTRL_DOWN_MASK));
		projectWindowMenuItem
				.addActionListener(new ProjectWindowActionListener(this));

		windowMenu.add(projectWindowMenuItem);
	}

	private void launchProjectFrame()
	{
		if (projectFrame == null || !projectFrame.isDisplayable())
		{
			projectFrame = new ProjectFrame(this, xmlProjectType);
			projectFrame.launch();
		} else
		{
			if (projectFrame.getState() == Frame.ICONIFIED)
			{
				projectFrame.setState(Frame.NORMAL);
			}
			projectFrame.requestFocus();
		}
	}

	private void loadFrameTitle()
	{
		setTitle(frameTitle + " - " + projectTitle);
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

	private void populateMembers(MessageType xmlMessageType)
	{
		HeaderType xmlHeaderType = xmlMessageType.getHeader();
		if (xmlHeaderType != null)
		{
			for (Object xmlMember : xmlHeaderType.getField())
			{
				if (xmlMember instanceof FieldType)
				{
					FieldType xmlFieldType = (FieldType) xmlMember;
					FieldPanel fieldPanel = (FieldPanel) MemberPanelUtil
							.findMemberPanelByName(xmlFieldType.getName(),
									headerMembers);
					fieldPanel.populate(xmlFieldType);
				}
			}
		}

		BodyType xmlBodyType = xmlMessageType.getBody();
		for (Object xmlMember : xmlBodyType.getFieldOrGroupsOrComponent())
		{
			if (xmlMember instanceof FieldType)
			{
				FieldType xmlFieldType = (FieldType) xmlMember;
				FieldPanel fieldPanel = (FieldPanel) MemberPanelUtil
						.findMemberPanelByName(xmlFieldType.getName(),
								bodyMembers);
				fieldPanel.populate(xmlFieldType);
			}

			if (xmlMember instanceof GroupsType)
			{
				GroupsType xmlGroupsType = (GroupsType) xmlMember;
				GroupPanel groupPanel = (GroupPanel) MemberPanelUtil
						.findMemberPanelByName(xmlGroupsType.getName(),
								bodyMembers);
				groupPanel.populate(xmlGroupsType);
			}

			if (xmlMember instanceof ComponentType)
			{
				ComponentType xmlComponentTypeMember = (ComponentType) xmlMember;
				ComponentPanel componentPanel = (ComponentPanel) MemberPanelUtil
						.findMemberPanelByName(
								xmlComponentTypeMember.getName(), bodyMembers);
				componentPanel.populate(xmlComponentTypeMember);
			}
		}

		TrailerType xmlTrailerType = xmlMessageType.getTrailer();
		if (xmlTrailerType != null)
		{
			for (Object xmlMember : xmlTrailerType.getField())
			{
				if (xmlMember instanceof FieldType)
				{
					FieldType xmlFieldType = (FieldType) xmlMember;
					FieldPanel fieldPanel = (FieldPanel) MemberPanelUtil
							.findMemberPanelByName(xmlFieldType.getName(),
									trailerMembers);
					fieldPanel.populate(xmlFieldType);
				}
			}
		}
	}

	private boolean selectMessage(MessageType xmlMessageType)
	{
		boolean isRecognizedMessage = false;
		ListModel<Message> listModel = messagesList.getModel();
		for (int i = 0; i < listModel.getSize(); i++)
		{
			Message message = listModel.getElementAt(i);
			if (message.getMsgType().equals(xmlMessageType.getMsgType()))
			{
				messagesList.setSelectedIndex(i);
				isRecognizedMessage = true;
			}
		}

		if (!isRecognizedMessage)
		{
			logger.error("Unrecognized message: ", xmlMessageType.getName()
					+ " (" + xmlMessageType.getMsgType() + ")");
			JOptionPane.showMessageDialog(
					this,
					"Unable to import message from unrecognized message: "
							+ xmlMessageType.getName() + " ("
							+ xmlMessageType.getMsgType() + ")", "Error",
					JOptionPane.ERROR_MESSAGE);
			return false;
		}

		if (xmlMessageType.isIsRequiredOnly())
		{
			requiredCheckBox.setSelected(true);
		} else
		{
			requiredCheckBox.setSelected(false);
		}

		if (xmlMessageType.getHeader() != null
				&& xmlMessageType.getHeader().getField() != null
				&& xmlMessageType.getHeader().getField().isEmpty())
		{
			modifyHeaderCheckBox.setSelected(true);
		} else
		{
			modifyHeaderCheckBox.setSelected(false);
		}

		if (xmlMessageType.getTrailer() != null
				&& xmlMessageType.getTrailer().getField() != null
				&& xmlMessageType.getTrailer().getField().isEmpty())
		{
			modifyTrailerCheckBox.setSelected(true);
		} else
		{
			modifyTrailerCheckBox.setSelected(false);
		}

		return true;
	}

	private boolean selectSession(SessionType xmlSessionType)
	{
		boolean isRecognizedSession = false;
		ListModel<Session> listModel = sessionsList.getModel();
		for (int i = 0; i < listModel.getSize(); i++)
		{
			Session session = listModel.getElementAt(i);
			if (QFixUtil.getSessionName(session.getSessionID()).equals(
					xmlSessionType.getName()))
			{
				sessionsList.setSelectedIndex(i);
				isRecognizedSession = true;
			}
		}

		if (isRecognizedSession)
		{
			if (isFixTSession)
			{
				if (xmlSessionType.getAppVersionId() != null
						&& !xmlSessionType.getAppVersionId().isEmpty())
				{
					boolean isRecognizedAppVersionId = false;
					ComboBoxModel<String> comboBoxModel = appVersionsComboBox
							.getModel();
					for (int i = 0; i < comboBoxModel.getSize(); i++)
					{
						if (comboBoxModel.getElementAt(i).equals(
								xmlSessionType.getAppVersionId()))
						{
							appVersionsComboBox.setSelectedIndex(i);
							isRecognizedAppVersionId = true;
						}
					}

					if (!isRecognizedAppVersionId)
					{
						logger.error("Unrecognized AppVersionId: "
								+ xmlSessionType.getAppVersionId());
						JOptionPane.showMessageDialog(
								this,
								"Unrecognized AppVersionId: "
										+ xmlSessionType.getAppVersionId(),
								"Error", JOptionPane.ERROR_MESSAGE);
						return false;
					}
				} else
				{
					logger.error("FIXT session ", xmlSessionType.getName()
							+ " has no AppVersionId");
					JOptionPane.showMessageDialog(this,
							"Unable to find AppVersionId for FIXT session: "
									+ xmlSessionType.getName(), "Error",
							JOptionPane.ERROR_MESSAGE);
					return false;
				}
			}

		} else
		{
			logger.error("Unrecognized session: ", xmlSessionType.getName());
			JOptionPane.showMessageDialog(this, "Unable to"
					+ " import message from unrecognized session: "
					+ xmlSessionType.getName(), "Error",
					JOptionPane.ERROR_MESSAGE);
			return false;
		}

		return true;
	}

	public static class XmlFileFilter extends FileFilter
	{
		public static final XmlFileFilter INSTANCE = new XmlFileFilter();

		@Override
		public boolean accept(File f)
		{
			if (f.isDirectory())
			{
				return true;
			}
			return f.getName().endsWith(".xml");
		}

		@Override
		public String getDescription()
		{
			return "XML Files (*.xml)";
		}
	}

	private static class AddMessageActionListener implements ActionListener
	{
		private QFixMessengerFrame frame;

		public AddMessageActionListener(QFixMessengerFrame frame)
		{
			this.frame = frame;
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			Session session = (Session) frame.sessionsList.getSelectedValue();

			if (frame.activeMessage != null)
			{
				if (!frame.activeMessage.equals(frame.freeTextMessage))
				{
					MessageType xmlMessageType = frame
							.serializeFormAsXmlMessage(session);

					ProjectType xmlProjectType = frame.getXmlProjectType();
					xmlProjectType.getMessages().getMessage()
							.add(xmlMessageType);
					frame.projectFrame.addedMessage(xmlMessageType);
				} else
				{
					JOptionPane.showMessageDialog(frame,
							"Free text message cannot be saved!", "Error",
							JOptionPane.WARNING_MESSAGE);
				}
			} else
			{
				JOptionPane.showMessageDialog(frame,
						"Please create a message!", "Error",
						JOptionPane.WARNING_MESSAGE);
			}
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

	private static class ExportMessageActionListener implements ActionListener
	{
		private QFixMessengerFrame frame;

		public ExportMessageActionListener(QFixMessengerFrame frame)
		{
			this.frame = frame;
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			Session session = (Session) frame.sessionsList.getSelectedValue();

			if (frame.activeMessage != null)
			{
				if (!frame.activeMessage.equals(frame.freeTextMessage))
				{
					MessageType xmlMessageType = frame
							.serializeFormAsXmlMessage(session);
					frame.marshallXmlMessage(xmlMessageType);
				} else
				{
					JOptionPane.showMessageDialog(frame,
							"Free text message cannot be exported!", "Error",
							JOptionPane.WARNING_MESSAGE);
				}
			} else
			{
				JOptionPane.showMessageDialog(frame,
						"Please create a message!", "Error",
						JOptionPane.WARNING_MESSAGE);
			}
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

	private static class ProjectWindowActionListener implements ActionListener
	{
		private QFixMessengerFrame frame;

		public ProjectWindowActionListener(QFixMessengerFrame frame)
		{
			this.frame = frame;
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			if (frame.xmlProjectType != null)
			{
				frame.launchProjectFrame();
			} else
			{
				JOptionPane.showMessageDialog(frame, "No active project!",
						"Error", JOptionPane.WARNING_MESSAGE);
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
			if (session != null)
			{
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
								int choice = JOptionPane.showConfirmDialog(
										frame, message.toString(),
										"Send FIX Message?",
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
								logger.info("Sending message "
										+ message.toString());
								session.send(message);
							}
						}
					} else
					{
						JOptionPane.showMessageDialog(frame,
								"Please create a message!", "Error",
								JOptionPane.WARNING_MESSAGE);
					}
				} else
				{
					JOptionPane.showMessageDialog(frame,
							QFixUtil.getSessionName(session.getSessionID())
									+ " is not logged on!", "Error",
							JOptionPane.ERROR_MESSAGE);
				}
			} else
			{
				JOptionPane.showMessageDialog(frame,
						"Please create a message!", "Error",
						JOptionPane.WARNING_MESSAGE);
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

			try
			{
				frame.contentLock.readLock().lock();

				if (frame.isModifyHeader)
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
