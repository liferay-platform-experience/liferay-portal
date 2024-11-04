/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.feature.flag.web.internal.upgrade.v1_0_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.feature.flag.web.internal.feature.flag.FeatureFlagsBag;
import com.liferay.feature.flag.web.internal.feature.flag.FeatureFlagsBagProvider;
import com.liferay.portal.kernel.feature.flag.FeatureFlag;
import com.liferay.portal.kernel.feature.flag.FeatureFlagType;
import com.liferay.portal.kernel.feature.flag.constants.FeatureFlagConstants;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.portlet.PortalPreferences;
import com.liferay.portal.kernel.service.PortalPreferencesLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.CompanyTestUtil;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;
import com.liferay.portal.upgrade.test.util.UpgradeTestUtil;
import com.liferay.portlet.PortalPreferencesWrapper;

import java.util.List;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Thiago Buarque
 */
@RunWith(Arquillian.class)
public class FeatureFlagUpgradeProcessTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testDeprecationFeatureFlagPortalPreference() throws Exception {
		Company company = CompanyTestUtil.addCompany();

		PortalPreferences portalPreferences = _getPortalPreferences(
			company.getCompanyId());

		portalPreferences.setValue(
			FeatureFlagConstants.PREFERENCE_NAMESPACE,
			FeatureFlagConstants.PREFERENCE_DEPRECATION_PROCESSED_KEY, "false");

		_portalPreferencesLocalService.updatePreferences(
			company.getCompanyId(), PortletKeys.PREFS_OWNER_TYPE_COMPANY,
			portalPreferences);

		_runUpgrade();

		FeatureFlagsBag featureFlagsBag =
			_featureFlagsBagProvider.getOrCreateFeatureFlagsBag(
				company.getCompanyId());

		List<FeatureFlag> deprecationFeatureFlags =
			featureFlagsBag.getFeatureFlags(
				FeatureFlagType.DEPRECATION.getPredicate());

		for (FeatureFlag deprecationFeatureFlag : deprecationFeatureFlags) {
			Assert.assertFalse(deprecationFeatureFlag.isEnabled());
		}

		portalPreferences = _getPortalPreferences(company.getCompanyId());

		Assert.assertTrue(
			GetterUtil.getBoolean(
				portalPreferences.getValue(
					FeatureFlagConstants.PREFERENCE_NAMESPACE,
					FeatureFlagConstants.
						PREFERENCE_DEPRECATION_PROCESSED_KEY)));
	}

	private PortalPreferences _getPortalPreferences(long companyId) {
		PortalPreferencesWrapper portalPreferencesWrapper =
			(PortalPreferencesWrapper)
				_portalPreferencesLocalService.getPreferences(
					companyId, PortletKeys.PREFS_OWNER_TYPE_COMPANY);

		return portalPreferencesWrapper.getPortalPreferencesImpl();
	}

	private void _runUpgrade() throws Exception {
		UpgradeProcess upgradeProcess = UpgradeTestUtil.getUpgradeStep(
			_upgradeStepRegistrator, _CLASS_NAME);

		upgradeProcess.upgrade();
	}

	private static final String _CLASS_NAME =
		"com.liferay.feature.flag.web.internal.upgrade.v1_0_0." +
			"FeatureFlagUpgradeProcess";

	@Inject(
		filter = "(&(component.name=com.liferay.feature.flag.web.internal.upgrade.registry.FeatureFlagUpgradeStepRegistrator))"
	)
	private static UpgradeStepRegistrator _upgradeStepRegistrator;

	@Inject
	private FeatureFlagsBagProvider _featureFlagsBagProvider;

	@Inject
	private PortalPreferencesLocalService _portalPreferencesLocalService;

}