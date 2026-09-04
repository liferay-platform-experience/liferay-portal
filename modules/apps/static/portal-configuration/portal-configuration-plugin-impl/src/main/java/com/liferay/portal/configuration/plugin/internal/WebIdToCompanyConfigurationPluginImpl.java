/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.configuration.plugin.internal;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.Dictionary;

import javax.sql.DataSource;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.cm.ConfigurationPlugin;

/**
 * @author Raymond Augé
 */
public class WebIdToCompanyConfigurationPluginImpl
	implements ConfigurationPlugin {

	public WebIdToCompanyConfigurationPluginImpl(BundleContext bundleContext) {
		_bundleContext = bundleContext;
	}

	@Override
	public void modifyConfiguration(
		ServiceReference<?> serviceReference,
		Dictionary<String, Object> properties) {

		String webId = (String)properties.get(
			"dxp.lxc.liferay.com.virtualInstanceId");

		if (Validator.isNull(webId)) {
			webId = (String)properties.get("companyWebId");
		}

		if (Validator.isNull(webId) || (properties.get("companyId") != null)) {
			return;
		}

		try {
			ServiceReference<DataSource> dataSourceServiceReference =
				_bundleContext.getServiceReference(DataSource.class);

			if (dataSourceServiceReference == null) {
				if (_log.isWarnEnabled()) {
					_log.warn("Data source service is null");
				}

				return;
			}

			Long companyId = CompanyIdResolver.getCompanyId(
				_bundleContext.getService(dataSourceServiceReference), webId);

			if (companyId == null) {
				if (_log.isWarnEnabled()) {
					_log.warn("Skip web ID " + webId);
				}

				return;
			}

			properties.put("companyId", companyId);

			if (_log.isInfoEnabled()) {
				_log.info(
					StringBundler.concat(
						"Injected company ID ", companyId, " for web ID ",
						webId));
			}
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			if (_log.isWarnEnabled()) {
				_log.warn("Skip web ID " + webId);
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		WebIdToCompanyConfigurationPluginImpl.class);

	private final BundleContext _bundleContext;

}