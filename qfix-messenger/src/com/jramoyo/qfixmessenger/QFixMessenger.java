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
 * QFixMessenger.java
 * 6 Jun 2011
 */
package com.jramoyo.qfixmessenger;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import quickfix.ConfigError;
import quickfix.Connector;
import quickfix.DefaultMessageFactory;
import quickfix.FileLogFactory;
import quickfix.FileStoreFactory;
import quickfix.Group;
import quickfix.LogFactory;
import quickfix.Message;
import quickfix.MessageFactory;
import quickfix.MessageStoreFactory;
import quickfix.Session;
import quickfix.SessionID;
import quickfix.SessionSettings;
import quickfix.SocketAcceptor;
import quickfix.SocketInitiator;
import quickfix.StringField;
import quickfix.field.ApplVerID;

import com.jramoyo.fix.model.parser.FixDictionaryParser;
import com.jramoyo.fix.xml.BodyType;
import com.jramoyo.fix.xml.ComponentType;
import com.jramoyo.fix.xml.FieldType;
import com.jramoyo.fix.xml.GroupType;
import com.jramoyo.fix.xml.GroupsType;
import com.jramoyo.fix.xml.HeaderType;
import com.jramoyo.fix.xml.MessageType;
import com.jramoyo.fix.xml.TrailerType;
import com.jramoyo.qfixmessenger.config.QFixMessengerConfig;
import com.jramoyo.qfixmessenger.quickfix.ComponentHelper;
import com.jramoyo.qfixmessenger.quickfix.QFixMessengerApplication;
import com.jramoyo.qfixmessenger.quickfix.parser.QFixDictionaryParser;
import com.jramoyo.qfixmessenger.quickfix.util.QFixUtil;
import com.jramoyo.qfixmessenger.ui.QFixMessengerFrame;

/**
 * QuickFIX Messenger main application
 * 
 * @author jamoyo
 */
public class QFixMessenger
{
	private static final Logger logger = LoggerFactory
			.getLogger(QFixMessenger.class);
	private static final CountDownLatch shutdownLatch = new CountDownLatch(1);

