/*******************************************************************************
 * Copyright (c) 2020, 2022 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.ibm.ws.transaction.test;

import static org.junit.Assert.assertNotNull;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.ibm.websphere.simplicity.ShrinkHelper;
import com.ibm.ws.transactionalEJB.web.TransactionalEJBTestServlet;

import componenttest.annotation.Server;
import componenttest.annotation.TestServlet;
import componenttest.custom.junit.runner.FATRunner;
import componenttest.topology.impl.LibertyServer;
import componenttest.topology.utils.FATServletClient;

@RunWith(FATRunner.class)
public class TransactionalEJBTest extends FATServletClient {

    public static final String APP_NAME = "transactionalEJB";
    public static final String SERVLET_NAME = APP_NAME + "/transactionalEJB";

    private final long TIMEOUT = 10000; // should have failed very fast

    @Server("com.ibm.ws.transactional")
    @TestServlet(servlet = TransactionalEJBTestServlet.class, contextRoot = APP_NAME)
    public static LibertyServer server;

    @Test
    public void testNoTransactionalEJB() throws Exception {
        // Check transactionalEJB app didn't start
        assertNotNull("TestEJB did not fail to load", server.waitForStringInLog("CWOWB2000E", TIMEOUT));
    }

    @AfterClass
    public static void teardown() throws Exception {
        server.stopServer("CWWKZ0002E");
        ShrinkHelper.cleanAllExportedArchives();
    }

    @BeforeClass
    public static void setup() throws Exception {
        ShrinkHelper.defaultDropinApp(server, APP_NAME, "com.ibm.ws.transactionalEJB.*");
        server.setServerStartTimeout(600000);
        LibertyServer.setValidateApps(false);
        server.startServer(true);
    }
}
