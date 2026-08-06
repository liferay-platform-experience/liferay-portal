/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import domAlign from 'dom-align';

export const LOCAL_STORAGE_KEYS = {
	CURRENT_STEP: `${themeDisplay.getUserId()}-tour-current-step`,
	POPOVER_VISIBILITY: `${themeDisplay.getUserId()}-tour-popover-visible`,
	SKIPPABLE: `${themeDisplay.getUserId()}-${themeDisplay.getSiteGroupId()}-tour-dismissed`,
};

export function doAlign({sourceElement, targetElement, ...config}) {
	return domAlign(sourceElement, targetElement, {
		...config,
		useCssRight: window.getComputedStyle(sourceElement).direction === 'rtl',
	});
}

/**
 * Shifts an absolutely positioned element back inside the document bounds
 * when part of it hangs outside, so anchored UI like the hotspot never
 * renders clipped by the page edges.
 * @param {HTMLElement} element element positioned through style.left/top
 */
export function clampToDocument(element) {
	const boundingClientRect = element.getBoundingClientRect();

	const documentElement = document.documentElement;

	const maxRight = Math.max(
		documentElement.clientWidth,
		documentElement.scrollWidth
	);

	const maxBottom = Math.max(
		documentElement.clientHeight,
		documentElement.scrollHeight
	);

	const left = boundingClientRect.left + window.scrollX;
	const top = boundingClientRect.top + window.scrollY;

	let deltaX = 0;
	let deltaY = 0;

	if (left < 0) {
		deltaX = -left;
	}
	else if (left + boundingClientRect.width > maxRight) {
		deltaX = maxRight - left - boundingClientRect.width;
	}

	if (top < 0) {
		deltaY = -top;
	}
	else if (top + boundingClientRect.height > maxBottom) {
		deltaY = maxBottom - top - boundingClientRect.height;
	}

	if (deltaX || deltaY) {
		element.style.left = `${
			parseFloat(element.style.left || '0') + deltaX
		}px`;
		element.style.top = `${parseFloat(element.style.top || '0') + deltaY}px`;
	}
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
 * take the whole tour down.
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
		console.warn(`Tour: "${selector}" is not a valid CSS selector`, error);

		return null;
	}
}
