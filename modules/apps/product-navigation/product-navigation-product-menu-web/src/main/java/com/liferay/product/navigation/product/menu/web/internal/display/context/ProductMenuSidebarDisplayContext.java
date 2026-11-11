/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.product.navigation.product.menu.web.internal.display.context;

import com.liferay.application.list.GroupProvider;
import com.liferay.application.list.PanelApp;
import com.liferay.application.list.PanelAppRegistry;
import com.liferay.application.list.PanelCategory;
import com.liferay.application.list.constants.ApplicationListWebKeys;
import com.liferay.application.list.constants.PanelCategoryKeys;
import com.liferay.application.list.display.context.logic.PanelCategoryHelper;
import com.liferay.application.list.util.PanelCategoryRegistryUtil;
import com.liferay.exportimport.kernel.staging.StagingUtil;
import com.liferay.item.selector.ItemSelector;
import com.liferay.item.selector.criteria.URLItemSelectorReturnType;
import com.liferay.layout.admin.constants.LayoutAdminPortletKeys;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.impl.VirtualLayout;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.portlet.ControlPanelEntry;
import com.liferay.portal.kernel.portlet.LiferayPortletURL;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.url.builder.ResourceURLBuilder;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.service.LayoutLocalServiceUtil;
import com.liferay.portal.kernel.service.PortletLocalServiceUtil;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.service.permission.GroupPermissionUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.SessionClicks;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.product.navigation.product.menu.constants.ProductNavigationProductMenuPortletKeys;
import com.liferay.site.item.selector.SiteItemSelectorCriterion;
import com.liferay.site.manager.RecentGroupManager;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletURL;
import jakarta.portlet.ResourceURL;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author Mario Leandro
 */
public class ProductMenuSidebarDisplayContext {

	public ProductMenuSidebarDisplayContext(
		HttpServletRequest httpServletRequest) {

		_httpServletRequest = httpServletRequest;

		_panelAppRegistry = (PanelAppRegistry)httpServletRequest.getAttribute(
			ApplicationListWebKeys.PANEL_APP_REGISTRY);

		_panelCategoryHelper = new PanelCategoryHelper(_panelAppRegistry);

		_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		_panelCategory = PanelCategoryRegistryUtil.getPanelCategory(
			PanelCategoryKeys.SITE_ADMINISTRATION);

		_updateLatentGroup();
	}

	public Map<String, Object> getProps() throws Exception {
		if (_panelCategory == null) {
			return Collections.emptyMap();
		}

		String itemSelectedEventName = String.format(
			"_%s_selectSite",
			ProductNavigationProductMenuPortletKeys.
				PRODUCT_NAVIGATION_PRODUCT_MENU);

		return HashMapBuilder.<String, Object>put(
			"canonicalName", _panelCategory.getLabel(LocaleUtil.ENGLISH)
		).put(
			"expandedKeys", _getExpandedKeys()
		).put(
			"expandedKeysSessionKey", _EXPANDED_KEYS_SESSION_KEY
		).put(
			"imageURL", _getImageURL()
		).put(
			"items", _getPropsItems()
		).put(
			"label", _getLabel()
		).put(
			"navigationItemsURL",
			() -> {
				LiferayPortletURL navigationItemsURL =
					(LiferayPortletURL)
						RequestBackedPortletURLFactoryUtil.create(
							_httpServletRequest
						).createResourceURL(
							ProductNavigationProductMenuPortletKeys.
								PRODUCT_NAVIGATION_PRODUCT_MENU
						);

				navigationItemsURL.setCopyCurrentRenderParameters(false);
				navigationItemsURL.setParameter(
					"p_v_l_s_g_id",
					String.valueOf(_themeDisplay.getScopeGroupId()));
				navigationItemsURL.setParameter(
					"rootPanelCategoryKey", _panelCategory.getKey());
				navigationItemsURL.setParameter(
					"selectedPortletId", _themeDisplay.getPpid());
				navigationItemsURL.setResourceID(
					"/product_navigation_product_menu/get_navigation_items");

				return navigationItemsURL.toString();
			}
		).put(
			"pageTree", this::_getPageTree
		).put(
			"selectedPortletId", _getSelectedPortletId()
		).put(
			"siteItemSelectedEventName", itemSelectedEventName
		).put(
			"siteItemSelectorURL",
			() -> {
				ItemSelector itemSelector = _itemSelectorSnapshot.get();

				SiteItemSelectorCriterion siteItemSelectorCriterion =
					new SiteItemSelectorCriterion();

				siteItemSelectorCriterion.setDesiredItemSelectorReturnTypes(
					new URLItemSelectorReturnType());

				return String.valueOf(
					itemSelector.getItemSelectorURL(
						RequestBackedPortletURLFactoryUtil.create(
							_httpServletRequest),
						itemSelectedEventName, siteItemSelectorCriterion));
			}
		).put(
			"staging", this::_getStaging
		).put(
			"visible", isVisible()
		).put(
			"visibleSessionKey", _VISIBLE_SESSION_KEY
		).build();
	}

