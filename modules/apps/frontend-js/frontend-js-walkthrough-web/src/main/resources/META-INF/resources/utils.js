/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import domAlign from 'dom-align';

export const LOCAL_STORAGE_KEYS = {
	CURRENT_STEP: `${themeDisplay.getUserId()}-walkthrough-current-step`,
	POPOVER_VISIBILITY: `${themeDisplay.getUserId()}-walkthrough-popover-visible`,
	SKIPPABLE: `${themeDisplay.getUserId()}-${themeDisplay.getSiteGroupId()}-walkthrough-dismissed`,
};

export function doAlign({sourceElement, targetElement, ...config}) {
	return domAlign(sourceElement, targetElement, {
		...config,
		useCssRight: window.getComputedStyle(sourceElement).direction === 'rtl',
	});
}

/**
 * Resolves a step text that can be either a plain string or an object keyed
 * by language id ({"en_US": "...", "es_ES": "..."}), falling back to the
 * site default language and then to the first available translation.
 * @param {String|Object} text
 * @returns {String} the localized text
 */
export function getLocalizedText(text) {
	if (!text || typeof text === 'string') {
		return text;
	}

	return (
		text[themeDisplay.getLanguageId()] ??
		text[themeDisplay.getDefaultLanguageId()] ??
		Object.values(text)[0]
	);
}

/**
 * Queries the DOM without letting an author-provided invalid CSS selector
 * take the whole walkthrough down.
 * @param {String} selector
 * @returns {Node|null} the matched node, or null when the selector is
 * missing, invalid, or matches nothing
 */
export function querySelectorSafe(selector) {
	if (!selector) {
		return null;
	}

	try {
		return document.querySelector(selector);
	}
	catch (error) {
		console.warn(
			`Walkthrough: "${selector}" is not a valid CSS selector`,
			error
		);

		return null;
	}
}
