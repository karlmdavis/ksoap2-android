/* Copyright (c) 2003,2004, Stefan Haustein, Oberhausen, Rhld., Germany
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or
 * sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The  above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 * IN THE SOFTWARE. */

package org.ksoap2.serialization;

import java.util.*;
import java.io.*;
import org.ksoap2.*;
import org.xmlpull.v1.*;

/**
 * @author Stefan Haustein
 * 
 * This class extends the SoapEnvelope with Soap Serialization functionality.
 */
public class SoapSerializationEnvelope extends SoapEnvelope {
    static final Marshal DEFAULT_MARSHAL = new DM();
    public Hashtable properties = new Hashtable();

    Hashtable idMap = new Hashtable();
    Vector multiRef; // = new Vector();
    Vector types = new Vector();

    public boolean implicitTypes;

    /**
     * Set this variable to true for compatibility with what seems to be the
     * default encoding for .Net-Services. This feature is an extremely ugly
     * hack. A much better option is to change the configuration of the
     * .Net-Server to standard Soap Serialization!
     */

    public boolean dotNet;

    /**
     * Map from XML qualified names to Java classes
     */

    protected Hashtable qNameToClass = new Hashtable();

    /**
     * Map from Java class names to XML name and namespace pairs
     */

    protected Hashtable classToQName = new Hashtable();

    public SoapSerializationEnvelope(int version) {
        super(version);
        addMapping(enc, "Array", PropertyInfo.VECTOR_CLASS);
        DEFAULT_MARSHAL.register(this);
    }

    public void parseBody(XmlPullParser parser) throws IOException, XmlPullParserException {
        bodyIn = null;
        parser.nextTag();
        if (parser.getEventType() == XmlPullParser.START_TAG && parser.getNamespace().equals(env) && parser.getName().equals("Fault")) {
            SoapFault fault = new SoapFault();
            fault.parse(parser);
            bodyIn = fault;
        } else {
            while (parser.getEventType() == XmlPullParser.START_TAG) {
                String rootAttr = parser.getAttributeValue(enc, "root");
                Object o = read(parser, null, -1, parser.getNamespace(), parser.getName(), PropertyInfo.OBJECT_TYPE);
                if ("1".equals(rootAttr) || bodyIn == null)
                    bodyIn = o;
                parser.nextTag();
            }
        }
    }

    protected void readSerializable(XmlPullParser parser, KvmSerializable obj) throws IOException, XmlPullParserException {
        int testIndex = -1; // inc at beg. of loop for perf. reasons
        int propertyCount = obj.getPropertyCount();
        PropertyInfo info = new PropertyInfo();
        while (parser.nextTag() != XmlPullParser.END_TAG) {
            String name = parser.getName();
            int countdown = propertyCount;
            while (true) {
                if (countdown-- == 0)
                    throw new RuntimeException("Unknown Property: " + name);
                if (++testIndex >= propertyCount)
                    testIndex = 0;
                obj.getPropertyInfo(testIndex, properties, info);
                if (info.namespace == null && info.name.equals(name) || info.name == null && testIndex == 0 || info.name.equals(name) && info.namespace.equals(parser.getNamespace())) {
                    break;
                }
            }
            obj.setProperty(testIndex, read(parser, obj, testIndex, null, null, info));
        }
        parser.require(XmlPullParser.END_TAG, null, null);
    }

    /**
     * If the type of the object cannot be determined, and thus no Marshal class
     * can handle the object, this method is called. It will build either a
     * SoapPrimitive or a SoapObject
     * 
     * @param parser
     * @param typeNamespace
     * @param typeName
     * @return unknownObject wrapped as a SoapPrimitive or SoapObject
     * @throws IOException
     * @throws XmlPullParserException
     */

