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
 * ProjectTreeModel.java
 * Sep 25, 2012
 */
package com.jramoyo.qfixmessenger.ui.models;

import javax.swing.event.EventListenerList;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

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
 * Represents a tree model for an projects
 * <p>
 * This serves as an adapter for a JAXB ProjectType to be displayed into a
 * JTree.
 * </p>
 * 
 * @author jramoyo
 */
public class ProjectTreeModel implements TreeModel
{
	private ProjectType xmlProjectType;

	private EventListenerList eventListenerList;

	public ProjectTreeModel(ProjectType xmlProjectType)
	{
		this.xmlProjectType = xmlProjectType;
		eventListenerList = new EventListenerList();
	}

	@Override
	public void addTreeModelListener(TreeModelListener listener)
	{
		eventListenerList.add(TreeModelListener.class, listener);
	}

	@Override
	public Object getChild(Object parent, int index)
	{
		if (parent instanceof ProjectType)
		{
			if (index == 0)
			{
				return ((ProjectType) parent).getMessages();
			}
		}

		else if (parent instanceof MessagesType)
		{
			return ((MessagesType) parent).getMessage().get(index);
		}

		else if (parent instanceof MessageType)
		{
			MessageType xmlMessageType = (MessageType) parent;
			switch (index)
			{
			case 0:
				return xmlMessageType.getSession();
			case 1:
				if (xmlMessageType.getHeader() != null)
				{
					return xmlMessageType.getHeader();
				} else
				{
					return xmlMessageType.getBody();
				}
			case 2:
				if (xmlMessageType.getHeader() != null)
				{
					return xmlMessageType.getBody();
				} else
				{
					return xmlMessageType.getTrailer();
				}
			case 3:
				return xmlMessageType.getTrailer();
			}
		}

		else if (parent instanceof SessionType)
		{
			SessionType xmlSessionType = (SessionType) parent;
			switch (index)
			{
			case 0:
				return xmlSessionType.getName();
			case 1:
				return xmlSessionType.getAppVersionId();
			}
		}

		else if (parent instanceof HeaderType)
		{
			return ((HeaderType) parent).getField().get(index);
		}

		else if (parent instanceof BodyType)
		{
			return ((BodyType) parent).getFieldOrGroupsOrComponent().get(index);
		}

		else if (parent instanceof TrailerType)
		{
			return ((TrailerType) parent).getField().get(index);
		}

		else if (parent instanceof GroupsType)
		{
			return ((GroupsType) parent).getGroup().get(index);
		}

		else if (parent instanceof GroupType)
		{
			return ((GroupType) parent).getFieldOrGroupsOrComponent()
					.get(index);
		}

		else if (parent instanceof ComponentType)
		{
			return ((ComponentType) parent).getFieldOrGroupsOrComponent().get(
					index);
		}

		else if (parent instanceof FieldType)
		{
			if (index == 0)
			{
				return ((FieldType) parent).getValue();
			}
		}

		return null;
	}

	@Override
	public int getChildCount(Object parent)
	{
		if (parent instanceof ProjectType)
		{
			ProjectType xmlProjectType = (ProjectType) parent;
			if (xmlProjectType.getMessages() != null)
			{
				return 1;
			}
		}

		else if (parent instanceof MessagesType)
		{
			MessagesType xmlMessagesType = (MessagesType) parent;
			return xmlMessagesType.getMessage().size();
		}

		else if (parent instanceof MessageType)
		{
			int i = 0;

			MessageType xmlMessageType = (MessageType) parent;
			if (xmlMessageType.getSession() != null)
			{
				i++;
			}

			if (xmlMessageType.getHeader() != null)
			{
				i++;
			}

			if (xmlMessageType.getBody() != null)
			{
				i++;
			}

			if (xmlMessageType.getTrailer() != null)
			{
				i++;
			}

			return i;
		}

		else if (parent instanceof SessionType)
		{
			int i = 0;

			SessionType xmlSessionType = (SessionType) parent;
			if (xmlSessionType.getName() != null)
			{
				i++;
			}

			if (xmlSessionType.getAppVersionId() != null)
			{
				i++;
			}

			return i;
		}

		else if (parent instanceof HeaderType)
		{
			HeaderType xmlHeaderType = (HeaderType) parent;
			return xmlHeaderType.getField().size();
		}

		else if (parent instanceof BodyType)
		{
			BodyType xmlBodyType = (BodyType) parent;
			return xmlBodyType.getFieldOrGroupsOrComponent().size();
		}

		else if (parent instanceof TrailerType)
		{
			TrailerType xmlTrailerType = (TrailerType) parent;
			return xmlTrailerType.getField().size();
		}

		else if (parent instanceof GroupsType)
		{
			GroupsType groupsType = (GroupsType) parent;
			return groupsType.getGroup().size();
		}

		else if (parent instanceof GroupType)
		{
			GroupType xmlGroupType = (GroupType) parent;
			return xmlGroupType.getFieldOrGroupsOrComponent().size();
		}

		else if (parent instanceof ComponentType)
		{
			ComponentType xmlComponentType = (ComponentType) parent;
			return xmlComponentType.getFieldOrGroupsOrComponent().size();
		}

		else if (parent instanceof FieldType)
		{
			FieldType xmlFieldType = (FieldType) parent;
			if (xmlFieldType.getValue() != null)
			{
				return 1;
			}
		}

		return 0;
	}

