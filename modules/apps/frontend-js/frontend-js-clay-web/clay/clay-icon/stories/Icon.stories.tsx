/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Provider} from '@clayui/core';
const spritemap = require('@clayui/css/src/images/icons/icons.svg');
const emptyStatesSpritemap = require('@clayui/css/src/images/images/empty_states.svg');
import ClayIcon from '@clayui/icon';
import React from 'react';

export default {
	component: ClayIcon,
	title: 'Design System/Components/Icon',
};
export function Default(args: any) {
	return <ClayIcon spritemap={spritemap} symbol={args.symbol} />;
}

Default.args = {
	symbol: 'add-cell',
};
export function ContextSpritemap(args: any) {
	return (
		<Provider spritemap={spritemap}>
			<ClayIcon symbol={args.symbol} />
		</Provider>
	);
}

ContextSpritemap.args = {
	symbol: 'add-cell',
};
export function ContextSpritemapEmptyStates(args: any) {

	// The illustration spritemap is drawn at 256x256 rather than as a 1em
	// glyph, so "lexiconIcon" is turned off and the size is set here.

	return (
		<Provider spritemap={emptyStatesSpritemap}>
			<div style={{height: '288px', width: '288px'}}>
				<ClayIcon lexiconIcon={false} symbol={args.symbol} />
			</div>
		</Provider>
	);
}

ContextSpritemapEmptyStates.args = {
	symbol: 'success-state',
};

ContextSpritemapEmptyStates.argTypes = {
	symbol: {
		control: {type: 'select'},
		options: [
			'action-toolbar',
			'ai-burst',
			'ai-chat',
			'ai-prompt',
			'ai-workflow',
			'audience',
			'contenttypes',
			'discovery',
			'document',
			'knowledge-graph',
			'pagebuilder',
			'success-state',
			'empty-state',
			'search-state',
			'toolbar-canvas',
			'toolbar-list',
			'workflow',
		],
	},
};
