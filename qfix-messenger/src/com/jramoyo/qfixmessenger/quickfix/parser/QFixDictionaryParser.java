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
 * QFixDictionaryParser.java
 * 7 Jun 2011
 */
package com.jramoyo.qfixmessenger.quickfix.parser;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jramoyo.fix.model.Component;
import com.jramoyo.fix.model.Field;
import com.jramoyo.fix.model.FieldType;
import com.jramoyo.fix.model.FieldValue;
import com.jramoyo.fix.model.FixDictionary;
import com.jramoyo.fix.model.Group;
import com.jramoyo.fix.model.Header;
import com.jramoyo.fix.model.Member;
import com.jramoyo.fix.model.Message;
import com.jramoyo.fix.model.MessageCategory;
import com.jramoyo.fix.model.Trailer;
import com.jramoyo.fix.model.parser.FixDictionaryParser;
import com.jramoyo.fix.model.parser.FixParsingException;
import com.jramoyo.qfixmessenger.util.StringUtil;

/**
 * QuickFIX implementation of FixDictionaryParser
 * <p>
 * Note: this implementation uses JDom.
 * </p>
 * 
 * @author jamoyo
 */
public class QFixDictionaryParser implements FixDictionaryParser
{
	private static final Logger logger = LoggerFactory
			.getLogger(QFixDictionaryParser.class);

	private final ExecutorService threadPool;

	/**
	 * Creates a new QFixDictionaryParser
	 * 
	 * @param noOfThreads the number of parser threads to use
	 */
	public QFixDictionaryParser(int noOfThreads)
	{
		threadPool = Executors.newFixedThreadPool(noOfThreads,
				new ParserThreadFactory());
	}

	@Override
	public FixDictionary parse(String fileName) throws FixParsingException
	{
		long startTime = System.currentTimeMillis();

		FixDictionary dictionary;
		try
		{
			FileInputStream fileInputStream = new FileInputStream(fileName);

			SAXBuilder saxBuilder = new SAXBuilder();
			Document document = saxBuilder.build(fileInputStream);

			Element root = document.getRootElement();

			// Parse the dictionary (version) attributes
			String type = root.getAttributeValue("type");
			String majorVersion = root.getAttributeValue("major");
			String minorVersion = root.getAttributeValue("minor");

			dictionary = new FixDictionary(type, majorVersion, minorVersion);

			// Parse the field definitions
			if (logger.isDebugEnabled())
			{
				logger.debug("Parsing field definitions...");
			}
			Element fieldsElement = root.getChild("fields");
			@SuppressWarnings("unchecked")
			List<Element> fieldElements = fieldsElement.getChildren("field");
			parseFields(dictionary, fieldElements);

			// Parse the component definitions
			if (logger.isDebugEnabled())
			{
				logger.debug("Parsing component definitions...");
			}
			Element componentsElement = root.getChild("components");
			if (componentsElement != null)
			{
				@SuppressWarnings("unchecked")
				List<Element> componentElements = componentsElement
						.getChildren("component");
				parseComponents(dictionary, componentElements);
			}

			// Parse the message definitions
			if (logger.isDebugEnabled())
			{
				logger.debug("Parsing message definitions...");
			}
			Element messagesElement = root.getChild("messages");
			@SuppressWarnings("unchecked")
			List<Element> messageElements = messagesElement
					.getChildren("message");
			parseMessages(dictionary, messageElements);

			// Parse the header definition
			if (logger.isDebugEnabled())
			{
				logger.debug("Parsing header definition...");
			}
			Element headerElement = root.getChild("header");
			parseHeader(dictionary, headerElement);

			// Parse the trailer definition
			if (logger.isDebugEnabled())
			{
				logger.debug("Parsing trailer definition...");
			}
			Element trailerElement = root.getChild("trailer");
			parseTrailer(dictionary, trailerElement);
		} catch (FileNotFoundException ex)
		{
			throw new FixParsingException("File " + fileName + " not foud!", ex);
		} catch (IOException ex)
		{
			throw new FixParsingException("An IOException occured!", ex);
		} catch (JDOMException ex)
		{
			throw new FixParsingException("A JDOMException occured!", ex);
		}

		if (logger.isDebugEnabled())
		{
			long elapsedTime = System.currentTimeMillis() - startTime;
			logger.debug("Time taken to parse dictionary " + dictionary + ": "
					+ elapsedTime + " ms.");
		}
		return dictionary;
	}