    protected Object readUnknown(XmlPullParser parser, String typeNamespace, String typeName) throws IOException, XmlPullParserException {
        String name = parser.getName();
        String namespace = parser.getNamespace();
        parser.next(); // move to text, inner start tag or end tag
        Object result = null;
        String text = null;
        if (parser.getEventType() == XmlPullParser.TEXT) {
            text = parser.getText();
            result = new SoapPrimitive(typeNamespace, typeName, text);
            parser.next();
        } else if (parser.getEventType() == XmlPullParser.END_TAG) {
            result = new SoapObject(typeNamespace, typeName);
        }

        if (parser.getEventType() == XmlPullParser.START_TAG) {
            if (text != null && text.trim().length() != 0) {
                throw new RuntimeException("Malformed input: Mixed content");
            }
            SoapObject so = new SoapObject(typeNamespace, typeName);
            while (parser.getEventType() != XmlPullParser.END_TAG) {
                so.addProperty(parser.getName(), read(parser, so, so.getPropertyCount(), null, null, PropertyInfo.OBJECT_TYPE));
                parser.nextTag();
            }
            result = so;
        }
        parser.require(XmlPullParser.END_TAG, namespace, name);
        return result;
    }

    private int getIndex(String value, int start, int dflt) {
        if (value == null)
            return dflt;
        return value.length() - start < 3 ? dflt : Integer.parseInt(value.substring(start + 1, value.length() - 1));
    }

    protected void readVector(XmlPullParser parser, Vector v, PropertyInfo elementType) throws IOException, XmlPullParserException {
        String namespace = null;
        String name = null;
        int size = v.size();
        boolean dynamic = true;
        String type = parser.getAttributeValue(enc, "arrayType");
        if (type != null) {
            int cut0 = type.indexOf(':');
            int cut1 = type.indexOf("[", cut0);
            name = type.substring(cut0 + 1, cut1);
            String prefix = cut0 == -1 ? "" : type.substring(0, cut0);
            namespace = parser.getNamespace(prefix);
            size = getIndex(type, cut1, -1);
            if (size != -1) {
                v.setSize(size);
                dynamic = false;
            }
        }
        if (elementType == null)
            elementType = PropertyInfo.OBJECT_TYPE;
        parser.nextTag();
        int position = getIndex(parser.getAttributeValue(enc, "offset"), 0, 0);
        while (parser.getEventType() != XmlPullParser.END_TAG) {
            // handle position
            position = getIndex(parser.getAttributeValue(enc, "position"), 0, position);
            if (dynamic && position >= size) {
                size = position + 1;
                v.setSize(size);
            }
            // implicit handling of position exceeding specified size
            v.setElementAt(read(parser, v, position, namespace, name, elementType), position);
            position++;
            parser.nextTag();
        }
        parser.require(XmlPullParser.END_TAG, null, null);
    }

    /**
     * Builds an object from the XML stream. This method is public for usage in
     * conjuction with Marshal subclasses. Precondition: On the start tag of the
     * object or property, so href can be read.
     */

    public Object read(XmlPullParser parser, Object owner, int index, String namespace, String name, PropertyInfo expected) throws IOException, XmlPullParserException {
        String elementName = parser.getName();
        String href = parser.getAttributeValue(null, "href");
        Object obj;
        if (href != null) {
            if (owner == null)
                throw new RuntimeException("href at root level?!?");
            href = href.substring(1);
            obj = idMap.get(href);
            if (obj == null || obj instanceof FwdRef) {
                FwdRef f = new FwdRef();
                f.next = (FwdRef) obj;
                f.obj = owner;
                f.index = index;
                idMap.put(href, f);
                obj = null;
            }
            parser.nextTag(); // start tag
            parser.require(XmlPullParser.END_TAG, null, elementName);
        } else {
            String nullAttr = parser.getAttributeValue(xsi, "nil");
            String id = parser.getAttributeValue(null, "id");
            if (nullAttr == null)
                nullAttr = parser.getAttributeValue(xsi, "null");
            if (nullAttr != null && SoapEnvelope.stringToBoolean(nullAttr)) {
                obj = null;
                parser.nextTag();
                parser.require(XmlPullParser.END_TAG, null, elementName);
            } else {
                String type = parser.getAttributeValue(xsi, "type");
                if (type != null) {
                    int cut = type.indexOf(':');
                    name = type.substring(cut + 1);
                    String prefix = cut == -1 ? "" : type.substring(0, cut);
                    namespace = parser.getNamespace(prefix);
                } else if (name == null && namespace == null) {
                    if (parser.getAttributeValue(enc, "arrayType") != null) {
                        namespace = enc;
                        name = "Array";
                    } else {
                        Object[] names = getInfo(expected.type, null);
                        namespace = (String) names[0];
                        name = (String) names[1];
                    }
                }
                obj = readInstance(parser, namespace, name, expected);
                if (obj == null)
                    obj = readUnknown(parser, namespace, name);
            }
            // finally, care about the id....
            if (id != null) {
                Object hlp = idMap.get(id);
                if (hlp instanceof FwdRef) {
                    FwdRef f = (FwdRef) hlp;
                    do {
                        if (f.obj instanceof KvmSerializable)
                            ((KvmSerializable) f.obj).setProperty(f.index, obj);
                        else
                            ((Vector) f.obj).setElementAt(obj, f.index);
                        f = f.next;
                    } while (f != null);
                } else if (hlp != null)
                    throw new RuntimeException("double ID");
                idMap.put(id, obj);
            }
        }

        parser.require(XmlPullParser.END_TAG, null, elementName);
        return obj;
    }

