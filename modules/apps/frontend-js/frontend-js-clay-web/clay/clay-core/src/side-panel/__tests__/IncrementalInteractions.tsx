/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import Button from '@clayui/button';
import {Provider} from '@clayui/provider';
import {ClayPortal} from '@clayui/shared';
import {act, cleanup, render, waitFor} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React, {useRef, useState} from 'react';

import '@testing-library/jest-dom';

import {Option, Picker} from '../../picker';
import {SidePanel} from '../SidePanel';

// FocusTrap is mocked in this test because JSDOM does not fully simulate
// browser focus behavior. In a real browser, closing the panel would
// unmount the trap and restore focus to the trigger element. Mocking
// FocusTrap prevents false negatives in tests caused by JSDOM's limitations.

const mockFocusTrap = jest.fn(({children}: any) => <>{children}</>);

jest.mock('../../focus-trap/FocusTrap', () => ({
	FocusTrap: (props: any) => mockFocusTrap(props),
}));

function Example({
	backdrop = false,
	closeOnEscape,
	defaultOpen = false,
	fullHeight = false,
	position,
}: {
	backdrop?: boolean;
	closeOnEscape?: boolean;
	defaultOpen?: boolean;
	fullHeight?: boolean;
	position?: 'absolute' | 'fixed';
}) {
	const [open, setOpen] = useState(defaultOpen);

	const ref = useRef(null);

	return (
		<Provider spritemap="icons.svg">
			<Button
				aria-controls="sidepanel"
				aria-pressed={open}
				onClick={() => setOpen(!open)}
			>
				Open
			</Button>

			<div ref={ref}>
				<SidePanel
					as="aside"
					backdrop={backdrop}
					closeOnEscape={closeOnEscape}
					containerRef={ref}
					fullHeight={fullHeight}
					id="sidepanel"
					onOpenChange={setOpen}
					open={open}
					position={position}
				>
					<SidePanel.Header
						messages={{
							closeAriaLabel: 'Close the Title side panel.',
						}}
					>
						<SidePanel.Title>Title</SidePanel.Title>
					</SidePanel.Header>

					<SidePanel.Body>Body</SidePanel.Body>

					<SidePanel.Footer>Footer</SidePanel.Footer>
				</SidePanel>
			</div>
		</Provider>
	);
}

function NestedExample({
	backdrop = false,
	children,
	fullHeight = false,
}: {
	backdrop?: boolean;
	children: React.ReactNode;
	fullHeight?: boolean;
}) {
	const ref = useRef(null);

	return (
		<div ref={ref}>
			<SidePanel
				backdrop={backdrop}
				containerRef={ref}
				fullHeight={fullHeight}
				open
				position="fixed"
			>
				{children}
			</SidePanel>
		</div>
	);
}

function OverlayExample({
	backdrop = false,
	closeOnEscape,
	defaultOpen = false,
	defaultOverlay = false,
	onOpenChange,
}: {
	backdrop?: boolean;
	closeOnEscape?: boolean;
	defaultOpen?: boolean;
	defaultOverlay?: boolean;
	onOpenChange?: (open: boolean) => void;
}) {
	const [open, setOpen] = useState(defaultOpen);
	const [overlay, setOverlay] = useState(defaultOverlay);

	const containerRef = useRef(null);
	const triggerRef = useRef<HTMLButtonElement>(null);

	return (
		<Provider reducedMotion="never" spritemap="icons.svg">
			<Button onClick={() => setOpen(!open)} ref={triggerRef}>
				Toggle open
			</Button>

			<Button onClick={() => setOverlay(!overlay)}>Toggle overlay</Button>

			<Button>Outside</Button>

			<div ref={containerRef}>
				<SidePanel
					as="aside"
					backdrop={backdrop}
					closeOnEscape={closeOnEscape}
					containerRef={containerRef}
					onOpenChange={(open) => {
						setOpen(open);
						onOpenChange?.(open);
					}}
					open={open}
					overlay={overlay}
					triggerRef={triggerRef}
				>
					<SidePanel.Body>
						<Button>Inside</Button>
					</SidePanel.Body>
				</SidePanel>
			</div>
		</Provider>
	);
}

