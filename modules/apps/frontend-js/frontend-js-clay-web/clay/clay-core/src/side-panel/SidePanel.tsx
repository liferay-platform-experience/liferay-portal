/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {useProvider} from '@clayui/provider';
import {
	ClayPortal,
	ClayPortalContext,
	IPortalBaseProps,
	Keys,
	useControlledState,
	useId,
	useIsMobileDevice,
	useObservedMaxWidth,
} from '@clayui/shared';
import classnames from 'classnames';
import React, {useEffect, useLayoutEffect, useRef, useState} from 'react';
import {CSSTransition} from 'react-transition-group';

import {FocusTrap} from '../focus-trap';
import {useIsFirstRender} from '../hooks';
import {ResizeHandle} from '../resize-handle';
import {Body} from './Body';
import {Footer} from './Footer';
import {Header} from './Header';
import {Title} from './Title';
import {SidePanelContext} from './context';

const PANEL_WIDTH_MIN = 280;

type ControlledState = {
	onOpenChange: (open: boolean) => void;
	open: boolean;
};

type UncontrolledState = {
	onOpenChange?: (open: boolean) => void;
	open?: boolean;
};

export type Props = {

	/**
	 * The global `aria-describedby` attribute identifies the element that
	 * describes the component.
	 */
	'aria-describedby'?: string;

	/**
	 * The `aria-label` attribute defines a string value that labels an interactive
	 * element.
	 */
	'aria-label'?: string;

	/**
	 * The `aria-labelledby` attribute identifies the element (or elements) that
	 * labels the element it is applied to.
	 */
	'aria-labelledby'?: string;

	/**
	 * Custom component.
	 * `aside` - Secondary menus or filters, complementary information...
	 * (e.g product filters in an e-commerce site, related articles in a blog...).
	 * `nav` or `section` - If the ontent is essential for navigation, sidebar
	 * contains primary actions like important forms or sidebar is the
	 * only important content.
	 */
	'as'?: 'aside' | 'div' | 'nav' | 'section';

	/**
	 * Flag to render a backdrop behind the SidePanel while it is open. The
	 * backdrop is appended to the end of the body element and closes the
	 * SidePanel when clicked.
	 */
	'backdrop'?: boolean;

	/**
	 * Props to add to the ClayPortal that renders the backdrop.
	 */
	'backdropContainerProps'?: IPortalBaseProps;

	/**
	 * Children content to render a content.
	 */
	'children': React.ReactNode;

	/**
	 * Sets the CSS className for the component.
	 */
	'className'?: string;

	/**
	 * Flag to determine whether the SidePanel should close when pressing
	 * the Escape key.
	 * NOTE: The default behavior is to close on Escape but it will be forced
	 * to `true` on mobile devices or when `backdrop` is set, and can be set
	 * to `false` for desktop if needed.
	 */
	'closeOnEscape'?: boolean;

	/**
	 * Element reference to the container of the SidePanel and primary content.
	 * NOTE: The containerRef is needed to properly handle layout and
	 * transitions of the SidePanel.
	 */
	'containerRef': React.RefObject<HTMLElement>;

	/**
	 * Property to set the default value (uncontrolled).
	 */
	'defaultOpen'?: boolean;

	/**
	 * Direction the panel will render.
	 */
	'direction'?: 'left' | 'right';

	/**
	 * Flag to determine which style the SidePanel will display.
	 */
	'displayType'?: 'light' | 'dark';

	/**
	 * External reference for the side panel.
	 */

	'externalSidePanelRef'?: React.RefObject<HTMLDivElement>;

	/**
	 * Property to determine whether the panel behaves in a fluid manner.
	 */
	'fluid'?: boolean;

	/**
	 * Flag to make a fixed SidePanel run the full height of the page, from
	 * the top and above fixed or sticky navigation, instead of starting below
	 * its container. A SidePanel with a backdrop always runs this way.
	 */
	'fullHeight'?: boolean;

	/**
	 * The id of the component.
	 */
	'id'?: string;

	/**
	 * Sets a custom width on the sidebar panel. The minimum width is 280px.
	 */
	'panelWidth'?: number;

	/**
	 * Property to determine how the SidePanel will be positioned.
	 */
	'position'?: 'absolute' | 'fixed';

	/**
	 * Element reference to open the SidePanel.
	 * NOTE: SidePanel automatically identifies the trigger but it may not
	 * work in some cases when the element disappears from the DOM.
	 */
	'triggerRef'?: React.RefObject<HTMLElement>;
} & (UncontrolledState | ControlledState);

