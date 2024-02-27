/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayButtonWithIcon} from '@clayui/button';
import {Option, Picker} from '@clayui/core';
import ClayForm, {ClayInput} from '@clayui/form';
import ClayIcon from '@clayui/icon';
import ClayLayout from '@clayui/layout';
import PropTypes from 'prop-types';
import React, {useCallback, useEffect, useState} from 'react';
import {v4 as uuidv4} from 'uuid';

const booleanValue = [
	{label: 'True', value: true},
	{label: 'False', value: false},
];

const dataTypes = ['String', 'Boolean'];

const AttributeFields = ({
	attribute = '',
	handleAddClick,
	handleRemoveClick,
	id,
	index,
	portletNamespace,
	updateAttributeList,
	value = '',
}) => {
	const [attrErrorMessage, setAttrErrorMessage] = useState<string>('');
	const [attributeName, setAttributeName] = useState<string>(attribute);
	const [attributeType, setAttributeType] = useState<string>(dataTypes[0]);
	const [attributeValue, setAttributeValue] = useState<boolean | string>(
		value
	);

	const handleAttributeChange = ({target}) => {
		const newValue = target.value;
		setAttributeName(newValue);

		if (newValue.toLowerCase().includes('src')) {
			setAttrErrorMessage(
				Liferay.Language.get('use-the-javaScript-url-field')
			);
		}
		else {
			setAttrErrorMessage('');
		}
	};

	useEffect(() => {
		updateAttributeList(index, {
			attribute: attributeName,
			id,
			type: attributeType,
			value: attributeValue,
		});
	}, [
		attributeName,
		attributeType,
		attributeValue,
		id,
		index,
		updateAttributeList,
	]);

	useEffect(() => {
		if (attributeType === 'String') {
			setAttributeValue('');
		}
		if (attributeType === 'Boolean') {
			setAttributeValue(booleanValue[0].value);
		}
	}, [attributeType]);

	return (
		<ClayLayout.Row className="mb-3">
			<ClayLayout.Col
				className={attrErrorMessage ? 'has-error' : ''}
				size={4}
			>
				<label htmlFor="attribute">
					{Liferay.Language.get('attribute')}

					<span className="inline-item-after reference-mark text-warning">
						<ClayIcon symbol="asterisk" />
					</span>
				</label>

				<ClayInput
					defaultValue={attributeName}
					id="attribute"
					name={`${portletNamespace}attribute_${index}`}
					onChange={handleAttributeChange}
					type="text"
				/>

				{attrErrorMessage && (
					<ClayForm.FeedbackGroup>
						<ClayForm.FeedbackItem>
							<ClayForm.FeedbackIndicator symbol="exclamation-full" />

							{attrErrorMessage}
						</ClayForm.FeedbackItem>
					</ClayForm.FeedbackGroup>
				)}
			</ClayLayout.Col>

			<ClayLayout.Col size={4}>
				<label htmlFor="type">{Liferay.Language.get('type')}</label>

				<Picker
					aria-labelledby="picker-label"
					id="type"
					items={dataTypes}
					onSelectionChange={(type) => {
						if (type === 'Boolean') {
							setAttributeValue(attributeValue);
						}

						setAttributeType(type);
					}}
					selectedKey={attributeType}
				>
					{(item) => <Option key={item}>{item}</Option>}
				</Picker>
			</ClayLayout.Col>

			<ClayLayout.Col size={4}>
				<div className="d-flex justify-content-between">
					<label htmlFor="value">
						{Liferay.Language.get('value')}
					</label>

					<div>
						{index > 0 && (
							<ClayButtonWithIcon
								aria-label={Liferay.Language.get('remove')}
								className="btn btn-primary btn-xs dm-field-repeatable-delete-button rounded-pill"
								onClick={() => handleRemoveClick(index)}
								symbol="hr"
								title={Liferay.Language.get('remove')}
								type="button"
							/>
						)}

						<ClayButtonWithIcon
							aria-label={Liferay.Language.get('add')}
							className="btn btn-primary btn-xs dm-field-repeatable-add-button ml-1 rounded-pill"
							onClick={() =>
								attributeName
									? handleAddClick(index)
									: setAttrErrorMessage(
											Liferay.Language.get(
												'attribute-field-is-required'
											)
									  )
							}
							symbol="plus"
							title={Liferay.Language.get('add')}
							type="button"
						/>
					</div>
				</div>

				{attributeType !== 'Boolean' ? (
					<ClayInput
						id="value"
						name={`${portletNamespace}value${index}`}
						onChange={({target}) => {
							const newValue = target.value;
							setAttributeValue(newValue);
						}}
						type="text"
					/>
				) : (
					<Picker
						aria-labelledby="picker-label"
						id="boolean"
						items={booleanValue}
						onSelectionChange={(value) => setAttributeValue(value)}
						selectedKey={attributeValue.toString()}
					>
						{(item) => (
							<Option key={item.value}>{item.label}</Option>
						)}
					</Picker>
				)}
			</ClayLayout.Col>
		</ClayLayout.Row>
	);
};

const ScriptElementAttributes = ({
	attributesListUpdated: initialAttributesList,
	portletNamespace,
}) => {
	const emptyRow = () => ({attribute: '', id: uuidv4(), type: '', value: ''});

	const [attributesList, settAtributesList] = useState(
		initialAttributesList && !!initialAttributesList.length
			? [initialAttributesList.map((item) => ({...item, id: uuidv4()}))]
			: [emptyRow()]
	);

	const addRow = (index) => {
		const tempList = [...attributesList];
		tempList.splice(index + 1, 0, emptyRow());
		settAtributesList(tempList);
	};

	const removeRow = (index) => {
		const tempList = [...attributesList];
		tempList.splice(index, 1);
		settAtributesList(tempList);
	};

	const updateAttributeList = useCallback((index, updatedValue) => {
		const updatedString = {
			attribute: updatedValue.attribute,
			id: updatedValue.id,
			type: updatedValue.type,
			value: updatedValue.value,
		};

		settAtributesList((prevList) => {
			const newList = [...prevList];
			newList[index] = updatedString;

			return newList;
		});
	}, []);

	const filteredList = attributesList
		.map(
			({attribute, type, value}) =>
				`${attribute}=${type === 'String' ? `"${value}"` : value}`
		)
		.join('\n');

	return (
		<>
			<input
				name={`${portletNamespace}scriptElementAttributes`}
				type="hidden"
				value={filteredList}
			></input>

			{attributesList.map((item, index) => (
				<AttributeFields
					attribute={item.attribute}
					handleAddClick={addRow}
					handleRemoveClick={removeRow}
					id={item.id}
					index={index}
					key={item.id}
					portletNamespace={portletNamespace}
					type={item.type}
					updateAttributeList={updateAttributeList}
					value={item.value}
				/>
			))}
		</>
	);
};

ScriptElementAttributes.propTypes = {
	attributesList: PropTypes.arrayOf(
		PropTypes.shape({
			attribute: PropTypes.string,
			value: PropTypes.string,
		})
	),
	portletNamespace: PropTypes.string.isRequired,
};

export default ScriptElementAttributes;
