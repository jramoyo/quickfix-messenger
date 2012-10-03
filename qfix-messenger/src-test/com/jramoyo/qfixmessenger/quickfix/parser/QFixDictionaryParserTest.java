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
 * QFixDictionaryParserTest.java
 * 7 Jun 2011
 */
package com.jramoyo.qfixmessenger.quickfix.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.util.Map;
import java.util.Map.Entry;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import com.jramoyo.fix.model.Component;
import com.jramoyo.fix.model.Field;
import com.jramoyo.fix.model.FieldValue;
import com.jramoyo.fix.model.FixDictionary;
import com.jramoyo.fix.model.Group;
import com.jramoyo.fix.model.Member;
import com.jramoyo.fix.model.Message;
import com.jramoyo.fix.model.parser.FixDictionaryParser;
import com.jramoyo.fix.model.parser.FixParsingException;

/**
 * JUnit test case for QFixDictionaryParser
 * 
 * @author jamoyo
 */
@RunWith(BlockJUnit4ClassRunner.class)
public class QFixDictionaryParserTest
{
	private static final String INDENT = "   ";

	@Test
	public void testParseFIX40()
	{
		String fileName = "resources/dictionary/FIX40.xml";
		FixDictionaryParser parser = new QFixDictionaryParser(20);

		try
		{
			long startTime = System.currentTimeMillis();
			FixDictionary dictionary = parser.parse(fileName);
			long elapsedTime = System.currentTimeMillis() - startTime;

			assertEquals("4", dictionary.getMajorVersion());
			assertEquals("0", dictionary.getMinorVersion());
			assertNull(dictionary.getType());

			assertNotNull(dictionary.getFields());
			for (Field field : dictionary.getFields().values())
			{
				assertField(field);
			}

			assertNotNull(dictionary.getComponents());
			for (Component component : dictionary.getComponents().values())
			{
				assertComponent(component);
			}

			assertNotNull(dictionary.getMessages());
			for (Message message : dictionary.getMessages().values())
			{
				assertMessage(message);
			}

			displayDictionary(dictionary);

			System.out.println("Time taken to parse: " + elapsedTime + "ms");
		} catch (FixParsingException ex)
		{
			fail("A QFIXParsingException occurred: " + ex);
		}
	}

	@Test
	public void testParseFIX41()
	{
		String fileName = "resources/dictionary/FIX41.xml";
		FixDictionaryParser parser = new QFixDictionaryParser(20);

		try
		{
			long startTime = System.currentTimeMillis();
			FixDictionary dictionary = parser.parse(fileName);
			long elapsedTime = System.currentTimeMillis() - startTime;

			assertEquals("4", dictionary.getMajorVersion());
			assertEquals("1", dictionary.getMinorVersion());
			assertNull(dictionary.getType());

			assertNotNull(dictionary.getFields());
			for (Field field : dictionary.getFields().values())
			{
				assertField(field);
			}

			assertNotNull(dictionary.getComponents());
			for (Component component : dictionary.getComponents().values())
			{
				assertComponent(component);
			}

			assertNotNull(dictionary.getMessages());
			for (Message message : dictionary.getMessages().values())
			{
				assertMessage(message);
			}

			displayDictionary(dictionary);
			System.out.println("Time taken to parse: " + elapsedTime + "ms");
		} catch (FixParsingException ex)
		{
			fail("A QFIXParsingException occurred: " + ex);
		}
	}

	@Test
	public void testParseFIX42()
	{
		String fileName = "resources/dictionary/FIX42.xml";
		FixDictionaryParser parser = new QFixDictionaryParser(20);

		try
		{
			long startTime = System.currentTimeMillis();
			FixDictionary dictionary = parser.parse(fileName);
			long elapsedTime = System.currentTimeMillis() - startTime;

			assertEquals("4", dictionary.getMajorVersion());
			assertEquals("2", dictionary.getMinorVersion());
			assertNull(dictionary.getType());

			assertNotNull(dictionary.getFields());
			for (Field field : dictionary.getFields().values())
			{
				assertField(field);
			}

			assertNotNull(dictionary.getComponents());
			for (Component component : dictionary.getComponents().values())
			{
				assertComponent(component);
			}

			assertNotNull(dictionary.getMessages());
			for (Message message : dictionary.getMessages().values())
			{
				assertMessage(message);
			}

			displayDictionary(dictionary);
			System.out.println("Time taken to parse: " + elapsedTime + "ms");
		} catch (FixParsingException ex)
		{
			fail("A QFIXParsingException occurred: " + ex);
		}
	}

