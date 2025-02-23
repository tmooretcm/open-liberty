/*******************************************************************************
 * Copyright (c) 2015 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package com.ibm.ws.cdi.jee.ejbWithJsp.servlet;

import javax.ejb.Stateless;
import javax.inject.Inject;

import com.ibm.ws.cdi.jee.ejbWithJsp.ejb.SessionBeanInterface;

@Stateless
public class MySessionBean implements SessionBeanInterface {

    @Inject
    HelloWorldExtensionBean2 bean;

}