	public boolean isVisible() {
		String state = SessionClicks.get(
			_httpServletRequest, _VISIBLE_SESSION_KEY, "open");

		return Objects.equals(state, "open");
	}

	private List<String> _getExpandedKeys() {
		List<String> expandedKeys = new ArrayList<>();

		String expandedKeysString = SessionClicks.get(
			_httpServletRequest, _EXPANDED_KEYS_SESSION_KEY, StringPool.BLANK);

		if (!expandedKeysString.isEmpty()) {
			Collections.addAll(
				expandedKeys, expandedKeysString.split(StringPool.COMMA));

			return expandedKeys;
		}

		for (PanelCategory childPanelCategory :
				_getRootChildPanelCategories()) {

			expandedKeys.add(childPanelCategory.getKey());
		}

		return expandedKeys;
	}

	private String _getGroupDisplayURL(Group group) {
		String groupDisplayURL = group.getDisplayURL(_themeDisplay, false);

		if (Validator.isNull(groupDisplayURL)) {
			groupDisplayURL = group.getDisplayURL(_themeDisplay, true);
		}

		if (Validator.isNull(groupDisplayURL)) {
			return StringPool.BLANK;
		}

		return HttpComponentsUtil.removeParameter(groupDisplayURL, "p_p_id");
	}

	private String _getGroupURL(Group group, boolean includeStagingGroup)
		throws PortalException {

		String groupDisplayURL = _getGroupDisplayURL(group);

		if (Validator.isNotNull(groupDisplayURL)) {
			return groupDisplayURL;
		}

		PermissionChecker permissionChecker =
			_themeDisplay.getPermissionChecker();

		if (includeStagingGroup && group.hasStagingGroup() &&
			GroupPermissionUtil.contains(
				permissionChecker, group, ActionKeys.VIEW_STAGING)) {

			return _getGroupURL(group.getStagingGroup(), false);
		}

		String portletId = _panelCategoryHelper.getFirstPortletId(
			PanelCategoryKeys.SITE_ADMINISTRATION, permissionChecker, group);

		if (Validator.isNull(portletId)) {
			return StringPool.BLANK;
		}

		PortletURL groupAdministrationURL =
			PortalUtil.getControlPanelPortletURL(
				_httpServletRequest, group, portletId, 0, 0,
				PortletRequest.RENDER_PHASE);

		return groupAdministrationURL.toString();
	}

	private Map<String, Object> _getHomePropsItem() {
		String groupDisplayURL = _getGroupDisplayURL(
			_themeDisplay.getScopeGroup());

		if (Validator.isNull(groupDisplayURL)) {
			return null;
		}

		return HashMapBuilder.<String, Object>put(
			"href", groupDisplayURL
		).put(
			"id", "home"
		).put(
			"label", LanguageUtil.get(_httpServletRequest, "home[page]")
		).put(
			"leadingIcon", "home"
		).build();
	}

	private String _getImageURL() {
		Company company = _themeDisplay.getCompany();

		if (!company.isSiteLogo()) {
			return _themeDisplay.getCompanyLogo();
		}

		Group group = _themeDisplay.getScopeGroup();

		return group.getLogoURL(_themeDisplay, true);
	}

	private String _getLabel() throws Exception {
		Group group = _themeDisplay.getScopeGroup();

		if (!group.isUser()) {
			return group.getDescriptiveName(_themeDisplay.getLocale());
		}

		if (group.getClassPK() == _themeDisplay.getUserId()) {
			return LanguageUtil.get(_httpServletRequest, "my-site");
		}

		User user = UserLocalServiceUtil.getUser(group.getClassPK());

		return LanguageUtil.format(
			_themeDisplay.getLocale(), "x-site", user.getFullName());
	}

