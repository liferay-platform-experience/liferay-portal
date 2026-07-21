/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React from 'react';

import ChatShellComponent, {ChatShellProps} from './ChatShell';
import ChatShellBody from './ChatShellBody';
import ChatShellHeader from './ChatShellHeader';

const ChatShell = ChatShellComponent as React.FC<ChatShellProps> & {
	Body: typeof ChatShellBody;
	Header: typeof ChatShellHeader;
};

ChatShell.Header = ChatShellHeader;
ChatShell.Body = ChatShellBody;

export default ChatShell;
