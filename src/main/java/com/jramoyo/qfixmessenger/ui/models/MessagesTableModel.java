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
 * MessagesTableModel.java
 * 14 Jun 2011
 */
package com.jramoyo.qfixmessenger.ui.models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import quickfix.FieldNotFound;
import quickfix.Message;
import quickfix.SessionID;
import quickfix.field.MsgType;

import com.jramoyo.qfixmessenger.quickfix.QFixMessageListener;
import com.jramoyo.qfixmessenger.quickfix.util.QFixUtil;
import com.jramoyo.qfixmessenger.ui.models.data.MessagesTableModelData;

/**
 * Represents a table for model for messages
 * 
 * @author jamoyo
 */
public class MessagesTableModel extends AbstractTableModel implements
		QFixMessageListener
{
	private static final Logger logger = LoggerFactory
			.getLogger(MessagesTableModel.class);

	private static final long serialVersionUID = 3045456639720725016L;

	private final List<MessagesTableModelData> tableData = new ArrayList<MessagesTableModelData>();

	/**
	 * Adds a message to the table
	 * 
	 * @param rowData
	 *            a message data
	 */
	public void addRow(MessagesTableModelData rowData)
	{
		int row = tableData.size();
		tableData.add(rowData);
		fireTableRowsInserted(row, row);
	}

	/**
	 * Clears the messages table
	 */
	public void clear()
	{
		tableData.clear();
		fireTableDataChanged();
	}

	@Override
	public Class<?> getColumnClass(int c)
	{
		Object object = getValueAt(0, c);
		if (object != null)
		{
			return object.getClass();
		}

		return null;
	}

	@Override
	public int getColumnCount()
	{
		return MessagesTableModelData.COLUMN_NAMES.length;
	}

	@Override
	public String getColumnName(int col)
	{
		return MessagesTableModelData.COLUMN_NAMES[col];
	}

	public MessagesTableModelData getData(int row)
	{
		return tableData.get(row);
	}

	@Override
	public int getRowCount()
	{
		return tableData.size();
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex)
	{
		if (!tableData.isEmpty())
		{
			MessagesTableModelData rowData = tableData.get(rowIndex);

			return rowData
					.getColumData(MessagesTableModelData.COLUMN_NAMES[columnIndex]);
		}

		return null;
	}

	@Override
	public boolean isCellEditable(int row, int col)
	{
		return false;
	}

	@Override
	public void onMessage(String direction, Message message, SessionID sessionId)
	{
		try
		{
			MsgType msgType = (MsgType) message.getHeader().getField(
					new MsgType());
			MessagesTableModelData data = new MessagesTableModelData(
					new Date(), direction, QFixUtil.getSessionName(sessionId),
					message.toString(), msgType.getValue());
			addRow(data);
		} catch (FieldNotFound ex)
		{
			logger.error("An exception occured!", ex);
		}
	}
}
