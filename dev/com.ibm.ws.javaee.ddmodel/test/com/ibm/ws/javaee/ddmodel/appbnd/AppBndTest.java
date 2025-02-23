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
package com.ibm.ws.javaee.ddmodel.appbnd;

import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import com.ibm.ws.javaee.dd.app.Application;
import com.ibm.ws.javaee.dd.appbnd.ApplicationBnd;
import com.ibm.ws.javaee.dd.appbnd.ClientProfile;
import com.ibm.ws.javaee.dd.appbnd.Group;
import com.ibm.ws.javaee.dd.appbnd.Profile;
import com.ibm.ws.javaee.dd.appbnd.SecurityRole;
import com.ibm.ws.javaee.dd.appbnd.SpecialSubject;
import com.ibm.ws.javaee.dd.appbnd.User;

public class AppBndTest extends AppBndTestBase {
    
    @Test
    public void testXMIGetVersion() throws Exception {
        Assert.assertEquals("Incorrect application binding version",
                "XMI",
                parseAppBndXMI( appBndXMI("", ""), app14() ).getVersion());
    }

    @Test
    public void testXMLGetVersion10() throws Exception {
        Assert.assertEquals("Incorrect application binding version",
                "1.0",
                parseAppBndXML(appBnd10()).getVersion());
    }
    
    @Test
    public void testXMLGetVersion11() throws Exception {    
        Assert.assertEquals("Incorrect application binding version",
                "1.1",
                parseAppBndXML(appBnd11()).getVersion());
    }
    
    @Test
    public void testXMLGetVersion12() throws Exception {        
        Assert.assertEquals("Incorrect application binding version",
                "1.2",
                parseAppBndXML(appBnd12()).getVersion());
    }

    @Test
    public void testXMIAppName() throws Exception {
        parseAppBndXMI( appBndXMI("appName=\"an\"", ""), app14() );
    }

