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

package org.restlet.test.bench;

import java.io.IOException;
import java.util.logging.Level;

import org.restlet.Client;
import org.restlet.engine.ConnectorHelper;
import org.restlet.engine.Engine;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;

public class TestGetClient {

    public static void main(String[] args) throws IOException {
        ConnectorHelper<Client> helper;
        helper = new org.restlet.engine.connector.HttpClientHelper(null);
        Engine.getInstance().getRegisteredClients().add(0, helper);
        Engine.setLogLevel(Level.FINE);
        long startTime = System.currentTimeMillis();

        ClientResource resource = new ClientResource("http://localhost:8554/");
        try {
            Representation entity = resource.get();
            System.out.println("Status: " + resource.getStatus());

            long expectedSize = entity.getSize();
            long receivedSize = entity.exhaust();

            System.out.println("Size expected: " + expectedSize);
            System.out.println("Size consumed: " + receivedSize);

            if ((expectedSize != -1) && (expectedSize != receivedSize)) {
                System.out.println("ERROR: SOME BYTES WERE LOST!");
            }
        } catch (ResourceException e) {
            System.out.println("Status: " + resource.getStatus());
        }

        long endTime = System.currentTimeMillis();
        System.out.println("Duration: " + (endTime - startTime) + " ms");
    }

}
