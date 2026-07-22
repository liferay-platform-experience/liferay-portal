/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Overlay, useIsFirstRender, usePrevious} from '@clayui/shared';
import React, {useEffect, useId, useRef} from 'react';

import FloatingPanel from './FloatingPanel';

export interface ChatFloatingContainerProps
	extends React.HTMLAttributes<HTMLDivElement> {
	children: React.ReactNode;
	id?: string;
	onOpenChange?: (open: boolean) => void;
	open?: boolean;
	trigger: React.ReactElement & {
		ref?: React.Ref<HTMLElement>;
	};
}

export default function ChatFloatingContainer({
	children,
	className,
	id,
	onOpenChange = () => {},
	open = false,
	trigger,
	...otherProps
}: ChatFloatingContainerProps) {
	const menuRef = useRef<HTMLDivElement | null>(null);
	const triggerRef = useRef<HTMLElement | null>(null);
	const generatedId = useId();

	const stableId = id ?? generatedId;

	const isFirstRender = useIsFirstRender();
	const previousOpen = usePrevious(open);

	const handleTriggerClick = (event: React.MouseEvent<HTMLElement>) => {
		trigger.props.onClick?.(event);

		onOpenChange(!open);
	};

	useEffect(() => {
		if (!isFirstRender && previousOpen && !open) {
			triggerRef.current?.focus();
		}
	}, [isFirstRender, previousOpen, open]);

	/*
	 * React Compiler cannot statically prove that cloning the trigger element
	 * is safe. That's the reason we're adding the eslint-disable below. This follows
	 * the same pattern as modal/components/Modal.tsx.
	 */

	// eslint-disable-next-line react-compiler/react-compiler
	const clonedTrigger = React.cloneElement(trigger, {
		'aria-controls': stableId,
		'aria-expanded': open,
		'aria-haspopup': 'dialog',
		'onClick': handleTriggerClick,
		'ref': triggerRef,
	});

	return (
		<>
			{clonedTrigger}

			{open && (
				<Overlay
					isKeyboardDismiss
					isOpen={open}
					menuRef={menuRef}
					onClose={() => onOpenChange(false)}
					triggerRef={triggerRef}
				>
					<FloatingPanel
						className={className}
						dialogId={stableId}
						menuRef={menuRef}
						onClose={() => onOpenChange(false)}
						otherProps={otherProps}
					>
						{children}
					</FloatingPanel>
				</Overlay>
			)}
		</>
	);
}
