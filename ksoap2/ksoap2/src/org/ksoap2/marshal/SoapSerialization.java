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

    ClassMap classMap;
    Hashtable idMap = new Hashtable();
    Vector multiRef = new Vector();
    Vector types = new Vector();

    public SoapSerialization(int version, ClassMap classMap) {
        super(version);
        this.classMap = classMap;
    }

    public void parseBody(XmlPullParser parser) throws IOException, XmlPullParserException {

        body = null;

        //System.out.println ("start parsing....");

        while (true) {
            parser.nextTag();
            int type = parser.getEventType();

            if (type == parser.END_TAG || type == parser.END_DOCUMENT)
                break;

            //  String name = namespaceMap.getPackage (start.getNamespace ())
            //  + "." + start.getName ();

            String rootAttr = parser.getAttributeValue(classMap.enc, "root");

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
            getIndex(parser.getAttributeValue(enc, "position"), 0, position);

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
                    Object[] names = classMap.getInfo(expected.type, null);

                    namespace = (String) names[0];
                    name = (String) names[1];

                    //  System.out.println ("getInfo for "+expected.type+": {"+namespace+"}"+name);
                }
            }

            obj = classMap.readInstance(this, parser, namespace, name, expected);

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

}
