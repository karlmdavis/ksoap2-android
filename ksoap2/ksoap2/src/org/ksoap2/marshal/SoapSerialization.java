package org.ksoap2.marshal;

import java.util.*;
import java.io.*;
import org.ksoap2.*;
import org.kobjects.serialization.*;
import org.xmlpull.v1.*;

/**
 * @author Stefan Haustein
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 */
public class SoapSerialization extends SoapEnvelope {

    static final Marshal DEFAULT_MARSHAL = new DM();

    Hashtable idMap = new Hashtable();
    Vector multiRef = new Vector();
    Vector types = new Vector();

    public boolean implicitTypes;


    /** 
     * Map from XML qualified names to Java classes */

    protected Hashtable qNameToClass = new Hashtable();

    /** 
     * Map from Java class names to XML name and namespace pairs */

    protected Hashtable classToQName = new Hashtable();

    public SoapSerialization(int version) {
        super(version);
        addMapping(enc, "Array", ElementType.VECTOR_CLASS);
        DEFAULT_MARSHAL.register(this);
    }

    public void parseBody(XmlPullParser parser)
        throws IOException, XmlPullParserException {

        body = null;

        //System.out.println ("start parsing....");

        while (true) {
            parser.nextTag();
            int type = parser.getEventType();

            if (type == parser.END_TAG || type == parser.END_DOCUMENT)
                break;

            //  String name = namespaceMap.getPackage (start.getNamespace ())
            //  + "." + start.getName ();

            String rootAttr = parser.getAttributeValue(enc, "root");

            Object o =
                read(
                    parser,
                    null,
                    -1,
                    parser.getNamespace(),
                    parser.getName(),
                    ElementType.OBJECT_TYPE);

            if ("1".equals(rootAttr) || body == null)
                body = o;
        }

        //System.out.println ("leaving root read");
    }

    protected void readSerializable(XmlPullParser parser, KvmSerializable obj)
        throws IOException, XmlPullParserException {

        int testIndex = -1; // inc at beg. of loop for perf. reasons
        int sourceIndex = 0;
        int cnt = obj.getPropertyCount();
        PropertyInfo info = new PropertyInfo();

        while (true) {
            if (parser.nextTag() == parser.END_TAG)
                break;

            String name = parser.getName();
            //      System.out.println ("tagname:"+name);

            int countdown = cnt;

            while (true) {
                if (countdown-- == 0)
                    throw new RuntimeException("Unknwon Property: " + name);

                if (++testIndex >= cnt)
                    testIndex = 0;

                obj.getPropertyInfo(testIndex, info);
                if (info.name == null
                    ? testIndex == sourceIndex
                    : info.name.equals(name))
                    break;

            }

            obj.setProperty(
                testIndex,
                read(parser, obj, testIndex, null, null, info));

            sourceIndex = 0;
        }

        parser.require(parser.END_TAG, null, null);
    }

    protected Object readUnknown(
        XmlPullParser parser,
        String namespace,
        String name)
        throws IOException, XmlPullParserException {

        parser.next(); // start tag

        Object result;

        if (parser.getEventType() == parser.TEXT)
            result = new SoapPrimitive(namespace, name, parser.getText());
        else {
            SoapObject so = new SoapObject(namespace, name);

            while (parser.getEventType() != parser.END_TAG) {
                so.addProperty(
                    parser.getName(),
                    read(
                        parser,
                        so,
                        so.getPropertyCount(),
                        null,
                        null,
                        ElementType.OBJECT_TYPE));
                parser.next();
            }
            result = so;
        }

        parser.require(parser.END_TAG, null, null);

        return result;
    }

    private int getIndex(String value, int start, int dflt) {
        if (value == null)
            return dflt;

        return value.length() - start < 3
            ? dflt
            : Integer.parseInt(value.substring(start + 1, value.length() - 1));
    }

    /** Reads a vector. Precondition: on "outer" start tag! */

    protected void readVector(
        XmlPullParser parser,
        Vector v,
        ElementType elementType)
        throws IOException, XmlPullParserException {

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
            elementType = ElementType.OBJECT_TYPE;

        parser.nextTag();

        int position = getIndex(parser.getAttributeValue(enc, "offset"), 0, 0);

        while (parser.getEventType() != parser.END_TAG) {
            // handle position

            position =
                getIndex(
                    parser.getAttributeValue(enc, "position"),
                    0,
                    position);

            if (dynamic && position >= size) {
                size = position + 1;
                v.setSize(size);
            }

            // implicit handling of position exceeding specified size
            v.setElementAt(
                read(parser, v, position, namespace, name, elementType),
                position);

            position++;
            parser.nextTag();
        }

        parser.require(parser.END_TAG, null, null);
    }

    /** Builds an object from the XML stream. This method
    is public for usage in conjuction with Marshal subclasses. 
    Precondition: On the start tag of the object or property, 
    so href can be read. */

