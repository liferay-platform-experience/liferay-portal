/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Token} from '../types/ColorPicker';

export default function isTokenLinkedTo(
	editedTokenValues: Record<string, Token> | undefined,
	linkedName: string,
	name: string
) {
	const visitedNames = new Set<string>();

	while (editedTokenValues?.[name]?.name && !visitedNames.has(name)) {
		visitedNames.add(name);

		name = editedTokenValues[name].name;

		if (linkedName === name) {
			return true;
		}
	}

	return false;
}
