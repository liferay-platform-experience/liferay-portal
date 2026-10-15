/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

export const DEFAULT_VARIATION = 'default';

export function getVariations({
	activeVariation = DEFAULT_VARIATION,
	frontendTokensValues = {},
	variations = {},
}) {
	return {
		...variations,
		[activeVariation]: {
			...variations[activeVariation],
			name: variations[activeVariation]?.name ?? activeVariation,
			tokens: frontendTokensValues,
		},
	};
}

export function parseVariations(frontendTokensValues = {}) {
	if (frontendTokensValues[DEFAULT_VARIATION]?.tokens) {
		return frontendTokensValues;
	}

	return {
		[DEFAULT_VARIATION]: {
			name: DEFAULT_VARIATION,
			tokens: frontendTokensValues,
		},
	};
}
