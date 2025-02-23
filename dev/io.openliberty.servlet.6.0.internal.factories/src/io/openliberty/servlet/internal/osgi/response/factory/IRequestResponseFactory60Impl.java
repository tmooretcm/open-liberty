/*******************************************************************************
 * Copyright (c) 2022 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package io.openliberty.servlet.internal.osgi.response.factory;

import org.osgi.service.component.annotations.Component;

import com.ibm.websphere.servlet.request.IRequest;
import com.ibm.websphere.servlet.response.IResponse;
import com.ibm.ws.webcontainer.osgi.request.IRequestFactory;
import com.ibm.ws.webcontainer.osgi.response.IResponseFactory;
import com.ibm.ws.webcontainer40.osgi.request.IRequest40Impl;
import com.ibm.ws.webcontainer40.osgi.response.IResponse40Impl;
import com.ibm.wsspi.http.HttpInboundConnection;

@Component(property = { "service.vendor=IBM", "service.ranking:Integer=60", "servlet.version=6.0" })
public class IRequestResponseFactory60Impl implements IRequestFactory, IResponseFactory {

    /*
     * (non-Javadoc)
     *
     * @see com.ibm.ws.webcontainer.osgi.response.IResponseFactory#createRequest(com.ibm.websphere.servlet.request.IRequest, com.ibm.wsspi.http.HttpInboundConnection)
     */
    @Override
    public IRequest createRequest(HttpInboundConnection inboundConnection) {
        // there appears to be nothing in IRequestImpl that needs to be different in servlet 3.1, so return the 3.0 version
        return new IRequest40Impl(inboundConnection);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ibm.ws.webcontainer.osgi.request.IRequestFactory#createRequest(com.ibm.wsspi.http.HttpInboundConnection)
     */
    @Override
    public IResponse createResponse(IRequest ireq, HttpInboundConnection inboundConnection) {
        return new IResponse40Impl(ireq, inboundConnection);
    }

}