    @Test
    public void testXMLSecurityRole() throws Exception {
        ApplicationBnd appBnd = parseAppBndXML(
                appBnd10(
                    "<security-role/>" +
                    "<security-role name=\"sr1\">" +
                        "<user/>" +
                        "<group/>" +
                        "<special-subject/>" +
                        "<run-as/>" +
                    "</security-role>" +
                    "<security-role name=\"sr2\">" +
                        "<user name=\"u0n\" access-id=\"u0ai\"/>" +
                        "<user name=\"u1n\" access-id=\"u1ai\"/>" +
                        "<group name=\"g0n\" access-id=\"g0ai\"/>" +
                        "<group name=\"g1n\" access-id=\"g1ai\"/>" +
                        "<special-subject type=\"EVERYONE\"/>" +
                        "<special-subject type=\"ALL_AUTHENTICATED_USERS\"/>" +
                        "<special-subject type=\"ALL_AUTHENTICATED_IN_TRUSTED_REALMS\"/>" +
                        "<special-subject type=\"SERVER\"/>" +
                        "<run-as userid=\"rau\" password=\"rap\"/>" +
                    "</security-role>"));

        List<SecurityRole> srs = appBnd.getSecurityRoles();
        Assert.assertEquals(srs.toString(), 3, srs.size());

        SecurityRole sr0 = srs.get(0);
        Assert.assertNull(sr0.getName());
        Assert.assertTrue(sr0.getUsers().toString(), sr0.getUsers().isEmpty());
        Assert.assertTrue(sr0.getGroups().toString(), sr0.getGroups().isEmpty());
        Assert.assertTrue(sr0.getSpecialSubjects().toString(), sr0.getSpecialSubjects().isEmpty());
        Assert.assertNull(sr0.getRunAs());

        SecurityRole sr1 = srs.get(1);
        Assert.assertEquals("sr1", sr1.getName());
        List<User> users = sr1.getUsers();
        Assert.assertEquals(users.toString(), 1, users.size());
        Assert.assertNull(users.get(0).getName());
        Assert.assertNull(users.get(0).getAccessId());
        List<Group> groups = sr1.getGroups();
        Assert.assertEquals(groups.toString(), 1, groups.size());
        Assert.assertNull(groups.get(0).getName());
        Assert.assertNull(groups.get(0).getAccessId());
        List<SpecialSubject> sss = sr1.getSpecialSubjects();
        Assert.assertEquals(sss.toString(), 1, sss.size());
        Assert.assertNull(sss.get(0).getType());
        Assert.assertNull(sr1.getRunAs().getUserid());
        Assert.assertNull(sr1.getRunAs().getPassword());

        SecurityRole sr2 = srs.get(2);
        Assert.assertEquals("sr2", sr2.getName());
        users = sr2.getUsers();
        Assert.assertEquals(users.toString(), 2, users.size());
        Assert.assertEquals("u0n", users.get(0).getName());
        Assert.assertEquals("u0ai", users.get(0).getAccessId());
        Assert.assertEquals("u1n", users.get(1).getName());
        Assert.assertEquals("u1ai", users.get(1).getAccessId());
        groups = sr2.getGroups();
        Assert.assertEquals(groups.toString(), 2, groups.size());
        Assert.assertEquals("g0n", groups.get(0).getName());
        Assert.assertEquals("g0ai", groups.get(0).getAccessId());
        Assert.assertEquals("g1n", groups.get(1).getName());
        Assert.assertEquals("g1ai", groups.get(1).getAccessId());
        sss = sr2.getSpecialSubjects();
        Assert.assertEquals(sss.toString(), 4, sss.size());
        Assert.assertEquals(SpecialSubject.Type.EVERYONE, sss.get(0).getType());
        Assert.assertEquals(SpecialSubject.Type.ALL_AUTHENTICATED_USERS, sss.get(1).getType());
        Assert.assertEquals(SpecialSubject.Type.ALL_AUTHENTICATED_IN_TRUSTED_REALMS, sss.get(2).getType());
        Assert.assertEquals(SpecialSubject.Type.SERVER, sss.get(3).getType());
        Assert.assertEquals("rau", sr2.getRunAs().getUserid());
        Assert.assertEquals("rap", sr2.getRunAs().getPassword());
    }

