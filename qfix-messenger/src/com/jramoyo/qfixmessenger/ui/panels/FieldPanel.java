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
import java.text.DecimalFormat;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.ComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JLayer;
import javax.swing.JPanel;
import javax.swing.plaf.LayerUI;

import quickfix.StringField;

import com.jramoyo.fix.model.Field;
import com.jramoyo.fix.model.FieldType;
import com.jramoyo.fix.model.FieldValue;
import com.jramoyo.fix.xml.ObjectFactory;
import com.jramoyo.qfixmessenger.QFixMessengerConstants;
import com.jramoyo.qfixmessenger.ui.QFixMessengerFrame;
import com.jramoyo.qfixmessenger.ui.layers.FieldValidationLayerUI;
import com.jramoyo.qfixmessenger.ui.renderers.FieldComboBoxCellRenderer;
import com.jramoyo.qfixmessenger.util.StringUtil;

/**
 * @author jamoyo
 */
public class FieldPanel extends
		AbstractMemberPanel<Field, StringField, com.jramoyo.fix.xml.FieldType>
{
	private static final long serialVersionUID = -8397965355420860765L;

	private static final FieldValue EMPTY_FIELD_VALUE = new FieldValue("", "");

	private final boolean isRequired;

	private JLabel fieldLabel;

	private JFormattedTextField fieldTextField;

	private JComboBox<FieldValue> fieldComboBox;

	private LayerUI<JFormattedTextField> layerUI;

	private JButton dateButton;

	public FieldPanel(QFixMessengerFrame frame, Field field, boolean isRequired)
	{
		super(frame, field);
		this.isRequired = isRequired;

		initComponents();
	}

	@Override
	public String getFixString()
	{
		if (!StringUtil.isNullOrEmpty(getValue()))
		{
			return "" + getMember().getNumber() + "=" + getValue();
		} else
		{
			return "";
		}
	}

	public StringField getQuickFixMember()
	{
		if (!StringUtil.isNullOrEmpty(getValue()))
		{
			/*
			 * Since all values are entered as text, all fields are represented
			 * as an instance of StringField.
			 */
			return new StringField(getMember().getNumber(), getValue());
		} else
		{
			return null;
		}
	}

	public com.jramoyo.fix.xml.FieldType getXmlMember()
	{
		if (!StringUtil.isNullOrEmpty(getValue()))
		{
			com.jramoyo.fix.xml.FieldType xmlFieldType = new ObjectFactory()
					.createFieldType();
			xmlFieldType.setId(getMember().getNumber());
			xmlFieldType.setName(getMember().getName());
			xmlFieldType.setValue(getValue());

			return xmlFieldType;
		} else
		{
			return null;
		}
	}

	@Override
	public boolean hasValidContent()
	{
		if (fieldComboBox != null)
		{
			return true;
		}

		else
		{
			return fieldTextField.isEditValid();
		}
	}

	public void populateXml(com.jramoyo.fix.xml.FieldType xmlFieldType)
	{
		if (fieldComboBox != null)
		{
			ComboBoxModel<FieldValue> comboBoxModel = fieldComboBox.getModel();
			for (int i = 0; i < comboBoxModel.getSize(); i++)
			{
				FieldValue fieldValue = comboBoxModel.getElementAt(i);
				if (fieldValue.getEnumValue().equals(xmlFieldType.getValue()))
				{
					fieldComboBox.setSelectedIndex(i);
				}
			}
		}

		else
		{
			fieldTextField.setText(xmlFieldType.getValue());
		}
	}

	private String generateUtcTimeStamp()
	{
		return new SimpleDateFormat(QFixMessengerConstants.UTC_DATE_FORMAT)
				.format(new Date());
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

		layerUI = new FieldValidationLayerUI(getFrame());

		fieldLabel = new JLabel(getMember().toString());
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

		if (getMember().getValues() != null
				&& !getMember().getValues().isEmpty())
		{
			List<FieldValue> fieldValues = new ArrayList<FieldValue>();
			if (!isRequired)
			{
				fieldValues.add(EMPTY_FIELD_VALUE);
			}

			fieldValues.addAll(getMember().getValues());
			fieldComboBox = new JComboBox<FieldValue>(
					fieldValues.toArray(new FieldValue[] {}));
			fieldComboBox.setRenderer(new FieldComboBoxCellRenderer());
			fieldComboBox.setToolTipText("Select a value");

			fieldValuePanel.add(fieldComboBox);
		}

		else
		{
			Format format;
			String toolTip;
			if (isFieldUtcType())
			{
				format = new SimpleDateFormat(
						QFixMessengerConstants.UTC_DATE_FORMAT);
				toolTip = "Enter a date in UTC format";

				dateButton = new JButton("UTC Date");
				dateButton.addActionListener(new ActionListener()
				{
					@Override
					public void actionPerformed(ActionEvent e)
					{
						fieldTextField.setText(generateUtcTimeStamp());
					}
				});
			}

			else if (isFieldDoubleType())
			{
				format = new DecimalFormat("###.###0");
				toolTip = "Enter a floating point value";
			}

			else if (isFieldIntegerType())
			{
				format = new DecimalFormat("###");
				toolTip = "Enter an integer value";
			}

			else
			{
				format = null;
				toolTip = "Enter a value";
			}

			fieldTextField = new JFormattedTextField(format);
			fieldTextField.setFocusLostBehavior(JFormattedTextField.COMMIT);
			fieldTextField.setToolTipText(toolTip);
			fieldValuePanel.add(new JLayer<JFormattedTextField>(fieldTextField,
					layerUI));

			if (dateButton != null)
			{
				fieldValuePanel.add(dateButton);
			}
		}

		add(fieldLabel);
		add(fieldValuePanel);
	}

	private boolean isFieldDoubleType()
	{
		if (getMember().getType().getJavaClass() != null
				&& getMember().getType().getJavaClass().equals(Double.class))
		{
			return true;
		}

		return false;
	}

	private boolean isFieldIntegerType()
	{
		if (getMember().getType().getJavaClass() != null
				&& getMember().getType().getJavaClass().equals(Integer.class))
		{
			return true;
		}

		return false;
	}

	private boolean isFieldUtcType()
	{
		if (getMember().getType().equals(FieldType.UTCTIMESTAMP)
				|| getMember().getType().equals(FieldType.UTCDATEONLY)
				|| getMember().getType().equals(FieldType.UTCDATE)
				|| getMember().getType().equals(FieldType.UTCTIMEONLY))
		{
			return true;
		}

		return false;
	}
}
