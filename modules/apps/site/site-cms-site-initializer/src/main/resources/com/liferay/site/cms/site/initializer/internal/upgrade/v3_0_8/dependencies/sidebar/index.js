/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

/* eslint-disable no-undef */

const sidebarId = `${fragmentEntryLinkNamespace}sidebar`;
const sidebarOpenKey = `${fragmentEntryLinkNamespace}sidebarOpen`;

const sidebar = fragmentElement.querySelector('.sidebar-container');
const sidebarCloseButton = fragmentElement.querySelector(
	'.sidebar-close-button'
);
const sidebarContainer = fragmentElement.querySelector('.sidebar-layout');
const sidebarTrigger = fragmentElement.querySelector('.sidebar-toggle');

let sidebarOpen = true;

// The server only adds this class in view mode with the LPD-88120 flag on, so
// the page editor keeps the drop zones in place.

if (
	layoutMode === 'view' &&
	document.body.classList.contains('cms-unified-sidebar')
) {
	mountUnifiedSidebar();
}
else {
	initSidebar();
}

function initSidebar() {
	fragmentElement.querySelector('.cms-unified-sidebar-host')?.remove();

	if (sidebarTrigger) {
		sidebar.setAttribute('id', sidebarId);
		sidebarTrigger.setAttribute('aria-controls', sidebarId);

		addSidebarToggleClickEventListener(sidebarTrigger);
	}

	if (sidebarCloseButton) {
		addSidebarToggleClickEventListener(sidebarCloseButton);
	}

	const sidebarOpenString = Liferay.Util.LocalStorage.getItem(
		sidebarOpenKey,
		Liferay.Util.LocalStorage.TYPES.FUNCTIONAL
	);

	sidebarOpen = sidebarOpenString === 'false' ? false : true;

	if (!sidebarOpen) {
		toggleSidebar(false);
	}
}

function mountUnifiedSidebar() {
	const navigationId = 'com_liferay_site_cms_site_initializer_sidebar';
	const stateChangedEventName = 'cmsSidebarStateChanged';
	const stateRequestedEventName = 'cmsSidebarStateRequested';

	const expandedKeysSessionKey =
		'com.liferay.site.cms.site.initializer_sidebarExpandedKeys';

	// The skeleton image only loads here, so pages without the unified sidebar
	// don't request it.

	const skeletonImage = fragmentElement.querySelector(
		'.cms-unified-sidebar-skeleton-image use'
	);

	skeletonImage?.setAttribute('href', skeletonImage.dataset.href);

	// The navigation menus and the spaces publish their items as JSON, in the
	// order they appear in the sidebar. Anything else, like the banner, is
	// moved into the sidebar below them.

	const sidebarBody = fragmentElement.querySelector('.sidebar-body');

	const propsElements = Array.from(
		sidebarBody.querySelectorAll(
			'.cms-spaces-props, .vertical-navigation-props'
		)
	);

	const sections = propsElements.map((propsElement) => ({
		props: JSON.parse(propsElement.textContent),
		type: propsElement.classList.contains('cms-spaces-props')
			? 'spaces'
			: 'navigation',
	}));

	const structureItem = propsElements[0]?.closest(
		'[class*="lfr-layout-structure-item-"]'
	);

	const elements = structureItem
		? Array.from(structureItem.parentElement.children)
				.filter(
					(element) =>
						!propsElements.some((propsElement) =>
							element.contains(propsElement)
						)
				)
				.map(toSlotElement)
		: [];

	const pageBar = fragmentElement.querySelector('.cms-control-menu');

	const image = pageBar?.querySelector('img');
	const title = pageBar?.querySelector('.cms-title');

	const visible = document.body.classList.contains('cms-sidebar-open');

	let sidebarVisible = visible;

	function handleStateChanged(event) {
		sidebarVisible = event.visible;

		updateSidebarTrigger(sidebarVisible);
	}

	Liferay.on(stateChangedEventName, handleStateChanged);

	let navigating = false;

	Liferay.once('beforeNavigate', () => {
		navigating = true;

		Liferay.detach(stateChangedEventName, handleStateChanged);
	});

	if (sidebarTrigger) {

		// The sidebar announces when it listens for requests, so a click
		// before that is not lost.

		Liferay.once('cmsSidebarReady', () => {
			sidebarTrigger.setAttribute('aria-controls', navigationId);
			sidebarTrigger.setAttribute('aria-keyshortcuts', 'Control+Alt+M');

			sidebarTrigger.addEventListener('click', () =>
				Liferay.fire(stateRequestedEventName, {
					visible: !sidebarVisible,
				})
			);

			sidebarTrigger.removeAttribute('disabled');

			updateSidebarTrigger(sidebarVisible);
		});
	}

	Promise.all([
		import('@liferay/site-cms-site-initializer/renderCMSSidebar'),
		Liferay.Util.Session.get(expandedKeysSessionKey).catch(() => null),
	])
		.then(([{default: renderCMSSidebar}, expandedKeys]) => {
			if (navigating) {
				return;
			}

			// The session answers "get" for a key that was never saved, which
			// must fall back to the navigation's default expanded keys.

			renderCMSSidebar(
				fragmentElement.querySelector('.cms-unified-sidebar-root'),
				{
					elements,
					expandedKeys:
						typeof expandedKeys === 'string' &&
						expandedKeys !== 'get'
							? expandedKeys.split(',').filter(Boolean)
							: null,
					expandedKeysSessionKey,
					imageURL: image?.src,
					label:
						title?.textContent.trim() ||
						sidebar.getAttribute('aria-label'),
					navigationId,
					sections,
					triggerElement: sidebarTrigger,
					visible,
				}
			);

			fragmentElement
				.querySelector('.cms-unified-sidebar-skeleton')
				?.remove();
		})
		.catch(() => {
			if (navigating) {
				return;
			}

			// Without the unified sidebar the page would have no navigation,
			// so fall back to the one the fragment renders.

			Liferay.detach(stateChangedEventName, handleStateChanged);

			document.body.classList.remove(
				'cms-sidebar-open',
				'cms-unified-sidebar'
			);

			initSidebar();
		});
}

