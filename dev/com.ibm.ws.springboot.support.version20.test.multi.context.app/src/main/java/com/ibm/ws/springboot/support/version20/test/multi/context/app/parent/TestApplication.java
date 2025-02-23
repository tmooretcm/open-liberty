/*******************************************************************************
 * Copyright (c) 2018, 2022 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.ibm.ws.springboot.support.version20.test.multi.context.app.parent;

import com.ibm.ws.springboot.support.version20.test.multi.context.app.child1.ServletConfig1;
import com.ibm.ws.springboot.support.version20.test.multi.context.app.child2.ServletConfig2;
import org.springframework.boot.WebApplicationType;

import org.springframework.boot.builder.SpringApplicationBuilder;

public class TestApplication {

	public static void main(String[] args) {
		new SpringApplicationBuilder().parent(Config.class)
			.web(WebApplicationType.NONE)
			.child(ServletConfig1.class)
			.web(WebApplicationType.SERVLET)
			.sibling(ServletConfig2.class)
			.web(WebApplicationType.SERVLET)
			.run(args);
	}

}