	@Override
	public int getIndexOfChild(Object parent, Object child)
	{
		if (parent instanceof ProjectType)
		{
			ProjectType xmlProjectType = (ProjectType) parent;
			if (child.equals(xmlProjectType.getMessages()))
			{
				return 0;
			}
		}

		else if (parent instanceof MessagesType)
		{
			int i = 0;
			MessagesType xmlMessagesType = (MessagesType) parent;
			for (MessageType xmlMessageType : xmlMessagesType.getMessage())
			{
				if (child.equals(xmlMessageType))
				{
					return i;
				}
				i++;
			}
		}

		else if (parent instanceof MessageType)
		{
			MessageType xmlMessageType = (MessageType) parent;
			if (child.equals(xmlMessageType.getSession()))
			{
				return 0;
			}

			if (child.equals(xmlMessageType.getHeader()))
			{
				return 1;
			}

			if (child.equals(xmlMessageType.getBody()))
			{
				if (xmlMessageType.getHeader() != null)
				{
					return 2;
				} else
				{
					return 1;
				}
			}

			if (child.equals(xmlMessageType.getTrailer()))
			{
				if (xmlMessageType.getHeader() != null)
				{
					return 3;
				} else
				{
					return 2;
				}
			}
		}

		else if (parent instanceof SessionType)
		{
			SessionType xmlSessionType = (SessionType) parent;
			if (child.equals(xmlSessionType.getName()))
			{
				return 0;
			}

			if (child.equals(xmlSessionType.getAppVersionId()))
			{
				return 1;
			}
		}

		else if (parent instanceof HeaderType)
		{
			int i = 0;
			HeaderType xmlHeaderType = (HeaderType) parent;
			for (FieldType xmlFieldType : xmlHeaderType.getField())
			{
				if (child.equals(xmlFieldType))
				{
					return i;
				}
				i++;
			}
		}

		else if (parent instanceof BodyType)
		{
			int i = 0;
			BodyType xmlBodyType = (BodyType) parent;
			for (Object xmlObject : xmlBodyType.getFieldOrGroupsOrComponent())
			{
				if (child.equals(xmlObject))
				{
					return i;
				}
				i++;
			}
		}

		else if (parent instanceof TrailerType)
		{
			int i = 0;
			TrailerType xmlTrailerType = (TrailerType) parent;
			for (FieldType xmlFieldType : xmlTrailerType.getField())
			{
				if (child.equals(xmlFieldType))
				{
					return i;
				}
				i++;
			}
		}

		else if (parent instanceof GroupsType)
		{
			int i = 0;
			GroupsType groupsType = (GroupsType) parent;
			for (GroupType xmlGroupType : groupsType.getGroup())
			{
				if (child.equals(xmlGroupType))
				{
					return i;
				}
				i++;
			}
		}

		else if (parent instanceof GroupType)
		{
			int i = 0;
			GroupType xmlGroupType = (GroupType) parent;
			for (Object xmlObject : xmlGroupType.getFieldOrGroupsOrComponent())
			{
				if (child.equals(xmlObject))
				{
					return i;
				}
				i++;
			}
		}

		else if (parent instanceof ComponentType)
		{
			int i = 0;
			ComponentType xmlComponentType = (ComponentType) parent;
			for (Object xmlObject : xmlComponentType
					.getFieldOrGroupsOrComponent())
			{
				if (child.equals(xmlObject))
				{
					return i;
				}
				i++;
			}
		}

		else if (parent instanceof FieldType)
		{
			FieldType xmlFieldType = (FieldType) parent;
			if (child.equals(xmlFieldType.getValue()))
			{
				return 0;
			}
		}

		return -1;
	}

	@Override
	public Object getRoot()
	{
		return xmlProjectType;
	}

