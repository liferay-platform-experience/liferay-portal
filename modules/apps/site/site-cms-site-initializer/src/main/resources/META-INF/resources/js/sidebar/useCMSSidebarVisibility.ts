/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {useMediaQuery} from '@liferay/layout-js-components-web';
import {useUnifiedSidebarVisibility} from 'frontend-js-components-web';
import {useCallback, useEffect, useRef, useState} from 'react';

export const CMS_SIDEBAR_READY_EVENT_NAME = 'cmsSidebarReady';

export const CMS_SIDEBAR_STATE_CHANGED_EVENT_NAME = 'cmsSidebarStateChanged';

export const CMS_SIDEBAR_STATE_REQUESTED_EVENT_NAME =
	'cmsSidebarStateRequested';

const OVERLAY_MEDIA_QUERY = '(max-width: 991.98px)';

const TRANSITION_TIMEOUT = 300;

const VISIBLE_SESSION_KEY =
	'com.liferay.site.cms.site.initializer_sidebarState';

export function useCMSSidebarVisibility(initialVisible: boolean) {
	const [closedAsOverlay] = useState(
		() => initialVisible && window.matchMedia(OVERLAY_MEDIA_QUERY).matches
	);

	const overlay = useMediaQuery(OVERLAY_MEDIA_QUERY);

	const transitionControllerRef = useRef<AbortController | null>(null);

	const updateBody = useCallback((visible: boolean) => {
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
		body.classList.toggle('cms-sidebar-open', visible);
	}, []);

	const {updateVisible, visible} = useUnifiedSidebarVisibility({
		hiddenSessionValue: 'closed',
		initialVisible: initialVisible && !closedAsOverlay,
		onVisibleChange: updateBody,
		sessionKey: VISIBLE_SESSION_KEY,
		stateChangedEventName: CMS_SIDEBAR_STATE_CHANGED_EVENT_NAME,
		stateRequestedEventName: CMS_SIDEBAR_STATE_REQUESTED_EVENT_NAME,
		visibleSessionValue: 'open',
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

			document.body.classList.remove('cms-sidebar-open');

			Liferay.fire(CMS_SIDEBAR_STATE_CHANGED_EVENT_NAME, {
				visible: false,
			});
		},
		[closedAsOverlay]
	);

	useEffect(function announceReady() {

		// The sidebar only listens for requests once its effects run, so the
		// trigger waits for this before it sends any.

		Liferay.fire(CMS_SIDEBAR_READY_EVENT_NAME);
	}, []);

	return {overlay, updateVisible, visible};
}
