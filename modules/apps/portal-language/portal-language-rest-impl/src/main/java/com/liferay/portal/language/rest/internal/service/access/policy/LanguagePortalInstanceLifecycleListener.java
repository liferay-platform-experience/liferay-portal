/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.language.rest.internal.service.access.policy;

import com.liferay.portal.instance.lifecycle.BasePortalInstanceLifecycleListener;
import com.liferay.portal.instance.lifecycle.PortalInstanceLifecycleListener;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.resource.bundle.ResourceBundleLoaderUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.security.service.access.policy.model.SAPEntry;
import com.liferay.portal.security.service.access.policy.service.SAPEntryLocalService;

import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Raymond Augé
 */
@Component(service = PortalInstanceLifecycleListener.class)
public class LanguagePortalInstanceLifecycleListener
	extends BasePortalInstanceLifecycleListener {

	@Override
	public void portalInstanceRegistered(Company company) throws Exception {
		try {
			_addSAPEntries(company.getCompanyId());
		}
		catch (PortalException portalException) {
			_log.error(
				"Unable to add service access policy entry for company " +
					company.getCompanyId(),
				portalException);
		}
	}

	private void _addSAPEntries(long companyId) throws PortalException {
		for (String[] sapEntryObjectArray : _SAP_ENTRY_OBJECT_ARRAYS) {
			String name = sapEntryObjectArray[0];

			SAPEntry sapEntry = _sapEntryLocalService.fetchSAPEntry(
				companyId, name);

			if (sapEntry != null) {
				continue;
			}

			String allowedServiceSignatures = sapEntryObjectArray[1];
			Map<Locale, String> map = ResourceBundleUtil.getLocalizationMap(
				ResourceBundleLoaderUtil.getPortalResourceBundleLoader(), name);

			_sapEntryLocalService.addSAPEntry(
				_userLocalService.getGuestUserId(companyId),
				allowedServiceSignatures, false, true, name, map,
				new ServiceContext());
		}
	}

	private static final String[][] _SAP_ENTRY_OBJECT_ARRAYS = {
		{
			"LANGUAGE_MESSAGE_EXPORT",
			"com.liferay.portal.language.rest.internal.resource.v1_0." +
				"MessageResourceImpl#postMessagesExportPage"
		}
	};

	private static final Log _log = LogFactoryUtil.getLog(
		LanguagePortalInstanceLifecycleListener.class);

	@Reference
	private SAPEntryLocalService _sapEntryLocalService;

	@Reference
	private UserLocalService _userLocalService;

}