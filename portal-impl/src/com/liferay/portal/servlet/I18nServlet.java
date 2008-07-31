/**
 * Copyright (c) 2000-2008 Liferay, Inc. All rights reserved.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.liferay.portal.servlet;

import com.liferay.portal.NoSuchLayoutException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.util.PortalUtil;
import com.liferay.portal.util.WebKeys;

import java.io.IOException;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.dom4j.Element;

/**
 * <a href="I18nServlet.java.html"><b><i>View Source</i></b></a>
 *
 * @author Brian Wing Shun Chan
 *
 */
public class I18nServlet extends HttpServlet {

	public static Set<String> getLanguageIds() {
		return _languageIds;
	}

	public static void setLanguageIds(Element root) {
		Iterator<Element> itr = root.elements("servlet-mapping").iterator();

		while (itr.hasNext()) {
			Element el = itr.next();

			String servletName = el.elementText("servlet-name");

			if (servletName.equals("I18nServlet")) {
				String urlPattern = el.elementText("url-pattern");

				String languageId = urlPattern.substring(
					0, urlPattern.lastIndexOf(StringPool.SLASH));

				_languageIds.add(languageId);
			}
		}
	}

	public void service(
			HttpServletRequest request, HttpServletResponse response)
		throws IOException, ServletException {

		try {
			String[] i18nData = getI18nData(request);

			if (i18nData == null) {
				PortalUtil.sendError(
					HttpServletResponse.SC_NOT_FOUND,
					new NoSuchLayoutException(), request, response);
			}
			else {
				String languageId = i18nData[0];
				String redirect = i18nData[1];

				request.setAttribute(WebKeys.I18N_LANGUAGE_ID, languageId);

				ServletContext servletContext = getServletContext();

				RequestDispatcher requestDispatcher =
					servletContext.getRequestDispatcher(redirect);

				requestDispatcher.forward(request, response);
			}
		}
		catch (Exception e) {
			_log.error(e, e);

			PortalUtil.sendError(
				HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e, request,
				response);
		}
	}

	protected String[] getI18nData(HttpServletRequest request) {
		String path = GetterUtil.getString(request.getPathInfo());

		if (Validator.isNull(path)) {
			return null;
		}

		String languageId = request.getServletPath();

		int pos = languageId.lastIndexOf(StringPool.SLASH);

		languageId = languageId.substring(pos + 1);

		if (_log.isDebugEnabled()) {
			_log.debug("Language ID " + languageId);
		}

		if (Validator.isNull(languageId)) {
			return null;
		}

		Locale locale = LocaleUtil.fromLanguageId(languageId);

		if (Validator.isNull(locale.getCountry())) {

			// Locales must contain the country code

			locale = LanguageUtil.getLocale(locale.getLanguage());

			languageId = LocaleUtil.toLanguageId(locale);
		}

		String redirect = path;

		if (_log.isDebugEnabled()) {
			_log.debug("Redirect " + redirect);
		}

		return new String[] {languageId, redirect};
	}

	private static Log _log = LogFactory.getLog(I18nServlet.class);

	private static Set<String> _languageIds = new HashSet<String>();

}