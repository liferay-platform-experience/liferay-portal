/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import classNames from 'classnames';
import React from 'react';

import {useChatShellContext} from './ChatShellContext';
import ChatActionButton from './components/ChatActionButton';
import ChatActionButtonRow from './components/ChatActionButtonRow';

export interface ChatShellHeaderProps
	extends Omit<React.HTMLAttributes<HTMLDivElement>, 'title'> {
	children?: React.ReactNode;
	title: string;
}

export default function ChatShellHeader({
	children,
	className,
	title,
	...otherProps
}: ChatShellHeaderProps) {
	const {onClose, titleId} = useChatShellContext();

	return (
		<div
			{...otherProps}
			className={classNames(
				'border-bottom c-pb-1 c-mb-1 chat-shell-header',
				className
			)}
		>
			<div className="align-items-center c-mb-2 d-flex flex-row justify-content-between">
				<span
					className="chat-shell-header-title text-3 text-weight-semi-bold"
					id={titleId}
				>
					{title}
				</span>

				<span className="align-items-center d-flex flex-row">
					<ChatActionButtonRow>{children}</ChatActionButtonRow>

					<ChatActionButton
						ariaLabel={Liferay.Language.get('close')}
						onClick={onClose}
						symbol="times"
					/>
				</span>
			</div>
		</div>
	);
}
