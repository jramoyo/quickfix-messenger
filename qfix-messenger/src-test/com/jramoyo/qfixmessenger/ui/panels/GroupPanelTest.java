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
 * GroupPanel.java
 * 9 Jun 2011
 */
package com.jramoyo.qfixmessenger.ui.panels;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import com.jramoyo.fix.model.Component;
import com.jramoyo.fix.model.Field;
import com.jramoyo.fix.model.FieldType;
import com.jramoyo.fix.model.Group;
import com.jramoyo.fix.model.Member;

/**
 * @author jamoyo
 */
@RunWith(BlockJUnit4ClassRunner.class)
public class GroupPanelTest extends MemberPanelTestCase
{
	@Test
	public void testGroupWithComponent()
	{
		CountDownLatch latch = new CountDownLatch(1);

		Map<Member, Boolean> members = new HashMap<Member, Boolean>();
		Field field1 = new Field(101, "VanillaField1", FieldType.STRING, null);
		Field field2 = new Field(102, "VanillaField2", FieldType.STRING, null);
		Field field3 = new Field(103, "VanillaField3", FieldType.STRING, null);
		Field field4 = new Field(104, "VanillaField4", FieldType.STRING, null);

		Map<Member, Boolean> componentMembers = new HashMap<Member, Boolean>();
		Field compField1 = new Field(101, "ComponentField1", FieldType.STRING,
				null);
		Field compField2 = new Field(102, "ComponentField2", FieldType.STRING,
				null);
		Field compField3 = new Field(103, "ComponentField3", FieldType.STRING,
				null);
		Field compField4 = new Field(104, "ComponentField4", FieldType.STRING,
				null);

		componentMembers.put(compField1, Boolean.TRUE);
		componentMembers.put(compField2, Boolean.TRUE);
		componentMembers.put(compField3, Boolean.FALSE);
		componentMembers.put(compField4, Boolean.FALSE);

		Component component = new Component("Component1", componentMembers,
				compField1);

		members.put(field1, Boolean.TRUE);
		members.put(field2, Boolean.TRUE);
		members.put(field3, Boolean.FALSE);
		members.put(field4, Boolean.FALSE);
		members.put(component, Boolean.TRUE);

		Field groupField = new Field(100, "GroupField", FieldType.STRING, null);

		Group group = new Group(groupField, members, field1);

		GroupPanel groupPanel = new GroupPanel(group, false, false);
		showInFrame(groupPanel, latch);

		try
		{
			latch.await();
		} catch (InterruptedException ex)
		{
			Thread.currentThread().interrupt();
		}
	}

	@Test
	public void testGroupWithComponentFirst()
	{
		CountDownLatch latch = new CountDownLatch(1);

		Map<Member, Boolean> members = new HashMap<Member, Boolean>();
		Field field1 = new Field(101, "VanillaField1", FieldType.STRING, null);
		Field field2 = new Field(102, "VanillaField2", FieldType.STRING, null);
		Field field3 = new Field(103, "VanillaField3", FieldType.STRING, null);
		Field field4 = new Field(104, "VanillaField4", FieldType.STRING, null);

		Map<Member, Boolean> componentMembers = new HashMap<Member, Boolean>();
		Field compField1 = new Field(101, "ComponentField1", FieldType.STRING,
				null);
		Field compField2 = new Field(102, "ComponentField2", FieldType.STRING,
				null);
		Field compField3 = new Field(103, "ComponentField3", FieldType.STRING,
				null);
		Field compField4 = new Field(104, "ComponentField4", FieldType.STRING,
				null);

		componentMembers.put(compField1, Boolean.TRUE);
		componentMembers.put(compField2, Boolean.TRUE);
		componentMembers.put(compField3, Boolean.FALSE);
		componentMembers.put(compField4, Boolean.FALSE);

		Component component = new Component("Component1", componentMembers,
				compField1);

		members.put(component, Boolean.TRUE);
		members.put(field1, Boolean.TRUE);
		members.put(field2, Boolean.TRUE);
		members.put(field3, Boolean.FALSE);
		members.put(field4, Boolean.FALSE);

		Field groupField = new Field(100, "GroupField", FieldType.STRING, null);

		Group group = new Group(groupField, members, component);

		GroupPanel groupPanel = new GroupPanel(group, false, false);
		showInFrame(groupPanel, latch);

		try
		{
			latch.await();
		} catch (InterruptedException ex)
		{
			Thread.currentThread().interrupt();
		}
	}

	@Test
	public void testVanillaGroup()
	{
		CountDownLatch latch = new CountDownLatch(1);

		Map<Member, Boolean> members = new HashMap<Member, Boolean>();
		Field field1 = new Field(101, "VanillaField1", FieldType.STRING, null);
		Field field2 = new Field(102, "VanillaField2", FieldType.STRING, null);
		Field field3 = new Field(103, "VanillaField3", FieldType.STRING, null);
		Field field4 = new Field(104, "VanillaField4", FieldType.STRING, null);

		members.put(field1, Boolean.TRUE);
		members.put(field2, Boolean.TRUE);
		members.put(field3, Boolean.FALSE);
		members.put(field4, Boolean.FALSE);

		Field groupField = new Field(100, "GroupField", FieldType.STRING, null);

		Group group = new Group(groupField, members, field1);

		GroupPanel groupPanel = new GroupPanel(group, false, false);
		showInFrame(groupPanel, latch);

		try
		{
			latch.await();
		} catch (InterruptedException ex)
		{
			Thread.currentThread().interrupt();
		}
	}
}
