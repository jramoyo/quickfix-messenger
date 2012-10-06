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
 * MemberPanel.java
 * 9 Jun 2011
 */
package com.jramoyo.qfixmessenger.ui.panels;

import com.jramoyo.fix.model.Member;

/**
 * MemberPanel
 * <p>
 * Common interface for all member panels.
 * </p>
 * 
 * @author jamoyo
 */
public interface MemberPanel<M extends Member, Q, X>
{
	/**
	 * Returns the FIX String representation of this MemberPanel
	 * 
	 * @return the FIX String representation of this MemberPanel
	 */
	String getFixString();

	/**
	 * Returns the member
	 * 
	 * @return the member
	 */
	M getMember();

	/**
	 * Returns the QuickFIX representation of this MemberPanel
	 * 
	 * @return the QuickFIX representation of this MemberPanel
	 */
	Q getQuickFixMember();

	/**
	 * Returns the XML representation of this MemberPanel
	 * 
	 * @return the XML representation of this MemberPanel
	 */
	X getXmlMember();

	/**
	 * Returns whether this MemberPanel has valid contents
	 * 
	 * @return whether this MemberPanel has valid contents
	 */
	boolean hasValidContent();

	/**
	 * Populates this MemberPanel from its XML representation
	 * 
	 * @param xmlObject
	 *            an XML representation
	 */
	void populateXml(X xmlObject);
}
