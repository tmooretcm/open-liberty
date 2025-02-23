/*******************************************************************************
 * Copyright (c) 2016 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
//
// Generated By:JAX-WS RI IBM 2.1.6 in JDK 6 (JAXB RI IBM JAXB 2.1.10 in JDK 6)
//

package com.ibm.ws.cdi.jee.webservices.client.services;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;

/**
 * This object contains factory methods for each
 * Java content interface and Java element interface
 * generated in the com.ibm.ws.jaxws.transport.security package.
 * <p>An ObjectFactory allows you to programatically
 * construct new instances of the Java representation
 * for XML content. The Java representation of XML
 * content can consist of schema derived interfaces
 * and classes representing the binding of schema
 * type definitions, element declarations and model
 * groups. Factory methods for each of these are
 * provided in this class.
 *
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _SayHelloResponse_QNAME = new QName("http://ibm.com/ws/jaxws/cdi/", "sayHelloResponse");
    private final static QName _SayHello_QNAME = new QName("http://ibm.com/ws/jaxws/cdi/", "sayHello");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.ibm.ws.jaxws.transport.security
     *
     */
    public ObjectFactory() {}

    /**
     * Create an instance of {@link SayHelloResponse }
     *
     */
    public SayHelloResponse createSayHelloResponse() {
        return new SayHelloResponse();
    }

    /**
     * Create an instance of {@link SayHello_Type }
     *
     */
    public SayHello_Type createSayHello_Type() {
        return new SayHello_Type();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SayHelloResponse }{@code >}
     *
     */
    @XmlElementDecl(namespace = "http://ibm.com/ws/jaxws/cdi/", name = "sayHelloResponse")
    public JAXBElement<SayHelloResponse> createSayHelloResponse(SayHelloResponse value) {
        return new JAXBElement<SayHelloResponse>(_SayHelloResponse_QNAME, SayHelloResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SayHello_Type }{@code >}
     *
     */
    @XmlElementDecl(namespace = "http://ibm.com/ws/jaxws/cdi/", name = "sayHello")
    public JAXBElement<SayHello_Type> createSayHello(SayHello_Type value) {
        return new JAXBElement<SayHello_Type>(_SayHello_QNAME, SayHello_Type.class, null, value);
    }

}
