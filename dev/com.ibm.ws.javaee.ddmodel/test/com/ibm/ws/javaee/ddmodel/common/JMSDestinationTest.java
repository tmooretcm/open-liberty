/*******************************************************************************
 * Copyright (c) 2014,2021 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.ibm.ws.javaee.ddmodel.common;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.ibm.ws.javaee.dd.common.Description;
import com.ibm.ws.javaee.dd.common.JMSDestination;
import com.ibm.ws.javaee.dd.common.Property;
import com.ibm.ws.javaee.ddmodel.DDParser;

public class JMSDestinationTest extends CommonTestBase {
    @Test
    public void testDefault() throws Exception {
        String xml = "<jms-destination>" +
                     "<name>name0</name>" +
                     "<interface-name>intf0</interface-name>" +
                     "</jms-destination>";
        JMSDestination o = parse(xml).getJMSDestinations().get(0);
        Assert.assertEquals(Arrays.asList(), o.getDescriptions());
        Assert.assertEquals("name0", o.getName());
        Assert.assertEquals("intf0", o.getInterfaceNameValue());
        Assert.assertNull(o.getClassNameValue());
        Assert.assertNull(o.getResourceAdapter());
        Assert.assertNull(o.getDestinationName());
        Assert.assertEquals(Arrays.asList(), o.getProperties());
    }

    @Test(expected = DDParser.ParseException.class)
    public void testDefaultEJB31() throws Exception {
        parseEJB31("<jms-destination>" +
                       "<name>name0</name>" +
                       "<interface-name>intf0</interface-name>" +
                   "</jms-destination>");
    }

    @Test
    public void testAll() throws Exception {
        String xml = "<jms-destination>" +
                         "<description>desc0</description>" +
                         "<description>desc1</description>" +
                         "<name>name0</name>" +
                         "<interface-name>intf0</interface-name>" +
                         "<class-name>class0</class-name>" +
                         "<resource-adapter>ra0</resource-adapter>" +
                         "<destination-name>dest0</destination-name>" +
                         "<property>" +
                             "<name>prop0</name>" +
                             "<value>value0</value>" +
                         "</property>" +
                         "<property>" +
                             "<name>prop1</name>" +
                             "<value>value1</value>" +
                         "</property>" +
                     "</jms-destination>";
        JMSDestination o = parse(xml).getJMSDestinations().get(0);
        List<Description> descs = o.getDescriptions();
        Assert.assertEquals(descs.toString(), 2, descs.size());
        Assert.assertEquals("desc0", descs.get(0).getValue());
        Assert.assertEquals("desc1", descs.get(1).getValue());
        Assert.assertEquals("name0", o.getName());
        Assert.assertEquals("intf0", o.getInterfaceNameValue());
        Assert.assertEquals("class0", o.getClassNameValue());
        Assert.assertEquals("ra0", o.getResourceAdapter());
        Assert.assertEquals("dest0", o.getDestinationName());
        List<Property> props = o.getProperties();
        Assert.assertEquals(props.toString(), 2, props.size());
        Assert.assertEquals("prop0", props.get(0).getName());
        Assert.assertEquals("value0", props.get(0).getValue());
        Assert.assertEquals("prop1", props.get(1).getName());
        Assert.assertEquals("value1", props.get(1).getValue());
    }
}
