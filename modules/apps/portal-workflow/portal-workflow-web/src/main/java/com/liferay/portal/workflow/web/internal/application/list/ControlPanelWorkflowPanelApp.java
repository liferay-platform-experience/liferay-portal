/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.web.internal.application.list;

import com.liferay.application.list.BasePanelApp;
import com.liferay.application.list.PanelApp;
import com.liferay.application.list.PanelAppNavigationItem;
import com.liferay.application.list.constants.PanelCategoryKeys;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.workflow.constants.WorkflowPortletKeys;
import com.liferay.portal.workflow.constants.WorkflowWebKeys;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Arrays;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Adam Brandizzi
 */
@Component(
	property = {
		"panel.app.order:Integer=100",
		"panel.category.key=" + PanelCategoryKeys.CONTROL_PANEL_WORKFLOW
	},
	service = PanelApp.class
)
public class ControlPanelWorkflowPanelApp extends BasePanelApp {

	@Override
	public String getIcon() {
		return "process-builder";
	}

	@Override
	public String getIconURL() {
		return StringBundler.concat(
			"data:image/svg+xml,%3Csvg%20width%3D%2240%22%20height%3D%2240%22",
			"%20viewBox%3D%220%200%2040%2040%22%20fill%3D%22none%22%20xmlns",
			"%3D%22http%3A%2F%2Fwww.w3.org%2F2000%2Fsvg%22%3E%3Cpath%20d%3D",
			"%22M15.2447%200.491808C17.0316%200.176694%2018.7308%200.910789",
			"%2019.978%201.63263C22.5528%203.1229%2025.4732%205.95181",
			"%2028.0037%208.41549C30.7324%2011.0721%2033.0345%2013.3284",
			"%2034.737%2014.3736C34.7415%2014.3764%2034.7465%2014.3791",
			"%2034.751%2014.3818C34.869%2013.0642%2034.4948%2011.2242",
			"%2033.9067%209.41252C33.6073%208.48991%2033.287%207.67506",
			"%2033.0414%207.0916C32.9191%206.8013%2032.8166%206.57127",
			"%2032.7465%206.41762C32.7116%206.34102%2032.6851%206.28348",
			"%2032.668%206.24723C32.6595%206.22914%2032.653%206.21647",
			"%2032.6495%206.20922C32.6478%206.2056%2032.6463%206.20225",
			"%2032.6463%206.20225L32.647%206.20289L32.5921%206.08253C32.0561",
			"%204.8172%2032.609%203.34328%2033.8691%202.74115C35.1698",
			"%202.1197%2036.7318%202.66249%2037.358%203.95355L35.0025",
			"%205.07917C37.3563%203.95441%2037.3586%203.95418%2037.3586",
			"%203.95418V3.95545L37.3631%203.96432C37.3655%203.96939%2037.3688",
			"%203.97597%2037.3726%203.98395C37.3803%203.99996%2037.3904",
			"%204.02188%2037.4033%204.0492C37.4291%204.10395%2037.4655",
			"%204.18118%2037.5098%204.2785C37.5985%204.47297%2037.7214",
			"%204.74951%2037.8653%205.0912C38.1521%205.77238%2038.528",
			"%206.72697%2038.8832%207.82132C39.5542%209.88865%2040.3096",
			"%2012.915%2039.8685%2015.5569C39.6022%2017.1514%2038.8219",
			"%2019.1925%2036.6234%2019.8047C34.7868%2020.3161%2033.0445",
			"%2019.436%2031.9878%2018.7874C29.6583%2017.3575%2026.8411",
			"%2014.5519%2024.3431%2012.1198C21.6468%209.49471%2019.2322",
			"%207.20814%2017.3443%206.11547C16.8546%205.83205%2016.5309",
			"%205.70765%2016.343%205.65243C16.3127%205.71951%2016.276",
			"%205.8064%2016.2371%205.91911C15.9837%206.65359%2016.007",
			"%207.16783%2016.119%207.56858C16.2402%208.00205%2016.5233",
			"%208.50197%2017.0814%209.08947C18.2765%2010.3477%2020.2055",
			"%2011.4965%2022.6488%2012.8996C24.9084%2014.1973%2027.6272",
			"%2015.7236%2029.6144%2017.6314C31.7093%2019.6427%2033.2872",
			"%2022.3686%2032.6789%2025.9434C32.3809%2027.6944%2031.6217",
			"%2029.4797%2029.8556%2030.399C28.1709%2031.276%2026.3703",
			"%2030.9302%2025.0745%2030.4819C23.712%2030.0106%2022.2883",
			"%2029.21%2020.9385%2028.3593C19.5709%2027.4973%2018.1186",
			"%2026.4784%2016.7189%2025.5069C13.789%2023.4733%2011.1445",
			"%2021.7014%208.96511%2020.9481C7.9033%2020.581%207.25692",
			"%2020.5705%206.89744%2020.658C6.661%2020.7155%206.31896",
			"%2020.8596%205.92869%2021.5676C4.64935%2023.8884%205.26888",
			"%2026.4722%206.72577%2028.9497C7.42823%2030.1442%208.24863",
			"%2031.1699%208.90448%2031.9034C9.22933%2032.2667%209.50641",
			"%2032.5496%209.69645%2032.737C9.79125%2032.8304%209.864",
			"%2032.8995%209.9096%2032.9422C9.93232%2032.9635%209.94828",
			"%2032.9781%209.95683%2032.9859L9.96321%2032.9923L10.057",
			"%2033.0803C11.0364%2034.0494%2011.0881%2035.6215%2010.1521",
			"%2036.6529C9.18589%2037.7175%207.53289%2037.8032%206.46029",
			"%2036.8442L8.20952%2034.9166C6.51721%2036.7813%206.4615",
			"%2036.8416%206.45901%2036.8429L6.4571%2036.8417C6.4571%2036.8417",
			"%206.45467%2036.8391%206.45327%2036.8379C6.45053%2036.8354",
			"%206.44741%2036.8324%206.4437%2036.829C6.43607%2036.8221",
			"%206.42595%2036.8136%206.41434%2036.803C6.39114%2036.7818",
			"%206.36062%2036.7526%206.32244%2036.7169C6.24615%2036.6454",
			"%206.14097%2036.5454%206.01229%2036.4185C5.75522%2036.1651",
			"%205.40143%2035.8026%204.99441%2035.3474C4.18659%2034.4439",
			"%203.13658%2033.1401%202.21071%2031.5658C0.410758%2028.505",
			"%20-1.25714%2023.7914%201.34216%2019.0763C2.32834%2017.2874",
			"%203.78638%2016.0718%205.653%2015.6177C7.3965%2015.1936",
			"%209.15638%2015.5196%2010.6843%2016.0478C13.6851%2017.0851",
			"%2016.9555%2019.3393%2019.7151%2021.2547C21.16%2022.2575",
			"%2022.4965%2023.1938%2023.7413%2023.9784C25.004%2024.7743",
			"%2026.0168%2025.3125%2026.795%2025.5817C27.0523%2025.6707",
			"%2027.2356%2025.7119%2027.3585%2025.7324C27.4077%2025.6015",
			"%2027.4706%2025.3946%2027.5244%2025.0787C27.7527%2023.7365",
			"%2027.2998%2022.6289%2025.98%2021.3617C24.5523%2019.9909",
			"%2022.4716%2018.7929%2020.031%2017.3913C17.7741%2016.0952",
			"%2015.1016%2014.5695%2013.2772%2012.6488C12.3256%2011.6468",
			"%2011.4941%2010.4309%2011.0819%208.95644C10.6606%207.44928",
			"%2010.731%205.86279%2011.2912%204.2386C11.9062%202.45592",
			"%2013.1393%200.863167%2015.2447%200.491808Z%22%20fill%3D%22url",
			"%28%23paint0_linear_2108_1482%29%22%2F%3E%3Cpath%20d%3D",
			"%22M29.0381%203.26843C30.1218%201.40353%2032.5218%200.764563",
			"%2034.3987%201.84126C36.2756%202.91796%2036.9187%205.3026",
			"%2035.8351%207.1675L21.5319%2031.7826C20.9428%2032.7965",
			"%2019.6381%2033.1438%2018.6177%2032.5585L15.5158",
			"%2030.7791C14.4954%2030.1938%2014.1458%2028.8974%2014.7349",
			"%2027.8836L29.0381%203.26843Z%22%20fill%3D%22%230B5FFF%22%2F%3E",
			"%3Cpath%20d%3D%22M19.3397%2032.9728C20.146%2033.4354%2020.2219",
			"%2034.5634%2019.4844%2035.1281L14.0236%2039.3096C13.0985",
			"%2040.0178%2011.7737%2039.2578%2011.9284%2038.1077L12.8426",
			"%2031.3181C12.966%2030.4012%2013.9868%2029.9022%2014.7931",
			"%2030.3647L19.3397%2032.9728Z%22%20fill%3D%22%230B5FFF%22%2F%3E",
			"%3Cpath%20d%3D%22M14.655%2035.7863C15.4031%2036.2154%2015.955",
			"%2036.8523%2016.2799%2037.5812L14.0236%2039.3096C13.0985",
			"%2040.0178%2011.7737%2039.2578%2011.9284%2038.1077L12.3086",
			"%2035.2828C13.0971%2035.205%2013.9165%2035.3626%2014.655",
			"%2035.7863Z%22%20fill%3D%22%230B5FFF%22%2F%3E%3Cpath%20d%3D",
			"%22M15.3034%2026.9053L22.1003%2030.8044L19.8112",
			"%2034.7439L13.0142%2030.8448L15.3034%2026.9053Z%22%20fill%3D%22",
			"%23B3CDFF%22%2F%3E%3Cdefs%3E%3ClinearGradient%20id%3D",
			"%22paint0_linear_2108_1482%22%20x1%3D%220%22%20y1%3D%2220.0001",
			"%22%20x2%3D%2240%22%20y2%3D%2220.0001%22%20gradientUnits%3D",
			"%22userSpaceOnUse%22%3E%3Cstop%20stop-color%3D%22%230B5FFF%22%2F",
			"%3E%3Cstop%20offset%3D%221%22%20stop-color%3D%22%230B5FFF%22",
			"%20stop-opacity%3D%220%22%2F%3E%3C%2FlinearGradient%3E%3C%2Fdefs",
			"%3E%3C%2Fsvg%3E");
	}

