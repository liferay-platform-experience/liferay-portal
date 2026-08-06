/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import './Tour.scss';

import ClayButton, {ClayButtonWithIcon} from '@clayui/button';
import {ClayCheckbox} from '@clayui/form';
import ClayLayout from '@clayui/layout';
import ClayPopover from '@clayui/popover';
import {ReactPortal, usePrevious} from '@liferay/frontend-js-react-web';
import {localStorage, navigate} from 'frontend-js-web';
import PropTypes from 'prop-types';
import React, {useCallback, useEffect, useMemo, useRef, useState} from 'react';

import {Hotspot} from '../components/Hotspot';
import {Overlay} from '../components/Overlay';
import {useClickOutside} from '../hooks/useClickOutside';
import {useFocusTrap} from '../hooks/useFocusTrap';
import {useLocalStorage} from '../hooks/useLocalStorage';
import {useObserveRect} from '../hooks/useObserveRect';
import {
	LOCAL_STORAGE_KEYS,
	doAlign,
	getLocalizedText,
	querySelectorSafe,
} from '../utils';

/**
 * This map humanize tuples received from dom-align
 * library to be passed in a format that ClayPopover allows
 */
const ALIGNMENTS_MAP = {
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

const POPOVER_GAP = 10;

/**
 * Computes the dom-align offset that pushes the popover away from the
 * highlighted element in the direction of the current alignment, so the
 * popover never overlaps the element it is pointing at.
 * @param {String} alignment one of the ALIGNMENTS_MAP keys
 * @returns {Array<Number>} [x, y] offset
 */
function getAlignmentOffset(alignment) {
	if (alignment.startsWith('bottom')) {
		return [0, POPOVER_GAP];
	}

	if (alignment.startsWith('left')) {
		return [-POPOVER_GAP, 0];
	}

	if (alignment.startsWith('top')) {
		return [0, -POPOVER_GAP];
	}

	return [POPOVER_GAP, 0];
}

/**
 * Since we can't set tuples as keys for literal dictionaries
 * and maps where are some errors with references like a.get(['bc','tc']) => undefined.
 * We are joining tuples to string to map there and use to lookup in some usages.
 */
const ALIGNMENTS_INVERSE_MAP = {
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

/**
 * Picks the ClayPopover alignment variant whose arrow lands closest to the
 * highlighted element. ClayPopover only draws the arrow centered or pinned to
 * an edge (its 12 `alignPosition` values), so when the popover cannot sit
 * centered on the element and gets pushed aside to stay within the viewport,
 * the arrow variant is switched to the side the element ended up on. The
 * decision is derived from the element center, the popover size, and the
 * viewport (never from the popover's current placement), so it converges in a
 * single pass instead of oscillating between variants.
 * @param {String} alignment the base alignment resolved from dom-align
 * @param {ClientRect} targetRect the highlighted element rect
 * @param {ClientRect} popoverRect the popover rect
 * @returns {String} a ClayPopover alignment value
 */
function getArrowAlignment(alignment, targetRect, popoverRect) {
	const [family] = alignment.split('-');

	if (family === 'bottom' || family === 'top') {
		const targetCenter = targetRect.left + targetRect.width / 2;

		const halfPopoverWidth = popoverRect.width / 2;

		const viewportWidth =
			window.innerWidth || document.documentElement.clientWidth;

		if (targetCenter - halfPopoverWidth < 0) {
			return `${family}-left`;
		}

		if (targetCenter + halfPopoverWidth > viewportWidth) {
			return `${family}-right`;
		}

		return family;
	}

	if (family === 'left' || family === 'right') {
		const targetCenter = targetRect.top + targetRect.height / 2;

		const halfPopoverHeight = popoverRect.height / 2;

		const viewportHeight =
			window.innerHeight || document.documentElement.clientHeight;

		if (targetCenter - halfPopoverHeight < 0) {
			return `${family}-top`;
		}

		if (targetCenter + halfPopoverHeight > viewportHeight) {
			return `${family}-bottom`;
		}

		return family;
	}

	return alignment;
}

/**
 * Removes search params on a given url
 * @param {String} url
 * @returns {String} url without the search params
 */
function stripSearchParams(url) {
	return url.split('?')[0];
}

/**
 * Given a `layoutRelativeURL,` this function will match the paths
 * of the pages contained in it and return the longest path.
 * @param {String} currentLayoutRelativeURL
 * @param {Array<String>} pagesArray
 * @returns {String} longest path
 */
function findLongestMatch(currentLayoutRelativeURL, pagesArray) {
	return pagesArray
		.filter((pagePath) =>
			stripSearchParams(currentLayoutRelativeURL).endsWith(pagePath)
		)
		.sort((a, b) => (a.length > b.length ? -1 : 1))[0];
}

function removeStartingSlash(path) {
	return path.replace('/', '');
}

/**
 * When receiving layoutRelativeURL, it contains parts we should use later, like the following example:
 * > themeDisplay.getLayoutRelativeURL()
 * > '/web/fiona/home'
 * We should take `'/web/fiona/'` from this URL because it can be used to be added as
 * a prefix for navigation stuff like `next` and `previous` and when matching the page.
 * So, people can provide paths in JSON without needing to specify the site name(/web/fiona in this case).
 * @param {String} currentPage
 * @returns {String} a string containg the path without the page URL
 */
function getSitePrefix(currentPage) {
	const currentDXPLayoutRelativeURL = removeStartingSlash(
		themeDisplay.getLayoutRelativeURL()
	).split('/');

	const firstPagePath = removeStartingSlash(currentPage.split('/')[0]);

	return currentDXPLayoutRelativeURL
		.slice(
			0,
			currentDXPLayoutRelativeURL.findIndex(
				(path) => path === firstPagePath
			)
		)
		.join('/');
}

const Step = ({
	closeOnClickOutside,
	closeable,
	currentPage,
	currentStep,
	lockScroll,
	memoizedTrigger,
	onCurrentStep,
	onPopoverVisible,
	pages,
	popoverVisible,
	skippable,
	steps,
}) => {
	const popoverRef = useRef(null);

	const hotspotRef = useRef(null);

	const {
		content,
		darkbg,
		next,
		positioning: defaultPositioning = 'right-top',
		previous,
		title,
	} = steps[currentStep];

	const [currentAlignment, setCurrentAlignment] =
		useState(defaultPositioning);

	const [checkboxValue, setCheckboxValue] = useState(false);

	const previousTrigger = usePrevious(memoizedTrigger);

	const changeStep = useCallback(
		(action, isNext, index) => {
			if (action && pages[action]?.includes(steps[index].id)) {
				onCurrentStep(index);

				return;
			}

			const direction = isNext ? 1 : -1;

			for (
				let nextIndex = index;
				nextIndex >= 0 && nextIndex < steps.length;
				nextIndex += direction
			) {
				if (querySelectorSafe(steps[nextIndex].nodeToHighlight)) {
					onCurrentStep(nextIndex);

					return;
				}

				console.warn(
					`Tour: skipping step "${steps[nextIndex].id}" because its "${steps[nextIndex].nodeToHighlight}" element does not exist in the DOM`
				);
			}
		},
		[pages, steps, onCurrentStep]
	);

	const onNext = useCallback(
		(action) => {
			const maybeNextIndex = currentStep + 1;

			if (steps[maybeNextIndex]) {
				changeStep(action, true, maybeNextIndex);
			}
		},
		[changeStep, currentStep, steps]
	);

	const onPrevious = useCallback(
		(action) => {
			const maybePreviousIndex = currentStep - 1;

			if (steps[maybePreviousIndex]) {
				changeStep(action, false, maybePreviousIndex);
			}
		},
		[changeStep, currentStep, steps]
	);

	/**
	 * This useEffect was necessary because Tour(Step) components
	 * are always mounted. So, currentAligment will not be updated when
	 * the component will be updated.
	 */
	useEffect(() => {
		setCurrentAlignment(defaultPositioning);
	}, [defaultPositioning]);

	const lastAlignmentSignatureRef = useRef(null);

	const align = useCallback(() => {
		if (!popoverVisible || !popoverRef.current || !memoizedTrigger) {
			return;
		}

		const triggerRect = memoizedTrigger.getBoundingClientRect();

		const alignmentSignature = {
			alignment: currentAlignment,
			left: Math.round(triggerRect.left + window.scrollX),
			popover: popoverRef.current,
			step: currentStep,
			top: Math.round(triggerRect.top + window.scrollY),
			trigger: memoizedTrigger,
		};

		const lastAlignmentSignature = lastAlignmentSignatureRef.current;

		/**
		 * Plain scrolling only changes viewport-relative rects, while the
		 * popover is anchored to the document and already moves with the
		 * page. Realigning in that case would fight the user's scroll, so
		 * only realign when the element actually moved within the document,
		 * the alignment changed, the step changed (its content can resize the
		 * popover even on the same anchor), or the popover was remounted (for
		 * example after being closed and reopened, which yields a brand new
		 * node that has never been positioned).
		 */
		if (
			lastAlignmentSignature &&
			lastAlignmentSignature.alignment === alignmentSignature.alignment &&
			lastAlignmentSignature.left === alignmentSignature.left &&
			lastAlignmentSignature.popover === alignmentSignature.popover &&
			lastAlignmentSignature.step === alignmentSignature.step &&
			lastAlignmentSignature.top === alignmentSignature.top &&
			lastAlignmentSignature.trigger === alignmentSignature.trigger
		) {
			return;
		}

		lastAlignmentSignatureRef.current = alignmentSignature;

		const points = ALIGNMENTS_MAP[currentAlignment];

		const alignment = doAlign({
			offset: getAlignmentOffset(currentAlignment),
			overflow: {
				adjustX: true,
				adjustY: true,
			},
			points,
			sourceElement: popoverRef.current,
			targetElement: memoizedTrigger,
		});

		const alignmentString = alignment.points.join('');

		/**
		 * Keep the popover arrow pointing at the highlighted element: the
		 * alignment handed to ClayPopover must reflect where dom-align
		 * actually placed the popover (described by its returned points),
		 * not the requested position, because dom-align may flip it to avoid
		 * overflowing the viewport. The arrow variant is then nudged to the
		 * side the element sits on when the popover cannot be centered on it.
		 */
		const resolvedAlignment = ALIGNMENTS_INVERSE_MAP[alignmentString];

		if (resolvedAlignment) {
			const nextAlignment = getArrowAlignment(
				resolvedAlignment,
				memoizedTrigger.getBoundingClientRect(),
				popoverRef.current.getBoundingClientRect()
			);

			if (nextAlignment !== currentAlignment) {
				setCurrentAlignment(nextAlignment);
			}
		}

		if (!darkbg) {
			memoizedTrigger?.classList.add('lfr-tour-element-shadow');

			if (previousTrigger && memoizedTrigger !== previousTrigger) {
				previousTrigger?.classList.remove('lfr-tour-element-shadow');
			}
		}
	}, [
		currentAlignment,
		currentStep,
		darkbg,
		popoverRef,
		popoverVisible,
		previousTrigger,
		memoizedTrigger,
	]);

	useEffect(() => {
		align();
	}, [align]);

	useObserveRect(align, popoverRef?.current);

	/**
	 * Drops the cached alignment once the popover is hidden, so reopening it
	 * always realigns the freshly mounted node instead of leaving it at its
	 * default top-left position.
	 */
	useEffect(() => {
		if (!popoverVisible) {
			lastAlignmentSignatureRef.current = null;
		}
	}, [popoverVisible]);

	/**
	 * Realigns the popover when the window resizes, since a reflow can move
	 * the highlighted element or change which side fits the viewport. The
	 * cached signature is cleared first so the guard does not short-circuit,
	 * and the work is coalesced with requestAnimationFrame to avoid running
	 * on every intermediate resize event.
	 */
	useEffect(() => {
		if (!popoverVisible) {
			return;
		}

		let animationFrameId = null;

		const onResize = () => {
			if (animationFrameId) {
				window.cancelAnimationFrame(animationFrameId);
			}

			animationFrameId = window.requestAnimationFrame(() => {
				lastAlignmentSignatureRef.current = null;

				align();
			});
		};

		window.addEventListener('resize', onResize);

		return () => {
			if (animationFrameId) {
				window.cancelAnimationFrame(animationFrameId);
			}

			window.removeEventListener('resize', onResize);
		};
	}, [align, popoverVisible]);

	/**
	 * Brings the highlighted element into view once when the popover opens
	 * or the step changes, and then leaves the scroll alone so the user can
	 * interact with the page freely.
	 */
	useEffect(() => {
		if (popoverVisible && memoizedTrigger) {
			memoizedTrigger.scrollIntoView?.({
				behavior: window.matchMedia?.(
					'(prefers-reduced-motion: reduce)'
				)?.matches
					? 'auto'
					: 'smooth',
				block: 'center',
			});
		}
	}, [memoizedTrigger, popoverVisible]);

	/**
	 * With `lockScroll` enabled, the page cannot be scrolled by the user
	 * while a step popover is open, so the highlighted element never leaves
	 * the viewport. An `overflow: hidden` box remains programmatically
	 * scrollable, so the tour can still move between steps. Hotspot
	 * mode never locks: discovery must stay non-blocking.
	 */
	useEffect(() => {
		if (!lockScroll || !popoverVisible) {
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
	}, [lockScroll, popoverVisible]);

	useClickOutside(['.lfr-tour-popover', '.lfr-tour-hotspot'], () => {
		if (closeOnClickOutside) {
			onPopoverVisible(false);
		}
	});

	useFocusTrap(popoverRef, popoverVisible);

	const SITE_PREFIX_PATH = `/${getSitePrefix(currentPage)}`;

	return (
		<>
			{!popoverVisible &&
				currentStep !== steps.length &&
				localStorage.getItem(
					LOCAL_STORAGE_KEYS.SKIPPABLE,
					localStorage.TYPES.NECESSARY
				) !== 'true' && (
					<Hotspot
						onHotspotClick={() => onPopoverVisible(true)}
						ref={hotspotRef}
						trigger={memoizedTrigger}
					/>
				)}

			{darkbg && (
				<Overlay
					popoverVisible={popoverVisible}
					trigger={memoizedTrigger}
				/>
			)}

			{popoverVisible && (
				<ReactPortal>
					<ClayPopover
						alignPosition={currentAlignment}
						className="lfr-tour-popover"
						displayType="secondary"
						header={
							<ClayLayout.ContentRow
								noGutters
								verticalAlign="center"
							>
								<ClayLayout.ContentCol expand>
									<span>{`${Liferay.Util.sub(
										Liferay.Language.get('step-x-of-x'),
										currentStep + 1,
										steps.length
									)}: ${getLocalizedText(title) || ''}`}</span>
								</ClayLayout.ContentCol>

								{closeable && (
									<ClayLayout.ContentCol>
										<ClayButtonWithIcon
											aria-label={Liferay.Language.get(
												'close'
											)}
											className="close"
											displayType="unstyled"
											onClick={() =>
												onPopoverVisible(false)
											}
											small
											symbol="times"
										/>
									</ClayLayout.ContentCol>
								)}
							</ClayLayout.ContentRow>
						}
						onShowChange={onPopoverVisible}
						ref={popoverRef}
						show={popoverVisible}
						size="lg"
					>
						<div
							className="lfr-tour-popover-content"
							dangerouslySetInnerHTML={{
								__html: getLocalizedText(content) || '',
							}}
						/>

						<ClayLayout.ContentRow noGutters verticalAlign="center">
							{skippable && (
								<ClayLayout.ContentCol expand>
									<ClayCheckbox
										checked={checkboxValue}
										label={Liferay.Language.get(
											'do-not-show-me-this-again'
										)}
										onChange={() => {
											const dismissed = !checkboxValue;

											setCheckboxValue(dismissed);

											if (dismissed) {
												localStorage.setItem(
													LOCAL_STORAGE_KEYS.SKIPPABLE,
													'true',
													localStorage.TYPES.NECESSARY
												);
											}
											else {
												localStorage.removeItem(
													LOCAL_STORAGE_KEYS.SKIPPABLE
												);
											}
										}}
									/>
								</ClayLayout.ContentCol>
							)}

							<ClayLayout.ContentCol>
								<ClayButton.Group spaced>
									{currentStep > 0 && (
										<ClayButton
											displayType="secondary"
											onClick={() => {
												onPrevious(previous);

												if (previous) {
													navigate(
														SITE_PREFIX_PATH === '/'
															? previous
															: SITE_PREFIX_PATH.concat(
																	previous
																)
													);
												}
											}}
											small
										>
											{Liferay.Language.get('previous')}
										</ClayButton>
									)}

									{currentStep + 1 !== steps.length ? (
										<ClayButton
											onClick={() => {
												onNext(next);

												if (next) {
													navigate(
														SITE_PREFIX_PATH === '/'
															? next
															: SITE_PREFIX_PATH.concat(
																	next
																)
													);
												}
											}}
											small
										>
											{Liferay.Language.get('ok')}
										</ClayButton>
									) : (
										<ClayButton
											onClick={() => {
												onPopoverVisible(false);
												onCurrentStep(0);
											}}
											small
										>
											{Liferay.Language.get('close')}
										</ClayButton>
									)}
								</ClayButton.Group>
							</ClayLayout.ContentCol>
						</ClayLayout.ContentRow>
					</ClayPopover>
				</ReactPortal>
			)}
		</>
	);
};

const Tour = ({
	closeOnClickOutside,
	closeable = true,
	lockScroll = false,
	pages = {},
	skippable = true,
	steps = [],
}) => {
	const [storedStepIndex, setCurrentStepIndex] = useLocalStorage(
		LOCAL_STORAGE_KEYS.CURRENT_STEP,
		() => (!steps.length ? null : 0)
	);

	const [storedPopoverVisible, setPopoverVisible] = useLocalStorage(
		LOCAL_STORAGE_KEYS.POPOVER_VISIBILITY,
		false
	);

	/**
	 * The persisted step index survives changes to the tour
	 * configuration, so a stored value can point outside the current steps
	 * (or hold garbage from a corrupted storage). Heal it instead of letting
	 * it take the whole tour down.
	 */
	const currentStepIndex = useMemo(() => {
		if (!steps.length) {
			return null;
		}

		if (
			!Number.isInteger(storedStepIndex) ||
			storedStepIndex < 0 ||
			storedStepIndex >= steps.length
		) {
			return 0;
		}

		return storedStepIndex;
	}, [steps, storedStepIndex]);

	useEffect(() => {
		if (currentStepIndex !== storedStepIndex) {
			setCurrentStepIndex(currentStepIndex);
		}
	}, [currentStepIndex, setCurrentStepIndex, storedStepIndex]);

	const popoverVisible = storedPopoverVisible === true;

	const currentLayoutRelativeURL = themeDisplay.getLayoutRelativeURL();

	const currentPage = useMemo(
		() => findLongestMatch(currentLayoutRelativeURL, Object.keys(pages)),
		[pages, currentLayoutRelativeURL]
	);

	/**
	 * When the current step's element is not in the DOM (the page changed
	 * since the tour was authored, or the selector has a typo), fall
	 * back to another step of the current page instead of rendering a
	 * misplaced hotspot, and render nothing when no step is renderable.
	 */
	const renderableStepIndex = useMemo(() => {
		if (currentStepIndex === null) {
			return null;
		}

		const pageStepIds = pages[currentPage];

		if (!pageStepIds?.includes(steps[currentStepIndex].id)) {
			return null;
		}

		if (querySelectorSafe(steps[currentStepIndex].nodeToHighlight)) {
			return currentStepIndex;
		}

		console.warn(
			`Tour: the "${steps[currentStepIndex].nodeToHighlight}" element of step "${steps[currentStepIndex].id}" does not exist in the DOM`
		);

		const fallbackStepIndex = steps.findIndex(
			(step, index) =>
				index !== currentStepIndex &&
				pageStepIds.includes(step.id) &&
				querySelectorSafe(step.nodeToHighlight)
		);

		if (fallbackStepIndex === -1) {
			return null;
		}

		return fallbackStepIndex;
	}, [currentPage, currentStepIndex, pages, steps]);

	const memoizedTrigger = useMemo(() => {
		if (renderableStepIndex === null) {
			return undefined;
		}

		return (
			querySelectorSafe(steps[renderableStepIndex].nodeToHighlight) ??
			undefined
		);
	}, [steps, renderableStepIndex]);

	if (renderableStepIndex === null) {
		return null;
	}

	return (
		<Step
			closeOnClickOutside={closeOnClickOutside}
			closeable={closeable}
			currentPage={currentPage}
			currentStep={renderableStepIndex}
			lockScroll={lockScroll}
			memoizedTrigger={memoizedTrigger}
			onCurrentStep={setCurrentStepIndex}
			onPopoverVisible={setPopoverVisible}
			pages={pages}
			popoverVisible={popoverVisible}
			skippable={skippable}
			steps={steps}
		/>
	);
};

Tour.propTypes = {
	closeOnClickOutside: PropTypes.bool,
	closeable: PropTypes.bool,
	lockScroll: PropTypes.bool,
	skippable: PropTypes.bool,
	steps: PropTypes.arrayOf(
		PropTypes.shape({
			content: PropTypes.oneOfType([PropTypes.string, PropTypes.object]),
			darkbg: PropTypes.bool,
			nodeToHighlight: PropTypes.string.isRequired,
			positioning: PropTypes.oneOf(Object.keys(ALIGNMENTS_MAP)),
			title: PropTypes.oneOfType([PropTypes.string, PropTypes.object]),
		})
	),
};

export default Tour;
