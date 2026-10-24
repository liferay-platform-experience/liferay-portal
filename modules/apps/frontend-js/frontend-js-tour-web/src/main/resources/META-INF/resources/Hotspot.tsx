/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {doAlign, observeRect} from '@clayui/shared';
import {ReactPortal} from '@liferay/frontend-js-react-web';
import React, {useEffect, useRef, useState} from 'react';

import './Hotspot.scss';

type Props = {
	element: HTMLElement;
	onClick: () => void;
};

/**
 * Marks the element of the step a paused tour stopped at, so the user can
 * resume the tour from there.
 */
export default function Hotspot({element, onClick}: Props) {
	const hotspotRef = useRef<HTMLButtonElement>(null);

	// The hotspot is rendered right before the element it marks, so keyboard
	// users reach it where the element is in the tab order instead of at the
	// end of the page. The anchor takes no space of its own.

	const [anchor] = useState(() => {
		const anchor = document.createElement('span');

		anchor.className = 'lfr-tour-hotspot-anchor';

		return anchor;
	});

	useEffect(() => {
		element.before(anchor);

		return () => anchor.remove();
	}, [anchor, element]);

	useEffect(() => {
		const hotspot = hotspotRef.current;

		if (!hotspot) {
			return;
		}

		hotspot.focus();

		// Centered on the top left corner of the element, like a badge, so it
		// does not cover what it marks. The overflow adjustment keeps it in
		// the viewport when the element touches an edge.

		return observeRect(element, () =>
			doAlign({
				overflow: {adjustX: true, adjustY: true},
				points: ['cc', 'tl'],
				sourceElement: hotspot,
				targetElement: element,
			})
		);
	}, [element]);

	return (
		<ReactPortal container={anchor} wrapper={false}>
			<button
				aria-label={Liferay.Language.get('resume-the-tour')}
				className="lfr-tour-hotspot"
				onClick={onClick}
				ref={hotspotRef}
				type="button"
			>
				<span className="lfr-tour-hotspot-inner" />
			</button>
		</ReactPortal>
	);
}
