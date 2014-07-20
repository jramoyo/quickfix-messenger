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
 * - Neither the name of the authors nor the names of its contributors 
 *   may be used to endorse or promote products derived from this software 
 *   without specific prior written permission.
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
 * IconBuilder.java
 * Mar 28, 2014
 */
package com.jramoyo.qfixmessenger.ui.util;

import javax.swing.ImageIcon;

import com.jramoyo.qfixmessenger.config.QFixMessengerConfig;

/**
 * @author jramoyo
 */
public final class IconBuilder
{
	public static String APP_ICON = "app.png";

	public static String ADD_ICON = "add.png";
	public static String SEND_ICON = "send.png";
	public static String DESTROY_ICON = "destroy.png";

	public static String NEW_ICON = "new.png";
	public static String OPEN_ICON = "open.png";
	public static String SAVE_ICON = "save.png";
	public static String CLOSE_ICON = "close.png";
	public static String IMPORT_ICON = "import.png";

	public static String EXPORT_ICON = "export.png";
	public static String EXIT_ICON = "exit.png";

	public static String LOGON_ICON = "logon.png";

	public static String LOGOFF_ICON = "logoff.png";
	public static String RESET_ICON = "reset.png";
	public static String HELP_ICON = "help.png";

	public static String WINDOW_ICON = "window.png";

	public static String LOAD_ICON = "load.png";

	public static String DELETE_ICON = "delete.png";
	public static String SEND_SMALL_ICON = "send-small.png";
	public static String SEND_ALL_ICON = "send-all.png";
	public static String COLLAPSE_ICON = "collapse.png";
	public static String EXPAND_ICON = "expand.png";
	public static String PROJECT_ICON = "project.png";

	public static String PROJECT_OPEN_ICON = "project-open.png";
	public static String MESSAGES_ICON = "messages.png";
	public static String MESSAGES_OPEN_ICON = "messages-open.png";
	public static String MESSAGE_ICON = "message.png";
	public static String MESSAGE_OPEN_ICON = "message-open.png";
	public static String SESSION_ICON = "session.png";
	public static String COMPONENT_ICON = "component.png";
	public static String GROUP_ICON = "group.png";
	public static String GROUPS_ICON = "groups.png";
	public static String FIELD_ICON = "field.png";
	public static String TEXT_ICON = "text.png";

	public static String CLEAR_ALL_ICON = "clear-all.png";
	public static String INVALID_FIELD = "invalid-field.png";

	public static ImageIcon build(QFixMessengerConfig config, String path)
	{
		String res = String.format("%s/%s", config.getIconsLocation(), path);
		return new ImageIcon(IconBuilder.class.getResource(res));
	}
}
