/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.client.extension.type.internal.manager.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.client.extension.constants.ClientExtensionEntryConstants;
import com.liferay.client.extension.type.CET;
import com.liferay.client.extension.type.configuration.CETConfiguration;
import com.liferay.client.extension.type.manager.CETManager;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.CompanyTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.vulcan.pagination.Pagination;

import java.util.List;
import java.util.Objects;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Thiago Buarque
 */
@RunWith(Arquillian.class)
public class CETManagerTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_company = CompanyTestUtil.addCompany();
	}

	@Test
	public void testGetCETs() throws PortalException {
		CET cet1 = _addGlobalJSCET("instance");
		CET cet2 = _addGlobalJSCET(null);

		try {
			List<CET> cets = _cetManager.getCETs(
				_company.getCompanyId(),
				ClientExtensionEntryConstants.TYPE_GLOBAL_JS);

			Assert.assertEquals(cets.toString(), 2, cets.size());

			for (CET cet : cets) {
				String name = cet.getName();

				Assert.assertTrue(
					Objects.equals(name, cet1.getName()) ||
					Objects.equals(name, cet2.getName()));
			}

			cets = _cetManager.getCETs(
				_company.getCompanyId(), true, null,
				ClientExtensionEntryConstants.TYPE_GLOBAL_JS,
				Pagination.of(QueryUtil.ALL_POS, QueryUtil.ALL_POS), null);

			Assert.assertEquals(cets.toString(), 1, cets.size());

			CET cet3 = cets.get(0);

			Assert.assertEquals(cet2.getName(), cet3.getName());
		}
		finally {
			_cetManager.deleteCET(cet1);
			_cetManager.deleteCET(cet2);
		}
	}

	private CET _addCET(String type, String typeSettings)
		throws PortalException {

		String projectName = RandomTestUtil.randomString();

		CETConfiguration cetConfiguration = ConfigurableUtil.createConfigurable(
			CETConfiguration.class,
			HashMapBuilder.<String, Object>put(
				"baseURL", "${portalURL}/o/" + projectName
			).put(
				"name", RandomTestUtil.randomString()
			).put(
				"project-id", RandomTestUtil.randomString()
			).put(
				"project-name", projectName
			).put(
				"properties", new String[0]
			).put(
				"service.pid",
				"com.liferay.client.extension.type.configuration." +
					"CETConfiguration~" + projectName
			).put(
				"type", type
			).put(
				"typeSettings", typeSettings
			).put(
				"webContextPath", "/" + projectName
			).build());

		return _cetManager.addCET(
			cetConfiguration, _company.getCompanyId(),
			RandomTestUtil.randomString());
	}

	private CET _addGlobalJSCET(String scope) throws PortalException {
		StringBuilder typeSettings = new StringBuilder();

		if (Validator.isNotNull(scope)) {
			typeSettings.append("scope=");
			typeSettings.append(scope);
		}

		return _addCET(
			ClientExtensionEntryConstants.TYPE_GLOBAL_JS,
			typeSettings.toString());
	}

	@Inject
	private CETManager _cetManager;

	private Company _company;

}