export function SidePanel({
	'aria-label': ariaLabel,
	'aria-labelledby': ariaLabelledby,
	'as': As = 'div',
	backdrop = false,
	backdropContainerProps = {},
	children,
	className,
	'closeOnEscape': externalCloseOnEscape = true,
	containerRef,
	defaultOpen,
	direction = 'right',
	displayType = 'light',
	externalSidePanelRef,
	fluid = false,
	fullHeight = false,
	'id': externalId,
	onOpenChange,
	'open': externalOpen,
	'panelWidth': externalPanelWidth,
	position = 'absolute',
	triggerRef,
	...otherProps
}: Props) {
	const [resizeWidth, setResizeWidth] = useState(window.innerWidth / 2);
	const internalSidePanelRef = useRef<HTMLDivElement>(null);
	const slideoutRef = useRef<HTMLDivElement>(null);

	const sidePanelRef = externalSidePanelRef || internalSidePanelRef;

	const isMobile = useIsMobileDevice();
	const sidePanelObservedMaxWidth = useObservedMaxWidth(sidePanelRef);
	const {prefersReducedMotion} = useProvider();

	const closeOnEscape = isMobile || backdrop || externalCloseOnEscape;
	const isResizable = fluid && !isMobile;

	const [open, setOpen] = useControlledState({
		defaultName: 'defaultOpen',
		defaultValue: defaultOpen,
		handleName: 'onOpenChange',
		name: 'open',
		onChange: onOpenChange,
		value: externalOpen,
	});

	const pushClassName =
		direction === 'left' ? 'c-slideout-push-start' : 'c-slideout-push-end';

	useEffect(() => {

		// Ensures SidePanel is open when initial state is open.

		if (open) {
			containerRef.current?.classList.add(pushClassName);
		}
	}, []);

	const isFirstRender = useIsFirstRender();

	useLayoutEffect(() => {
		if (isFirstRender && !open) {
			return;
		}

		const container = containerRef.current;

		container?.classList.add('c-slideout-transition');
		container?.classList.toggle('c-slideout-transition-in', !!open);
		container?.classList.toggle('c-slideout-transition-out', !open);
		container?.classList.toggle(pushClassName, !!open);

		const timer = setTimeout(
			() => {
				container?.classList.remove(
					'c-slideout-transition',
					'c-slideout-transition-in',
					'c-slideout-transition-out'
				);
			},
			prefersReducedMotion ? 0 : open ? 300 : 200
		);

		return () => clearTimeout(timer);
	}, [open]);

	useEffect(() => {
		if (open) {

			// Saves the last element that has focus before moving to the
			// sidepanel to regain focus when closing the sidepanel.

			const element =
				(document.activeElement as HTMLElement | null) ||
				triggerRef?.current ||
				null;

			return () => {
				element?.focus();
			};
		}
	}, [open]);

	useLayoutEffect(() => {
		containerRef.current?.classList.add('c-slideout-container');

		if (fluid) {
			containerRef.current?.classList.add('c-slideout-fluid');
		}
	}, [containerRef.current]);

	useEffect(() => {
		if (open) {
			const onKeyDown = (event: KeyboardEvent) => {
				if (event.key === Keys.Esc && closeOnEscape) {
					event.stopImmediatePropagation();
					event.preventDefault();

					setOpen(false);
				}
			};

			// Add Side Panel content to DOM focus management

			sidePanelRef.current?.removeAttribute('inert');

			document.addEventListener('keydown', onKeyDown);

			return () => {
				document.removeEventListener('keydown', onKeyDown);
			};
		}
		else {

			// Remove Side Panel content from DOM focus management

			sidePanelRef.current?.setAttribute('inert', '');
		}
	}, [closeOnEscape, open]);

	const internalPanelId = useId();
	const titleId = useId();

	const offsetTop = useOffsetTop(containerRef);

	const heightFull = fullHeight || backdrop;

	// A full height SidePanel stacks above dropdowns, so nested portals mount
	// inside it, the same way they mount inside a modal.

	const parentPortalRef = React.useContext(ClayPortalContext);

	const panelId = externalId ?? internalPanelId;
	const panelWidth = isResizable
		? Math.min(sidePanelObservedMaxWidth, resizeWidth)
		: externalPanelWidth && Math.max(externalPanelWidth, PANEL_WIDTH_MIN);

	return (
		<>
			<div
				className={classnames(
					`c-slideout c-slideout-${position} c-slideout-push`,
					{
						'c-slideout-end': direction === 'right',
						'c-slideout-height-full': heightFull,
						'c-slideout-start': direction === 'left',
					}
				)}
				ref={slideoutRef}
				style={
					position === 'fixed'
						? {top: heightFull ? 0 : `${offsetTop}px`}
						: undefined
				}
			>
				<CSSTransition
					appear={open}
					className={classnames('sidebar', className, {
						'sidebar-dark': displayType === 'dark',
						'sidebar-light': displayType === 'light',
					})}
					classNames={{
						appearActive: 'c-slideout-show',
						appearDone: 'c-slideout-show',
						enter: 'c-slideout-transition c-slideout-transition-in',
						enterActive: 'c-slideout-show',
						enterDone: 'c-slideout-show',
						exit: 'c-slideout-transition c-slideout-transition-out',
						exitActive: '',
					}}
					in={open}
					onEntered={(_, isAppearing) => {
						slideoutRef.current?.classList.add('c-slideout-shown');

						if (!isAppearing) {

							// Move focus to sidepanel when opening but not if
							// opened on first load.

							sidePanelRef.current?.focus({preventScroll: true});
						}
					}}
					onExited={() => {
						slideoutRef.current?.classList.remove(
							'c-slideout-shown'
						);
					}}
					timeout={prefersReducedMotion ? 0 : open ? 300 : 200}
				>
					<As
						{...otherProps}
						aria-label={ariaLabel}
						aria-labelledby={
							!ariaLabelledby && !ariaLabel
								? titleId
								: ariaLabelledby
						}
						id={panelId}
						ref={sidePanelRef}
						style={panelWidth ? {width: panelWidth} : undefined}
						tabIndex={-1}
					>
						<ClayPortalContext.Provider
							value={heightFull ? slideoutRef : parentPortalRef}
						>
							<SidePanelContext.Provider
								value={{onOpenChange: setOpen, open, titleId}}
							>
								<FocusTrap
									active={(isMobile || backdrop) && open}
								>
									{children}
								</FocusTrap>

								{isResizable && (
									<ResizeHandle
										aria-controls={panelId}
										maxWidth={sidePanelObservedMaxWidth}
										minWidth={PANEL_WIDTH_MIN}
										onWidthChange={setResizeWidth}
										position={direction}
										width={panelWidth}
									/>
								)}
							</SidePanelContext.Provider>
						</ClayPortalContext.Provider>
					</As>
				</CSSTransition>
			</div>

			{backdrop && open && (
				<ClayPortal {...backdropContainerProps}>
					<div
						aria-hidden="true"
						className="c-slideout-backdrop"
						onClick={() => setOpen(false)}
					/>
				</ClayPortal>
			)}
		</>
	);
}

function useOffsetTop(ref: React.RefObject<HTMLElement>) {
	const [offsetTop, setOffsetTop] = React.useState<Number>();

	const checkOffset = () => {
		const handleResize = () => {
			if (ref.current) {
				const offsetTop =
					ref.current?.getBoundingClientRect().top + window.scrollY;

				setOffsetTop(offsetTop);
			}
		};

		handleResize();

		window.addEventListener('resize', handleResize);

		return () => {
			window.removeEventListener('resize', handleResize);
		};
	};

	React.useLayoutEffect(() => {
		if (!ref.current) {
			return;
		}

		checkOffset();
	}, [ref.current]);

	React.useEffect(() => {
		checkOffset();
	}, []);

	return offsetTop;
}

SidePanel.Header = Header;
SidePanel.Title = Title;
SidePanel.Body = Body;
SidePanel.Footer = Footer;
