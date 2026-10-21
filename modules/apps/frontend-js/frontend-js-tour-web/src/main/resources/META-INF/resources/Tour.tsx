/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton, {ClayButtonWithIcon} from '@clayui/button';
import {FocusTrap, OverlayMask} from '@clayui/core';
import {ClayCheckbox} from '@clayui/form';
import ClayLayout from '@clayui/layout';
import ClayPopover from '@clayui/popover';
import {doAlign, observeRect} from '@clayui/shared';
import {isReducedMotion} from '@liferay/accessibility-settings-state-web';
import React, {
	useCallback,
	useEffect,
	useId,
	useMemo,
	useRef,
	useState,
} from 'react';

import Hotspot from './Hotspot';

import type {
	AlignPosition,
	TourCloseReason,
	TourDefinition,
	TourStep,
} from './types';

const ALIGNMENTS_MAP: Record<AlignPosition, readonly [string, string]> = {
	'bottom': ['tc', 'bc'],
	'bottom-left': ['tl', 'bl'],
	'bottom-right': ['tr', 'br'],
	'left': ['cr', 'cl'],
	'left-bottom': ['br', 'bl'],
	'left-top': ['tr', 'tl'],
	'right': ['cl', 'cr'],
	'right-bottom': ['bl', 'br'],
	'right-top': ['tl', 'tr'],
	'top': ['bc', 'tc'],
	'top-left': ['bl', 'tl'],
	'top-right': ['br', 'tr'],
};

const ALIGNMENTS_INVERSE_MAP: Record<string, AlignPosition> = {
	bctc: 'top',
	blbr: 'right-bottom',
	bltl: 'top-left',
	brbl: 'left-bottom',
	brtr: 'top-right',
	clcr: 'right',
	crcl: 'left',
	tcbc: 'bottom',
	tlbl: 'bottom-left',
	tltr: 'right-top',
	trbr: 'bottom-right',
	trtl: 'left-top',
};

const DEFAULT_ALIGN_POSITION: AlignPosition = 'right-top';

const INITIAL_BOUNDS = {height: 0, width: 0, x: 0, y: 0};

const OVERLAY_BORDER_RADIUS = 12;

const OVERLAY_PADDING = 12;

const POPOVER_GAP = 24;

/**
 * Returns the index of the first step, from the given index in the given
 * direction, whose element is in the DOM, or -1 when there is none.
 */
function findStepIndex(steps: TourStep[], index: number, direction: 1 | -1) {
	for (let i = index; i >= 0 && i < steps.length; i += direction) {
		if (document.querySelector(steps[i].selector)) {
			return i;
		}

		console.warn(
			`Tour: skipping step "${steps[i].title}" because its "${steps[i].selector}" element does not exist in the DOM`
		);
	}

	return -1;
}

/**
 * Returns the offset that pushes the popover away from the highlighted
 * element on the side it is placed on, so it never overlaps the element it
 * points at.
 */
function getAlignmentOffset(alignPosition: AlignPosition): [number, number] {
	if (alignPosition.startsWith('bottom')) {
		return [0, POPOVER_GAP];
	}

	if (alignPosition.startsWith('left')) {
		return [-POPOVER_GAP, 0];
	}

	if (alignPosition.startsWith('top')) {
		return [0, -POPOVER_GAP];
	}

	return [POPOVER_GAP, 0];
}

/**
 * Picks the ClayPopover alignment whose arrow lands on the highlighted
 * element. ClayPopover only draws the arrow centered or pinned to an edge, so
 * when the popover cannot sit centered on the element without leaving the
 * viewport, the arrow is moved to the side the element is on. The decision
 * depends on the element center, the popover size, and the viewport only, so
 * it is the same on every pass and never oscillates.
 */