	@Override
	public List<PanelAppNavigationItem> getPanelAppNavigationItems(
			HttpServletRequest httpServletRequest)
		throws PortalException {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		return TransformUtil.unsafeTransform(
			Arrays.asList(
				WorkflowWebKeys.WORKFLOW_TAB_DEFINITION,
				WorkflowWebKeys.WORKFLOW_TAB_DEFINITION_LINK),
			tabName -> new PanelAppNavigationItem(
				_language.get(LocaleUtil.ENGLISH, tabName),
				PortletURLBuilder.create(
					getPortletURL(httpServletRequest)
				).setParameter(
					"tab", tabName
				).buildString(),
				_language.get(themeDisplay.getLocale(), tabName)));
	}

	@Override
	public Portlet getPortlet() {
		return _portlet;
	}

	@Override
	public String getPortletId() {
		return WorkflowPortletKeys.CONTROL_PANEL_WORKFLOW;
	}

	@Override
	public PortletURL getPortletURL(HttpServletRequest httpServletRequest) {
		return _portal.getControlPanelPortletURL(
			httpServletRequest, getGroup(httpServletRequest), getPortletId(), 0,
			0, PortletRequest.RENDER_PHASE);
	}

	@Override
	protected Group getGroup(HttpServletRequest httpServletRequest) {
		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		return themeDisplay.getControlPanelGroup();
	}

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

	@Reference(
		target = "(jakarta.portlet.name=" + WorkflowPortletKeys.CONTROL_PANEL_WORKFLOW + ")"
	)
	private Portlet _portlet;

}