	private String _getLiveGroupURL(Group group) throws PortalException {
		if (group.isStagedRemotely()) {
			return _getRemoteLiveGroupURL(group);
		}

		if (!group.isStagingGroup()) {
			return StringPool.BLANK;
		}

		Group liveGroup = StagingUtil.getLiveGroup(group.getGroupId());

		if (liveGroup == null) {
			return StringPool.BLANK;
		}

		return _getGroupURL(liveGroup, false);
	}

	private Map<String, Object> _getPageTree() throws Exception {
		if (!_isShowPageTree()) {
			return null;
		}

		return HashMapBuilder.<String, Object>put(
			"stateSessionKey", _PAGE_TREE_GROUP_ID_SESSION_KEY
		).put(
			"url",
			() -> ResourceURLBuilder.createResourceURL(
				(ResourceURL)RequestBackedPortletURLFactoryUtil.create(
					_httpServletRequest
				).createResourceURL(
					ProductNavigationProductMenuPortletKeys.
						PRODUCT_NAVIGATION_PRODUCT_MENU
				)
			).setRedirect(
				_themeDisplay.getURLCurrent()
			).setBackURL(
				_themeDisplay.getURLCurrent()
			).setParameter(
				"p_v_l_s_g_id", _themeDisplay.getScopeGroupId()
			).setParameter(
				"selPpid", _themeDisplay.getPpid()
			).setResourceID(
				"/product_navigation_product_menu/get_page_tree"
			).buildString()
		).put(
			"visible", _isPageTreeVisible()
		).build();
	}

	private Map<String, Object> _getPageTreePropsItem() throws Exception {
		if (!_isShowPageTree()) {
			return null;
		}

		return HashMapBuilder.<String, Object>put(
			"disabled", _isPageTreeDisabled()
		).put(
			"id", "pageTree"
		).put(
			"label", LanguageUtil.get(_httpServletRequest, "page-tree")
		).put(
			"leadingIcon", "pages-tree"
		).put(
			"pageTree", true
		).build();
	}

	private List<Map<String, Object>> _getPanelAppPropsItems(
			PanelCategory panelCategory)
		throws Exception {

		List<Map<String, Object>> propsItems = new ArrayList<>();

		for (PanelApp panelApp :
				_panelAppRegistry.getPanelApps(
					panelCategory.getKey(),
					_themeDisplay.getPermissionChecker(),
					_themeDisplay.getScopeGroup())) {

			propsItems.add(
				HashMapBuilder.<String, Object>put(
					"canonicalName", panelApp.getLabel(LocaleUtil.ENGLISH)
				).put(
					"href",
					panelApp.getPortletURL(
						_httpServletRequest
					).toString()
				).put(
					"id", panelApp.getPortletId()
				).put(
					"label", panelApp.getLabel(_themeDisplay.getLocale())
				).put(
					"leadingIcon", panelApp.getIcon()
				).build());
		}

		return propsItems;
	}

	private List<Map<String, Object>> _getPropsItems() throws Exception {
		List<Map<String, Object>> propsItems = new ArrayList<>();

		Map<String, Object> homePropsItem = _getHomePropsItem();

		if (homePropsItem != null) {
			propsItems.add(homePropsItem);
		}

		Map<String, Object> pageTreePropsItem = _getPageTreePropsItem();

		if (pageTreePropsItem != null) {
			propsItems.add(pageTreePropsItem);
		}

		propsItems.addAll(_getPanelAppPropsItems(_panelCategory));

		for (PanelCategory childPanelCategory :
				_getRootChildPanelCategories()) {

			List<Map<String, Object>> panelAppPropsItems =
				_getPanelAppPropsItems(childPanelCategory);

			if (panelAppPropsItems.isEmpty()) {
				continue;
			}

			propsItems.add(
				HashMapBuilder.<String, Object>put(
					"id", childPanelCategory.getKey()
				).put(
					"items", panelAppPropsItems
				).put(
					"label",
					childPanelCategory.getLabel(_themeDisplay.getLocale())
				).build());
		}

		return propsItems;
	}

