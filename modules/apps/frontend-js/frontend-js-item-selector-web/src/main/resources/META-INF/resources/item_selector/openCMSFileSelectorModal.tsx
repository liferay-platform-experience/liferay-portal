/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayIcon from '@clayui/icon';
import {ClayVerticalNav} from '@clayui/nav';
import {EConfigInURLBehavior} from '@liferay/frontend-data-set-web';
import {render} from '@liferay/frontend-js-react-web';
import {mimeTypeUtils} from 'frontend-js-web';
import React, {useCallback, useEffect, useId, useMemo, useState} from 'react';

import CMSFileUploaderComponent from '../item_selector_file_uploader/CMSFileUploaderComponent';
import DLFileUploaderComponent from '../item_selector_file_uploader/DLFileUploaderComponent';
import {fetchSites} from '../utils/fetchSites';
import {
	normalizeDLItem,
	normalizeExtensions,
	parseExtensions,
} from '../utils/normalize';
import {buildDLApiURL, buildSpacesApiURL} from '../utils/searchApiURL';
import DetachedCMSFilesItemSelectorModal from './DetachedCMSFilesItemSelectorModal';
import {
	getCMSItemSelectorFilters,
	getCMSItemSelectorGroupedFilters,
} from './getCMSItemSelectorFilters';
import {
	ActiveKey,
	AnyFileItem,
	CMSFile,
	CMSFileItemSelectorModalConfig,
	CMSFileItemSelectorModalProps,
	CMSFileSelectorModalProps,
	DLFile,
	Site,
	SourceSidebarProps,
} from './types';

const lastActiveKeyMap = new Map<string, ActiveKey>();

const CMS_FILE_ITEM_SELECTOR_CONFIG: CMSFileItemSelectorModalConfig = {
	items: [],
	locator: {
		id: 'embedded.id',
		label: 'embedded.title',
		value: 'embedded.id',
	},
	multiSelect: false,
};

const FDS_PROPS: Omit<
	CMSFileItemSelectorModalProps['fdsProps'],
	'filters' | 'id' | 'items'
> = {
	configInURLBehavior: EConfigInURLBehavior.OFF,
	pagination: {
		deltas: [{label: 20}, {label: 40}, {label: 60}],
		initialDelta: 20,
	},
	views: [
		{
			contentRenderer: 'cards',
			label: Liferay.Language.get('cards'),
			name: 'cards',
			schema: {
				description: 'description',
				symbol: '',
				title: 'title',
			},

			setItemComponentProps: ({
				item,
				props: initialProps,
			}: {
				item: AnyFileItem;
				props: object;
			}) => {
				const embedded = item.embedded ?? {};

				const props = {
					...initialProps,
					title:
						(initialProps as {title?: string}).title ||
						item.title ||
						embedded.title ||
						embedded.name ||
						'',
				};

				if (embedded.file) {
					const mimeType = embedded.file.mimeType || '';

					return {
						...props,
						imgProps: {src: embedded.file.thumbnailURL},
						stickerProps: {
							className:
								mimeTypeUtils.getClassNameFromMimeType(
									mimeType
								),
							content: (
								<ClayIcon
									symbol={mimeTypeUtils.getIconFromMimeType(
										mimeType
									)}
								/>
							),
							displayType: 'unstyled',
						},
					};
				}

				if (embedded.videoURL) {
					return {
						...props,
						stickerProps: {
							className: 'file-icon-color-3',
							content: <ClayIcon symbol="document-multimedia" />,
							displayType: 'unstyled',
						},
					};
				}

				if (embedded.contentUrl) {
					const mimeType = embedded.encodingFormat || '';

					return {
						...props,
						imgProps: mimeType.startsWith('image/')
							? {src: embedded.contentUrl}
							: undefined,
						stickerProps: {
							className:
								mimeTypeUtils.getClassNameFromMimeType(
									mimeType
								),
							content: (
								<ClayIcon
									symbol={mimeTypeUtils.getIconFromMimeType(
										mimeType
									)}
								/>
							),
							displayType: 'unstyled',
						},
					};
				}

				return {
					...props,
					stickerProps: {
						className: 'file-icon-color-5',
						displayType: 'unstyled',
					},
				};
			},

			thumbnail: 'cards2',
		},
		{
			contentRenderer: 'table',
			label: Liferay.Language.get('table'),
			name: 'table',
			schema: {
				fields: [
					{
						contentRenderer: 'cmsFilesTitleCellRenderer',
						fieldName: 'title',
						label: Liferay.Language.get('title'),
						sortable: true,
					},
					{
						contentRenderer: 'cmsFilesFallbackCellRenderer',
						fieldName: 'embedded.file.mimeType',
						label: Liferay.Language.get('type'),
						sortable: false,
					},
					{
						contentRenderer: 'cmsFilesFallbackCellRenderer',
						fieldName: 'embedded.file.size',
						label: Liferay.Language.get('size'),
						sortable: false,
					},
					{
						contentRenderer: 'dateTime',
						fieldName: 'dateModified',
						label: Liferay.Language.get('modified-date'),
						sortable: true,
					},
					{
						contentRenderer: 'dateTime',
						fieldName: 'dateCreated',
						label: Liferay.Language.get('create-date'),
						sortable: true,
					},
				],
			},
			thumbnail: 'table',
		},
	],
};