	public static void main(String[] args) throws Exception
	{
		// Set handler for all uncaught exceptions
		Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler()
		{
			@Override
			public void uncaughtException(Thread t, Throwable ex)
			{
				logger.error("An unexpected exception occured!", ex);
			}
		});

		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				setLookAndFeel();
			}
		});

		if (args.length == 2)
		{
			InputStream inputStream = null;
			String configFileName = args[0];
			try
			{
				inputStream = new FileInputStream(args[1]);
			} catch (FileNotFoundException ex)
			{
				logger.error("File not found: " + args[1]);
				logger.error("Quitting...");
				System.err.println("File not found: " + args[1]);
				System.err.println("Quitting...");
				System.exit(0);
			}

			if (inputStream != null)
			{
				try
				{
					SessionSettings settings = new SessionSettings(inputStream);
					inputStream.close();

					QFixMessenger messenger = new QFixMessenger(configFileName,
							settings);
					QFixMessengerFrame frame = new QFixMessengerFrame(messenger);

					messenger.logon();
					frame.launch();

					shutdownLatch.await();
					logger.info("Shutting down at " + new Date() + "...");
					System.exit(0);
				} catch (ConfigError ex)
				{
					logger.error("Unable to read config file!", ex);
					logger.error("Quitting...");
					System.err.println("Unable to read config file!");
					System.err.println("Quitting...");
					System.exit(1);
				} catch (IOException ex)
				{
					logger.warn("Unable to close config files!", ex);
				}
			}
		} else
		{
			System.out.println("Usage: QFixMessenger <app cfg file>"
					+ " <quickfix cfg file>");
			System.exit(0);
		}
	}

	private static void setLookAndFeel()
	{
		try
		{
			String useSystemLookAndFeelProperty = System
					.getProperty("useSystemLAF");
			if (Boolean.valueOf(useSystemLookAndFeelProperty))
			{
				UIManager.setLookAndFeel(UIManager
						.getSystemLookAndFeelClassName());
			} else
			{
				UIManager.setLookAndFeel("com.lipstikLF.LipstikLookAndFeel");
			}
		} catch (Exception ex)
		{
			logger.warn(ex.getMessage(), ex);
		}
	}

	private final QFixMessengerConfig config;

	private final FixDictionaryParser parser;

	private final QFixMessengerApplication application;

	private final Connector connector;

	private final AtomicBoolean connectorStarted;

	private JAXBContext jaxbContext;

	private QFixMessenger(String configFileName, SessionSettings settings)
			throws ConfigError, IOException
	{
		// Load application configuration
		config = new QFixMessengerConfig(configFileName);

		// Create the dictionary parser
		parser = new QFixDictionaryParser(config.getNoOfParserThreads());

		// Create the QuickFIX application
		application = new QFixMessengerApplication(settings);

		// Initialise the factories
		MessageStoreFactory messageStoreFactory = new FileStoreFactory(settings);
		LogFactory logFactory = new FileLogFactory(settings);
		MessageFactory messageFactory = new DefaultMessageFactory();

		if (config.isInitiator())
		{
			connector = new SocketInitiator(application, messageStoreFactory,
					settings, logFactory, messageFactory);
		} else
		{
			connector = new SocketAcceptor(application, messageStoreFactory,
					settings, logFactory, messageFactory);
		}

		connectorStarted = new AtomicBoolean(false);

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
		logout();
		connector.stop();
		shutdownLatch.countDown();
	}

	/**
	 * Returns the QuickFIX application
	 * 
	 * @return the QuickFIX application
	 */
	public QFixMessengerApplication getApplication()
	{
		return application;
	}

	/**
	 * Returns the QuickFIX configuration
	 * 
	 * @return the QuickFIX configuration
	 */
	public QFixMessengerConfig getConfig()
	{
		return config;
	}

	/**
	 * Returns the QuickFIX connector
	 * 
	 * @return the QuickFIX connector
	 */
	public Connector getConnector()
	{
		return connector;
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
	 * Returns the FixDictionaryParser
	 * 
	 * @return the FixDictionaryParser
	 */
	public FixDictionaryParser getParser()
	{
		return parser;
	}

	/**
	 * Logs the connector on
	 */
	public void logon()
	{
		if (!connectorStarted.get())
		{
			try
			{
				connector.start();
				connectorStarted.getAndSet(true);
			} catch (Exception ex)
			{
				logger.error("Logon failed!", ex);
			}
		} else
		{
			Iterator<SessionID> sessionIds = connector.getSessions().iterator();
			while (sessionIds.hasNext())
			{
				SessionID sessionId = (SessionID) sessionIds.next();
				Session.lookupSession(sessionId).logon();
			}
		}
	}

	/**
	 * Logs the connector out
	 */
	public void logout()
	{
		Iterator<SessionID> sessionIds = connector.getSessions().iterator();
		while (sessionIds.hasNext())
		{
			SessionID sessionId = (SessionID) sessionIds.next();
			Session.lookupSession(sessionId).logout("user requested");
		}
	}

	/**
	 * Sends a QuickFIX Message across a given session
	 * 
	 * @param message
	 *            a QuickFIX Message
	 * @param session
	 *            a QuickFIX Session
	 * @return whether the message was sent or not
	 */
	public boolean sendQFixMessage(Message message, Session session)
	{
		logger.info("Sending message: " + message.toString());
		return session.send(message);
	}

	/**
	 * Converts an XML Message to a QuickFIX Message and sends it across the
	 * session
	 * 
	 * @param xmlMessageType
	 *            an XML Message
	 * @return whether the message was sent or not
	 * @throws QFixMessengerException
	 */
	public boolean sendXmlMessage(MessageType xmlMessageType)
			throws QFixMessengerException
	{
		Session session = null;
		List<SessionID> sessionIds = connector.getSessions();
		for (SessionID sessionId : sessionIds)
		{
			if (QFixUtil.getSessionName(sessionId).equals(
					xmlMessageType.getSession().getName()))
			{
				session = Session.lookupSession(sessionId);
			}
		}

		if (session != null)
		{
			if (session.isLoggedOn())
			{
				quickfix.Message message = session.getMessageFactory().create(
						session.getSessionID().getBeginString(),
						xmlMessageType.getMsgType());

				if (xmlMessageType.getSession().getAppVersionId() != null)
				{
					ApplVerID applVerID = new ApplVerID(
							QFixMessengerConstants.APPVER_ID_MAP
									.get(xmlMessageType.getSession()
											.getAppVersionId()));
					message.getHeader().setField(applVerID);
				}

				if (xmlMessageType.getHeader() != null)
				{
					HeaderType xmlHeaderType = xmlMessageType.getHeader();
					for (FieldType xmlFieldType : xmlHeaderType.getField())
					{
						message.getHeader().setField(
								createStringField(xmlFieldType));
					}
				}

				BodyType xmlBodyType = xmlMessageType.getBody();
				for (Object xmlObject : xmlBodyType
						.getFieldOrGroupsOrComponent())
				{
					if (xmlObject instanceof FieldType)
					{
						FieldType xmlFieldType = (FieldType) xmlObject;
						message.setField(createStringField(xmlFieldType));
					}

					else if (xmlObject instanceof GroupsType)
					{
						GroupsType xmlGroupsType = (GroupsType) xmlObject;
						for (Group group : createGroups(xmlGroupsType))
						{
							message.addGroup(group);
						}
					}

					else if (xmlObject instanceof ComponentType)
					{
						ComponentType xmlComponentType = (ComponentType) xmlObject;
						ComponentHelper componentHelper = createComponent(xmlComponentType);
						for (StringField stringField : componentHelper
								.getFields())
						{
							message.setField(stringField);
						}

						for (Group group : componentHelper.getGroups())
						{
							message.addGroup(group);
						}
					}
				}

				if (xmlMessageType.getTrailer() != null)
				{
					TrailerType xmlTrailerType = xmlMessageType.getTrailer();
					for (FieldType xmlFieldType : xmlTrailerType.getField())
					{
						message.getTrailer().setField(
								createStringField(xmlFieldType));
					}
				}

				return sendQFixMessage(message, session);
			} else
			{
				throw new QFixMessengerException("Session not logged on: "
						+ xmlMessageType.getSession().getName());
			}
		} else
		{
			throw new QFixMessengerException("Unrecognized session: "
					+ xmlMessageType.getSession().getName());
		}
	}

	private ComponentHelper createComponent(ComponentType xmlComponentType)
	{
		List<StringField> fields = new ArrayList<StringField>();
		for (Object xmlObject : xmlComponentType.getFieldOrGroupsOrComponent())
		{
			if (xmlObject instanceof FieldType)
			{
				fields.add(createStringField((FieldType) xmlObject));
			}
		}

		List<quickfix.Group> groups = new ArrayList<quickfix.Group>();
		for (Object xmlObject : xmlComponentType.getFieldOrGroupsOrComponent())
		{
			if (xmlObject instanceof GroupsType)
			{
				groups.addAll(createGroups((GroupsType) xmlObject));
			}
		}

		return new ComponentHelper(fields, groups);
	}

	private List<Group> createGroups(GroupsType xmlGroupsType)
	{
		List<Group> groups = new ArrayList<Group>();

		for (GroupType xmlGroupType : xmlGroupsType.getGroup())
		{
			Object firstMember = xmlGroupType.getFieldOrGroupsOrComponent()
					.get(0);
			FieldType firstFieldType;
			if (firstMember instanceof ComponentType)
			{
				firstFieldType = (FieldType) ((ComponentType) firstMember)
						.getFieldOrGroupsOrComponent().get(0);
			} else
			{
				firstFieldType = (FieldType) firstMember;
			}

			Group group = new Group(xmlGroupsType.getId(),
					firstFieldType.getId());
			for (Object xmlObject : xmlGroupType.getFieldOrGroupsOrComponent())
			{
				if (xmlObject instanceof FieldType)
				{
					FieldType xmlFieldType = (FieldType) xmlObject;
					group.setField(createStringField(xmlFieldType));
				}

				else if (xmlObject instanceof GroupsType)
				{
					GroupsType memberXmlGroupsType = (GroupsType) xmlObject;
					for (Group memberGroup : createGroups(memberXmlGroupsType))
					{
						group.addGroup(memberGroup);
					}
				}

				else if (xmlObject instanceof ComponentType)
				{
					ComponentType xmlComponentType = (ComponentType) xmlObject;
					ComponentHelper componentHelper = createComponent(xmlComponentType);
					for (StringField stringField : componentHelper.getFields())
					{
						group.setField(stringField);
					}

					for (Group memberGroup : componentHelper.getGroups())
					{
						group.addGroup(memberGroup);
					}
				}
			}

			groups.add(group);
		}

		return groups;
	}

	private StringField createStringField(FieldType xmlFieldType)
	{
		return new StringField(xmlFieldType.getId(), xmlFieldType.getValue());
	}
}
