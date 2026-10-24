/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {cleanup, render} from '@testing-library/react';
import React from 'react';

import {Button, OverlayMask} from '../../';

describe('OverlayMask basic rendering', () => {
	afterEach(cleanup);

	it('render highlight for the component', () => {
		const {container} = render(
			<OverlayMask visible>
				<Button>Button</Button>
			</OverlayMask>
		);

		expect(container).toMatchSnapshot();
	});

	it('render highlight for component via render props', () => {
		const {container} = render(
			<OverlayMask<HTMLButtonElement> visible>
				{(ref) => <Button ref={ref}>Button</Button>}
			</OverlayMask>
		);

		expect(container).toMatchSnapshot();
	});

	it("don't render highlighting for component when not visible", () => {
		const {container} = render(
			<OverlayMask>
				<Button>Button</Button>
			</OverlayMask>
		);

		expect(container).toMatchSnapshot();
	});

	it('render highlight to a specific area', () => {
		const {container} = render(
			<OverlayMask
				defaultBounds={{
					height: 100,
					width: 100,
					x: 50,
					y: 50,
				}}
				visible
			/>
		);

		expect(container).toMatchSnapshot();
	});

	it('render highlight for component with a custom padding', () => {
		const {container} = render(
			<OverlayMask padding={50} visible>
				<Button>Button</Button>
			</OverlayMask>
		);

		expect(container).toMatchSnapshot();
	});

	it('render highlight with rounded corners', () => {
		const {container} = render(
			<OverlayMask
				borderRadius={8}
				defaultBounds={{
					height: 100,
					width: 100,
					x: 50,
					y: 50,
				}}
				padding={0}
				visible
			/>
		);

		expect(
			container.querySelector('clipPath path')!.getAttribute('d')
		).toBe(
			'M 0 0 L 0 0 L 0 0 L 0 0 z M 50 58 L 50 142 A 8,8 0 0 0 58,150 L 142 150 A 8,8 0 0 0 150,142 L 150 58 A 8,8 0 0 0 142,50 L 58 50 A 8,8 0 0 0 50,58'
		);
	});

	it('limits the corner radius to half of the highlight', () => {
		const {container} = render(
			<OverlayMask
				borderRadius={100}
				defaultBounds={{
					height: 20,
					width: 100,
					x: 0,
					y: 0,
				}}
				padding={0}
				visible
			/>
		);

		expect(
			container.querySelector('clipPath path')!.getAttribute('d')
		).toContain('A 10,10');
	});
});