	private String _getRemoteLiveGroupURL(Group group) throws PortalException {
		Layout layout = _themeDisplay.getLayout();

		boolean privateLayout = layout.isPrivateLayout();

		if (layout instanceof VirtualLayout) {
			VirtualLayout virtualLayout = (VirtualLayout)layout;

			Group targetGroup = virtualLayout.getGroup();

			if (!targetGroup.hasPrivateLayouts()) {
				privateLayout = false;
			}
		}

		HttpSession httpSession = _httpServletRequest.getSession();

		String remoteLiveGroupURLKey = StringBundler.concat(
			_REMOTE_LIVE_GROUP_URL_KEY, group.getGroupId(), StringPool.POUND,
			privateLayout);

		String remoteLiveGroupURL = (String)httpSession.getAttribute(
			remoteLiveGroupURLKey);

		if (remoteLiveGroupURL == null) {
			remoteLiveGroupURL = StagingUtil.getRemoteSiteURL(
				group, privateLayout);

			httpSession.setAttribute(remoteLiveGroupURLKey, remoteLiveGroupURL);
		}

		return remoteLiveGroupURL;
	}

	private List<PanelCategory> _getRootChildPanelCategories() {
		if (_rootChildPanelCategories != null) {
			return _rootChildPanelCategories;
		}

		_rootChildPanelCategories =
			_panelCategoryHelper.getChildPanelCategories(
				_panelCategory.getKey(), _themeDisplay);

		return _rootChildPanelCategories;
	}

	private String _getSelectedPortletId() {
		Layout layout = _themeDisplay.getLayout();

		if ((layout == null) || layout.isTypeControlPanel()) {
			return _themeDisplay.getPpid();
		}

		Group group = _themeDisplay.getScopeGroup();

		boolean homePrivateLayout = Validator.isNull(
			group.getDisplayURL(_themeDisplay, false));

		if (layout.isPrivateLayout() != homePrivateLayout) {
			return _themeDisplay.getPpid();
		}

		Layout firstLayout = LayoutLocalServiceUtil.fetchFirstLayout(
			layout.getGroupId(), homePrivateLayout,
			LayoutConstants.DEFAULT_PARENT_LAYOUT_ID, false);

		if ((firstLayout != null) &&
			(firstLayout.getPlid() == layout.getPlid())) {

			return "home";
		}

		return _themeDisplay.getPpid();
	}

	private Map<String, Object> _getStaging() throws Exception {
		Group group = _themeDisplay.getScopeGroup();

		if ((group == null) || (!group.isStaged() && !group.isStagingGroup()) ||
			!_hasStagingPermission(group)) {

			return null;
		}

		boolean staging = false;

		if (group.isStagedRemotely() || group.isStagingGroup()) {
			staging = true;
		}

		return HashMapBuilder.<String, Object>put(
			"current", staging ? "staging" : "live"
		).put(
			"liveURL",
			() -> {
				try {
					return _getLiveGroupURL(group);
				}
				catch (Exception exception) {
					_log.error(
						"Unable to get live group URL: " +
							exception.getMessage());

					if (_log.isDebugEnabled()) {
						_log.debug(exception);
					}

					return StringPool.BLANK;
				}
			}
		).put(
			"remote", group.isStagedRemotely()
		).put(
			"stagingURL", () -> _getStagingGroupURL(group)
		).build();
	}

	private String _getStagingGroupURL(Group group) throws PortalException {
		if (group.isStagedRemotely() || !group.hasStagingGroup()) {
			return StringPool.BLANK;
		}

		Group stagingGroup = StagingUtil.getStagingGroup(group.getGroupId());

		if (stagingGroup == null) {
			return StringPool.BLANK;
		}

		return _getGroupURL(stagingGroup, false);
	}

	private boolean _hasStagingPermission(Group group) throws Exception {
		PermissionChecker permissionChecker =
			_themeDisplay.getPermissionChecker();

		if (GroupPermissionUtil.contains(
				permissionChecker, group, ActionKeys.MANAGE_STAGING) ||
			GroupPermissionUtil.contains(
				permissionChecker, group, ActionKeys.PUBLISH_STAGING) ||
			GroupPermissionUtil.contains(
				permissionChecker, group, ActionKeys.VIEW_STAGING)) {

			return true;
		}

		return false;
	}

