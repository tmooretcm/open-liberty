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
package io.openliberty.security.oidcclientcore.discovery;

import javax.net.ssl.SSLSocketFactory;

//import com.ibm.json.java.JSONObject;
import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.security.common.http.HttpUtils;

public class DiscoveryHandler {

    public static final TraceComponent tc = Tr.register(DiscoveryHandler.class);

    private final SSLSocketFactory sslSocketFactory;
    public HttpUtils httpUtils;

    public DiscoveryHandler(SSLSocketFactory sslSocketFactory) {
        this.sslSocketFactory = sslSocketFactory;
        this.httpUtils = new HttpUtils();
    }

    public String fetchDiscoveryData(String discoveryUrl, boolean hostNameVerificationEnabled, boolean useSystemProperties) throws Exception {
        return httpUtils.getHttpJsonRequest(sslSocketFactory, discoveryUrl, hostNameVerificationEnabled, useSystemProperties);
    }

}