    @Test
    public void testXMISecurityRole() throws Exception {
        Application app = parseApp(
            app(Application.VERSION_1_4,
                  "<security-role id=\"sr0id\"/>" +
                  "<security-role id=\"sr1id\">" +
                      "<role-name>sr1</role-name>" +
                  "</security-role>" +
                  "<security-role id=\"sr2id\">" +
                      "<role-name>sr2</role-name>" +
                  "</security-role>"),
                Application.VERSION_7);
        
        ApplicationBnd appBnd = parseAppBndXMI(
            appBndXMI("",
                "<authorizationTable>" +
                    "<authorizations>" +
                    "   <role href=\"META-INF/application.xml#sr0id\"/>" +
                    "</authorizations>" +
                    "<authorizations>" +
                        "<role href=\"META-INF/application.xml#sr1id\"/>" +
                        "<users/>" +
                        "<groups/>" +
                        "<specialSubjects/>" +
                    "</authorizations>" +
                    "<authorizations>" +
                        "<role href=\"META-INF/application.xml#sr2id\"/>" +
                        "<users name=\"u0n\" accessId=\"u0ai\"/>" +
                        "<users name=\"u1n\" accessId=\"u1ai\"/>" +
                        "<groups name=\"g0n\" accessId=\"g0ai\"/>" +
                        "<groups name=\"g1n\" accessId=\"g1ai\"/>" +
                        "<specialSubjects xmi:type=\"applicationbnd:Everyone\" name=\"ignored\" accessId=\"ignored\"/>" +
                        "<specialSubjects xmi:type=\"applicationbnd:AllAuthenticatedUsers\"/>" +
                        "<specialSubjects xmi:type=\"applicationbnd:AllAuthenticatedInTrustedRealms\"/>" +
                        "<specialSubjects xmi:type=\"applicationbnd:Server\"/>" +
                    "</authorizations>" +
                "</authorizationTable>" +
                "<runAsMap>" +
                    "<runAsBindings>" +
                        "<authData xmi:type=\"commonbnd:BasicAuthData\"/>" +
                        "<securityRole href=\"META-INF/application.xml#sr1id\"/>" +
                    "</runAsBindings>" +
                    "<runAsBindings>" +
                        "<authData xmi:type=\"commonbnd:BasicAuthData\" userId=\"rau\" password=\"rap\"/>" +
                        "<securityRole href=\"META-INF/application.xml#sr2id\"/>" +
                    "</runAsBindings>" +
                "</runAsMap>"),
            app);

        List<SecurityRole> srs = appBnd.getSecurityRoles();
        Assert.assertEquals(srs.toString(), 3, srs.size());

        SecurityRole sr0 = srs.get(0);
        Assert.assertNull(sr0.getName());
        Assert.assertTrue(sr0.getUsers().toString(), sr0.getUsers().isEmpty());
        Assert.assertTrue(sr0.getGroups().toString(), sr0.getGroups().isEmpty());
        Assert.assertTrue(sr0.getSpecialSubjects().toString(), sr0.getSpecialSubjects().isEmpty());
        Assert.assertNull(sr0.getRunAs());

        SecurityRole sr1 = srs.get(1);
        Assert.assertEquals("sr1", sr1.getName());
        List<User> users = sr1.getUsers();
        Assert.assertEquals(users.toString(), 1, users.size());
        Assert.assertNull(users.get(0).getName());
        Assert.assertNull(users.get(0).getAccessId());
        List<Group> groups = sr1.getGroups();
        Assert.assertEquals(groups.toString(), 1, groups.size());
        Assert.assertNull(groups.get(0).getName());
        Assert.assertNull(groups.get(0).getAccessId());
        List<SpecialSubject> sss = sr1.getSpecialSubjects();
        Assert.assertEquals(sss.toString(), 1, sss.size());
        Assert.assertNull(sss.get(0).getType());
        Assert.assertNull(sr1.getRunAs().getUserid());
        Assert.assertNull(sr1.getRunAs().getPassword());

        SecurityRole sr2 = srs.get(2);
        Assert.assertEquals("sr2", sr2.getName());
        users = sr2.getUsers();
        Assert.assertEquals(users.toString(), 2, users.size());
        Assert.assertEquals("u0n", users.get(0).getName());
        Assert.assertEquals("u0ai", users.get(0).getAccessId());
        Assert.assertEquals("u1n", users.get(1).getName());
        Assert.assertEquals("u1ai", users.get(1).getAccessId());
        groups = sr2.getGroups();
        Assert.assertEquals(groups.toString(), 2, groups.size());
        Assert.assertEquals("g0n", groups.get(0).getName());
        Assert.assertEquals("g0ai", groups.get(0).getAccessId());
        Assert.assertEquals("g1n", groups.get(1).getName());
        Assert.assertEquals("g1ai", groups.get(1).getAccessId());
        sss = sr2.getSpecialSubjects();
        Assert.assertEquals(sss.toString(), 4, sss.size());
        Assert.assertEquals(SpecialSubject.Type.EVERYONE, sss.get(0).getType());
        Assert.assertEquals(SpecialSubject.Type.ALL_AUTHENTICATED_USERS, sss.get(1).getType());
        Assert.assertEquals(SpecialSubject.Type.ALL_AUTHENTICATED_IN_TRUSTED_REALMS, sss.get(2).getType());
        Assert.assertEquals(SpecialSubject.Type.SERVER, sss.get(3).getType());
        Assert.assertEquals("rau", sr2.getRunAs().getUserid());
        Assert.assertEquals("rap", sr2.getRunAs().getPassword());
    }

