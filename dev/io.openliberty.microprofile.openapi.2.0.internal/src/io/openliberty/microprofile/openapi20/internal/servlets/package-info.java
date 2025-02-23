/*******************************************************************************
 * Copyright (c) 2020, 2022 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/

/**
 * Contains the servlet which actually serves up the generated OpenAPI document.
 */
@Version(Constants.OSGI_VERSION)
@TraceOptions(traceGroup = Constants.TRACE_GROUP, messageBundle = Constants.TRACE_OPENAPI)
package io.openliberty.microprofile.openapi20.internal.servlets;

import org.osgi.annotation.versioning.Version;

import com.ibm.websphere.ras.annotation.TraceOptions;

import io.openliberty.microprofile.openapi20.internal.utils.Constants;