function SourceSidebar({
	activeKey,
	onActiveKeyChange,
	sites,
}: SourceSidebarProps) {
	const sourceItems: Array<{icon: string; key: ActiveKey; label: string}> = [
		{icon: 'box-container', key: 'cms', label: 'Spaces'},
		...sites.map((site) => ({
			icon: 'sites',
			key: `dm-${site.id}` as ActiveKey,
			label: site.name,
		})),
	];

	const items = sourceItems.map((source) => ({
		active: activeKey === source.key,
		label: (
			<>
				<ClayIcon
					className="inline-item inline-item-before"
					symbol={source.icon}
				/>

				{source.label}
			</>
		),
		onClick: () => onActiveKeyChange(source.key),
		textValue: source.label,
	}));

	return (
		<>
			<h3 className="p-2 text-2 text-secondary text-uppercase">
				{Liferay.Language.get('select-from')}
			</h3>

			<ClayVerticalNav
				aria-label={Liferay.Language.get('source')}
				displayType="primary"
				items={items}
				stacked
			/>
		</>
	);
}

function CMSFileSelectorModal({
	allowDragAndDrop,
	allowedExtensions,
	cmsFdsFilters,
	extensionList,
	fdsPropsOverride,
	finalConfig,
	folderMemoryKey,
	groupId,
	itemTypeLabel,
	maxFileSize,
	onSelect,
}: CMSFileSelectorModalProps) {
	const [activeKey, setActiveKey] = useState<ActiveKey>(
		() =>
			(folderMemoryKey && lastActiveKeyMap.get(folderMemoryKey)) || 'cms'
	);
	const [sites, setSites] = useState<Site[]>([]);

	const fdsBaseId = useId();

	const fdsId = `CMSItemSelectorFDS_${fdsBaseId}_${activeKey}`;

	const isSidebarEnabled = Liferay.FeatureFlags['LPD-95652'];

	useEffect(() => {
		if (!isSidebarEnabled) {
			return;
		}

		fetchSites().then(setSites);
	}, [isSidebarEnabled]);

	useEffect(() => {
		if (folderMemoryKey) {
			lastActiveKeyMap.set(folderMemoryKey, activeKey);
		}
	}, [activeKey, folderMemoryKey]);

	const activeSiteId = useMemo(
		() =>
			activeKey.startsWith('dm-')
				? parseInt(activeKey.slice(3), 10)
				: null,
		[activeKey]
	);

	const buildApiURL = useCallback(
		(folderId: number | null) => {
			if (activeSiteId !== null) {
				return buildDLApiURL(extensionList, activeSiteId);
			}

			return buildSpacesApiURL({
				filters: cmsFdsFilters,
				folderId: folderId ?? undefined,
			});
		},
		[activeSiteId, cmsFdsFilters, extensionList]
	);

	const rootLabel = useMemo(
		() =>
			activeSiteId !== null
				? sites.find((site) => site.id === activeSiteId)?.name
				: undefined,
		[activeSiteId, sites]
	);

	const {fdsFilters, fdsGroupedFilters} = useMemo(
		() =>
			activeSiteId !== null
				? {fdsFilters: undefined, fdsGroupedFilters: undefined}
				: {
						fdsFilters: getCMSItemSelectorFilters(
							groupId ?? Liferay.ThemeDisplay.getScopeGroupId()
						),
						fdsGroupedFilters: getCMSItemSelectorGroupedFilters(),
					},
		[activeSiteId, groupId]
	);

	const sidebar = isSidebarEnabled ? (
		<SourceSidebar
			activeKey={activeKey}
			onActiveKeyChange={setActiveKey}
			sites={sites}
		/>
	) : undefined;

	return (
		<DetachedCMSFilesItemSelectorModal
			{...finalConfig}
			activeKey={activeKey}
			allowedExtensions={allowedExtensions}
			buildApiURL={buildApiURL}
			fdsProps={{
				...FDS_PROPS,
				emptyState: allowDragAndDrop
					? {
							description: Liferay.Language.get(
								'drag-and-drop-to-upload'
							),
							image: '/states/cms_empty_state_files.svg',
							title: Liferay.Language.get('no-files-yet'),
						}
					: undefined,
				filters: fdsFilters,
				groupedFilters: fdsGroupedFilters,
				hideManagementBarInEmptyState: true,
				...fdsPropsOverride,
				id: fdsId,
			}}
			filesUploaderComponent={
				allowDragAndDrop
					? activeSiteId !== null
						? DLFileUploaderComponent
						: CMSFileUploaderComponent
					: undefined
			}
			folderMemoryKey={folderMemoryKey}
			groupId={activeSiteId ?? groupId}
			itemTypeLabel={itemTypeLabel ?? Liferay.Language.get('files')}
			locator={finalConfig.locator}
			maxFileSize={maxFileSize}
			onItemsChange={(items) =>
				onSelect(
					activeSiteId !== null
						? (items as unknown as DLFile[]).map(normalizeDLItem)
						: (items as CMSFile[])
				)
			}
			rootLabel={rootLabel}
			sidebar={sidebar}
		/>
	);
}