// Wraps the element's content without taking it out of the document, so the
// components inside can still find their containers until the sidebar moves
// the wrapper.

function toSlotElement(element) {
	const slotElement = document.createElement('div');

	slotElement.append(...element.childNodes);

	element.append(slotElement);

	return slotElement;
}

function updateSidebarTrigger(visible) {
	if (!sidebarTrigger) {
		return;
	}

	const [openIcon, closedIcon] =
		sidebarTrigger.querySelectorAll('.inline-item');

	openIcon?.classList.toggle('hide', !visible);
	closedIcon?.classList.toggle('hide', visible);

	sidebarTrigger.setAttribute('aria-expanded', visible);
	sidebarTrigger.setAttribute('aria-pressed', visible);
	sidebarTrigger.setAttribute(
		'title',
		visible
			? sidebarTrigger.dataset.titleclose
			: sidebarTrigger.dataset.titleopen
	);
}

function addSidebarToggleClickEventListener(node) {
	node.addEventListener('click', () => handleSidebarToggleClick());

	node.removeAttribute('disabled');
}

function handleSidebarToggleClick() {
	sidebarOpen = !sidebarOpen;

	toggleSidebar();

	Liferay.Util.LocalStorage.setItem(
		sidebarOpenKey,
		sidebarOpen,
		Liferay.Util.LocalStorage.TYPES.FUNCTIONAL
	);
}

function toggleSidebar(moveFocus = true) {
	sidebarContainer.classList.toggle('open');

	sidebar.toggleAttribute('inert');

	if (sidebarTrigger) {
		sidebarTrigger
			.querySelectorAll('.inline-item')
			.forEach((node) => node.classList.toggle('hide'));

		sidebarTrigger.toggleAttribute('aria-expanded');
		sidebarTrigger.toggleAttribute('aria-pressed');

		if (sidebarOpen) {
			sidebarTrigger.setAttribute(
				'title',
				sidebarTrigger.dataset.titleclose
			);
		}
		else {
			sidebarTrigger.setAttribute(
				'title',
				sidebarTrigger.dataset.titleopen
			);
		}
	}

	if (moveFocus) {
		if (sidebarTrigger) {
			sidebarTrigger.focus();
		}
	}
}
