/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.configuration.internal.resource.v1_0;

import com.liferay.headless.admin.configuration.dto.v1_0.Configuration;
import com.liferay.headless.admin.configuration.resource.v1_0.ConfigurationResource;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.vulcan.pagination.Page;

import jakarta.ws.rs.NotFoundException;

import java.util.ArrayList;
import java.util.List;

import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Thiago Buarque
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/configuration.properties",
	scope = ServiceScope.PROTOTYPE, service = ConfigurationResource.class
)
public class ConfigurationResourceImpl extends BaseConfigurationResourceImpl {

	public Page<Configuration> getSiteConfigurationsPage(
			String siteExternalReferenceCode)
		throws Exception {

		Group group = _groupLocalService.fetchGroupByExternalReferenceCode(
			siteExternalReferenceCode, contextCompany.getCompanyId());

		if ((group == null) || !group.isSite()) {
			throw new NotFoundException(
				"Unable to find site with external reference code " +
					siteExternalReferenceCode);
		}

		org.osgi.service.cm.Configuration[] configurations =
			_configurationAdmin.listConfigurations(
				"(groupId=" + group.getGroupId() + ")");

		List<Configuration> configurationsList = new ArrayList<>();

		for (org.osgi.service.cm.Configuration configuration : configurations) {
			Configuration configuration2 = new Configuration();

			configuration2.setProperties(
				() -> HashMapBuilder.putAll(
					configuration.getProperties()
				).build());
			configuration2.setExternalReferenceCode(configuration::getPid);

			configurationsList.add(configuration2);
		}

		return Page.of(configurationsList);
	}

	public Configuration putSiteConfiguration(
			String siteExternalReferenceCode,
			String configurationExternalReferenceCode,
			Configuration configuration)
		throws Exception {

		return new Configuration();
	}

	@Reference
	private ConfigurationAdmin _configurationAdmin;

	@Reference
	private GroupLocalService _groupLocalService;

}