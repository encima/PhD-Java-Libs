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

package org.restlet.ext.xstream;

import java.io.IOException;
import java.io.Writer;

import org.restlet.data.CharacterSet;
import org.restlet.data.MediaType;
import org.restlet.representation.Representation;
import org.restlet.representation.WriterRepresentation;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.HierarchicalStreamDriver;
import com.thoughtworks.xstream.io.json.JettisonMappedXmlDriver;
import com.thoughtworks.xstream.io.xml.DomDriver;

/**
 * Representation based on the XStream library. It can serialize and deserialize
 * automatically in XML. It also supports a bridge to JSON leveraging the
 * Jettison library.
 * 
 * @see <a href="http://xstream.codehaus.org/">XStream project</a>
 * @author Jerome Louvel
 * @param <T>
 *            The type to wrap.
 */
public class XstreamRepresentation<T> extends WriterRepresentation {

    /** The XStream JSON driver class. */
    private Class<? extends HierarchicalStreamDriver> jsonDriverClass;

    /** The (parsed) object to format. */
    private T object;

    /** The target class of the object to serialize. */
    private Class<T> targetClass;

    /** The representation to parse. */
    private Representation representation;

    /** The XStream XML driver class. */
    private Class<? extends HierarchicalStreamDriver> xmlDriverClass;

    /** The modifiable XStream object. */
    private XStream xstream;

    /**
     * Constructor.
     * 
     * @param mediaType
     *            The target media type.
     * @param object
     *            The object to format.
     */
    public XstreamRepresentation(MediaType mediaType, T object) {
        super(mediaType);
        this.object = object;
        this.representation = null;
        this.jsonDriverClass = JettisonMappedXmlDriver.class;
        this.xmlDriverClass = DomDriver.class;
        this.xstream = null;
    }

    /**
     * Constructor.
     * 
     * @param representation
     *            The representation to parse.
     * @deprecated Use {@link #XstreamRepresentation(Representation, Class)}
     *             instead.
     */
    public XstreamRepresentation(Representation representation) {
        this(representation, null);
    }

    /**
     * Constructor.
     * 
     * @param representation
     *            The representation to parse.
     * @param targetClass
     *            The target class of the object to serialize.
     */
    public XstreamRepresentation(Representation representation,
            Class<T> targetClass) {
        super(representation.getMediaType());
        this.object = null;
        this.targetClass = targetClass;
        this.representation = representation;
        this.jsonDriverClass = JettisonMappedXmlDriver.class;
        this.xmlDriverClass = DomDriver.class;
        this.xstream = null;
    }

    /**
     * Constructor. Uses the {@link MediaType#APPLICATION_XML} media type by
     * default.
     * 
     * @param object
     *            The object to format.
     */
    public XstreamRepresentation(T object) {
        this(MediaType.APPLICATION_XML, object);
    }

    /**
     * Creates an XStream object based on a media type. By default, it creates a
     * {@link HierarchicalStreamDriver} or a {@link DomDriver}.
     * 
     * @param mediaType
     *            The serialization media type.
     * @return The XStream object.
     * @throws IOException
     */
    protected XStream createXstream(MediaType mediaType) throws IOException {
        XStream result = null;

        try {
            if (MediaType.APPLICATION_JSON.isCompatible(mediaType)) {
                result = new XStream(getJsonDriverClass().newInstance());
                result.setMode(XStream.NO_REFERENCES);
            } else {
                result = new XStream(getXmlDriverClass().newInstance());
            }

            result.autodetectAnnotations(true);
        } catch (Exception e) {
            IOException ioe = new IOException(
                    "Unable to create the XStream driver: " + e.getMessage());
            ioe.initCause(e);
            throw ioe;
        }

        return result;
    }

    /**
     * Returns the XStream JSON driver class.
     * 
     * @return TXStream JSON driver class.
     */
    public Class<? extends HierarchicalStreamDriver> getJsonDriverClass() {
        return jsonDriverClass;
    }

    /**
     * Returns the wrapped object, deserializing the representation with XStream
     * if necessary.
     * 
     * @return The wrapped object.
     * @throws IOException
     */
    @SuppressWarnings("unchecked")
    public T getObject() throws IOException {
        T result = null;

        if (this.object != null) {
            getXstream().processAnnotations(this.object.getClass());
            result = this.object;
        } else if (this.representation != null) {
            if (this.targetClass != null) {
                getXstream().processAnnotations(this.targetClass);
            }

            result = (T) getXstream().fromXML(this.representation.getStream());
        }

        return result;
    }

    /**
     * Returns the XStream XML driver class.
     * 
     * @return The XStream XML driver class.
     */
    public Class<? extends HierarchicalStreamDriver> getXmlDriverClass() {
        return xmlDriverClass;
    }

    /**
     * Returns the modifiable XStream object. Useful to customize mappings.
     * 
     * @return The modifiable XStream object.
     * @throws IOException
     */
    public XStream getXstream() throws IOException {
        if (this.xstream == null) {
            this.xstream = createXstream(getMediaType());
        }

        return this.xstream;
    }

    /**
     * Sets the XStream JSON driver class.
     * 
     * @param jsonDriverClass
     *            The XStream JSON driver class.
     */
    public void setJsonDriverClass(
            Class<? extends HierarchicalStreamDriver> jsonDriverClass) {
        this.jsonDriverClass = jsonDriverClass;
    }

    /**
     * Sets the XStream XML driver class.
     * 
     * @param xmlDriverClass
     *            The XStream XML driver class.
     */
    public void setXmlDriverClass(
            Class<? extends HierarchicalStreamDriver> xmlDriverClass) {
        this.xmlDriverClass = xmlDriverClass;
    }

    /**
     * Sets the XStream object.
     * 
     * @param xstream
     *            The XStream object.
     */
    public void setXstream(XStream xstream) {
        this.xstream = xstream;
    }

    @Override
    public void write(Writer writer) throws IOException {
        if (representation != null) {
            representation.write(writer);
        } else if (object != null) {
            CharacterSet charSet = (getCharacterSet() == null) ? CharacterSet.ISO_8859_1
                    : getCharacterSet();

            if (!MediaType.APPLICATION_JSON.isCompatible(getMediaType())) {
                writer.append("<?xml version=\"1.0\" encoding=\""
                        + charSet.getName() + "\" ?>\n");
            }

            getXstream().toXML(object, writer);
        }
    }
}
