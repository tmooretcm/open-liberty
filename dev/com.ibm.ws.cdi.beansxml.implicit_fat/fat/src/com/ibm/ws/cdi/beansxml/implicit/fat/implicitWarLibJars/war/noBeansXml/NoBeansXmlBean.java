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
package com.ibm.ws.cdi.beansxml.implicit.fat.implicitWarLibJars.war.noBeansXml;

import javax.enterprise.context.Dependent;

import com.ibm.ws.cdi.beansxml.implicit.fat.utils.SimpleAbstract;

/**
 * This bean is in an archive with no beans.xml. This is an <em>implicit</em> bean archive.
 */
@Dependent
public class NoBeansXmlBean extends SimpleAbstract {}
