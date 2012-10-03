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
 * FieldPanel.java
 * 9 Jun 2011
 */
package com.jramoyo.qfixmessenger.ui.panels;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import com.jramoyo.fix.model.Field;
import com.jramoyo.fix.model.FieldType;
import com.jramoyo.fix.model.FieldValue;

/**
 * @author jamoyo
 */
@RunWith(BlockJUnit4ClassRunner.class)
public class FieldPanelTest extends MemberPanelTestCase
{
	@Test
	public void testDateField()
	{
		CountDownLatch latch = new CountDownLatch(1);
		Field field = new Field(100, "TestField", FieldType.UTCDATE, null);

		FieldPanel fieldPanel = new FieldPanel(field, false);
		showInFrame(fieldPanel, latch);

		try
		{
			latch.await();
		} catch (InterruptedException ex)
		{
			Thread.currentThread().interrupt();
		}
	}

	@Test
	public void testEnumField()
	{
		CountDownLatch latch = new CountDownLatch(1);

		List<FieldValue> values = new ArrayList<FieldValue>(2);
		values.add(new FieldValue("1", "DESC 1"));
		values.add(new FieldValue("2", "DESC 2"));

		Field field = new Field(100, "EnumField", FieldType.STRING, values);

		FieldPanel fieldPanel = new FieldPanel(field, false);
		showInFrame(fieldPanel, latch);

		try
		{
			latch.await();
		} catch (InterruptedException ex)
		{
			Thread.currentThread().interrupt();
		}
	}

	@Test
	public void testRequiredVanillaField()
	{
		CountDownLatch latch = new CountDownLatch(1);
		Field field = new Field(100, "RequiredVanillaField", FieldType.STRING,
				null);

		FieldPanel fieldPanel = new FieldPanel(field, true);
		showInFrame(fieldPanel, latch);

		try
		{
			latch.await();
		} catch (InterruptedException ex)
		{
			Thread.currentThread().interrupt();
		}
	}

	@Test
	public void testVanillaField()
	{
		CountDownLatch latch = new CountDownLatch(1);
		Field field = new Field(100, "VanillaField", FieldType.STRING, null);

		FieldPanel fieldPanel = new FieldPanel(field, false);
		showInFrame(fieldPanel, latch);

		try
		{
			latch.await();
		} catch (InterruptedException ex)
		{
			Thread.currentThread().interrupt();
		}
	}
}
