/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {useIsMobileDevice} from '@clayui/shared';
import {useCallback, useEffect, useState} from 'react';

enum PeekEvent {
	Dismiss = 'dismiss',
	Enter = 'enter',
	GracePeriodEnd = 'gracePeriodEnd',
	HoverOutside = 'hoverOutside',
	MoveOutside = 'moveOutside',
	Reset = 'reset',
}

enum PeekStage {
	Dismissed = 'dismissed',
	Inside = 'inside',
	Leaving = 'leaving',
	Outside = 'outside',
}

const DEFAULT_STAGE = PeekStage.Outside;
const OPEN_POPUP_TRIGGER_SELECTOR =
	'[aria-expanded="true"][aria-haspopup], [data-restore-title]';
const NEXT_STAGE: Record<PeekStage, Partial<Record<PeekEvent, PeekStage>>> = {
	[PeekStage.Dismissed]: {
		[PeekEvent.MoveOutside]: PeekStage.Outside,
		[PeekEvent.Reset]: DEFAULT_STAGE,
	},
	[PeekStage.Inside]: {
		[PeekEvent.Dismiss]: PeekStage.Dismissed,
		[PeekEvent.HoverOutside]: PeekStage.Leaving,
		[PeekEvent.MoveOutside]: PeekStage.Leaving,
		[PeekEvent.Reset]: DEFAULT_STAGE,
	},
	[PeekStage.Leaving]: {
		[PeekEvent.Dismiss]: PeekStage.Outside,
		[PeekEvent.Enter]: PeekStage.Inside,
		[PeekEvent.GracePeriodEnd]: PeekStage.Outside,
		[PeekEvent.Reset]: DEFAULT_STAGE,
	},
	[PeekStage.Outside]: {
		[PeekEvent.Enter]: PeekStage.Inside,
	},
};

/**
 * Tells whether a closed sidebar peeks as an overlay. The peek area is the
 * toggle plus the panel. Hovering it shows the peek right away, leaving it
 * starts a short grace period that coming back cancels, and Escape hides the
 * peek. After Escape, only a real mouse move outside the toggle and the panel
 * lets the peek return, because elements that slide under a still mouse fire
 * `pointerover` and `pointerout` too. The saved state (`open`) only masks the
 * peek and is never changed here.
 *
 * Pointer tracking keeps running while the sidebar is pinned, so unpinning
 * with the pointer on the toggle or the panel turns into a peek.
 *
 * The panel carries `navigationId` as its `id`, and the toggle points to it
 * through `aria-controls`, so one selector finds the whole peek area. It runs
 * on every event instead of once, because the toggle renders in a separate
 * React root that can mount after the sidebar.
 *
 * The peek also holds while a menu, a picker or a tooltip opened from the
 * panel shows, because those render outside of it.
 */
export function useUnifiedSidebarPeek({
	navigationId,
	open,
}: {
	navigationId?: string;
	open: boolean;
}) {
	const [stage, setStage] = useState(DEFAULT_STAGE);

	const isMobile = useIsMobileDevice();
	const enabled = !!navigationId && !isMobile;

	const dispatch = useCallback((event: PeekEvent) => {
		setStage((stage) => NEXT_STAGE[stage][event] ?? stage);
	}, []);

	const dismiss = useCallback(() => {
		dispatch(PeekEvent.Dismiss);
	}, [dispatch]);

	useEffect(
		function setupMouseListeners() {
			if (!enabled) {
				return;
			}

			function isInPeekArea(node: EventTarget | null) {
				const panelSelector = `[id="${navigationId}"]`;
				const peekAreaSelector = `${panelSelector}, [aria-controls="${navigationId}"]`;

				if (node instanceof Element && node.closest(peekAreaSelector)) {
					return true;
				}

				const panel = document.querySelector(panelSelector);

				return !!panel?.querySelector(OPEN_POPUP_TRIGGER_SELECTOR);
			}

			function isPointerEventEligible(event: PointerEvent) {
				return event.pointerType === 'mouse';
			}

			function handlePointerMove(event: PointerEvent) {
				if (!isPointerEventEligible(event)) {
					return;
				}

				if (!isInPeekArea(event.target)) {
					dispatch(PeekEvent.MoveOutside);
				}
			}

			function handlePointerOut(event: PointerEvent) {
				if (!isPointerEventEligible(event)) {
					return;
				}

				dispatch(
					isInPeekArea(event.relatedTarget)
						? PeekEvent.Enter
						: PeekEvent.HoverOutside
				);
			}

			function handlePointerOver(event: PointerEvent) {
				if (!isPointerEventEligible(event)) {
					return;
				}

				dispatch(
					isInPeekArea(event.target)
						? PeekEvent.Enter
						: PeekEvent.HoverOutside
				);
			}

			document.addEventListener('pointermove', handlePointerMove);
			document.addEventListener('pointerout', handlePointerOut);
			document.addEventListener('pointerover', handlePointerOver);

			return () => {
				document.removeEventListener('pointermove', handlePointerMove);
				document.removeEventListener('pointerout', handlePointerOut);
				document.removeEventListener('pointerover', handlePointerOver);

				dispatch(PeekEvent.Reset);
			};
		},
		[enabled, navigationId, dispatch]
	);

	useEffect(
		function leaveAfterGracePeriod() {
			if (stage !== PeekStage.Leaving) {
				return;
			}

			const timeoutId = setTimeout(() => {
				dispatch(PeekEvent.GracePeriodEnd);
			}, 250);

			return () => clearTimeout(timeoutId);
		},
		[stage, dispatch]
	);

	return {
		dismiss,
		peek:
			enabled &&
			!open &&
			(stage === PeekStage.Inside || stage === PeekStage.Leaving),
	};
}