    @Test
    public void testXMISecurityRoleCompat() throws Exception {
        // This "applicationbnd" prefix erroneously has no xmlns.
        
        ApplicationBnd appBnd = parseAppBndXMI(
            "<com.ibm.ejs.models.base.bindings.applicationbnd.applicationbnd:ApplicationBinding" +
                // " xmlns=\"http://websphere.ibm.com/xml/ns/javaee\"" +        
                " xmlns:com.ibm.ejs.models.base.bindings.applicationbnd.applicationbnd=\"applicationbnd.xmi\"" +
                " xmlns:xmi=\"http://www.omg.org/XMI\"" +
                " xmi:version=\"2.0\"" +
                ">" +
                "<application href=\"META-INF/application.xml#Application_ID\"/>" +
                    "<authorizationTable>" +
                        "<authorizations>" +
                            "<specialSubjects xmi:type=\"applicationbnd:Everyone\"/>" +
                            "<specialSubjects xmi:type=\"applicationbnd:AllAuthenticatedUsers\"/>" +
                            "<specialSubjects xmi:type=\"applicationbnd:AllAuthenticatedInTrustedRealms\"/>" +
                            "<specialSubjects xmi:type=\"applicationbnd:Server\"/>" +
                        "</authorizations>" +
                    "</authorizationTable>" +
                "</com.ibm.ejs.models.base.bindings.applicationbnd.applicationbnd:ApplicationBinding>",
                app14() );

        List<SecurityRole> srs = appBnd.getSecurityRoles();
        Assert.assertEquals(srs.toString(), 1, srs.size());
        List<SpecialSubject> sss = srs.get(0).getSpecialSubjects();
        Assert.assertEquals(sss.toString(), 4, sss.size());
        Assert.assertEquals(SpecialSubject.Type.EVERYONE, sss.get(0).getType());
        Assert.assertEquals(SpecialSubject.Type.ALL_AUTHENTICATED_USERS, sss.get(1).getType());
        Assert.assertEquals(SpecialSubject.Type.ALL_AUTHENTICATED_IN_TRUSTED_REALMS, sss.get(2).getType());
        Assert.assertEquals(SpecialSubject.Type.SERVER, sss.get(3).getType());
    }

    @Test
    public void testXMISecurityRoleEmpty() throws Exception {
        parseAppBndXMI( appBndXMI("",
                            "<authorizationTable/>" +
                            "<runAsMap/>"),
                        app14() );
    }

    @Test
    public void testXMLProfile() throws Exception {
        ApplicationBnd appBnd =
                parseAppBndXML( appBnd10(
                    "<profile/>" +
                        "<profile name=\"pn1\">" +
                        "<client-profile/>" +
                        "<client-profile name=\"cpn\"/>" +
                    "</profile>"));

        List<Profile> profiles = appBnd.getProfiles();
        Assert.assertEquals(profiles.toString(), 2, profiles.size());

        Profile p0 = profiles.get(0);
        Assert.assertNull(p0.getName());
        Assert.assertTrue(p0.getClientProfiles().toString(), p0.getClientProfiles().isEmpty());

        Profile p1 = profiles.get(1);
        Assert.assertEquals("pn1", p1.getName());
        List<ClientProfile> cps = p1.getClientProfiles();
        Assert.assertEquals(cps.toString(), 2, cps.size());
        Assert.assertNull(cps.get(0).getName());
        Assert.assertEquals("cpn", cps.get(1).getName());
    }

    @Test
    public void testXMLJASPIRef() throws Exception {
        ApplicationBnd appBnd =
                parseAppBndXML(appBnd10("<jaspi-ref provider-name=\"pn0\"/>"));
        Assert.assertEquals("pn0", appBnd.getJASPIRef().getProviderName());
    }        
}