describe('SidePanel incremental interactions', () => {
	afterEach(cleanup);

	it('when opening move focus to the root element', async () => {
		const {getAllByRole, getByRole} = render(<Example />);

		const [button] = getAllByRole('button');
		const panel = getByRole('complementary');

		userEvent.tab();

		expect(button).toHaveFocus();
		expect(panel).toHaveAttribute('inert');

		userEvent.click(button!);

		await waitFor(() => {
			expect(panel).toHaveFocus();
			expect(panel).not.toHaveAttribute('inert');
		});
	});

	it('clicking the close button closes the side panel and moves the focus to the trigger', async () => {
		const {getAllByRole, getByRole} = render(<Example />);

		const [button, close] = getAllByRole('button');
		const panel = getByRole('complementary');

		userEvent.tab();

		expect(button).toHaveFocus();
		expect(panel).toHaveAttribute('inert');

		userEvent.click(button!);

		await waitFor(() => {
			expect(panel).toHaveFocus();
			expect(panel).not.toHaveAttribute('inert');
		});

		userEvent.click(close!);

		expect(button).toHaveFocus();
		expect(panel).toHaveAttribute('inert');
	});

	it('pressing ESC closes and moves focus to the trigger', async () => {
		const {getAllByRole, getByRole} = render(<Example />);

		const [button] = getAllByRole('button');
		const panel = getByRole('complementary');

		userEvent.tab();

		expect(button).toHaveFocus();
		expect(panel).toHaveAttribute('inert');

		userEvent.click(button!);

		await waitFor(() => {
			expect(panel).toHaveFocus();
			expect(panel).not.toHaveAttribute('inert');
		});

		userEvent.keyboard('{Escape}');

		expect(button).toHaveFocus();
		expect(panel).toHaveAttribute('inert');
	});

	it('appends the backdrop to the body and closes when clicking it', async () => {
		const {getAllByRole, getByRole} = render(<Example backdrop />);

		const [button] = getAllByRole('button');
		const panel = getByRole('complementary');

		expect(document.querySelector('.c-slideout-backdrop')).toBeNull();

		userEvent.click(button!);

		const backdrop = document.querySelector('.c-slideout-backdrop');

		expect(backdrop?.parentElement?.parentElement).toBe(document.body);

		userEvent.click(backdrop!);

		await waitFor(() => {
			expect(document.querySelector('.c-slideout-backdrop')).toBeNull();
			expect(panel).toHaveAttribute('inert');
		});
	});

	it('closes on ESC and traps focus while the backdrop is shown', async () => {
		const {getAllByRole, getByRole} = render(
			<Example backdrop closeOnEscape={false} />
		);

		const [button] = getAllByRole('button');
		const panel = getByRole('complementary');

		userEvent.click(button!);

		await waitFor(() => {
			expect(panel).not.toHaveAttribute('inert');
		});

		expect(mockFocusTrap).toHaveBeenLastCalledWith(
			expect.objectContaining({active: true})
		);
		expect(panel.closest('.c-slideout')).toHaveClass(
			'c-slideout-height-full'
		);

		userEvent.keyboard('{Escape}');

		await waitFor(() => {
			expect(panel).toHaveAttribute('inert');
		});
	});

	it('runs the full height from the top', () => {
		const {container} = render(<Example fullHeight position="fixed" />);

		const slideout = container.querySelector('.c-slideout');

		expect(slideout).toHaveClass('c-slideout-height-full');
		expect(slideout).toHaveStyle({top: '0px'});
	});

	it('runs the full height from the top with a backdrop', () => {
		const {container} = render(<Example backdrop position="fixed" />);

		const slideout = container.querySelector('.c-slideout');

		expect(slideout).toHaveClass('c-slideout-height-full');
		expect(slideout).toHaveStyle({top: '0px'});
	});

	it('mounts nested portals inside the side panel when running the full height', () => {
		const {container, getByText} = render(
			<NestedExample fullHeight>
				<ClayPortal>Menu</ClayPortal>
			</NestedExample>
		);

		const slideout = container.querySelector('.c-slideout');

		expect(slideout).toContainElement(getByText('Menu'));
		expect(container.querySelector('.sidebar')).not.toContainElement(
			getByText('Menu')
		);
	});

	it('mounts nested portals in the body when not running the full height', () => {
		const {container, getByText} = render(
			<NestedExample>
				<ClayPortal>Menu</ClayPortal>
			</NestedExample>
		);

		expect(container).not.toContainElement(getByText('Menu'));
		expect(document.body).toContainElement(getByText('Menu'));
	});

	it('closes an open dropdown on ESC before the side panel', async () => {
		const {getByRole} = render(
			<NestedExample backdrop>
				<Picker aria-label="Fruits" items={['Apple', 'Banana']}>
					{(item) => <Option key={item}>{item}</Option>}
				</Picker>
			</NestedExample>
		);

		const combobox = getByRole('combobox');

		await userEvent.click(combobox);

		expect(combobox).toHaveAttribute('aria-expanded', 'true');

		await userEvent.keyboard('{Escape}');

		expect(combobox).toHaveAttribute('aria-expanded', 'false');
		expect(document.querySelector('.sidebar')).toHaveClass(
			'c-slideout-show'
		);
	});
});

