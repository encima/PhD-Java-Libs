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

package org.restlet.security;

import java.security.Principal;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.restlet.Application;

/**
 * Application specific role. Common examples are "administrator", "user",
 * "anonymous", "supervisor". Note that for reusability purpose, it is
 * recommended that those role don't reflect an actual organization, but more
 * the functional requirements of your application.
 * 
 * Also, two roles are not considered equals if they have the same the name and
 * child roles, they need to be the same Java object. If you need to reuse the
 * same role, you should call {@link Application#getRole(String)} method
 * instead.
 * 
 * @author Jerome Louvel
 */
public class Role implements Principal {

    /**
     * Unmodifiable role that covers all existing roles. Its name is "*" by
     * convention.
     */
    public static final Role ALL = new Role("*",
            "Role that covers all existing roles.") {
        @Override
        public void setDescription(String description) {
            throw new IllegalStateException("Unmodifiable role");
        }

        @Override
        public void setName(String name) {
            throw new IllegalStateException("Unmodifiable role");
        }

    };

    /** The modifiable list of child roles. */
    private final List<Role> childRoles;

    /** The description. */
    private volatile String description;

    /** The name. */
    private volatile String name;

    /**
     * Default constructor.
     */
    public Role() {
        this(null, null);
    }

    /**
     * Constructor.
     * 
     * @param name
     *            The name.
     */
    public Role(String name) {
        this(name, null);
    }

    /**
     * Constructor.
     * 
     * @param name
     *            The name.
     * @param description
     *            The description.
     */
    public Role(String name, String description) {
        this.name = name;
        this.description = description;
        this.childRoles = new CopyOnWriteArrayList<Role>();
    }

    /**
     * Returns the modifiable list of child roles.
     * 
     * @return The modifiable list of child roles.
     */
    public List<Role> getChildRoles() {
        return childRoles;
    }

    /**
     * Returns the description.
     * 
     * @return The description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the name.
     * 
     * @return The name.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the modifiable list of child roles. This method clears the current
     * list and adds all entries in the parameter list.
     * 
     * @param childRoles
     *            A list of child roles.
     */
    public void setChildRoles(List<Role> childRoles) {
        synchronized (getChildRoles()) {
            if (childRoles != getChildRoles()) {
                getChildRoles().clear();

                if (childRoles != null) {
                    getChildRoles().addAll(childRoles);
                }
            }
        }
    }

    /**
     * Sets the description.
     * 
     * @param description
     *            The description.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Sets the name.
     * 
     * @param name
     *            The name.
     */
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return getName();
    }

}