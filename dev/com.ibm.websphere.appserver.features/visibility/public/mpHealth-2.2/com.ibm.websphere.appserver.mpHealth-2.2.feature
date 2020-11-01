-include= ~${workspace}/cnf/resources/bnd/feature.props
symbolicName=com.ibm.websphere.appserver.mpHealth-2.2
WLP-DisableAllFeatures-OnConflict: false
visibility=public
singleton=true
IBM-App-ForceRestart: install, \
 uninstall
IBM-API-Package: \
  org.eclipse.microprofile.health;  type="stable", \
  org.eclipse.microprofile.health.spi;  type="stable"
IBM-ShortName: mpHealth-2.2
Subsystem-Name: MicroProfile Health 2.2
-features=com.ibm.websphere.appserver.org.eclipse.microprofile.health-2.2, \
 com.ibm.websphere.appserver.cdi-2.0; ibm.tolerates:=1.2, \
 com.ibm.websphere.appserver.contextService-1.0, \
 com.ibm.websphere.appserver.jndi-1.0, \
 com.ibm.websphere.appserver.json-1.0, \
 com.ibm.websphere.appserver.servlet-4.0; ibm.tolerates:=3.1, \
 com.ibm.wsspi.appserver.webBundle-1.0 
-bundles=\
 com.ibm.websphere.org.eclipse.microprofile.health.2.2; location:="dev/api/stable/,lib/"; mavenCoordinates="org.eclipse.microprofile.health:microprofile-health-api:2.2", \
 com.ibm.websphere.jsonsupport, \
 com.ibm.ws.microprofile.health.2.0; apiJar=false; location:="lib/", \
 com.ibm.ws.org.joda.time.1.6.2
kind=ga
edition=core
WLP-Activation-Type: parallel