export default function openCMSFileSelectorModal({
	allowDragAndDrop = false,
	allowedExtensions,
	config,
	fdsProps,
	filters,
	folderMemoryKey,
	groupId,
	itemTypeLabel,
	maxFileSize,
	onSelect,
}: {
	allowDragAndDrop?: boolean;
	allowedExtensions?: string;
	config?: Partial<CMSFileItemSelectorModalConfig>;
	fdsProps?: Partial<CMSFileItemSelectorModalProps['fdsProps']>;
	filters?: string[];

	/**
	 * Stable identifier of the field opening the selector. When provided, the
	 * selector reopens at the folder where this field's previous selection was
	 * made. Each field must pass its own key so the remembered folder is not
	 * shared between fields.
	 */
	folderMemoryKey?: string;
	groupId?: number;
	itemTypeLabel?: string;
	maxFileSize?: number;
	onSelect: (items: Array<CMSFile>) => void;
}) {
	let cmsFdsFilters: string[] = [];
	let extensionList: string[] = [];

	if (filters?.length) {
		cmsFdsFilters = filters;
	}
	else if (allowedExtensions) {
		cmsFdsFilters = [normalizeExtensions(allowedExtensions)];
	}

	if (allowedExtensions) {
		extensionList = parseExtensions(allowedExtensions);
	}

	const finalConfig: CMSFileItemSelectorModalConfig = {
		...CMS_FILE_ITEM_SELECTOR_CONFIG,
		...config,
	};

	return render(
		CMSFileSelectorModal,
		{
			allowDragAndDrop,
			allowedExtensions,
			cmsFdsFilters,
			extensionList,
			fdsPropsOverride: fdsProps,
			finalConfig,
			folderMemoryKey,
			groupId,
			itemTypeLabel,
			maxFileSize,
			onSelect,
		},
		document.createElement('div')
	);
}