    protected Object read(
        XmlPullParser parser,
        Object owner,
        int index,
        String namespace,
        String name,
        ElementType expected)
        throws IOException, XmlPullParserException {

        //  System.out.println ("in read");

        // determine wire element type

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
            parser.require(parser.END_TAG, null, null);
        }
        else {
            String nullAttr = parser.getAttributeValue(xsi, "nil");

            if (nullAttr == null)
                nullAttr = parser.getAttributeValue(xsi, "null");

            if (nullAttr != null && SoapEnvelope.stringToBoolean(nullAttr)) {
                obj = null;
                parser.nextTag();
                parser.require(parser.END_TAG, null, null);
            }
            else {
                String type = parser.getAttributeValue(xsi, "type");

                if (type != null) {
                    int cut = type.indexOf(':');

                    name = type.substring(cut + 1);
                    String prefix = cut == -1 ? "" : type.substring(0, cut);
                    namespace = parser.getNamespace(prefix);
                }
                else if (name == null && namespace == null) {
                    if (parser.getAttributeValue(enc, "arrayType") != null) {
                        namespace = enc;
                        name = "Array";
                    }
                    else {
                        Object[] names = getInfo(expected.type, null);

                        namespace = (String) names[0];
                        name = (String) names[1];

                        //  System.out.println ("getInfo for "+expected.type+": {"+namespace+"}"+name);
                    }
                }

                obj =
                    readInstance(
                        parser,
                        namespace,
                        name,
                        expected);

                if (obj == null)
                    obj = readUnknown(parser, namespace, name);
            }

            // finally, care about the id....
            String id = parser.getAttributeValue(null, "id");

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
                    }
                    while (f != null);
                }
                else if (hlp != null)
                    throw new RuntimeException("double ID");

                idMap.put(id, obj);
            }
        }

        //  System.out.println ("leaving read");

        return obj;
    }

    /** 
     * Returns a new object read from the given parser.  If no
     * mapping is found, null is returned.  This method is used by
     * the SoapParser in order to convert the XML code to Java
     * objects. */

    public Object readInstance(
        XmlPullParser parser,
        String namespace,
        String name,
        ElementType expected)
        throws IOException, XmlPullParserException {

        /*  if (xsdNamespace.equals (namespace)) {
            if ("int".equals (name)) 
            return new Integer (Integer.parseInt (readText (parser)));
            else if ("long".equals (name))
            return new Long (Long.parseLong (readText (parser)));
            else if ("string".equals (name))
            return readText (parser);
            else if ("boolean".equals (name)) 
            return new Boolean (SoapEnvelope.stringToBoolean (readText (parser)));
            }*/

        Class clazz = null;

        Object obj = qNameToClass.get(new SoapPrimitive(namespace, name, null));

        if (obj == null)
            return null;

        if (obj instanceof Marshal)
            return ((Marshal) obj).readInstance(
                parser,
                namespace,
                name,
                expected);

        if (obj instanceof SoapObject)
            obj = ((SoapObject) obj).newInstance();
        else
            try {
                obj = ((Class) obj).newInstance();
            }
            catch (Exception e) {
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
     * Returns a string array containing the namespace, name, id and
     * Marshal object for the given java object. This method is used
     * by the SoapWriter in order to map Java objects to the
     * corresponding SOAP section five XML code.*/

    public Object[] getInfo(Object type, Object instance) {

        if (type == null) {
            if (instance instanceof SoapObject
                || instance instanceof SoapPrimitive)
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
            return new Object[] {
                sp.getNamespace(),
                sp.getName(),
                null,
                DEFAULT_MARSHAL };
        }

        if ((type instanceof Class) && type != ElementType.OBJECT_CLASS) {

            Object[] tmp =
                (Object[]) classToQName.get(((Class) type).getName());

            if (tmp != null)
                return tmp;
        }

        return new Object[] { xsd, "anyType", null, null };
    }

    /** 
     * Defines a direct mapping from a namespace and name to a java
     * class (and vice versa), using the given marshal mechanism */

    public void addMapping(
        String namespace,
        String name,
        Class clazz,
        Marshal marshal) {

        qNameToClass.put(
            new SoapPrimitive(namespace, name, null),
            marshal == null ? (Object) clazz : marshal);

        classToQName.put(
            clazz.getName(),
            new Object[] { namespace, name, null, marshal });

        //    if (prefixMap.getPrefix(namespace) == null)
        //       prefixMap = new PrefixMap(prefixMap, "n" + (cnt++), namespace);
    }

    /** Defines a direct mapping from a namespace and name to a java
        class (and vice versa) */

    public void addMapping(String namespace, String name, Class clazz) {

        addMapping(namespace, name, clazz, null);
    }

    /** 
     * Adds a SoapObject to the class map. During parsing,
     * objects of the given type (namespace/name) will be
     * mapped to corresponding copies of the given SoapObject,
     * maintaining the structure of the template. */

    public void addTemplate(SoapObject so) {

        qNameToClass.put(new SoapPrimitive(so.namespace, so.name, null), so);

        //     if (prefixMap.getPrefix(so.namespace) == null)
        //        prefixMap = new PrefixMap(prefixMap, "n" + (cnt++), so.namespace);
    }

}
