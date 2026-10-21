/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

declare module Liferay {
	namespace Tour {
		export function start(tourDefinition: {
			closeable?: boolean;
			dismissable?: boolean;
			lockScroll?: boolean;
			onClose?: (reason: 'closed' | 'dismissed' | 'finished') => void;
			pauseable?: boolean;
			steps: Array<{
				content: string;
				positioning?:
					| 'bottom'
					| 'bottom-left'
					| 'bottom-right'
					| 'left'
					| 'left-bottom'
					| 'left-top'
					| 'right'
					| 'right-bottom'
					| 'right-top'
					| 'top'
					| 'top-left'
					| 'top-right';
				selector: string;
				title: string;
			}>;
		}): Promise<void>;
	}
}