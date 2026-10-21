/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import type {ALIGN_POSITIONS} from '@clayui/popover';

export type AlignPosition = (typeof ALIGN_POSITIONS)[number];

export type TourCloseReason = 'closed' | 'dismissed' | 'finished';

export type TourStep = {

	/**
	 * Text shown in the body of the step popover.
	 */
	content: string;

	/**
	 * Side of the highlighted element the popover is placed on. The popover
	 * is flipped to the opposite side when it does not fit in the viewport.
	 */
	positioning?: AlignPosition;

	/**
	 * CSS selector of the element to highlight. A step whose element is not
	 * in the DOM is skipped.
	 */
	selector: string;

	/**
	 * Text shown in the header of the step popover.
	 */
	title: string;
};

export type TourDefinition = {

	/**
	 * Whether the popover shows a close button. Escape closes the tour
	 * regardless of this flag.
	 */
	closeable?: boolean;

	/**
	 * Whether the popover shows a "Do not show me this again" checkbox. The
	 * tour does not persist anything: when the tour ends with the checkbox
	 * checked, `onClose` receives the `dismissed` reason and the trigger
	 * decides where to store that preference.
	 */
	dismissable?: boolean;

	/**
	 * Whether the page cannot be scrolled while the tour is open, so the
	 * highlighted element never leaves the viewport.
	 */
	lockScroll?: boolean;

	/**
	 * Called when the tour ends, either because the user finished the last
	 * step, closed it, or asked not to see it again.
	 */
	onClose?: (reason: TourCloseReason) => void;

	/**
	 * Whether closing the popover pauses the tour instead of ending it: a
	 * hotspot stays on the highlighted element and resumes the tour from
	 * that step. Finishing the last step always ends the tour.
	 */
	pauseable?: boolean;

	steps: TourStep[];
};
