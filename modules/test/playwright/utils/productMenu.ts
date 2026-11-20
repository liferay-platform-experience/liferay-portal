/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Page, expect} from '@playwright/test';

function isPinned(element: Element) {
	return (
		element.classList.contains('open') ||
		(element.classList.contains('c-slideout-show') &&
			element
				.querySelector('[data-qa-id="unifiedSidebarHeaderToggler"]')
				?.getAttribute('aria-expanded') === 'true')
	);
}

function isClosed(element: Element) {
	return (
		!element.classList.contains('open') &&
		!element.classList.contains('c-slideout-show')
	);
}

function isPeeking(element: Element) {
	return (
		element.classList.contains('c-slideout-show') &&
		element
			.querySelector('[data-qa-id="unifiedSidebarHeaderToggler"]')
			?.getAttribute('aria-expanded') === 'false'
	);
}

export async function openProductMenu(page: Page) {
	const productMenu = page
		.getByLabel('Product Menu', {exact: true})
		.or(page.getByTestId('sideNavigation'));

	if (await productMenu.evaluate(isPinned)) {
		return;
	}

	const openButton = page
		.getByLabel('Open Product Menu')
		.or(page.getByTestId('sideNavigationToggler'));
	const pinButton = page.getByLabel('Pin Product Menu');

	await expect(async () => {
		if (await productMenu.evaluate(isPinned)) {
			return;
		}

		if (await productMenu.evaluate(isPeeking)) {
			await pinButton.click({timeout: 2000});
		}
		else {
			await openButton.click({timeout: 2000});
		}

		await expect
			.poll(() => productMenu.evaluate(isPinned), {timeout: 2000})
			.toBeTruthy();
	}).toPass();
}

export async function closeProductMenu(page: Page) {
	const productMenu = page
		.getByLabel('Product Menu', {exact: true})
		.or(page.getByTestId('sideNavigation'));

	if (await productMenu.evaluate(isClosed)) {
		return;
	}

	const closeButton = page.getByRole('tab', {name: 'Close Product Menu'}).or(
		page.getByRole('button', {
			exact: true,
			expanded: true,
			name: 'Close Product Menu',
		})
	);

	await expect(async () => {
		if (await productMenu.evaluate(isPinned)) {
			await closeButton.click({timeout: 2000});
		}

		await page.mouse.move(800, 500);

		await expect
			.poll(() => productMenu.evaluate(isClosed), {timeout: 2000})
			.toBeTruthy();
	}).toPass({
		timeout: 5000,
	});
}
