/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page, expect, mergeTests} from '@playwright/test';

import {loginTest} from '../../../fixtures/loginTest';
import {performLoginViaApi, performLogout} from '../../../utils/performLogin';

export const test = mergeTests(loginTest());

const TOAST_SELECTOR = '.alert-notifications-fixed .alert';

async function getOldestAndNewestBoxes(locator: Locator) {
	const newestBox = await locator.last().boundingBox();
	const oldestBox = await locator.first().boundingBox();

	if (!newestBox || !oldestBox) {
		throw new Error('An element has no bounding box');
	}

	return {newestBox, oldestBox};
}

async function openToasts(page: Page, total: number) {
	const count = await page.locator(TOAST_SELECTOR).count();

	await page.evaluate(
		({count, total}) => {
			for (let i = count + 1; i <= total; i++) {
				Liferay.Util.openToast({
					autoClose: false,
					message: `Toast ${i} ${'with a long message '.repeat(8)}`,
				});
			}
		},
		{count, total}
	);

	await expect(page.locator(TOAST_SELECTOR)).toHaveCount(total);
}

test('Check timeout toast has focus', {tag: '@LPD-79043'}, async ({page}) => {
	await performLogout(page);
	await performLoginViaApi({page, rememberMe: false, screenName: 'test'});

	await expect(page.getByText('Due to inactivity')).toBeVisible();

	await expect(
		page.locator('#sessionToast').locator('.alert-autofit-row')
	).toBeFocused();
});

test('Stack toasts as they pile up', {tag: '@LPD-109681'}, async ({page}) => {
	const slots = page.locator('.alert-notifications-fixed > div');

	await test.step('6 toasts keep their full width', async () => {
		await openToasts(page, 6);

		const {newestBox, oldestBox} = await getOldestAndNewestBoxes(slots);

		expect(oldestBox.width).toBe(newestBox.width);
	});

	await test.step('7 toasts narrow the older ones', async () => {
		await openToasts(page, 7);

		const {newestBox, oldestBox} = await getOldestAndNewestBoxes(slots);

		expect(oldestBox.width).toBeLessThan(newestBox.width);
	});

	await test.step('13 toasts show only a strip of the older ones', async () => {
		await openToasts(page, 13);

		const {newestBox, oldestBox} = await getOldestAndNewestBoxes(slots);

		expect(oldestBox.height).toBe(20);
		expect(oldestBox.height).toBeLessThan(newestBox.height);
	});

	const toasts = page.locator(TOAST_SELECTOR);

	await test.step('21 toasts hide the oldest text visually only', async () => {
		await openToasts(page, 21);

		const {newestBox, oldestBox} = await getOldestAndNewestBoxes(slots);

		expect(oldestBox.height).toBe(12);
		expect(oldestBox.height).toBeLessThan(newestBox.height);

		await expect(toasts.first().locator('.alert-autofit-row')).toHaveCSS(
			'opacity',
			'0'
		);
		await expect(toasts.first().getByRole('alert')).toContainText(
			'Toast 1 with a long message'
		);
	});

	await test.step('hovering a slot raises it above the newer ones', async () => {
		await slots.first().hover();

		const newerSlotBox = await slots.nth(1).boundingBox();

		const olderSlotCoversNewerSlot = await page.evaluate(
			({height, width, x, y}) =>
				!!document
					.elementFromPoint(x + width / 2, y + height / 2)
					?.closest('.alert-notifications-fixed > div:first-child'),
			newerSlotBox!
		);

		expect(olderSlotCoversNewerSlot).toBe(true);
	});

	await test.step('closing with the keyboard removes one toast', async () => {
		await toasts.first().getByRole('button', {name: 'Close'}).focus();

		await page.keyboard.press('Enter');

		await expect(toasts).toHaveCount(20);
	});
});
