/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {render, screen} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';

import '@testing-library/jest-dom';

// eslint-disable-next-line @liferay/portal/no-cross-module-deep-import, @liferay/no-extraneous-dependencies
import {checkAccessibility} from '@liferay/layout-js-components-web/test/__lib__/index';

import {ChatShell} from '../../src/main/resources/META-INF/resources';
import {ChatShellContext} from '../../src/main/resources/META-INF/resources/ai-assistant-chat/ChatShellContext';

function renderChatShell(onClose: () => void) {
	return render(
		<ChatShellContext.Provider
			value={{
				dialogId: 'd1',
				onClose,
				titleId: 't1',
			}}
		>
			<ChatShell>
				<ChatShell.Header title="Chat" />

				<ChatShell.Body>Hello</ChatShell.Body>
			</ChatShell>
		</ChatShellContext.Provider>
	);
}

describe('ChatShell', () => {
	it('renders the header title and body content', () => {
		renderChatShell(jest.fn());

		expect(screen.getByText('Chat')).toBeInTheDocument();
		expect(screen.getByText('Hello')).toBeInTheDocument();
	});

	it('exposes an accessible name on the close button', () => {
		renderChatShell(jest.fn());

		expect(screen.getByRole('button', {name: 'close'})).toBeInTheDocument();
	});

	it('renders dialog semantics labelled by the header title', () => {
		renderChatShell(jest.fn());

		const dialog = screen.getByRole('dialog');
		const title = screen.getByText('Chat');

		expect(dialog).toHaveAccessibleName('Chat');
		expect(dialog).toHaveAttribute('aria-labelledby', title.id);
	});

	it('fires onClose when the close button is clicked', async () => {
		const onClose = jest.fn();

		renderChatShell(onClose);

		await userEvent.click(screen.getByRole('button', {name: 'close'}));

		expect(onClose).toHaveBeenCalledTimes(1);
	});

	it('has no accessibility violations', async () => {
		const {container} = renderChatShell(jest.fn());

		await checkAccessibility({bestPractices: true, context: container});
	});
});
