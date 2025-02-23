/*******************************************************************************
 * Copyright (c) 2016, 2022 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.ibm.websphere.simplicity.config;

import java.util.Set;

import javax.xml.bind.annotation.XmlAttribute;

/**
 * Represents the <bell> element in server.xml
 */
public class Bell extends ConfigElement {

    private final Class<?> c = Bell.class;

    private String libraryRef;

    private Set<String> service;

    private String enableSpiVisibility;

    @XmlAttribute(required = true)
    public String getLibraryRef() {
        return libraryRef;
    }

    public void setLibraryRef(String libraryRef) {
        this.libraryRef = libraryRef;
    }

    @XmlAttribute
    public Set<String> getService() {
        return service;
    }

    public void setService(Set<String> service) {
        this.service = service;
    }

    @XmlAttribute
    public String getEnableSpiVisibility() {
        return enableSpiVisibility;
    }

    public void setEnableSpiVisibility(String spiVisibility) {
        this.enableSpiVisibility = spiVisibility;
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder(getClass().getSimpleName()).append('{');
        // attributes
        if (getId() != null)
            buf.append("id=").append(getId()).append(' ');
        if (getLibraryRef() != null)
            buf.append("libraryRef=").append(getLibraryRef()).append(' ');
        if (getService() != null)
            buf.append("service=").append(getService()).append(' ');
        if (getEnableSpiVisibility() != null)
            buf.append("enableSpiVisibility=").append(getEnableSpiVisibility()).append(' ');
        buf.append('}');
        return buf.toString();
    }
}
