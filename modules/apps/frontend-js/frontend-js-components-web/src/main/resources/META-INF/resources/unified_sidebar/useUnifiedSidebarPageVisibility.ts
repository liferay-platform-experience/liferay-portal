/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	useCallback,
	useEffect,
	useRef,
	useState,
	useSyncExternalStore,
} from 'react';

import {useUnifiedSidebarVisibility} from './useUnifiedSidebarVisibility';

const OVERLAY_MEDIA_QUERY = '(max-width: 991.98px)';

const TRANSITION_TIMEOUT = 300;

function isOverlay() {
	return window.matchMedia(OVERLAY_MEDIA_QUERY).matches;
}

function subscribeToOverlay(onChange: () => void) {
	const mediaQueryList = window.matchMedia(OVERLAY_MEDIA_QUERY);

	mediaQueryList.addEventListener('change', onChange);

	return () => mediaQueryList.removeEventListener('change', onChange);
}

export function useUnifiedSidebarPageVisibility({
	bodyClassName,
	initialVisible,
	readyEventName,
	sessionKey,
	stateChangedEventName,
	stateRequestedEventName,
}: {
	bodyClassName: string;
	initialVisible: boolean;
	readyEventName: string;
	sessionKey: string;
	stateChangedEventName: string;
	stateRequestedEventName: string;
}) {
	const [closedAsOverlay] = useState(() => initialVisible && isOverlay());

	const initiallyVisible = initialVisible && !closedAsOverlay;

	const overlay = useSyncExternalStore(subscribeToOverlay, isOverlay);

	const transitionControllerRef = useRef<AbortController | null>(null);

	const updateBody = useCallback(
		(visible: boolean) => {
			transitionControllerRef.current?.abort();

			const transitionController = new AbortController();

			transitionControllerRef.current = transitionController;

			const {body} = document;

			const endTransition = () => {
				body.classList.remove('sidenav-transition');

				transitionController.abort();
			};

			body.addEventListener(
				'transitionend',
				(event) => {
					if (
						event.propertyName === 'padding-left' &&
						(event.target as HTMLElement).id === 'wrapper'
					) {
						endTransition();
					}
				},
				{signal: transitionController.signal}
			);

			const timeoutId = setTimeout(endTransition, TRANSITION_TIMEOUT);

			transitionController.signal.addEventListener('abort', () =>
				clearTimeout(timeoutId)
			);

			body.classList.add('sidenav-transition');

			bodyClassName.split(' ').forEach((className) => {
				body.classList.toggle(className, visible);
			});
		},
		[bodyClassName]
	);

	const {updateVisible, visible} = useUnifiedSidebarVisibility({
		initialVisible: initiallyVisible,
		onVisibleChange: updateBody,
		sessionKey,
		stateChangedEventName,
		stateRequestedEventName,
	});

	useEffect(function cleanupTransition() {
		return () => {
			transitionControllerRef.current?.abort();

			document.body.classList.remove('sidenav-transition');
		};
	}, []);

	useEffect(
		function closeOverlayOnLoad() {
			if (!closedAsOverlay) {
				return;
			}

			document.body.classList.remove(...bodyClassName.split(' '));

			Liferay.fire(stateChangedEventName, {visible: false});
		},
		[bodyClassName, closedAsOverlay, stateChangedEventName]
	);

	useEffect(
		function announceReady() {

			// Requests fired before the sidebar subscribed were lost, so let
			// their senders know they can fire them again.

			Liferay.fire(readyEventName, {visible: initiallyVisible});
		},
		[initiallyVisible, readyEventName]
	);

	return {overlay, updateVisible, visible};
}