    /**
     * Returns a new object read from the given parser. If no mapping is found,
     * null is returned. This method is used by the SoapParser in order to
     * convert the XML code to Java objects.
     */

    public Object readInstance(XmlPullParser parser, String namespace, String name, PropertyInfo expected) throws IOException, XmlPullParserException {
        Object obj = qNameToClass.get(new SoapPrimitive(namespace, name, null));
        if (obj == null)
            return null;
        if (obj instanceof Marshal)
            return ((Marshal) obj).readInstance(parser, namespace, name, expected);
        if (obj instanceof SoapObject)
            obj = ((SoapObject) obj).newInstance();
        else
            try {
                obj = ((Class) obj).newInstance();
            } catch (Exception e) {
                throw new RuntimeException(e.toString());
            }
        // ok, obj is now the instance, fill it....
        if (obj instanceof KvmSerializable)
            readSerializable(parser, (KvmSerializable) obj);
        else if (obj instanceof Vector)
            readVector(parser, (Vector) obj, expected.elementType);
        else
            throw new RuntimeException("no deserializer for " + obj.getClass());
        return obj;
    }

    /**
     * Returns a string array containing the namespace, name, id and Marshal
     * object for the given java object. This method is used by the SoapWriter
     * in order to map Java objects to the corresponding SOAP section five XML
     * code.
     */

    public Object[] getInfo(Object type, Object instance) {
        if (type == null) {
            if (instance instanceof SoapObject || instance instanceof SoapPrimitive)
                type = instance;
            else
                type = instance.getClass();
        }
        if (type instanceof SoapObject) {
            SoapObject so = (SoapObject) type;
            return new Object[] { so.getNamespace(), so.getName(), null, null };
        }
        if (type instanceof SoapPrimitive) {
            SoapPrimitive sp = (SoapPrimitive) type;
            return new Object[] { sp.getNamespace(), sp.getName(), null, DEFAULT_MARSHAL };
        }
        if ((type instanceof Class) && type != PropertyInfo.OBJECT_CLASS) {
            Object[] tmp = (Object[]) classToQName.get(((Class) type).getName());
            if (tmp != null)
                return tmp;
        }
        return new Object[] { xsd, "anyType", null, null };
    }

    /**
     * Defines a direct mapping from a namespace and name to a java class (and
     * vice versa), using the given marshal mechanism
     */

    public void addMapping(String namespace, String name, Class clazz, Marshal marshal) {
        qNameToClass.put(new SoapPrimitive(namespace, name, null), marshal == null ? (Object) clazz : marshal);
        classToQName.put(clazz.getName(), new Object[] { namespace, name, null, marshal });
    }

    /**
     * Defines a direct mapping from a namespace and name to a java class (and
     * vice versa)
     */

    public void addMapping(String namespace, String name, Class clazz) {
        addMapping(namespace, name, clazz, null);
    }

    /**
     * Adds a SoapObject to the class map. During parsing, objects of the given
     * type (namespace/name) will be mapped to corresponding copies of the given
     * SoapObject, maintaining the structure of the template.
     */

    public void addTemplate(SoapObject so) {
        qNameToClass.put(new SoapPrimitive(so.namespace, so.name, null), so);
    }

