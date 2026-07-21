/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {createContext, useContext} from 'react';

interface ChatShellContextValue {
	dialogId: string;
	onClose: () => void;
	titleId: string;
}

export const ChatShellContext = createContext<ChatShellContextValue>({
	dialogId: '',
	onClose: () => {},
	titleId: '',
});

export function useChatShellContext(): ChatShellContextValue {
	return useContext(ChatShellContext);
}
