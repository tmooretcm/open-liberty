/*******************************************************************************
 * Copyright (c) 2013, 2021 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.ibm.ws.ejbcontainer.fat.ejbinwarpackaging.ejb;

import com.ibm.ws.ejbcontainer.fat.beaninterfaceholderlib.EJBInWARPackagingLocal;

public class EJBInWARPackagingSingletonBean implements EJBInWARPackagingLocal {

    @Override
    public String getBeanName() {
        return EJBInWARPackagingSingletonBean.class.getName();
    }
}
