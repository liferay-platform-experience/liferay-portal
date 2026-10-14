/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayDropDown, {Align} from '@clayui/drop-down';
import ClayIcon from '@clayui/icon';
import {openConfirmModal, openModal} from 'frontend-js-components-web';
import {sub} from 'frontend-js-web';
import React, {useState} from 'react';

import AddVariationModalContent from './AddVariationModalContent';
import {config} from './config';
import {
	useActiveVariation,
	useAddVariation,
	useDeleteVariation,
	useSelectVariation,
	useVariations,
} from './contexts/StyleBookEditorContext';
import {DEFAULT_VARIATION} from './utils/variations';

export default function VariationsSelector() {
	const activeVariation = useActiveVariation();
	const addVariation = useAddVariation();
	const deleteVariation = useDeleteVariation();
	const selectVariation = useSelectVariation();
	const variations = useVariations();

	const [active, setActive] = useState(false);

	const variationKeys = [
		DEFAULT_VARIATION,
		...Object.keys(variations)
			.filter((key) => key !== DEFAULT_VARIATION)
			.sort(),
	];

	const getLabel = (key) =>
		key === DEFAULT_VARIATION
			? Liferay.Language.get('default')
			: variations[key]?.name || key;

	const openAddVariationModal = () => {
		setActive(false);

		openModal({
			contentComponent: ({closeModal}) => (
				<AddVariationModalContent
					closeModal={closeModal}
					namespace={config.namespace}
					onAdd={(variation) =>
						addVariation(variation).then(closeModal)
					}
					variationKeys={variationKeys}
				/>
			),
		});
	};

	const openDeleteVariationModal = () => {
		setActive(false);

		openConfirmModal({
			message: sub(
				Liferay.Language.get(
					'are-you-sure-you-want-to-delete-the-x-variation'
				),
				getLabel(activeVariation)
			),
			onConfirm: (isConfirmed) => {
				if (isConfirmed) {
					deleteVariation(activeVariation);
				}
			},
		});
	};

	return (
		<ClayDropDown
			active={active}
			alignmentPosition={Align.BottomLeft}
			menuElementAttrs={{
				containerProps: {
					className: 'cadmin',
				},
			}}
			onActiveChange={setActive}
			trigger={
				<ClayButton
					aria-label={Liferay.Language.get('variations')}
					displayType="secondary"
					size="sm"
				>
					<span className="inline-item inline-item-before">
						<ClayIcon symbol="adjust" />
					</span>

					{getLabel(activeVariation)}

					<span className="inline-item inline-item-after">
						<ClayIcon symbol="caret-bottom" />
					</span>
				</ClayButton>
			}
		>
			<ClayDropDown.ItemList>
				{variationKeys.map((key) => (
					<ClayDropDown.Item
						active={key === activeVariation}
						key={key}
						onClick={() => {
							setActive(false);
							selectVariation(key);
						}}
					>
						{getLabel(key)}

						{key !== DEFAULT_VARIATION && (
							<span className="ml-2 text-secondary">{key}</span>
						)}
					</ClayDropDown.Item>
				))}

				<ClayDropDown.Divider />

				<ClayDropDown.Item onClick={openAddVariationModal}>
					<span className="inline-item inline-item-before">
						<ClayIcon symbol="plus" />
					</span>

					{Liferay.Language.get('create-variation')}
				</ClayDropDown.Item>

				{activeVariation !== DEFAULT_VARIATION && (
					<ClayDropDown.Item onClick={openDeleteVariationModal}>
						<span className="inline-item inline-item-before">
							<ClayIcon symbol="trash" />
						</span>

						{Liferay.Language.get('delete-variation')}
					</ClayDropDown.Item>
				)}
			</ClayDropDown.ItemList>
		</ClayDropDown>
	);
}
