/*******************************************************************************
 * Copyright (c) 2014 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.ibm.ws.webcontainer.osgi.webapp.internal;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;

import com.ibm.websphere.csi.J2EENameFactory;
import com.ibm.ws.container.service.metadata.MetaDataService;
import com.ibm.ws.managedobject.ManagedObjectService;
import com.ibm.ws.webcontainer.osgi.webapp.WebApp;
import com.ibm.ws.webcontainer.osgi.webapp.WebAppConfiguration;
import com.ibm.ws.webcontainer.osgi.webapp.WebAppFactory;
import com.ibm.wsspi.injectionengine.ReferenceContext;

import io.openliberty.checkpoint.spi.CheckpointPhase;


@Component(service= WebAppFactory.class, property = { "service.vendor=IBM" })
public class WebAppFactoryImpl implements WebAppFactory {
	private final CheckpointPhase checkpointPhase;
    @Activate
    public WebAppFactoryImpl(@Reference(cardinality = ReferenceCardinality.OPTIONAL) CheckpointPhase checkpointPhase) {
        this.checkpointPhase = checkpointPhase;
    }

   /* (non-Javadoc)
    * @see com.ibm.ws.webcontainer.osgi.webapp.WebAppFactory#createWebApp(com.ibm.ws.webcontainer.osgi.webapp.WebAppConfiguration, java.lang.ClassLoader, com.ibm.wsspi.injectionengine.ReferenceContext, com.ibm.ws.container.service.metadata.MetaDataService, com.ibm.websphere.csi.J2EENameFactory)
    */
   @Override
   public WebApp createWebApp(WebAppConfiguration webAppConfig, ClassLoader moduleLoader, ReferenceContext referenceContext, MetaDataService metaDataService,
                              J2EENameFactory j2eeNameFactory, ManagedObjectService managedObjectService) {
	   return new WebApp(webAppConfig, moduleLoader, referenceContext, metaDataService, j2eeNameFactory, managedObjectService, checkpointPhase);
   }
}