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
package com.ibm.ws.javaee.ddmodel.suite.simple.war;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.ibm.ws.javaee.ddmodel.suite.simple.CommonTests_Simple;

import componenttest.custom.junit.runner.FATRunner;
import componenttest.custom.junit.runner.Mode;
import componenttest.custom.junit.runner.Mode.TestMode;

// Module configuration extension supplied context root ("ext").

@RunWith(FATRunner.class)
@Mode(TestMode.LITE)
public class DDSimpleWarTests_ConfigExt_Exp extends CommonTests_Simple {
    public static final Class<?> TEST_CLASS = DDSimpleWarTests_ConfigExt_Exp.class;

    @BeforeClass
    public static void setUp() throws Exception {
        commonSetUp(TEST_CLASS, "server_simple_war_ext_exp.xml", setUpSimpleWarNoExt);
    }

    @AfterClass
    public static void tearDown() throws Exception {
        commonTearDown(TEST_CLASS, tearDownTestModules, NO_ALLOWED_ERRORS);
    }

    @Test
    public void testContextRoot_War_ConfigExt_Exp() throws Exception {
        testHello(TEST_CLASS, EXP_CONTEXT_ROOT);
    }
}
