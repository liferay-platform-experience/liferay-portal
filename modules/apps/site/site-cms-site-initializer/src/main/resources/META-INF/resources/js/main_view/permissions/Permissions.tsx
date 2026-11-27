/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayLayout from '@clayui/layout';
import {ClayVerticalNav} from '@clayui/nav';
import {navigate} from 'frontend-js-web';
import React, {useState} from 'react';

import Toolbar from '../../common/components/Toolbar';
import {
	DEPOT_ENTRY_CLASS_NAME,
	OBJECT_ENTRY_FOLDER_CLASS_NAME,
} from '../../common/utils/constants';

import './Permissions.scss';

type DefaultsSection = 'contents' | 'files' | 'subfolders';

interface PermissionsProps {
	backURL: string;
	className: string;
	classPK: number;
	defaultsSections: DefaultsSection[];
}

const DEFAULTS_SECTION_LABELS: Record<DefaultsSection, string> = {
	contents: Liferay.Language.get('contents'),
	files: Liferay.Language.get('files'),
	subfolders: Liferay.Language.get('subfolders'),
};

const OWN_PERMISSIONS = 'own-permissions';

type SectionType = DefaultsSection | typeof OWN_PERMISSIONS;

function _getSectionLabel(className: string, section: SectionType) {
	if (section !== OWN_PERMISSIONS) {
		return DEFAULTS_SECTION_LABELS[section];
	}

	if (className === OBJECT_ENTRY_FOLDER_CLASS_NAME) {
		return Liferay.Language.get('this-folder');
	}

	if (className === DEPOT_ENTRY_CLASS_NAME) {
		return Liferay.Language.get('this-space');
	}

	return Liferay.Language.get('permissions');
}

export default function Permissions({
	backURL,
	className,
	defaultsSections,
}: PermissionsProps) {
	const [activeSection, setActiveSection] =
		useState<SectionType>(OWN_PERMISSIONS);

	const sections: SectionType[] = [OWN_PERMISSIONS, ...defaultsSections];

	return (
		<>
			<Toolbar
				backURL={backURL}
				title={Liferay.Language.get('permissions')}
			>
				<Toolbar.Item>
					<ClayButton
						borderless
						displayType="secondary"
						onClick={() => navigate(backURL)}
						size="sm"
					>
						{Liferay.Language.get('cancel')}
					</ClayButton>
				</Toolbar.Item>
			</Toolbar>

			<ClayLayout.ContainerFluid size={false}>
				<ClayLayout.Row className="min-vh-100">
					{defaultsSections.length ? (
						<ClayLayout.Col
							className="cms-sidebar-nav sidebar-layout"
							md="auto"
							sm={12}
						>
							<div className="px-md-2 py-3 py-md-4">
								<ClayVerticalNav
									items={sections.map((section) => ({
										active: activeSection === section,
										label: _getSectionLabel(
											className,
											section
										),
										onClick: () => {
											setActiveSection(section);
										},
									}))}
								/>
							</div>
						</ClayLayout.Col>
					) : (
						<div className="min-vh-100 py-4" />
					)}

					<ClayLayout.Col className="col-md px-4 py-4" sm={12}>
						<ClayLayout.ContainerFluid size="xxl">
							<h2 className="text-5">
								{_getSectionLabel(className, activeSection)}
							</h2>
						</ClayLayout.ContainerFluid>
					</ClayLayout.Col>

					<aside className="cms-permissions-aside">
						<p>{Liferay.Language.get('unsaved-changes')}</p>
					</aside>
				</ClayLayout.Row>
			</ClayLayout.ContainerFluid>
		</>
	);
}
