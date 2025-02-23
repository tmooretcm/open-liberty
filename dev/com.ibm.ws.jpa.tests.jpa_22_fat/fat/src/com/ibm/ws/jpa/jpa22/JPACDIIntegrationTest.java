/*******************************************************************************
 * Copyright (c) 2017 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package com.ibm.ws.jpa.jpa22;

import java.util.Set;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;

import com.ibm.websphere.simplicity.ShrinkHelper;
import com.ibm.websphere.simplicity.config.FeatureManager;
import com.ibm.websphere.simplicity.config.ServerConfiguration;
import com.ibm.ws.jpa.FATSuite;

import cdi.web.ELIServlet;
import componenttest.annotation.Server;
import componenttest.annotation.TestServlet;
import componenttest.custom.junit.runner.FATRunner;
import componenttest.topology.impl.LibertyServer;
import componenttest.topology.utils.PrivHelper;

/**
 * Test cases for verifying JPA + CDI integration, by using the application-server provided
 * BeanManager with JPA EntityListeners and Converters.
 *
 */
@RunWith(FATRunner.class)
public class JPACDIIntegrationTest {
    public static final String APP_NAME = "cdi";
    public static final String SERVLET = "eli";

    @Server("CDIFatServer")
    @TestServlet(servlet = ELIServlet.class, path = APP_NAME + "/" + SERVLET)
    public static LibertyServer server1;

    @BeforeClass
    public static void setUp() throws Exception {
        final String resPath = "test-applications/jpa22/" + APP_NAME + "/resources/";

        PrivHelper.generateCustomPolicy(server1, FATSuite.JAXB_PERMS);

        WebArchive app = ShrinkWrap.create(WebArchive.class, APP_NAME + ".war");
        app.addPackage("cdi.web");
        app.addPackage("cdi.model");
        ShrinkHelper.addDirectory(app, resPath);

        if (isLesserThanCDI4()) {
            ShrinkHelper.addDirectory(app, "test-applications/jpa22/" + APP_NAME + "/resources.cdiLN4/");
        } else {
            ShrinkHelper.addDirectory(app, "test-applications/jpa22/" + APP_NAME + "/resources.cdiEGT4/");
        }

        ShrinkHelper.exportAppToServer(server1, app);
        server1.addInstalledAppForValidation(APP_NAME);

        server1.startServer();
    }

    @AfterClass
    public static void tearDown() throws Exception {
//        server1.dumpServer("cdi");
        server1.stopServer("CWWJP9991W"); // From Eclipselink drop-and-create tables option
    }

    private static boolean isLesserThanCDI4() {
        try {
            ServerConfiguration svrCfg = server1.getServerConfiguration();
            FeatureManager fm = svrCfg.getFeatureManager();
            Set<String> features = fm.getFeatures();
            if (features.contains("cdi-1.2") || features.contains("cdi-2.0") || features.contains("cdi-3.0")) {
                return true;
            }
        } catch (Throwable t) {
            t.printStackTrace();
            return true;
        }

        return false;
    }
}