	@Test
	public void testParseFIX43()
	{
		String fileName = "resources/dictionary/FIX43.xml";
		FixDictionaryParser parser = new QFixDictionaryParser(20);

		try
		{
			long startTime = System.currentTimeMillis();
			FixDictionary dictionary = parser.parse(fileName);
			long elapsedTime = System.currentTimeMillis() - startTime;

			assertEquals("4", dictionary.getMajorVersion());
			assertEquals("3", dictionary.getMinorVersion());
			assertNull(dictionary.getType());

			assertNotNull(dictionary.getFields());
			for (Field field : dictionary.getFields().values())
			{
				assertField(field);
			}

			assertNotNull(dictionary.getComponents());
			for (Component component : dictionary.getComponents().values())
			{
				assertComponent(component);
			}

			assertNotNull(dictionary.getMessages());
			for (Message message : dictionary.getMessages().values())
			{
				assertMessage(message);
			}

			displayDictionary(dictionary);
			System.out.println("Time taken to parse: " + elapsedTime + "ms");
		} catch (FixParsingException ex)
		{
			fail("A QFIXParsingException occurred: " + ex);
		}
	}

	@Test
	public void testParseFIX44()
	{
		String fileName = "resources/dictionary/FIX44.xml";
		FixDictionaryParser parser = new QFixDictionaryParser(20);

		try
		{
			long startTime = System.currentTimeMillis();
			FixDictionary dictionary = parser.parse(fileName);
			long elapsedTime = System.currentTimeMillis() - startTime;

			assertEquals("4", dictionary.getMajorVersion());
			assertEquals("4", dictionary.getMinorVersion());
			assertNull(dictionary.getType());

			assertNotNull(dictionary.getFields());
			for (Field field : dictionary.getFields().values())
			{
				assertField(field);
			}

			assertNotNull(dictionary.getComponents());
			for (Component component : dictionary.getComponents().values())
			{
				assertComponent(component);
			}

			assertNotNull(dictionary.getMessages());
			for (Message message : dictionary.getMessages().values())
			{
				assertMessage(message);
			}

			displayDictionary(dictionary);
			System.out.println("Time taken to parse: " + elapsedTime + "ms");
		} catch (FixParsingException ex)
		{
			fail("A QFIXParsingException occurred: " + ex);
		}
	}

	@Test
	public void testParseFIX50()
	{
		String fileName = "resources/dictionary/FIX50.xml";
		FixDictionaryParser parser = new QFixDictionaryParser(20);

		try
		{
			long startTime = System.currentTimeMillis();
			FixDictionary dictionary = parser.parse(fileName);
			long elapsedTime = System.currentTimeMillis() - startTime;

			assertEquals("5", dictionary.getMajorVersion());
			assertEquals("0", dictionary.getMinorVersion());
			assertNull(dictionary.getType());

			assertNotNull(dictionary.getFields());
			for (Field field : dictionary.getFields().values())
			{
				assertField(field);
			}

			assertNotNull(dictionary.getComponents());
			for (Component component : dictionary.getComponents().values())
			{
				assertComponent(component);
			}

			assertNotNull(dictionary.getMessages());
			for (Message message : dictionary.getMessages().values())
			{
				assertMessage(message);
			}

			displayDictionary(dictionary);
			System.out.println("Time taken to parse: " + elapsedTime + "ms");
		} catch (FixParsingException ex)
		{
			fail("A QFIXParsingException occurred: " + ex);
		}
	}

	@Test
	public void testParseFIXT11()
	{
		String fileName = "resources/dictionary/FIXT11.xml";
		FixDictionaryParser parser = new QFixDictionaryParser(20);

		try
		{
			long startTime = System.currentTimeMillis();
			FixDictionary dictionary = parser.parse(fileName);
			long elapsedTime = System.currentTimeMillis() - startTime;

			assertEquals("1", dictionary.getMajorVersion());
			assertEquals("1", dictionary.getMinorVersion());
			assertEquals("FIXT", dictionary.getType());

			assertNotNull(dictionary.getFields());
			for (Field field : dictionary.getFields().values())
			{
				assertField(field);
			}

			assertNotNull(dictionary.getComponents());
			for (Component component : dictionary.getComponents().values())
			{
				assertComponent(component);
			}

			assertNotNull(dictionary.getMessages());
			for (Message message : dictionary.getMessages().values())
			{
				assertMessage(message);
			}

			displayDictionary(dictionary);
			System.out.println("Time taken to parse: " + elapsedTime + "ms");
		} catch (FixParsingException ex)
		{
			fail("A QFIXParsingException occurred: " + ex);
		}
	}

