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
 * ComponentPanelTest.java
 * 13 Jun 2011
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
public class ComponentPanelTest extends MemberPanelTestCase
{
	@Test
	public void testComponentWithGroup()
	{
		CountDownLatch latch = new CountDownLatch(1);

		Map<Member, Boolean> members = new HashMap<Member, Boolean>();
		Field field1 = new Field(101, "VanillaField1", FieldType.STRING, null);
		Field field2 = new Field(102, "VanillaField2", FieldType.STRING, null);
		Field field3 = new Field(103, "VanillaField3", FieldType.STRING, null);
		Field field4 = new Field(104, "VanillaField4", FieldType.STRING, null);

		Map<Member, Boolean> groupMembers = new HashMap<Member, Boolean>();
		Field groupField1 = new Field(201, "GroupField1", FieldType.STRING,
				null);
		Field groupField2 = new Field(202, "GroupField2", FieldType.STRING,
				null);
		Field groupField3 = new Field(203, "GroupField3", FieldType.STRING,
				null);
		Field groupField4 = new Field(204, "GroupField4", FieldType.STRING,
				null);

		groupMembers.put(groupField1, Boolean.TRUE);
		groupMembers.put(groupField2, Boolean.FALSE);
		groupMembers.put(groupField3, Boolean.FALSE);
		groupMembers.put(groupField4, Boolean.TRUE);

		Field groupField = new Field(100, "GroupField", FieldType.STRING, null);
		Group group = new Group(groupField, groupMembers, groupField1);

		members.put(field1, Boolean.TRUE);
		members.put(field2, Boolean.TRUE);
		members.put(field3, Boolean.FALSE);
		members.put(field4, Boolean.FALSE);
		members.put(group, Boolean.TRUE);

		Component component = new Component("Component1", members, field1);

		ComponentPanel componentPanel = new ComponentPanel(component, false,
				false);
		showInFrame(componentPanel, latch);

		try
		{
			latch.await();
		} catch (InterruptedException ex)
		{
			Thread.currentThread().interrupt();
		}
	}

	@Test
	public void testVanillaComponent()
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

		Component component = new Component("Component1", members, field1);

		ComponentPanel componentPanel = new ComponentPanel(component, false,
				false);
		showInFrame(componentPanel, latch);

		try
		{
			latch.await();
		} catch (InterruptedException ex)
		{
			Thread.currentThread().interrupt();
		}
	}
}