	private boolean _isPageTreeDisabled() {
		if (!Objects.equals(
				_themeDisplay.getPpid(), LayoutAdminPortletKeys.GROUP_PAGES)) {

			return false;
		}

		HttpServletRequest originalHttpServletRequest =
			PortalUtil.getOriginalServletRequest(_httpServletRequest);
		String groupPagesPortletNamespace = PortalUtil.getPortletNamespace(
			LayoutAdminPortletKeys.GROUP_PAGES);

		if (Validator.isNotNull(
				ParamUtil.getString(
					originalHttpServletRequest,
					groupPagesPortletNamespace + "mvcRenderCommandName")) ||
			Validator.isNotNull(
				ParamUtil.getString(originalHttpServletRequest, "mvcPath"))) {

			return false;
		}

		return true;
	}

	private boolean _isPageTreeVisible() {
		if (_isPageTreeDisabled()) {
			return false;
		}

		String pageTreeGroupId = SessionClicks.get(
			_httpServletRequest, _PAGE_TREE_GROUP_ID_SESSION_KEY,
			StringPool.BLANK);

		if (Validator.isNull(pageTreeGroupId)) {
			return false;
		}

		if (pageTreeGroupId.equals(
				String.valueOf(_themeDisplay.getScopeGroupId()))) {

			return true;
		}

		SessionClicks.put(
			_httpServletRequest, _PAGE_TREE_GROUP_ID_SESSION_KEY,
			StringPool.BLANK);

		return false;
	}

	private boolean _isShowPageTree() throws Exception {
		if (_showPageTree != null) {
			return _showPageTree;
		}

		_showPageTree = false;

		Group group = _themeDisplay.getScopeGroup();

		if ((group == null) || group.isCompany() || group.isDepot()) {
			return _showPageTree;
		}

		Portlet portlet = PortletLocalServiceUtil.getPortletById(
			_themeDisplay.getCompanyId(), LayoutAdminPortletKeys.GROUP_PAGES);

		if (portlet == null) {
			return _showPageTree;
		}

		ControlPanelEntry controlPanelEntry =
			portlet.getControlPanelEntryInstance();

		_showPageTree = controlPanelEntry.hasAccessPermission(
			_themeDisplay.getPermissionChecker(), group, portlet);

		return _showPageTree;
	}

	private void _updateLatentGroup() {
		GroupProvider groupProvider = _groupProviderSnapshot.get();

		Group group = groupProvider.getGroup(_httpServletRequest);

		if (group == null) {
			return;
		}

		RecentGroupManager recentGroupManager =
			_recentGroupManagerSnapshot.get();

		recentGroupManager.addRecentGroup(
			_httpServletRequest, group.getGroupId());

		groupProvider.setGroup(_httpServletRequest, group);
	}

	private static final String _EXPANDED_KEYS_SESSION_KEY =
		"com.liferay.product.navigation.product.menu.web_sidebarExpandedKeys";

	private static final String _PAGE_TREE_GROUP_ID_SESSION_KEY =
		"com.liferay.product.navigation.product.menu.web_sidebarPageTree";

	private static final String _REMOTE_LIVE_GROUP_URL_KEY =
		ProductMenuSidebarDisplayContext.class.getName() +
			"#REMOTE_LIVE_GROUP_URL#";

	private static final String _VISIBLE_SESSION_KEY =
		"com.liferay.product.navigation.product.menu.web_productMenuState";

	private static final Log _log = LogFactoryUtil.getLog(
		ProductMenuSidebarDisplayContext.class);

	private static final Snapshot<GroupProvider> _groupProviderSnapshot =
		new Snapshot<>(
			ProductMenuSidebarDisplayContext.class, GroupProvider.class);
	private static final Snapshot<ItemSelector> _itemSelectorSnapshot =
		new Snapshot<>(
			ProductMenuSidebarDisplayContext.class, ItemSelector.class);
	private static final Snapshot<RecentGroupManager>
		_recentGroupManagerSnapshot = new Snapshot<>(
			ProductMenuSidebarDisplayContext.class, RecentGroupManager.class);

	private final HttpServletRequest _httpServletRequest;
	private final PanelAppRegistry _panelAppRegistry;
	private final PanelCategory _panelCategory;
	private final PanelCategoryHelper _panelCategoryHelper;
	private List<PanelCategory> _rootChildPanelCategories;
	private Boolean _showPageTree;
	private final ThemeDisplay _themeDisplay;

}