-include= ~${workspace}/cnf/resources/bnd/feature.props
symbolicName=io.openliberty.jakarta.activation-2.1
visibility=private
singleton=true
IBM-App-ForceRestart: uninstall, install
IBM-Process-Types: client, server
Subsystem-Name: Jakarta Activation 2.1
IBM-API-Package: \
 jakarta.activation; type="spec"
-features=com.ibm.websphere.appserver.eeCompatible-10.0
-bundles=\
  io.openliberty.jakarta.activation.2.1; location:="dev/api/spec/,lib/"; mavenCoordinates="jakarta.activation:jakarta.activation-api:2.1.0", \
  io.openliberty.org.glassfish.hk2.osgi-resource-locator
kind=noship
edition=full
WLP-Activation-Type: parallel