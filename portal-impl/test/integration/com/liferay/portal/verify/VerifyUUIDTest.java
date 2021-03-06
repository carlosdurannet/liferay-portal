/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.portal.verify;

import com.liferay.portal.kernel.exception.BulkException;
import com.liferay.portal.kernel.test.ExecutionTestListeners;
import com.liferay.portal.test.listeners.MainServletExecutionTestListener;
import com.liferay.portal.test.runners.LiferayIntegrationJUnitTestRunner;
import com.liferay.portal.verify.model.LayoutVerifiableModel;
import com.liferay.portal.verify.model.VerifiableUUIDModel;

import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Manuel de la Peña
 */
@ExecutionTestListeners(listeners = {MainServletExecutionTestListener.class})
@RunWith(LiferayIntegrationJUnitTestRunner.class)
public class VerifyUUIDTest extends BaseVerifyProcessTestCase {

	@Test
	public void testVerifyModel() throws Exception {
		VerifyUUID.verify(new LayoutVerifiableModel());
	}

	@Test(expected = BulkException.class)
	public void testVerifyModelWithUnknownPKColumnName() throws Exception {
		VerifyUUID.verify(
			new VerifiableUUIDModel() {

				@Override
				public String getPrimaryKeyColumnName() {
					return _UNKNOWN;
				}

				@Override
				public String getTableName() {
					return "Layout";
				}

			});
	}

	@Test(expected = BulkException.class)
	public void testVerifyUnknownModelWithUnknownPKColumnName()
		throws Exception {

		VerifyUUID.verify(
			new VerifiableUUIDModel() {

				@Override
				public String getPrimaryKeyColumnName() {
					return _UNKNOWN;
				}

				@Override
				public String getTableName() {
					return _UNKNOWN;
				}

			});
	}

	@Override
	protected VerifyProcess getVerifyProcess() {
		return new VerifyUUID();
	}

	private static final String _UNKNOWN = "Unknown";

}