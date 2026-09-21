/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.style.book.web.internal.portlet.action;

import com.liferay.frontend.token.definition.FrontendTokenDefinition;
import com.liferay.frontend.token.definition.FrontendTokenDefinitionRegistry;
import com.liferay.frontend.token.definition.util.FrontendTokenDefinitionUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.portlet.JSONPortletResponseUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.DigesterUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.style.book.constants.StyleBookPortletKeys;
import com.liferay.style.book.exception.NoSuchEntryException;
import com.liferay.style.book.model.StyleBookEntry;
import com.liferay.style.book.service.StyleBookEntryLocalService;
import com.liferay.style.book.service.StyleBookEntryService;
import com.liferay.style.book.web.internal.handler.StyleBookEntryExceptionRequestHandlerUtil;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Gabriel Lima
 */
@Component(
	property = {
		"jakarta.portlet.name=" + StyleBookPortletKeys.STYLE_BOOK,
		"mvc.command.name=/style_book/add_style_book_entry_frontend_token"
	},
	service = MVCActionCommand.class
)
public class AddStyleBookEntryFrontendTokenMVCActionCommand
	extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		try {
			ThemeDisplay themeDisplay =
				(ThemeDisplay)actionRequest.getAttribute(WebKeys.THEME_DISPLAY);

			StyleBookEntry styleBookEntry = _addFrontendToken(actionRequest);

			JSONObject jsonObject = JSONUtil.put(
				"frontendTokenDefinitions",
				_getFrontendTokenDefinitionsJSONArray(
					themeDisplay.getLocale(), styleBookEntry));

			JSONPortletResponseUtil.writeJSON(
				actionRequest, actionResponse, jsonObject);
		}
		catch (PortalException portalException) {
			hideDefaultErrorMessage(actionRequest);

			StyleBookEntryExceptionRequestHandlerUtil.handlePortalException(
				actionRequest, actionResponse, portalException);
		}
	}

	private StyleBookEntry _addFrontendToken(ActionRequest actionRequest)
		throws PortalException {

		long styleBookEntryId = ParamUtil.getLong(
			actionRequest, "styleBookEntryId");

		StyleBookEntry styleBookEntry =
			_styleBookEntryLocalService.fetchStyleBookEntry(styleBookEntryId);

		if (styleBookEntry == null) {
			throw new NoSuchEntryException();
		}

		long draftStyleBookEntryId = styleBookEntryId;

		if (styleBookEntry.isHead()) {
			StyleBookEntry draftStyleBookEntry =
				_styleBookEntryLocalService.getDraft(styleBookEntryId);

			draftStyleBookEntryId = draftStyleBookEntry.getStyleBookEntryId();
		}

		String label = ParamUtil.getString(actionRequest, "label");

		String frontendTokenName =
			"token" + DigesterUtil.digestHex(DigesterUtil.SHA_256, label);

		return _styleBookEntryService.addFrontendToken(
			draftStyleBookEntryId, frontendTokenName,
			ParamUtil.getString(actionRequest, "editorType"),
			ParamUtil.getString(actionRequest, "categoryName"),
			ParamUtil.getString(actionRequest, "description"), label,
			frontendTokenName,
			ParamUtil.getString(actionRequest, "tokenSetDescription"),
			ParamUtil.getString(actionRequest, "tokenSetLabel"),
			ParamUtil.getString(actionRequest, "tokenSetName"),
			ParamUtil.getString(actionRequest, "value"),
			ServiceContextFactory.getInstance(actionRequest));
	}

	private JSONArray _getFrontendTokenDefinitionsJSONArray(
		Locale locale, StyleBookEntry styleBookEntry) {

		JSONArray jsonArray = _jsonFactory.createJSONArray();

		FrontendTokenDefinition themeFrontendTokenDefinition =
			_frontendTokenDefinitionRegistry.getFrontendTokenDefinition(
				styleBookEntry.getCompanyId(), styleBookEntry.getThemeId());

		JSONObject themeFrontendTokenDefinitionJSONObject =
			themeFrontendTokenDefinition.getJSONObject(locale);

		JSONObject overrideFrontendTokenDefinitionJSONObject =
			FrontendTokenDefinitionUtil.parseFrontendTokenDefinitionJSONObject(
				styleBookEntry.getFrontendTokenDefinition());

		if (overrideFrontendTokenDefinitionJSONObject != null) {
			themeFrontendTokenDefinitionJSONObject =
				FrontendTokenDefinitionUtil.
					mergeFrontendTokenDefinitionJSONObject(
						themeFrontendTokenDefinitionJSONObject,
						overrideFrontendTokenDefinitionJSONObject);
		}

		themeFrontendTokenDefinitionJSONObject.put(
			"id", themeFrontendTokenDefinition.getThemeId()
		).put(
			"name", themeFrontendTokenDefinition.getThemeName(locale)
		).put(
			"priority", themeFrontendTokenDefinition.getPriority()
		);

		jsonArray.put(themeFrontendTokenDefinitionJSONObject);

		FrontendTokenDefinition globalFrontendTokenDefinition =
			_frontendTokenDefinitionRegistry.getFrontendTokenDefinition(
				styleBookEntry.getCompanyId(),
				"com.liferay.frontend.js.clay.web");

		if (globalFrontendTokenDefinition == null) {
			return jsonArray;
		}

		JSONObject globalFrontendTokenDefinitionJSONObject =
			globalFrontendTokenDefinition.getJSONObject(locale);

		return jsonArray.put(
			globalFrontendTokenDefinitionJSONObject.put(
				"id", globalFrontendTokenDefinition.getThemeId()
			).put(
				"name", globalFrontendTokenDefinition.getThemeName(locale)
			).put(
				"priority", globalFrontendTokenDefinition.getPriority()
			));
	}

	@Reference
	private FrontendTokenDefinitionRegistry _frontendTokenDefinitionRegistry;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private StyleBookEntryLocalService _styleBookEntryLocalService;

	@Reference
	private StyleBookEntryService _styleBookEntryService;

}