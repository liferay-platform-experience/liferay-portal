/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import classNames from 'classnames';
import React, {useId} from 'react';

import {useChatShellContext} from './ChatShellContext';

export type ChatShellProps = React.HTMLAttributes<HTMLDivElement>;

export default function ChatShell({
	children,
	className,
	...otherProps
}: ChatShellProps) {
	const {dialogId, titleId} = useChatShellContext();

	const fallbackId = useId();

	return (
		<div
			{...otherProps}
			aria-labelledby={titleId || undefined}
			className={classNames('c-p-3 chat-shell', className)}
			id={dialogId || fallbackId}
			role="dialog"
			tabIndex={-1}
		>
			{children}
		</div>
	);
}
