/*******************************************************************************
 * Copyright (c) 2018, 2021 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.ibm.ws.security.mp.jwt11.fat;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.ibm.ws.security.fat.common.AlwaysRunAndPassTest;
import com.ibm.ws.security.fat.common.mp.jwt.MPJwt11FatConstants;

@RunWith(Suite.class)
@SuiteClasses({
        // All tests are run from the different mpJwt version FAT projects - this project is just the source of all of those tests
        AlwaysRunAndPassTest.class,

})

public class FATSuite {

    @SuppressWarnings("restriction")
    public static String authHeaderPrefix = MPJwt11FatConstants.TOKEN_TYPE_BEARER;

}