    public Object getResult() {
        KvmSerializable ks = (KvmSerializable) bodyIn;
        return ks.getPropertyCount() == 0 ? null : ks.getProperty(0);
    }

    /** Serializes the given object */

    public void writeBody(XmlSerializer writer) throws IOException {
        multiRef = new Vector();
        multiRef.addElement(bodyOut);
        types.addElement(PropertyInfo.OBJECT_TYPE);
        for (int i = 0; i < multiRef.size(); i++) {
            Object obj = multiRef.elementAt(i);
            Object[] qName = getInfo(null, obj);
            writer.startTag((String) qName[0], (String) qName[1]);
            writer.attribute(null, "id", qName[2] == null ? ("o" + i) : (String) qName[2]);
            if (i == 0)
                writer.attribute(enc, "root", "1");
            if (qName[3] != null)
                ((Marshal) qName[3]).writeInstance(writer, obj);
            else if (obj instanceof KvmSerializable)
                writeObjectBody(writer, (KvmSerializable) obj);
            else if (obj instanceof Vector)
                writeVectorBody(writer, (Vector) obj, ((PropertyInfo) types.elementAt(i)).elementType);
            else
                throw new RuntimeException("Cannot serialize: " + obj);
            writer.endTag((String) qName[0], (String) qName[1]);
        }
    }

    /**
     * Writes the body of an KvmSerializable object. This method is public for
     * access from Marshal subclasses.
     */

    public void writeObjectBody(XmlSerializer writer, KvmSerializable obj) throws IOException {
        PropertyInfo info = new PropertyInfo();
        int cnt = obj.getPropertyCount();
        String namespace = dotNet ? writer.getNamespace() : ""; // unused...curious
        for (int i = 0; i < cnt; i++) {
            obj.getPropertyInfo(i, properties, info);
            if ((info.flags & PropertyInfo.TRANSIENT) == 0) {
                // TODO: looks broken here
                String nsp = info.namespace;
                if (nsp == null)
                    nsp = info.namespace;
                writer.startTag(nsp, info.name);
                writeProperty(writer, obj.getProperty(i), info);
                writer.endTag(nsp, info.name);
            }
        }
    }

    protected void writeProperty(XmlSerializer writer, Object obj, PropertyInfo type) throws IOException {
        if (obj == null) {
            writer.attribute(xsi, version >= VER12 ? "nil" : "null", "true");
            return;
        }
        Object[] qName = getInfo(null, obj);
        if (type.multiRef || qName[2] != null) {
            int i = multiRef.indexOf(obj);
            if (i == -1) {
                i = multiRef.size();
                multiRef.addElement(obj);
                types.addElement(type);
            }
            writer.attribute(null, "href", qName[2] == null ? ("#o" + i) : "#" + qName[2]);
        } else {
            if (!implicitTypes || obj.getClass() != type.type) {
                String prefix = writer.getPrefix((String) qName[0], true);
                writer.attribute(xsi, "type", prefix + ":" + qName[1]);
            }
            if (qName[3] != null)
                ((Marshal) qName[3]).writeInstance(writer, obj);
            else if (obj instanceof KvmSerializable)
                writeObjectBody(writer, (KvmSerializable) obj);
            else if (obj instanceof Vector)
                writeVectorBody(writer, (Vector) obj, type.elementType);
            else
                throw new RuntimeException("Cannot serialize: " + obj);
        }
    }

    protected void writeVectorBody(XmlSerializer writer, Vector vector, PropertyInfo elementType) throws IOException {
        if (elementType == null)
            elementType = PropertyInfo.OBJECT_TYPE;
        int cnt = vector.size();
        Object[] arrType = getInfo(elementType.type, null);
        writer.attribute(enc, "arrayType", writer.getPrefix((String) arrType[0], false) + ":" + arrType[1] + "[" + cnt + "]");
        boolean skipped = false;
        for (int i = 0; i < cnt; i++) {
            if (vector.elementAt(i) == null)
                skipped = true;
            else {
                writer.startTag(null, "item");
                if (skipped) {
                    writer.attribute(enc, "position", "[" + i + "]");
                    skipped = false;
                }
                writeProperty(writer, vector.elementAt(i), elementType);
                writer.endTag(null, "item");
            }
        }
    }

}
