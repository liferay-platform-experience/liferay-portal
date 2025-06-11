/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.cookies.internal.configuration.provider;

import com.liferay.cookies.configuration.CookiesConfigurationProvider;
import com.liferay.cookies.configuration.CookiesPreferenceHandlingConfiguration;
import com.liferay.cookies.configuration.banner.CookiesBannerConfiguration;
import com.liferay.cookies.configuration.consent.CookiesConsentConfiguration;
import com.liferay.cookies.internal.configuration.admin.service.CookiesPreferenceHandlingManagedServiceFactory;
import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.LayoutSet;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.service.LayoutSetLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapDictionary;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;

import java.util.Dictionary;
import java.util.function.Function;
import java.util.function.Supplier;

import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.cm.ManagedServiceFactory;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Daniel Sanz
 */
@Component(service = CookiesConfigurationProvider.class)
public class CookiesConfigurationProviderImpl
	implements CookiesConfigurationProvider {

	@Override
	public CookiesBannerConfiguration getCookiesBannerConfiguration(
			ThemeDisplay themeDisplay)
		throws Exception {

		return _getCookiesConfiguration(
			CookiesBannerConfiguration.class, themeDisplay);
	}

	@Override
	public CookiesConsentConfiguration getCookiesConsentConfiguration(
			ThemeDisplay themeDisplay)
		throws Exception {

		return _getCookiesConfiguration(
			CookiesConsentConfiguration.class, themeDisplay);
	}

	@Override
	public CookiesPreferenceHandlingConfiguration
			getCookiesPreferenceHandlingConfiguration(ThemeDisplay themeDisplay)
		throws Exception {

		return _getCookiesConfiguration(
			CookiesPreferenceHandlingConfiguration.class, themeDisplay);
	}

	@Override
	public boolean isCookiesPreferenceHandlingConfigurationDefined(
			ExtendedObjectClassDefinition.Scope scope, long scopePK)
		throws Exception {

		if (scope == ExtendedObjectClassDefinition.Scope.SYSTEM) {
			try {
				CookiesPreferenceHandlingConfiguration
					cookiesPreferenceHandlingConfiguration =
						_configurationProvider.getSystemConfiguration(
							CookiesPreferenceHandlingConfiguration.class);

				if (cookiesPreferenceHandlingConfiguration != null) {
					return true;
				}
			}
			catch (ConfigurationException configurationException) {
				_log.error(configurationException);

				return false;
			}
		}

		if (_getScopedConfiguration(scope, scopePK) == null) {
			return false;
		}

		return true;
	}

	@Override
	public boolean isCookiesPreferenceHandlingEnabled(
		ExtendedObjectClassDefinition.Scope scope, long scopePK) {

		return _getScopeConfigurationAttribute(
			scope, scopePK, this::_isCompanyCookiesPreferenceHandlingEnabled,
			this::_isGroupCookiesPreferenceHandlingEnabled,
			this::_isSystemCookiesPreferenceHandlingEnabled);
	}

	@Override
	public boolean isCookiesPreferenceHandlingExplicitConsentMode(
		ExtendedObjectClassDefinition.Scope scope, long scopePK) {

		return _getScopeConfigurationAttribute(
			scope, scopePK,
			this::_isCompanyCookiesPreferenceHandlingExplicitConsentMode,
			this::_isGroupCookiesPreferenceHandlingExplicitConsentMode,
			this::_isSystemCookiesPreferenceHandlingExplicitConsentMode);
	}

	@Override
	public void resetCookiesPreferenceHandlingConfiguration(
			ExtendedObjectClassDefinition.Scope scope, long scopePK)
		throws ConfigurationException {

		if (scope == ExtendedObjectClassDefinition.Scope.COMPANY) {
			_configurationProvider.deleteCompanyConfiguration(
				CookiesPreferenceHandlingConfiguration.class, scopePK);
		}
		else if (scope == ExtendedObjectClassDefinition.Scope.GROUP) {
			_configurationProvider.deleteGroupConfiguration(
				CookiesPreferenceHandlingConfiguration.class, scopePK);
		}
		else if (scope == ExtendedObjectClassDefinition.Scope.SYSTEM) {
			_configurationProvider.deleteSystemConfiguration(
				CookiesPreferenceHandlingConfiguration.class);
		}
	}

	@Override
	public void updateCookiesPreferenceHandlingConfiguration(
			boolean enabled, boolean explicitConsentMode,
			ExtendedObjectClassDefinition.Scope scope, long scopePK)
		throws Exception {

		Dictionary<String, Object> dictionary = _createDictionary(
			enabled, explicitConsentMode);

		if (scope == ExtendedObjectClassDefinition.Scope.COMPANY) {
			_configurationProvider.saveCompanyConfiguration(
				CookiesPreferenceHandlingConfiguration.class, scopePK,
				dictionary);
		}
		else if (scope == ExtendedObjectClassDefinition.Scope.GROUP) {
			_configurationProvider.saveGroupConfiguration(
				CookiesPreferenceHandlingConfiguration.class, scopePK,
				dictionary);
		}
		else if (scope == ExtendedObjectClassDefinition.Scope.SYSTEM) {
			_configurationProvider.saveSystemConfiguration(
				CookiesPreferenceHandlingConfiguration.class, dictionary);
		}
		else {
			throw new IllegalArgumentException("Unsupported scope: " + scope);
		}
	}

	private HashMapDictionary<String, Object> _createDictionary(
		boolean enabled, boolean explicitConsentMode) {

		return HashMapDictionaryBuilder.<String, Object>put(
			"enabled", enabled
		).put(
			"explicitConsentMode", explicitConsentMode
		).build();
	}

	private <T> T _getCookiesConfiguration(
			Class<T> clazz, ThemeDisplay themeDisplay)
		throws Exception {

		LayoutSet layoutSet = _layoutSetLocalService.fetchLayoutSet(
			themeDisplay.getServerName());

		if (layoutSet != null) {
			Group group = layoutSet.getGroup();

			return _configurationProvider.getGroupConfiguration(
				clazz, group.getGroupId());
		}

		return _configurationProvider.getCompanyConfiguration(
			clazz, themeDisplay.getCompanyId());
	}

	private <T> T _getScopeConfigurationAttribute(
		ExtendedObjectClassDefinition.Scope scope, long scopePK,
		Function<Long, T> companyFunction, Function<Long, T> groupFunction,
		Supplier<T> systemFunction) {

		if (scope == ExtendedObjectClassDefinition.Scope.COMPANY) {
			return companyFunction.apply(scopePK);
		}
		else if (scope == ExtendedObjectClassDefinition.Scope.GROUP) {
			return groupFunction.apply(scopePK);
		}
		else if (scope == ExtendedObjectClassDefinition.Scope.SYSTEM) {
			return systemFunction.get();
		}

		throw new IllegalArgumentException("Unsupported scope: " + scope);
	}

	private Configuration _getScopedConfiguration(
			ExtendedObjectClassDefinition.Scope scope, long scopePK)
		throws Exception {

		Configuration[] configurations = _configurationAdmin.listConfigurations(
			String.format(
				"(&(service.factoryPid=%s)(%s=%d))",
				CookiesPreferenceHandlingConfiguration.class.getName() +
					".scoped",
				scope.getPropertyKey(), scopePK));

		if (configurations == null) {
			return null;
		}

		return configurations[0];
	}

	private boolean _isCompanyCookiesPreferenceHandlingEnabled(long companyId) {
		if (_cookiesPreferenceHandlingManagedServiceFactory == null) {
			_cookiesPreferenceHandlingManagedServiceFactory =
				(CookiesPreferenceHandlingManagedServiceFactory)
					_managedServiceFactory;
		}

		return _cookiesPreferenceHandlingManagedServiceFactory.
			getCompanyEnabled(companyId);
	}

	private boolean _isCompanyCookiesPreferenceHandlingExplicitConsentMode(
		long companyId) {

		if (_cookiesPreferenceHandlingManagedServiceFactory == null) {
			_cookiesPreferenceHandlingManagedServiceFactory =
				(CookiesPreferenceHandlingManagedServiceFactory)
					_managedServiceFactory;
		}

		return _cookiesPreferenceHandlingManagedServiceFactory.
			getCompanyExplicitConsentMode(companyId);
	}

	private boolean _isGroupCookiesPreferenceHandlingEnabled(long groupId) {
		if (_cookiesPreferenceHandlingManagedServiceFactory == null) {
			_cookiesPreferenceHandlingManagedServiceFactory =
				(CookiesPreferenceHandlingManagedServiceFactory)
					_managedServiceFactory;
		}

		return _cookiesPreferenceHandlingManagedServiceFactory.getGroupEnabled(
			groupId);
	}

	private boolean _isGroupCookiesPreferenceHandlingExplicitConsentMode(
		long groupId) {

		if (_cookiesPreferenceHandlingManagedServiceFactory == null) {
			_cookiesPreferenceHandlingManagedServiceFactory =
				(CookiesPreferenceHandlingManagedServiceFactory)
					_managedServiceFactory;
		}

		return _cookiesPreferenceHandlingManagedServiceFactory.
			getGroupExplicitConsentMode(groupId);
	}

	private boolean _isSystemCookiesPreferenceHandlingEnabled() {
		if (_cookiesPreferenceHandlingManagedServiceFactory == null) {
			_cookiesPreferenceHandlingManagedServiceFactory =
				(CookiesPreferenceHandlingManagedServiceFactory)
					_managedServiceFactory;
		}

		return _cookiesPreferenceHandlingManagedServiceFactory.
			getSystemEnabled();
	}

	private boolean _isSystemCookiesPreferenceHandlingExplicitConsentMode() {
		if (_cookiesPreferenceHandlingManagedServiceFactory == null) {
			_cookiesPreferenceHandlingManagedServiceFactory =
				(CookiesPreferenceHandlingManagedServiceFactory)
					_managedServiceFactory;
		}

		return _cookiesPreferenceHandlingManagedServiceFactory.
			getSystemExplicitConsentMode();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CookiesConfigurationProviderImpl.class);

	@Reference
	private ConfigurationAdmin _configurationAdmin;

	@Reference
	private ConfigurationProvider _configurationProvider;

	private CookiesPreferenceHandlingManagedServiceFactory
		_cookiesPreferenceHandlingManagedServiceFactory;

	@Reference
	private LayoutSetLocalService _layoutSetLocalService;

	@Reference(
		target = "(component.name=com.liferay.cookies.internal.configuration.admin.service.CookiesPreferenceHandlingManagedServiceFactory)"
	)
	private ManagedServiceFactory _managedServiceFactory;

}