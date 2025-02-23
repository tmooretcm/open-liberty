-include= ~${workspace}/cnf/resources/bnd/feature.props
symbolicName=io.openliberty.mpRestClient-3.0
visibility=public
singleton=true
IBM-App-ForceRestart: install, \
 uninstall
IBM-API-Package: \
  org.eclipse.microprofile.rest.client; type="stable", \
  org.eclipse.microprofile.rest.client.annotation; type="stable", \
  org.eclipse.microprofile.rest.client.ext; type="stable", \
  org.eclipse.microprofile.rest.client.inject; type="stable", \
  org.eclipse.microprofile.rest.client.spi; type="stable", \
  org.reactivestreams; type="stable", \
  org.jboss.resteasy.client.jaxrs.internal; type="internal", \
  org.jboss.resteasy.client.jaxrs.internal.proxy; type="internal", \
  org.jboss.resteasy.microprofile.client; type="internal"
IBM-ShortName: mpRestClient-3.0
Subsystem-Name: MicroProfile Rest Client 3.0

-features=\
  io.openliberty.jsonp-2.0, \
  io.openliberty.restfulWSClient-3.0, \
  io.openliberty.mpConfig-3.0, \
  io.openliberty.mpCompatible-5.0, \
  io.openliberty.org.eclipse.microprofile.rest.client-3.0, \
  com.ibm.websphere.appserver.org.reactivestreams.reactive-streams-1.0

-bundles=\
  io.openliberty.org.jboss.resteasy.cdi; apiJar=false; location:="lib/", \
  io.openliberty.org.jboss.resteasy.mprestclient; apiJar=false; location:="lib/"
kind=ga
edition=core
WLP-Activation-Type: parallel
