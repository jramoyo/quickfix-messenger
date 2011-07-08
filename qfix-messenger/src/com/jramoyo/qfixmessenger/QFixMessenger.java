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
import java.util.Date;
import java.util.Iterator;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.UIManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import quickfix.ConfigError;
import quickfix.Connector;
import quickfix.DefaultMessageFactory;
import quickfix.FileLogFactory;
import quickfix.FileStoreFactory;
import quickfix.LogFactory;
import quickfix.MessageFactory;
import quickfix.MessageStoreFactory;
import quickfix.Session;
import quickfix.SessionID;
import quickfix.SessionSettings;
import quickfix.SocketAcceptor;
import quickfix.SocketInitiator;

import com.jramoyo.fix.model.parser.FixDictionaryParser;
import com.jramoyo.qfixmessenger.config.QFixMessengerConfig;
import com.jramoyo.qfixmessenger.quickfix.QFixMessengerApplication;
import com.jramoyo.qfixmessenger.quickfix.parser.QFixDictionaryParser;
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

	private final QFixMessengerConfig config;
	private final FixDictionaryParser parser;

	private final QFixMessengerApplication application;
	private final Connector connector;
	private final AtomicBoolean connectorStarted;

	private QFixMessenger(String configFileName, SessionSettings settings)
			throws ConfigError, IOException
	{
		// Load application configuration
		config = new QFixMessengerConfig(configFileName);

		// Create the dictionary parser
		parser = new QFixDictionaryParser(config.getNoOfParserThreads());

		// Create the QuickFIX application
		application = new QFixMessengerApplication();

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
	}

	public static void main(String[] args) throws Exception
	{
		// Set the UI look and feel
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

	public void exit()
	{
		logout();
		connector.stop();
		shutdownLatch.countDown();
	}

	public QFixMessengerApplication getApplication()
	{
		return application;
	}

	public QFixMessengerConfig getConfig()
	{
		return config;
	}

	public Connector getConnector()
	{
		return connector;
	}

	public FixDictionaryParser getParser()
	{
		return parser;
	}

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

	public void logout()
	{
		Iterator<SessionID> sessionIds = connector.getSessions().iterator();
		while (sessionIds.hasNext())
		{
			SessionID sessionId = (SessionID) sessionIds.next();
			Session.lookupSession(sessionId).logout("user requested");
		}
	}

}
