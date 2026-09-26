/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayIcon from '@clayui/icon';
import ClayLayout from '@clayui/layout';
import {Keys} from '@clayui/shared';
import ClaySticker from '@clayui/sticker';
import React from 'react';

import ClayCard from './Card';
import {CardType, ClayCardNavigation} from './CardNavigation';

interface IAspectRatioProps {

	/**
	 * Props for the aspect ratio element wrapping the card's content slot.
	 */
	container?: Omit<
		React.ComponentProps<typeof ClayCard.AspectRatio>,
		'children'
	>;

	/**
	 * Props for the aspect ratio item element holding the card's `children`.
	 */
	item?: React.HTMLAttributes<HTMLSpanElement>;
}

interface IProps
	extends React.BaseHTMLAttributes<HTMLAnchorElement | HTMLDivElement> {

	/**
	 * Props for the aspect ratio element and the aspect ratio item element.
	 */
	aspectRatioProps?: IAspectRatioProps;

	/**
	 * Determines which card type classes are emitted and which aspect ratio
	 * defaults apply.
	 */
	cardType?: CardType;

	children?: React.ReactNode;

	/**
	 * Value displayed that describes the card
	 */
	description?: React.ReactText;

	/**
	 * Flag to indicate if card should be the `horizontal` variant
	 */
	horizontal?: boolean;

	/**
	 * Icon to display when card is `horizontal`
	 */
	horizontalSymbol?: string;

	/**
	 * Path or url for click through
	 */
	href?: string;

	/**
	 * Callback for when card is clicked on
	 */
	onClick?: (event: React.MouseEvent | React.KeyboardEvent) => void;

	/**
	 * Callback for when a keyboard key pressed on a card
	 */
	onKeyDown?: (event: React.KeyboardEvent) => void;

	/**
	 * Path to spritemap for icon symbol.
	 */
	spritemap?: string;

	/**
	 * Title for bottom-left icon.
	 */
	stickerTitle?: string;

	/**
	 * Value displayed for the card's title
	 */
	title?: string;
}

const DEFAULT_ASPECT_RATIO_PROPS: Record<
	'navigation' | 'template',
	IAspectRatioProps
> = {
	navigation: {
		container: {
			containerAspectRatio: '16/9',
		},
		item: {
			className: 'aspect-ratio-item aspect-ratio-item-top-right',
		},
	},
	template: {
		container: {},
		item: {
			className:
				'aspect-ratio-item aspect-ratio-item-center-middle aspect-ratio-item-flush',
		},
	},
};

function noop() {}

export function ClayCardWithNavigation({
	'aria-label': ariaLabel,
	'aspectRatioProps': externalAspectRatioProps,
	cardType = 'template',
	children,
	description,
	horizontal = false,
	horizontalSymbol = '',
	href,
	onClick,
	onKeyDown = noop,
	spritemap,
	stickerTitle,
	title,
	...otherProps
}: IProps) {
	const defaultAspectRatioProps =
		DEFAULT_ASPECT_RATIO_PROPS[
			cardType === 'template' ? 'template' : 'navigation'
		];

	return (
		<ClayCardNavigation
			{...otherProps}
			cardType={cardType}
			horizontal={horizontal}
			href={href}
			onClick={onClick}
			onKeyDown={(event: React.KeyboardEvent) => {
				if (
					(!href && event.key === Keys.Enter) ||
					(onClick && event.key === Keys.Spacebar)
				) {
					event.preventDefault();
					if (onClick) {
						onClick(event);
					}
				}
				onKeyDown(event);
			}}
			tabIndex={0}
		>
			{!horizontal && (
				<ClayCard.AspectRatio
					{...defaultAspectRatioProps.container}
					{...externalAspectRatioProps?.container}
				>
					<span
						{...defaultAspectRatioProps.item}
						{...externalAspectRatioProps?.item}
					>
						{children}
					</span>
				</ClayCard.AspectRatio>
			)}

			{(horizontal || title || description) && (
				<ClayCard.Body>
					{!horizontal && (
						<>
							{title && (
								<ClayCard.Description
									aria-label={title}
									displayType="title"
									truncate
								>
									{title}
								</ClayCard.Description>
							)}

							{description && (
								<ClayCard.Description
									displayType="subtitle"
									truncate
								>
									{description}
								</ClayCard.Description>
							)}
						</>
					)}

					{horizontal && (
						<ClayCard.Row>
							<ClayLayout.ContentCol>
								<ClaySticker inline title={stickerTitle}>
									<ClayIcon
										spritemap={spritemap}
										symbol={horizontalSymbol}
									/>
								</ClaySticker>
							</ClayLayout.ContentCol>

							{title && (
								<ClayLayout.ContentCol expand>
									<ClayLayout.ContentSection>
										<ClayCard.Description
											aria-label={ariaLabel ?? title}
											displayType="title"
											truncate
										>
											{title}
										</ClayCard.Description>
									</ClayLayout.ContentSection>
								</ClayLayout.ContentCol>
							)}
						</ClayCard.Row>
					)}
				</ClayCard.Body>
			)}
		</ClayCardNavigation>
	);
}