	private Component parseComponent(FixDictionary dictionary,
			Element componentElement) throws FixParsingException
	{
		String name = componentElement.getAttributeValue("name");
		if (logger.isTraceEnabled())
		{
			logger.trace("Parsing component " + name);
		}

		Element firstTagElement = componentElement.getChild("field");
		String firstTagName = null;
		if (firstTagElement != null)
		{
			firstTagName = firstTagElement.getAttributeValue("name");
		}
		Field firstTag = null;
		if (!StringUtil.isNullOrEmpty(firstTagName))
		{
			firstTag = dictionary.getFields().get(firstTagName);
		}

		Map<Member, Boolean> members = parseMembers(dictionary,
				componentElement);

		return new Component(name, members, firstTag);
	}

	/*
	 * Due to nested components, parsing cannot be done in parallel
	 */
	private void parseComponents(final FixDictionary dictionary,
			List<Element> componentElements) throws FixParsingException
	{
		long startTime = System.nanoTime();

		for (Element componentElement : componentElements)
		{
			String componentName = componentElement.getAttributeValue("name");
			if (dictionary.getComponents().get(componentName) == null)
			{
				Component component = parseComponent(dictionary,
						componentElement);
				dictionary.getComponents().put(component.getName(), component);
			}
		}

		if (logger.isDebugEnabled())
		{
			long elapsedTime = System.nanoTime() - startTime;
			logger.debug("Time taken to parse components: " + elapsedTime
					+ " ns.");
		}
	}

	private Field parseField(Element fieldElement)
	{
		int number = Integer.parseInt(fieldElement.getAttributeValue("number"));
		String name = fieldElement.getAttributeValue("name");

		if (logger.isTraceEnabled())
		{
			logger.trace("Parsing field " + name);
		}

		FieldType type;
		try
		{
			// TODO Map all fields
			type = FieldType.valueOf(fieldElement.getAttributeValue("type"));
		} catch (IllegalArgumentException ex)
		{
			// Default to String
			type = FieldType.STRING;
		}

		List<FieldValue> values = null;
		@SuppressWarnings("unchecked")
		List<Element> valueElements = fieldElement.getChildren("value");
		if (valueElements != null && !valueElements.isEmpty())
		{
			values = new ArrayList<FieldValue>();
			for (Element valueElement : valueElements)
			{
				String enumValue = valueElement.getAttributeValue("enum");
				String description = valueElement
						.getAttributeValue("description");

				FieldValue value = new FieldValue(enumValue, description);
				values.add(value);
			}
		}

		return new Field(number, name, type, values);
	}

	/*
	 * Because field definitions are independent of each other, they can be
	 * parsed in parallel.
	 */
	private void parseFields(final FixDictionary dictionary,
			List<Element> fieldElements)
	{
		long startTime = System.nanoTime();

		final CountDownLatch latch = new CountDownLatch(fieldElements.size());
		for (final Element fieldElement : fieldElements)
		{
			threadPool.execute(new Runnable()
			{
				@Override
				public void run()
				{
					Field field = parseField(fieldElement);
					dictionary.getFields().put(field.getName(), field);
					latch.countDown();
					if (logger.isTraceEnabled())
					{
						logger.trace("Completed parsing " + field
								+ ". Remaining field tasks: "
								+ latch.getCount());
					}
				}
			});
		}

		try
		{
			if (logger.isDebugEnabled())
			{
				logger.debug("Awaiting field parsing tasks to complete...");
			}
			latch.await();
		} catch (InterruptedException ex)
		{
			Thread.currentThread().interrupt();
		}

		if (logger.isDebugEnabled())
		{
			long elapsedTime = System.nanoTime() - startTime;
			logger.debug("Time taken to parse fields: " + elapsedTime + " ns.");
		}
	}

	private Group parseGroup(FixDictionary dictionary, Element groupElement)
			throws FixParsingException
	{
		String groupName = groupElement.getAttributeValue("name");

		Field field = dictionary.getFields().get(groupName);
		Map<Member, Boolean> members = parseMembers(dictionary, groupElement);

		Member firstMember = null;
		Element firstChildElement = (Element) groupElement.getChildren().get(0);
		if (firstChildElement.getName().equals("field"))
		{
			String firstFieldName = firstChildElement.getAttributeValue("name");
			if (!StringUtil.isNullOrEmpty(firstFieldName))
			{
				firstMember = dictionary.getFields().get(firstFieldName);
			}
		}

		else if (firstChildElement.getName().equals("component"))
		{
			String firstComponentName = firstChildElement
					.getAttributeValue("name");
			if (!StringUtil.isNullOrEmpty(firstComponentName))
			{
				firstMember = dictionary.getComponents()
						.get(firstComponentName);
			}
		}

		return new Group(field, members, firstMember);
	}

	private void parseHeader(FixDictionary dictionary, Element headerElement)
			throws FixParsingException
	{
		Map<Member, Boolean> members = parseMembers(dictionary, headerElement);
		dictionary.setHeader(new Header(members));
	}

