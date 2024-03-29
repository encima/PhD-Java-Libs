/**
 * Copyright 2005-2013 Restlet S.A.S.
 * 
 * The contents of this file are subject to the terms of one of the following
 * open source licenses: Apache 2.0 or LGPL 3.0 or LGPL 2.1 or CDDL 1.0 or EPL
 * 1.0 (the "Licenses"). You can select the license that you prefer but you may
 * not use this file except in compliance with one of these Licenses.
 * 
 * You can obtain a copy of the Apache 2.0 license at
 * http://www.opensource.org/licenses/apache-2.0
 * 
 * You can obtain a copy of the LGPL 3.0 license at
 * http://www.opensource.org/licenses/lgpl-3.0
 * 
 * You can obtain a copy of the LGPL 2.1 license at
 * http://www.opensource.org/licenses/lgpl-2.1
 * 
 * You can obtain a copy of the CDDL 1.0 license at
 * http://www.opensource.org/licenses/cddl1
 * 
 * You can obtain a copy of the EPL 1.0 license at
 * http://www.opensource.org/licenses/eclipse-1.0
 * 
 * See the Licenses for the specific language governing permissions and
 * limitations under the Licenses.
 * 
 * Alternatively, you can obtain a royalty free commercial license with less
 * limitations, transferable or non-transferable, directly at
 * http://www.restlet.com/products/restlet-framework
 * 
 * Restlet is a registered trademark of Restlet S.A.S.
 */

package org.restlet.test.engine;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.restlet.test.engine.connector.AsynchroneTestCase;
import org.restlet.test.engine.connector.HttpInboundRequestTestCase;
import org.restlet.test.engine.io.BioUtilsTestCase;
import org.restlet.test.engine.io.BufferTestCase;
import org.restlet.test.engine.io.ReaderInputStreamTestCase;
import org.restlet.test.engine.util.Base64TestCase;

/**
 * Suite of unit tests for the Restlet Framework.
 * 
 * @author Jerome Louvel
 */
public class EngineTestSuite extends TestSuite {

    /**
     * JUnit constructor.
     * 
     * @return The unit test.
     */
    public static Test suite() {
        return new EngineTestSuite();
    }

    /** Constructor. */
    public EngineTestSuite() {
        super("Engine package");
        addTestSuite(AlphaNumericComparatorTestCase.class);
        addTestSuite(AnnotationUtilsTestCase.class);
        addTestSuite(Base64TestCase.class);
        addTestSuite(BufferTestCase.class);
        addTestSuite(BioUtilsTestCase.class);
        addTestSuite(CookiesTestCase.class);
        addTestSuite(ContentTypeTestCase.class);
        addTestSuite(HeaderTestCase.class);
        addTestSuite(HttpCallTestCase.class);
        addTestSuite(AsynchroneTestCase.class);
        addTestSuite(HttpInboundRequestTestCase.class);
        addTestSuite(ImmutableDateTestCase.class);
        addTestSuite(UnclosableInputStreamTestCase.class);
        addTestSuite(UnclosableOutputStreamTestCase.class);
        addTestSuite(PreferencesTestCase.class);
        addTestSuite(ReaderInputStreamTestCase.class);

        // Tests based on HTTP client connectors are not supported by the GAE
        // edition.
        addTestSuite(AuthenticationTestCase.class);
        addTestSuite(TunnelFilterTestCase.class);
        addTestSuite(UserAgentTunnelFilterTestCase.class);

        addTest(ConnectorsTestSuite.suite());
    }
}
