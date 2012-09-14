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

import java.awt.Color;
import java.awt.Cursor;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import quickfix.StringField;

import com.jramoyo.fix.model.Field;
import com.jramoyo.fix.model.FieldType;
import com.jramoyo.fix.model.FieldValue;
import com.jramoyo.fix.model.Member;
import com.jramoyo.qfixmessenger.QFixMessengerConstants;
import com.jramoyo.qfixmessenger.ui.renderers.FieldComboBoxCellRenderer;
import com.jramoyo.qfixmessenger.util.StringUtil;

/**
 * @author jamoyo
 */
public class FieldPanel extends AbstractMemberPanel
{
	private static final long serialVersionUID = -8397965355420860765L;

	private static final FieldValue EMPTY_FIELD_VALUE = new FieldValue("", "");

	private final Field field;

	private final boolean isRequired;

	private JLabel fieldLabel;

	private JTextField fieldTextField;

	private JComboBox<FieldValue> fieldComboBox;

	private JButton dateButton;

	public FieldPanel(Field field, boolean isRequired)
	{
		this.field = field;
		this.isRequired = isRequired;

		initComponents();
	}

	public FieldPanel(FieldPanel fieldPanel)
	{
		this.field = fieldPanel.field;
		this.isRequired = fieldPanel.isRequired;

		initComponents();
		copyValue(fieldPanel);
	}

	/*
	 * Since all values are entered as text, all fields are represented as an
	 * instance of StringField.
	 */
	public StringField getQuickFixField()
	{
		if (!StringUtil.isNullOrEmpty(getValue()))
		{
			return new StringField(field.getNumber(), getValue());
		} else
		{
			return null;
		}
	}

	@Override
	public String getFixString()
	{
		if (!StringUtil.isNullOrEmpty(getValue()))
		{
			return new StringBuilder("" + field.getNumber()).append('=')
					.append(getValue()).toString();
		} else
		{
			return "";
		}
	}

	@Override
	public Member getMember()
	{
		return field;
	}

	private void copyValue(FieldPanel fieldPanel)
	{
		if (fieldComboBox != null && fieldPanel.fieldComboBox != null)
		{
			fieldComboBox.setSelectedItem(fieldPanel.fieldComboBox
					.getSelectedItem());
		}

		else
		{
			fieldTextField.setText(fieldPanel.fieldTextField.getText().trim());
		}
	}

	private String getValue()
	{
		if (fieldComboBox != null)
		{
			FieldValue fieldValue = (FieldValue) fieldComboBox
					.getSelectedItem();
			return fieldValue.getEnumValue();
		}

		else
		{
			return fieldTextField.getText().trim();
		}
	}

	private void initComponents()
	{
		setLayout(new GridLayout(2, 1));

		fieldLabel = new JLabel(field.toString());
		fieldLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		fieldLabel.addMouseListener(new LinkMouseAdapter(this));
		fieldLabel.setToolTipText("Double-click to look-up in FIXwiki");
		if (isRequired)
		{
			fieldLabel.setForeground(Color.BLUE);
		}

		JPanel fieldValuePanel = new JPanel();
		fieldValuePanel.setLayout(new BoxLayout(fieldValuePanel,
				BoxLayout.X_AXIS));

		if (field.getValues() != null && !field.getValues().isEmpty())
		{
			List<FieldValue> fieldValues = new ArrayList<FieldValue>();
			if (!isRequired)
			{
				fieldValues.add(EMPTY_FIELD_VALUE);
			}

			fieldValues.addAll(field.getValues());
			fieldComboBox = new JComboBox<FieldValue>(
					fieldValues.toArray(new FieldValue[] {}));
			fieldComboBox.setRenderer(new FieldComboBoxCellRenderer());

			fieldValuePanel.add(fieldComboBox);
		}

		else if (field.getType().equals(FieldType.UTCTIMESTAMP)
				|| field.getType().equals(FieldType.UTCDATEONLY)
				|| field.getType().equals(FieldType.UTCDATE)
				|| field.getType().equals(FieldType.UTCTIMEONLY))
		{
			fieldTextField = new JTextField();
			dateButton = new JButton("UTC Date");
			dateButton.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					fieldTextField.setText(generateUTCTimeStamp());
				}
			});

			fieldValuePanel.add(fieldTextField);
			fieldValuePanel.add(dateButton);
		}

		else
		{
			fieldTextField = new JTextField();
			fieldValuePanel.add(fieldTextField);
		}

		add(fieldLabel);
		add(fieldValuePanel);
	}

	private String generateUTCTimeStamp()
	{
		return new SimpleDateFormat(QFixMessengerConstants.UTC_DATE_FORMAT)
				.format(new Date());
	}
}
