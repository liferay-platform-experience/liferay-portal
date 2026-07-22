/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayButtonWithIcon} from '@clayui/button';
import classNames from 'classnames';
import React, {useCallback, useMemo} from 'react';

import {ChatShellContext} from '../ChatShellContext';
import useChatContainer from '../hooks/useChatContainer';
import useFloatingPanel, {
	NUDGE_STEP,
	RESIZE_STEP,
} from '../hooks/useFloatingPanel';
import createPointerDragHandler from '../utils/createPointerDragHandler';
import InitialFocus from './InitialFocus';

import './ChatContainers.scss';

interface FloatingPanelProps {
	children: React.ReactNode;
	className?: string;
	dialogId: string;
	menuRef: React.MutableRefObject<HTMLDivElement | null>;
	onClose: () => void;
	otherProps: React.HTMLAttributes<HTMLDivElement>;
}

export default function FloatingPanel({
	children,
	className,
	dialogId,
	menuRef,
	onClose,
	otherProps,
}: FloatingPanelProps) {
	const {drag, resize, state} = useFloatingPanel(dialogId);

	const handleDragPointerDown = useMemo(
		() => createPointerDragHandler(drag),
		[drag]
	);

	const handleResizePointerDown = useMemo(
		() => createPointerDragHandler(resize),
		[resize]
	);

	const handleDragKeyDown = useCallback(
		(event: React.KeyboardEvent) => {
			if (event.key === 'ArrowLeft') {
				event.preventDefault();
				drag(-NUDGE_STEP, 0);
			}
			else if (event.key === 'ArrowRight') {
				event.preventDefault();
				drag(NUDGE_STEP, 0);
			}
			else if (event.key === 'ArrowUp') {
				event.preventDefault();
				drag(0, -NUDGE_STEP);
			}
			else if (event.key === 'ArrowDown') {
				event.preventDefault();
				drag(0, NUDGE_STEP);
			}
		},
		[drag]
	);

	const handleResizeKeyDown = useCallback(
		(event: React.KeyboardEvent) => {
			if (event.key === 'ArrowLeft') {
				event.preventDefault();
				resize(-RESIZE_STEP, 0);
			}
			else if (event.key === 'ArrowRight') {
				event.preventDefault();
				resize(RESIZE_STEP, 0);
			}
			else if (event.key === 'ArrowUp') {
				event.preventDefault();
				resize(0, -RESIZE_STEP);
			}
			else if (event.key === 'ArrowDown') {
				event.preventDefault();
				resize(0, RESIZE_STEP);
			}
		},
		[resize]
	);

	const titleBarProps = useMemo(
		() => ({onPointerDown: handleDragPointerDown}),
		[handleDragPointerDown]
	);

	const contextValue = useChatContainer({
		id: dialogId,
		onClose,
		titleBarLeading: (
			<ClayButtonWithIcon
				aria-label={Liferay.Language.get('move-assistant')}
				borderless
				className="chat-container-floating-drag-handle"
				displayType="secondary"
				onKeyDown={handleDragKeyDown}
				size="sm"
				symbol="drag"
			/>
		),
		titleBarProps,
	});

	return (
		<div
			className="border chat-container-floating-panel rounded-lg shadow"
			ref={menuRef}
			style={{
				height: state.height,
				left: state.x,
				top: state.y,
				width: state.width,
			}}
		>
			<InitialFocus menuRef={menuRef}>
				<ChatShellContext.Provider value={contextValue}>
					<div
						{...otherProps}
						className={classNames(
							'chat-container chat-container-floating',
							className
						)}
					>
						{children}
					</div>

					<button
						aria-label={Liferay.Language.get('resize-assistant')}
						className="chat-container-floating-resize-handle"
						onKeyDown={handleResizeKeyDown}
						onPointerDown={handleResizePointerDown}
						type="button"
					/>
				</ChatShellContext.Provider>
			</InitialFocus>
		</div>
	);
}