function getArrowAlignment(
	alignPosition: AlignPosition,
	elementRect: DOMRect,
	popoverRect: DOMRect
): AlignPosition {
	const [side] = alignPosition.split('-');

	if (side === 'bottom' || side === 'top') {
		const elementCenter = elementRect.left + elementRect.width / 2;

		if (elementCenter - popoverRect.width / 2 < 0) {
			return `${side}-left`;
		}

		if (elementCenter + popoverRect.width / 2 > window.innerWidth) {
			return `${side}-right`;
		}

		return side;
	}

	const elementCenter = elementRect.top + elementRect.height / 2;

	if (elementCenter - popoverRect.height / 2 < 0) {
		return `${side}-top` as AlignPosition;
	}

	if (elementCenter + popoverRect.height / 2 > window.innerHeight) {
		return `${side}-bottom` as AlignPosition;
	}

	return side as AlignPosition;
}

export default function Tour({
	closeable = true,
	dismissable = false,
	initialStep = 0,
	lockScroll = false,
	onClose,
	onPause,
	pauseable = false,
	paused = false,
	steps,
}: TourDefinition) {
	const [currentStepIndex, setCurrentStepIndex] = useState(() =>
		findStepIndex(
			steps,
			Math.max(0, Math.min(initialStep, steps.length - 1)),
			1
		)
	);

	const step: TourStep | undefined = steps[currentStepIndex];

	const [alignPosition, setAlignPosition] = useState<AlignPosition>(
		step?.positioning ?? DEFAULT_ALIGN_POSITION
	);

	const [bounds, setBounds] = useState(INITIAL_BOUNDS);

	const [dismissed, setDismissed] = useState(false);

	const [status, setStatus] = useState<'closed' | 'open' | 'paused'>(
		paused ? 'paused' : 'open'
	);

	const contentId = useId();

	const popoverRef = useRef<HTMLDivElement>(null);

	const previouslyFocusedElementRef = useRef(document.activeElement);

	const titleId = useId();

	const element = useMemo(
		() =>
			step ? document.querySelector<HTMLElement>(step.selector) : null,
		[step]
	);

	const align = useCallback(() => {
		const popover = popoverRef.current;

		if (!element || !popover) {
			return;
		}

		const {points} = doAlign({
			offset: getAlignmentOffset(alignPosition),
			overflow: {adjustX: true, adjustY: true},
			points: ALIGNMENTS_MAP[alignPosition],
			sourceElement: popover,
			targetElement: element,
		});

		// The alignment handed to ClayPopover must reflect where dom-align
		// placed the popover, which may be the opposite side of the requested
		// one when that side does not fit in the viewport, so the arrow keeps
		// pointing at the highlighted element.

		const resolvedAlignPosition = ALIGNMENTS_INVERSE_MAP[points.join('')];

		if (resolvedAlignPosition) {
			setAlignPosition(
				getArrowAlignment(
					resolvedAlignPosition,
					element.getBoundingClientRect(),
					popover.getBoundingClientRect()
				)
			);
		}
	}, [alignPosition, element]);

	// Observing the element reports its rect right away and then whenever it
	// moves, so a single observer both places the popover and keeps it and
	// the overlay on the element after a scroll or a reflow.

	useEffect(() => {
		if (!element || status !== 'open') {
			return;
		}

		return observeRect(element, (rect) => {
			const {height, width, x, y} =
				rect ?? element.getBoundingClientRect();

			setBounds({height, width, x, y});

			align();
		});
	}, [align, element, status]);

	// The lock goes before the scroll below: changing the overflow of the
	// scrolling element while a smooth scroll is in progress cancels it.

	useEffect(() => {
		if (!lockScroll || status !== 'open') {
			return;
		}

		const documentElementOverflow = document.documentElement.style.overflow;

		const bodyOverflow = document.body.style.overflow;

		document.documentElement.style.overflow = 'hidden';
		document.body.style.overflow = 'hidden';

		return () => {
			document.documentElement.style.overflow = documentElementOverflow;
			document.body.style.overflow = bodyOverflow;
		};
	}, [lockScroll, status]);

	useEffect(() => {
		if (element && status === 'open') {
			element.scrollIntoView({
				behavior: isReducedMotion() ? 'auto' : 'smooth',
				block: 'center',
			});
		}
	}, [element, status]);

	// Focus goes back to the trigger once the popover is gone, after the focus
	// trap has released the rest of the page.

	useEffect(() => {
		const previouslyFocusedElement = previouslyFocusedElementRef.current;

		if (
			status === 'closed' &&
			previouslyFocusedElement instanceof HTMLElement
		) {
			previouslyFocusedElement.focus();
		}
	}, [status]);

	const close = (reason: TourCloseReason) => {
		if (dismissed) {
			reason = 'dismissed';
		}
		else if (pauseable && reason === 'closed') {
			setStatus('paused');

			onPause?.(currentStepIndex);

			return;
		}

		setStatus('closed');

		onClose?.(reason);
	};

	const goToStep = (index: number) => {
		setAlignPosition(steps[index].positioning ?? DEFAULT_ALIGN_POSITION);
		setCurrentStepIndex(index);
	};

	const onNext = () => {
		const nextStepIndex = findStepIndex(steps, currentStepIndex + 1, 1);

		if (nextStepIndex === -1) {
			close('finished');
		}
		else {
			goToStep(nextStepIndex);
		}
	};

	const onPrevious = () => {
		const previousStepIndex = findStepIndex(
			steps,
			currentStepIndex - 1,
			-1
		);

		if (previousStepIndex !== -1) {
			goToStep(previousStepIndex);
		}
	};

	if (!element || !step || status === 'closed') {
		return null;
	}

	if (status === 'paused') {
		return <Hotspot element={element} onClick={() => setStatus('open')} />;
	}

	return (
		<>
			<OverlayMask
				borderRadius={OVERLAY_BORDER_RADIUS}
				bounds={bounds}
				onBoundsChange={setBounds}
				padding={OVERLAY_PADDING}
				visible
			/>

			<FocusTrap active focusElementRef={popoverRef}>
				<ClayPopover
					alignPosition={alignPosition}
					aria-describedby={contentId}
					aria-labelledby={titleId}
					aria-modal="true"
					className="lfr-tour-popover"
					disableScroll
					header={
						<ClayLayout.ContentRow noGutters verticalAlign="center">
							<ClayLayout.ContentCol expand>
								<span id={titleId}>
									{`${Liferay.Util.sub(
										Liferay.Language.get('step-x-of-x'),
										currentStepIndex + 1,
										steps.length
									)}: ${step.title}`}
								</span>
							</ClayLayout.ContentCol>

							{closeable && (
								<ClayLayout.ContentCol>
									<ClayButtonWithIcon
										aria-label={Liferay.Language.get(
											'close'
										)}
										className="close"
										displayType="unstyled"
										onClick={() => close('closed')}
										size="sm"
										symbol="times"
									/>
								</ClayLayout.ContentCol>
							)}
						</ClayLayout.ContentRow>
					}
					onShowChange={(show) => {
						if (!show) {
							close('closed');
						}
					}}
					ref={popoverRef}
					role="dialog"
					show
					size="lg"
				>
					<p className="mb-0" id={contentId}>
						{step.content}
					</p>

					<ClayLayout.ContentRow
						className="mt-3"
						noGutters
						verticalAlign="center"
					>
						<ClayLayout.ContentCol expand>
							{dismissable && (
								<ClayCheckbox
									checked={dismissed}
									label={Liferay.Language.get(
										'do-not-show-me-this-again'
									)}
									onChange={() => setDismissed(!dismissed)}
								/>
							)}
						</ClayLayout.ContentCol>

						<ClayLayout.ContentCol>
							<ClayButton.Group spaced>
								{currentStepIndex > 0 && (
									<ClayButton
										displayType="secondary"
										onClick={onPrevious}
										size="sm"
									>
										{Liferay.Language.get('previous')}
									</ClayButton>
								)}

								<ClayButton onClick={onNext} size="sm">
									{currentStepIndex === steps.length - 1
										? Liferay.Language.get('done')
										: Liferay.Language.get('next')}
								</ClayButton>
							</ClayButton.Group>
						</ClayLayout.ContentCol>
					</ClayLayout.ContentRow>
				</ClayPopover>
			</FocusTrap>
		</>
	);
}
