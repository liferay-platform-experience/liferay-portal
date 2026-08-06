/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {useEffect} from 'react';

const FOCUSABLE_SELECTOR = [
	'a[href]',
	'button:not([disabled])',
	'input:not([disabled])',
	'select:not([disabled])',
	'textarea:not([disabled])',
	'[tabindex]:not([tabindex="-1"])',
].join(',');

/**
 * Keeps keyboard focus inside the element referenced by `ref` while `active`
 * is true. When the tour highlights an element behind a dark overlay,
 * this prevents Tab (or a stray programmatic focus) from reaching the dimmed,
 * non-interactive area behind the popover. On activation it moves focus to the
 * first focusable element inside the container.
 *
 * @param {React.RefObject<HTMLElement>} ref the container to trap focus within
 * @param {boolean} active whether the trap is engaged
 */
export function useFocusTrap(ref, active) {
	useEffect(() => {
		const container = ref.current;

		if (!active || !container) {
			return;
		}

		const getFocusableElements = () =>
			Array.from(container.querySelectorAll(FOCUSABLE_SELECTOR));

		const focusableElements = getFocusableElements();

		(focusableElements[0] ?? container).focus?.();

		const onKeyDown = (event) => {
			if (event.key !== 'Tab') {
				return;
			}

			const elements = getFocusableElements();

			if (!elements.length) {
				event.preventDefault();

				return;
			}

			const firstElement = elements[0];

			const lastElement = elements[elements.length - 1];

			if (
				event.shiftKey &&
				(document.activeElement === firstElement ||
					!container.contains(document.activeElement))
			) {
				event.preventDefault();

				lastElement.focus();
			}
			else if (
				!event.shiftKey &&
				(document.activeElement === lastElement ||
					!container.contains(document.activeElement))
			) {
				event.preventDefault();

				firstElement.focus();
			}
		};

		const onFocusIn = (event) => {
			if (!container.contains(event.target)) {
				(getFocusableElements()[0] ?? container).focus?.();
			}
		};

		document.addEventListener('keydown', onKeyDown, true);
		document.addEventListener('focusin', onFocusIn, true);

		return () => {
			document.removeEventListener('keydown', onKeyDown, true);
			document.removeEventListener('focusin', onFocusIn, true);
		};
	}, [ref, active]);
}
