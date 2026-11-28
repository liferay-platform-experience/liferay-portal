/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {IItemSelectorModalProps} from './ItemSelectorModal';

export enum EEntityFieldType {
	COLLECTION = 'collection',
	COLLECTION_INTEGER = 'collection-integer',
	COLLECTION_STRING = 'collection-string',
	DATE = 'date',
	DATE_TIME = 'date-time',
	INTEGER = 'integer',
	STRING = 'string',
}

export interface IBaseFilterConfig {
	entityFieldType: EEntityFieldType | `${EEntityFieldType}`;
	id: string;
	label: string;
	type: 'selection' | 'dateRange' | 'clientExtension';
}

export interface ISelectionFilterConfig extends IBaseFilterConfig {
	apiURL?: string;
	autocompleteEnabled?: boolean;
	itemKey?: string;
	itemLabel?: string;
	items?: Array<{label: string; value: string}>;
	multiple?: boolean;
	type: 'selection';
}

export interface IDateRangeFilterConfig extends IBaseFilterConfig {
	type: 'dateRange';
}

export type TFilterConfig = ISelectionFilterConfig | IDateRangeFilterConfig;

export interface IGroupedFilterConfig {
	filters: string[];
	label: string;
}

export type TDetachedItemSelectorModal<T> = Omit<
	IItemSelectorModalProps<T>,
	'observer' | 'onOpenChange' | 'open'
>;

export interface CMSFile {
	description: string;
	embedded: {
		file?: {
			link?: {
				href?: string;
			};
			mimeType: string;
			thumbnailURL: string;
		};
		id: number;
		title: string;
		videoURL?: string;
	};
	title: string;
}

export interface DLFile {
	embedded: {
		contentUrl?: string;
		encodingFormat?: string;
		id: number;
		name?: string;
		title?: string;
	};
	title: string;
}

export type AnyFileItem = {
	embedded: {
		contentUrl?: string;
		encodingFormat?: string;
		file?: {
			link?: {href?: string};
			mimeType: string;
			thumbnailURL: string;
		};
		id: number;
		name?: string;
		title?: string;
		videoURL?: string;
	};
	title: string;
};

export type Site = {id: number; kind: 'global' | 'site'; name: string};

export type ActiveKey = 'cms' | `dm-${number}`;

export type CMSFileItemSelectorModalProps = IItemSelectorModalProps<CMSFile>;

export type CMSFileItemSelectorModalConfig = {
	items: CMSFileItemSelectorModalProps['items'];
	locator: CMSFileItemSelectorModalProps['locator'];
	multiSelect: CMSFileItemSelectorModalProps['multiSelect'];
};

export type SourceSidebarProps = {
	activeKey: ActiveKey;
	onActiveKeyChange: (key: ActiveKey) => void;
	sites: Site[];
};

export type CMSFileSelectorModalProps = {
	allowDragAndDrop: boolean;
	allowedExtensions?: string;
	cmsFdsFilters: string[];
	extensionList: string[];
	fdsPropsOverride?: Partial<CMSFileItemSelectorModalProps['fdsProps']>;
	finalConfig: CMSFileItemSelectorModalConfig;
	folderMemoryKey?: string;
	groupId?: number;
	itemTypeLabel?: string;
	maxFileSize?: number;
	onSelect: (items: Array<CMSFile>) => void;
};
