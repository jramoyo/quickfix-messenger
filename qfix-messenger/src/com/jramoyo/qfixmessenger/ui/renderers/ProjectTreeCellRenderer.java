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
 * ProjectTreeCellRenderer.java
 * Sep 26, 2012
 */
package com.jramoyo.qfixmessenger.ui.renderers;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

import com.jramoyo.fix.xml.BodyType;
import com.jramoyo.fix.xml.ComponentType;
import com.jramoyo.fix.xml.FieldType;
import com.jramoyo.fix.xml.GroupType;
import com.jramoyo.fix.xml.GroupsType;
import com.jramoyo.fix.xml.HeaderType;
import com.jramoyo.fix.xml.MessageType;
import com.jramoyo.fix.xml.MessagesType;
import com.jramoyo.fix.xml.ProjectType;
import com.jramoyo.fix.xml.SessionType;
import com.jramoyo.fix.xml.TrailerType;

/**
 * @author jramoyo
 */
public class ProjectTreeCellRenderer extends DefaultTreeCellRenderer
{
	private static final long serialVersionUID = -435212244413010769L;

	public Component getTreeCellRendererComponent(JTree tree, Object value,
			boolean sel, boolean expanded, boolean leaf, int row,
			boolean hasFocus)
	{
		JLabel label = (JLabel) super.getTreeCellRendererComponent(tree, value,
				sel, expanded, leaf, row, hasFocus);

		if (value instanceof ProjectType)
		{
			ProjectType xmlProjectType = (ProjectType) value;
			label.setText(xmlProjectType.getName());
		}

		else if (value instanceof MessagesType)
		{
			label.setText("Messages");
		}

		else if (value instanceof MessageType)
		{
			MessageType xmlMessageType = (MessageType) value;
			label.setText(xmlMessageType.getName() + " ("
					+ xmlMessageType.getMsgType() + ")");
		}

		else if (value instanceof SessionType)
		{
			label.setText("Session");
		}

		else if (value instanceof HeaderType)
		{
			label.setText("Header");
		}

		else if (value instanceof BodyType)
		{
			label.setText("Body");
		}

		else if (value instanceof TrailerType)
		{
			label.setText("Trailer");
		}

		else if (value instanceof GroupsType)
		{
			GroupsType xmlGroupsType = (GroupsType) value;
			label.setText(xmlGroupsType.getName() + " ("
					+ xmlGroupsType.getId() + ")");
		}

		else if (value instanceof GroupType)
		{
			label.setText("Group");
		}

		else if (value instanceof ComponentType)
		{
			ComponentType xmlComponentType = (ComponentType) value;
			label.setText(xmlComponentType.getName());
		}

		else if (value instanceof FieldType)
		{
			FieldType xmlFieldType = (FieldType) value;
			label.setText(xmlFieldType.getName() + " (" + xmlFieldType.getId()
					+ ")");
		}

		return label;
	}
}