	private Map<Member, Boolean> parseMembers(FixDictionary dictionary,
			Element element) throws FixParsingException
	{
		Map<Member, Boolean> members = new HashMap<Member, Boolean>();

		@SuppressWarnings("unchecked")
		List<Element> memberElements = element.getChildren();
		for (Element memberElement : memberElements)
		{
			if (memberElement.getName().equals("field"))
			{
				String fieldName = memberElement.getAttributeValue("name");
				Boolean isRequired = convertStringToBoolean(memberElement
						.getAttributeValue("required"));

				Field field = dictionary.getFields().get(fieldName);
				if (field != null)
				{
					members.put(field, isRequired);
				} else
				{
					throw new FixParsingException(fieldName
							+ " is not defined!");
				}
			}

			if (memberElement.getName().equals("group"))
			{
				Boolean isRequired = convertStringToBoolean(memberElement
						.getAttributeValue("required"));
				members.put(parseGroup(dictionary, memberElement), isRequired);
			}

			if (memberElement.getName().equals("component"))
			{
				String componentName = memberElement.getAttributeValue("name");
				Boolean isRequired = convertStringToBoolean(memberElement
						.getAttributeValue("required"));

				Component component = dictionary.getComponents().get(
						componentName);
				if (component == null)
				{
					// TODO Improve
					Element root = element.getDocument().getRootElement();
					@SuppressWarnings("unchecked")
					List<Element> componentElements = root.getChild(
							"components").getChildren("component");
					for (Element componentElement : componentElements)
					{
						String componentElementName = componentElement
								.getAttributeValue("name");
						if (componentElementName.equals(componentName))
						{
							component = parseComponent(dictionary,
									componentElement);
						}
					}
				}
				members.put(component, isRequired);
			}
		}

		return members;
	}

	private Message parseMessage(FixDictionary dictionary,
			Element messageElement) throws FixParsingException
	{
		String name = messageElement.getAttributeValue("name");
		if (logger.isTraceEnabled())
		{
			logger.trace("Parsing message " + name);
		}

		String msgType = messageElement.getAttributeValue("msgtype");
		MessageCategory category = MessageCategory.valueOf(messageElement
				.getAttributeValue("msgcat"));
		Map<Member, Boolean> members = parseMembers(dictionary, messageElement);

		return new Message(name, msgType, category, members);
	}

	/*
	 * Because message definitions are independent of each other, they can be
	 * parsed in parallel.
	 */
	private void parseMessages(final FixDictionary dictionary,
			List<Element> messageElements) throws FixParsingException
	{
		long startTime = System.nanoTime();

		final CountDownLatch latch = new CountDownLatch(messageElements.size());
		List<Future<Void>> results = new ArrayList<Future<Void>>();
		for (final Element messageElement : messageElements)
		{
			Future<Void> result = threadPool.submit(new Callable<Void>()
			{
				@Override
				public Void call() throws Exception
				{
					Message message = parseMessage(dictionary, messageElement);
					dictionary.getMessages().put(message.getName(), message);
					latch.countDown();
					if (logger.isTraceEnabled())
					{
						logger.trace("Completed parsing " + message
								+ ". Remaining message tasks: "
								+ latch.getCount());
					}

					return null;
				}
			});
			results.add(result);
		}

		try
		{
			if (logger.isDebugEnabled())
			{
				logger.debug("Awaiting messages parsing tasks to complete...");
			}
			latch.await();
			for (Future<Void> result : results)
			{
				try
				{
					result.get();
				} catch (ExecutionException ex)
				{
					throw new FixParsingException("Unable to parse messages!",
							ex);
				}
			}
		} catch (InterruptedException ex)
		{
			Thread.currentThread().interrupt();
		}

		if (logger.isDebugEnabled())
		{
			long elapsedTime = System.nanoTime() - startTime;
			logger.debug("Time taken to parse messages: " + elapsedTime
					+ " ns.");
		}
	}

	private void parseTrailer(FixDictionary dictionary, Element trailerElement)
			throws FixParsingException
	{
		Map<Member, Boolean> members = parseMembers(dictionary, trailerElement);
		dictionary.setTrailer(new Trailer(members));
	}

	private Boolean convertStringToBoolean(String string)
	{
		if (string == null)
			return Boolean.FALSE;

		if (string.equalsIgnoreCase("Y"))
			return Boolean.TRUE;
		else
			return Boolean.FALSE;
	}

	private static class ParserThreadFactory implements ThreadFactory
	{
		@Override
		public Thread newThread(Runnable r)
		{
			return new Thread(r, "parser");
		}
	}
}
