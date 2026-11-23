/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import {SidePanel} from '@clayui/core';
import ClayIcon from '@clayui/icon';
import {IPortalBaseProps} from '@clayui/shared';
import {sub} from 'frontend-js-web';
import React, {useRef} from 'react';

import UnifiedSidebarColorSchemeButton from './UnifiedSidebarColorSchemeButton';
import {useUnifiedSidebarPeek} from './useUnifiedSidebarPeek';

import './UnifiedSidebar.scss';

export type HeaderMessages = {
	backAriaLabel?: string;
};

export interface UnifiedSidebarProps {
	backdrop?: boolean;
	backdropContainerProps?: IPortalBaseProps;
	canonicalName?: string;
	categoryImageUrl: string;
	children?: React.ReactNode;
	colorScheme?: 'dark' | 'light';
	colorSchemeSessionKey?: string;
	containerRef?: React.RefObject<HTMLElement>;
	headerActions?: React.ReactNode;
	headerMessages?: HeaderMessages;
	label: string;
	navigationId?: string;
	onBack?: () => void;
	onOpenChange?: (open: boolean) => void;
	open?: boolean;
	testId?: string;
}

function UnifiedSidebarTop({children}: {children?: React.ReactNode}) {
	return <>{children}</>;
}

export default function UnifiedSidebar({
	backdrop,
	backdropContainerProps,
	canonicalName,
	categoryImageUrl,
	children,
	colorScheme,
	colorSchemeSessionKey,
	containerRef: externalContainerRef,
	headerActions,
	headerMessages,
	label,
	navigationId,
	onBack,
	onOpenChange,
	open = false,
	testId = 'unified-sidebar',
}: UnifiedSidebarProps) {
	const internalContainerRef = useRef<HTMLElement>(null);

	const containerRef = externalContainerRef ?? internalContainerRef;

	const {dismiss, peek} = useUnifiedSidebarPeek({
		navigationId,
		open,
	});

	const mergedHeaderMessages = {
		backAriaLabel: Liferay.Language.get('go-back'),
		...headerMessages,
	};

	const togglerLabel = open
		? Liferay.Language.get('close-product-menu')
		: Liferay.Language.get('pin-product-menu');

	return (
		<SidePanel
			aria-label={sub(Liferay.Language.get('x-menu'), label)}
			backdrop={backdrop && open}
			backdropContainerProps={backdropContainerProps}
			className="unified-sidebar"
			closeOnEscape={peek}
			containerRef={containerRef}
			data-canonical-name={canonicalName}
			data-qa-id={testId}
			data-testid={testId}
			direction="left"
			displayType="light"
			fullHeight
			id={navigationId}
			onOpenChange={peek ? dismiss : onOpenChange}
			open={open || peek}
			overlay={peek}
			position="fixed"
		>
			<SidePanel.Header
				className="c-mx-1 c-px-2 unified-sidebar-header"
				data-qa-id="unifiedSidebarHeader"
				hideCloseButton
				messages={mergedHeaderMessages}
				onBack={onBack}
			>
				<SidePanel.Title className="align-items-center c-my-0 d-flex">
					<ClayButton
						aria-expanded={open}
						aria-label={togglerLabel}
						borderless
						className="c-mr-1 unified-sidebar-header-toggler"
						data-qa-id="unifiedSidebarHeaderToggler"
						displayType="secondary"
						monospaced
						onClick={() => onOpenChange?.(!open)}
						size="sm"
						title={togglerLabel}
					>
						{peek && (
							<img
								alt=""
								className="unified-sidebar-header-toggler-category-image"
								src={categoryImageUrl}
							/>
						)}

						<ClayIcon
							symbol={
								open
									? 'product-menu-open'
									: 'product-menu-closed'
							}
						/>
					</ClayButton>

					<span
						className="c-ml-2 text-5 text-truncate"
						data-qa-id="unifiedSidebarLabel"
						title={label}
					>
						{label}
					</span>

					{headerActions}

					{Liferay.FeatureFlags['LPD-57922'] && !onBack && (
						<UnifiedSidebarColorSchemeButton
							className="c-ml-2"
							colorScheme={colorScheme}
							colorSchemeSessionKey={colorSchemeSessionKey}
						/>
					)}
				</SidePanel.Title>
			</SidePanel.Header>

			{children}
		</SidePanel>
	);
}

UnifiedSidebar.Body = SidePanel.Body;
UnifiedSidebar.Top = UnifiedSidebarTop;
