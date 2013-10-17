================================================
Edition for Java SE - dependencies between JAR files
================================================


Below is a list of the dependencies between Restlet libraries. You need to ensure 
that all the dependencies of the libraries that you are using are on the classpath
of your Restlet program, otherwise ClassNotFound exceptions will be thrown.

A minimal Restlet application requires the org.restlet JAR.

To configure connectors such as HTTP server or HTTP client connectors, please refer
to the Restlet User Guide: http://wiki.restlet.org/docs_2.1/

org.restlet.ext.atom (Restlet Extension - Atom)
--------------------
 - nothing beside org.restlet JAR.

org.restlet (Restlet Core - API and Engine)
-----------
 - J2SE 5.0

org.restlet.test (Restlet Unit Tests)
----------------
 - org.junit_4.8

org.restlet.ext.crypto (Restlet Extension - Crypto)
----------------------
 - nothing beside org.restlet JAR.

org.restlet.ext.emf (Restlet Extension - EMF)
-------------------
 - org.eclipse.emf.common_2.6
 - org.eclipse.emf.ecore_2.6
 - org.eclipse.emf.ecore.xmi_2.6

org.restlet.example (Restlet examples)
-------------------
 - com.db4o_7.12
 - com.db4o.instrumentation_7.12
 - com.db4o.nativequery_7.12
 - com.db4o.optional_7.12
 - com.db4o.ta_7.12
 - com.db4o.tools_7.12
 - org.junit_4.8
 - com.sun.syndication_1.0
 - org.testng_6.7

org.restlet.ext.fileupload (Restlet Extension - FileUpload)
--------------------------
 - org.apache.commons.fileupload_1.2
 - javax.servlet_2.5

org.restlet.ext.freemarker (Restlet Extension - FreeMarker)
--------------------------
 - org.freemarker_2.3

org.restlet.ext.gwt (Restlet Extension - GWT)
-------------------
 - com.google.gwt.server_2.3

org.restlet.ext.html (Restlet Extension - HTML)
--------------------
 - nothing beside org.restlet JAR.

org.restlet.ext.httpclient (Restlet Extension - Apache HTTP Client)
--------------------------
 - org.apache.commons.codec_1.5
 - org.apache.httpclient_4.1
 - org.apache.httpcore_4.1
 - org.apache.httpmime_4.1
 - net.jcip.annotations_1.0
 - org.apache.commons.logging_1.1
 - org.apache.james.mime4j_0.6

org.restlet.ext.jaas (Restlet Extension - JAAS)
--------------------
 - nothing beside org.restlet JAR.

org.restlet.ext.jackson (Restlet Extension - Jackson)
-----------------------
 - org.codehaus.jackson.core_1.9
 - org.codehaus.jackson.mapper_1.9

org.restlet.ext.javamail (Restlet Extension - JavaMail)
------------------------
 - javax.activation_1.1
 - javax.mail_1.4

org.restlet.ext.jaxb (Restlet Extension - JAXB)
--------------------
 - javax.xml.bind_2.1
 - com.sun.jaxb_2.1
 - javax.xml.stream_1.0

org.restlet.ext.jaxrs (Restlet Extension - JAX-RS)
---------------------
 - javax.activation_1.1
 - org.apache.commons.fileupload_1.2
 - javax.mail_1.4
 - javax.xml.bind_2.1
 - com.sun.jaxb_2.1
 - javax.ws.rs_1.0
 - org.json_2.0
 - javax.servlet_2.5
 - javax.xml.stream_1.0

org.restlet.ext.jdbc (Restlet Extension - JDBC)
--------------------
 - org.apache.commons.dbcp_1.3
 - org.apache.commons.pool_1.5

org.restlet.ext.jetty (Restlet Extension - Jetty)
---------------------
 - org.eclipse.jetty.ajp_7.6
 - org.eclipse.jetty.continuation_7.6
 - org.eclipse.jetty.http_7.6
 - org.eclipse.jetty.io_7.6
 - org.eclipse.jetty.server_7.6
 - org.eclipse.jetty.util_7.6
 - javax.servlet_2.5

org.restlet.ext.jibx (Restlet Extension - JiBX)
--------------------
 - org.jibx.runtime_1.2

org.restlet.ext.json (Restlet Extension - JSON)
--------------------
 - org.json_2.0

org.restlet.ext.lucene (Restlet Extension - Lucene)
----------------------
 - org.apache.commons.io_2.0
 - org.apache.lucene_2.9
 - org.apache.solr_1.4
 - org.apache.solr.common_1.4
 - org.apache.tika_0.6
 - org.apache.tika.parsers_0.6

org.restlet.ext.net (Restlet Extension - Net)
-------------------
 - nothing beside org.restlet JAR.

org.restlet.ext.oauth (Restlet Extension - OAuth)
---------------------
 - org.apache.commons.codec_1.5
 - org.apache.commons.dbcp_1.3
 - org.freemarker_2.3
 - org.json_2.0
 - net.oauth_1.0
 - org.apache.commons.pool_1.5

org.restlet.ext.odata (Restlet Extension - OData)
---------------------
 - org.freemarker_2.3

org.restlet.ext.openid (Restlet Extension - OpenID)
----------------------
 - org.apache.httpclient_4.1
 - net.sourceforge.nekohtml_1.9
 - org.openid4java_0.9

org.restlet.ext.rdf (Restlet Extension - RDF)
-------------------
 - nothing beside org.restlet JAR.

org.restlet.ext.rome (Restlet Extension - ROME)
--------------------
 - org.jdom_1.1
 - com.sun.syndication_1.0

org.restlet.ext.sdc (Restlet Extension - SDC)
-------------------
 - com.google.collections_1.0
 - com.google.gdata_1.0
 - com.google.gdata.client_1.0
 - com.google.guice_2.0
 - org.apache.log4j_1.2
 - com.google.protobuf_2.2
 - com.google.sdc_1.3
 - com.thoughtworks.xstream_1.4

org.restlet.ext.simple (Restlet Extension - Simple)
----------------------
 - org.simpleframework_4.1

org.restlet.ext.sip (Restlet Extension - SIP)
-------------------
 - org.junit_4.8

org.restlet.ext.slf4j (Restlet Extension - SLF4J)
---------------------
 - org.slf4j_1.6

org.restlet.ext.spring (Restlet Extension - Spring Framework)
----------------------
 - net.sf.cglib_2.2
 - org.apache.commons.logging_1.1
 - org.springframework.asm_3.0
 - org.springframework.beans_3.0
 - org.springframework.context_3.0
 - org.springframework.core_3.0
 - org.springframework.expression_3.0
 - org.springframework.web_3.0
 - org.springframework.webmvc_3.0

org.restlet.ext.ssl (Restlet Extension - SSL)
-------------------
 - org.jsslutils_1.0

org.restlet.ext.velocity (Restlet Extension - Velocity)
------------------------
 - org.apache.commons.collections_3.2
 - org.apache.commons.lang_2.6
 - org.apache.velocity_1.6

org.restlet.ext.wadl (Restlet Extension - WADL)
--------------------
 - nothing beside org.restlet JAR.

org.restlet.ext.xml (Restlet Extension - XML)
-------------------
 - nothing beside org.restlet JAR.

org.restlet.ext.xstream (Restlet Extension - XStream)
-----------------------
 - org.codehaus.jettison_1.3
 - javax.xml.stream_1.0
 - com.thoughtworks.xstream_1.4