describe('SidePanel overlay', () => {
	let clientWidthSpy: jest.SpyInstance;

	const advance = (ms: number) =>
		act(() => {
			jest.advanceTimersByTime(ms);
		});
	const click = (element: HTMLElement) =>
		act(() => {
			element.click();
		});
	const focus = (element: HTMLElement) =>
		act(() => {
			element.focus();
		});
	const getClasses = (element: Element, prefix: string) =>
		Array.from(element.classList).filter((name) => name.startsWith(prefix));

	const setup = (props: React.ComponentProps<typeof OverlayExample> = {}) => {
		const {getByRole, getByText} = render(<OverlayExample {...props} />);
		const panel = getByRole('complementary');
		const slideout = panel.parentElement as HTMLElement;

		return {
			container: slideout.parentElement as HTMLElement,
			inside: getByText('Inside'),
			openToggle: getByText('Toggle open'),
			outside: getByText('Outside'),
			overlayToggle: getByText('Toggle overlay'),
			panel,
			slideout,
		};
	};

	beforeEach(() => {
		jest.useFakeTimers();

		clientWidthSpy = jest
			.spyOn(HTMLElement.prototype, 'clientWidth', 'get')
			.mockReturnValue(1024);
	});

	afterEach(() => {
		clientWidthSpy.mockRestore();

		jest.useRealTimers();
	});

	it('opens and closes as an overlay without pushing the container, and returns focus on close', () => {
		const {container, inside, openToggle, outside, panel, slideout} = setup(
			{
				defaultOverlay: true,
			}
		);

		focus(outside);
		click(openToggle);
		advance(300);

		expect(panel).toHaveClass('c-slideout-show');
		expect(slideout).toHaveClass('c-slideout-overlay');
		expect(getClasses(container, 'c-slideout-push-')).toEqual([]);
		expect(outside).toHaveFocus();

		focus(inside);
		click(openToggle);

		expect(outside).toHaveFocus();
		expect(panel).toHaveAttribute('inert');

		advance(300);

		expect(slideout).not.toHaveClass('c-slideout-shown');
	});

	it('mounts open as an overlay without a push or transition on the container', () => {
		const {container, panel} = setup({
			defaultOpen: true,
			defaultOverlay: true,
		});

		expect(panel).toHaveClass('c-slideout-show');
		expect(getClasses(container, 'c-slideout-push-')).toEqual([]);
		expect(getClasses(container, 'c-slideout-transition')).toEqual([]);
	});

	it.each([true, false])(
		'keeps the same shown panel when switching presentation while open (overlay at start: %s)',
		(overlayAtStart) => {
			const {container, overlayToggle, panel, slideout} = setup({
				defaultOpen: true,
				defaultOverlay: overlayAtStart,
			});

			advance(70);
			click(overlayToggle);
			advance(10);
			click(overlayToggle);
			advance(10);
			click(overlayToggle);
			advance(500);

			expect(document.querySelector('aside')).toBe(panel);
			expect(panel).toHaveClass('c-slideout-show');
			expect(slideout.classList.contains('c-slideout-overlay')).toBe(
				!overlayAtStart
			);
			expect(container.classList.contains('c-slideout-push-end')).toBe(
				overlayAtStart
			);
			expect(getClasses(container, 'c-slideout-transition')).toEqual([]);
		}
	);

	it.each([true, false])(
		'stays open on ESC with closeOnEscape false (overlay: %s)',
		(overlay) => {
			const onOpenChange = jest.fn();

			const {panel} = setup({
				closeOnEscape: false,
				defaultOpen: true,
				defaultOverlay: overlay,
				onOpenChange,
			});

			act(() => {
				document.dispatchEvent(
					new KeyboardEvent('keydown', {bubbles: true, key: 'Escape'})
				);
			});

			expect(onOpenChange).not.toHaveBeenCalled();
			expect(panel).not.toHaveAttribute('inert');
		}
	);

	it.each([
		[
			'overlay to pushed',
			true,
			'overlayToggle',
			'outside',
			'overlayToggle',
		],
		['pushed to overlay', false, 'overlayToggle', 'outside', 'outside'],
	] as const)(
		'moves focus on close by the presentation at close after switching from %s',
		(_, overlayAtOpen, atOpen, beforeClose, expected) => {
			const elements = setup({defaultOverlay: overlayAtOpen});

			focus(elements[atOpen]);
			click(elements.openToggle);
			advance(300);
			click(elements.overlayToggle);
			advance(300);

			expect(elements.slideout).toHaveClass('c-slideout-shown');

			focus(elements[beforeClose]);
			click(elements.openToggle);

			expect(elements[expected]).toHaveFocus();
		}
	);

	it.each([
		['on a mobile device', {}, 767],
		['with a backdrop', {backdrop: true}, 1024],
	])('is not an overlay %s', (_, props, width) => {
		clientWidthSpy.mockReturnValue(width);

		const {slideout} = setup({
			...props,
			defaultOpen: true,
			defaultOverlay: true,
		});

		expect(slideout).not.toHaveClass('c-slideout-overlay');
	});
});