	private void assertComponent(Component component)
	{
		assertNotNull(component.getName());
		assertNotNull(component.getMembers());
		for (Member member : component.getMembers().keySet())
		{
			assertMembers(member);
		}
	}

	private void assertField(Field field)
	{
		assertNotNull(field.getNumber());
		assertNotNull(field.getName());
		assertNotNull(field.getType());
		if (field.getValues() != null)
		{
			for (FieldValue fieldValue : field.getValues())
			{
				assertNotNull(fieldValue.getEnumValue());
				assertNotNull(fieldValue.getDescription());
			}
		}
	}

	private void assertGroup(Group group)
	{
		assertNotNull(group.getName());
		assertNotNull(group.getNumber());
		assertNotNull(group.getMembers());
		for (Member member : group.getMembers().keySet())
		{
			assertMembers(member);
		}
	}

	private void assertMembers(Member member)
	{
		if (member instanceof Field)
		{
			Field field = (Field) member;
			assertField(field);
		}

		if (member instanceof Group)
		{
			Group group = (Group) member;
			assertGroup(group);
		}

		if (member instanceof Component)
		{
			Component component = (Component) member;
			assertComponent(component);
		}
	}

	private void assertMessage(Message message)
	{
		assertNotNull(message.getName());
		assertNotNull(message.getMsgType());
		assertNotNull(message.getCategory());
		for (Member member : message.getMembers().keySet())
		{
			assertMembers(member);
		}
	}

	private void displayComponent(String indent, Component component,
			Boolean isRequired)
	{
		System.out.print(indent);
		if (isRequired)
		{
			System.out.print("* ");
		}
		System.out.println(component.getName());
		displayMembers(indent + INDENT, component.getMembers());
	}

	private void displayDictionary(FixDictionary dictionary)
	{
		System.out.println("FIX Version: " + dictionary.getFullVersion());

		System.out.println("Messages");
		displayMessages(INDENT, dictionary.getMessages());

		System.out.println("Header");
		displayMembers(INDENT, dictionary.getHeader().getMembers());
		System.out.println();

		System.out.println("Trailer");
		displayMembers(INDENT, dictionary.getTrailer().getMembers());
		System.out.println();
	}

	private void displayField(String indent, Field field, Boolean isRequired)
	{
		System.out.print(indent);
		if (isRequired)
		{
			System.out.print("* ");
		}
		System.out.print(field.getName());
		System.out.print('(');
		System.out.print(field.getNumber());
		System.out.println(')');

		if (field.getValues() != null)
		{
			for (FieldValue fieldValue : field.getValues())
			{
				System.out.print(indent + indent);
				System.out.print(fieldValue.getEnumValue());
				System.out.print('[');
				System.out.print(fieldValue.getDescription());
				System.out.println(']');
			}
		}
	}

	private void displayGroup(String indent, Group group, Boolean isRequired)
	{
		System.out.print(indent);
		if (isRequired)
		{
			System.out.print("* ");
		}
		System.out.print(group.getName());
		System.out.print('(');
		System.out.print(group.getNumber());
		System.out.println(')');

		displayMembers(indent + INDENT, group.getMembers());
	}

	private void displayMembers(String indent, Map<Member, Boolean> members)
	{
		for (Entry<Member, Boolean> entry : members.entrySet())
		{
			if (entry.getKey() instanceof Field)
			{
				Field field = (Field) entry.getKey();
				displayField(indent, field, entry.getValue());
			}

			if (entry.getKey() instanceof Group)
			{
				Group group = (Group) entry.getKey();
				displayGroup(indent, group, entry.getValue());
			}

			if (entry.getKey() instanceof Component)
			{
				Component component = (Component) entry.getKey();
				displayComponent(indent, component, entry.getValue());
			}
		}
	}

	private void displayMessages(String indent, Map<String, Message> messages)
	{
		for (Entry<String, Message> message : messages.entrySet())
		{
			System.out.print(indent);
			System.out.print(message.getValue().getName());
			System.out.print('(');
			System.out.print(message.getValue().getMsgType());
			System.out.println(')');

			displayMembers(indent + INDENT, message.getValue().getMembers());
			System.out.println();
		}
	}
}
