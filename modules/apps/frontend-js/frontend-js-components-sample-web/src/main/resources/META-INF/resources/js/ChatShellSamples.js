/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayLayout from '@clayui/layout';
import {
	ChatActionButton,
	ChatDropdownContainer,
	ChatShell,
} from 'frontend-js-components-web';
import React, {useState} from 'react';

const DEMO_CELL_HEIGHT = 500;

function ChatShellSample() {
	const noop = () => {};

	return (
		<ChatShell>
			<ChatShell.Header title="AI Assistant">
				<ChatActionButton
					ariaLabel="Add"
					onClick={noop}
					symbol="plus"
				/>

				<ChatActionButton
					ariaLabel="Share"
					onClick={noop}
					symbol="share"
				/>
			</ChatShell.Header>

			<ChatShell.Body>Ask me anything about this page.</ChatShell.Body>
		</ChatShell>
	);
}

function ChatDropdownCell() {
	const [open, setOpen] = useState(true);

	return (
		<ClayLayout.Col size={4}>
			<h4>Dropdown</h4>

			<div style={{height: DEMO_CELL_HEIGHT}}>
				<ChatDropdownContainer
					onOpenChange={setOpen}
					open={open}
					trigger={
						<ClayButton onClick={() => setOpen(true)}>
							Open AI Assistant
						</ClayButton>
					}
				>
					<ChatShellSample />
				</ChatDropdownContainer>
			</div>
		</ClayLayout.Col>
	);
}

export default function ChatShellSamples() {
	return (
		<ClayLayout.Row>
			<ChatDropdownCell />
		</ClayLayout.Row>
	);
}