	/**
	 * Returns the listeners to this tree model
	 * 
	 * @return the listeners to this tree model
	 */
	public TreeModelListener[] getTreeModelListeners()
	{
		return (TreeModelListener[]) eventListenerList
				.getListeners(TreeModelListener.class);
	}

	@Override
	public boolean isLeaf(Object node)
	{
		if (node instanceof String)
		{
			return true;
		} else
		{
			return false;
		}
	}

	@Override
	public void removeTreeModelListener(TreeModelListener listener)
	{
		eventListenerList.remove(TreeModelListener.class, listener);
	}

	/**
	 * Updates the entire tree model
	 */
	public void update()
	{
		int n = getChildCount(xmlProjectType);
		int[] childIdx = new int[n];
		Object[] children = new Object[n];

		for (int i = 0; i < n; i++)
		{
			childIdx[i] = i;
			children[i] = getChild(xmlProjectType, i);
		}

		fireTreeStructureChanged(this, new Object[] { xmlProjectType },
				childIdx, children);
	}

	/**
	 * Updates the tree model that a message has been added to the project
	 * 
	 * @param xmlMessageType
	 *            the added XML MessageType
	 */
	public void updateMessageAdded(MessageType xmlMessageType)
	{
		Object[] path = { xmlProjectType, xmlProjectType.getMessages() };
		int[] childIndices = { getIndexOfChild(xmlProjectType.getMessages(),
				xmlMessageType) };
		Object[] children = { xmlMessageType };
		fireTreeNodesInserted(this, path, childIndices, children);
	}

	/**
	 * Updates the tree model that a message has been removed from the project
	 * 
	 * @param xmlMessageType
	 *            the removed XML MessageType
	 */
	public void updateMessageRemoved(MessageType xmlMessageType, int index)
	{
		Object[] path = { xmlProjectType, xmlProjectType.getMessages() };
		int[] childIndices = { index };
		Object[] children = { xmlMessageType };
		fireTreeNodesRemoved(this, path, childIndices, children);
	}

	@Override
	public void valueForPathChanged(TreePath treePath, Object newValue)
	{
		Object[] path;
		int[] childIndices = null;
		Object[] children = null;
		if (treePath.getPathCount() > 1)
		{
			Object parent = treePath.getParentPath().getLastPathComponent();
			if (parent instanceof FieldType)
			{
				((FieldType) parent).setValue((String) newValue);
				childIndices = new int[] { 0 };
			}

			else if (parent instanceof SessionType)
			{
				/*
				 * TODO Find a better way to know whether the new value is a
				 * session name or an application version
				 */
				String newString = (String) newValue;
				if ((newString.contains("FIX.") || (newString.contains("FIXT.")))
						&& newString.contains(">"))
				{
					((SessionType) parent).setName(newString);
					childIndices = new int[] { 0 };
				} else if (newString.contains("FIX."))
				{
					((SessionType) parent).setAppVersionId(newString);
					childIndices = new int[] { 1 };
				}
			}

			treePath = treePath.getParentPath().pathByAddingChild(newValue);
			path = treePath.getParentPath().getPath();
			children = new Object[] { newValue };
		} else
		{
			path = treePath.getPath();
		}

		fireTreeNodesChanged(this, path, childIndices, children);
	}

	protected void fireTreeNodesChanged(Object source, Object[] path,
			int[] childIndices, Object[] children)
	{
		TreeModelEvent event = new TreeModelEvent(source, path, childIndices,
				children);
		for (TreeModelListener listener : getTreeModelListeners())
		{
			listener.treeNodesChanged(event);
		}
	}

	protected void fireTreeNodesInserted(Object source, Object[] path,
			int[] childIndices, Object[] children)
	{
		TreeModelEvent event = new TreeModelEvent(source, path, childIndices,
				children);
		for (TreeModelListener listener : getTreeModelListeners())
		{
			listener.treeNodesInserted(event);
		}
	}

	protected void fireTreeNodesRemoved(Object source, Object[] path,
			int[] childIndices, Object[] children)
	{
		TreeModelEvent event = new TreeModelEvent(source, path, childIndices,
				children);
		for (TreeModelListener listener : getTreeModelListeners())
		{
			listener.treeNodesRemoved(event);
		}
	}

	protected void fireTreeStructureChanged(Object source, Object[] path,
			int[] childIndices, Object[] children)
	{
		TreeModelEvent event = new TreeModelEvent(source, path, childIndices,
				children);
		for (TreeModelListener listener : getTreeModelListeners())
		{
			listener.treeStructureChanged(event);
		}
	}
}
