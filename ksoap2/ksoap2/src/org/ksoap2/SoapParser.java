/* kSOAP
 *
 * The contents of this file are subject to the Enhydra Public License
 * Version 1.1 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License
 * on the Enhydra web site ( http://www.enhydra.org/ ).
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See
 * the License for the specific terms governing rights and limitations
 * under the License.
 *
 * The Initial Developer of kSOAP is Stefan Haustein. Copyright (C)
 * 2000, 2001 Stefan Haustein, D-46045 Oberhausen (Rhld.),
 * Germany. All Rights Reserved.
 *
 * Contributor(s): John D. Beatty, F. Hunter, Renaud Tognelli, Sean McDaniel
 *
 * */

package org.ksoap2;

import java.util.*;
import java.io.*;

import org.xmlpull.v1.*;
import org.kobjects.serialization.*;

/** A SOAP parser. Limitations:
    <ul>
    <li>Multi-Dimensional Arrays are not supported</li>
    <li>The hrefs must be local</li>
    </ul>

      */

public class SoapParser {

    ClassMap classMap;
    Hashtable idMap = new Hashtable();
    public XmlPullParser parser;

    class FwdRef {
        FwdRef next;
        Object obj;
        int index;
    }

    public SoapParser(XmlPullParser parser, ClassMap classMap) {

        this.parser = parser;
        this.classMap = classMap;
    }

    /** 
     * Extracts namespace and name and calls readBody for actual reading */

    public Object read() throws IOException, XmlPullParserException {

        Object root = null;

        //System.out.println ("start parsing....");

        while (true) {
            parser.nextTag();
            int type = parser.getEventType();

            if (type == parser.END_TAG || type == parser.END_DOCUMENT)
                break;
                
            //	String name = namespaceMap.getPackage (start.getNamespace ())
            //	+ "." + start.getName ();

            String rootAttr = parser.getAttributeValue(classMap.enc, "root");

            Object o =
                read(
                    null,
                    -1,
                    parser.getNamespace(),
                    parser.getName(),
                    ElementType.OBJECT_TYPE);

            if ("1".equals (rootAttr) || root == null)
                root = o;
        }

        //System.out.println ("leaving root read");

        return root;
    }

    /** Builds an object from the XML stream. This method
        is public for usage in conjuction with Marshal subclasses. 
        Precondition: On the start tag of the object or property, 
        so href can be read. */

    public Object read(
        Object owner,
        int index,
        String namespace,
        String name,
        ElementType expected)
        throws IOException, XmlPullParserException {

        //	System.out.println ("in read");

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
            String nullAttr = parser.getAttributeValue(classMap.xsi, "nil");

            if (nullAttr == null)
                nullAttr = parser.getAttributeValue(classMap.xsi, "null");

            if (nullAttr != null && Soap.stringToBoolean(nullAttr)) {
                obj = null;
                parser.nextTag();
                parser.require(parser.END_TAG, null, null);
            }
            else {
                String type = parser.getAttributeValue(classMap.xsi, "type");

                if (type != null) {
                    int cut = type.indexOf(':');

                    name = type.substring(cut + 1);
                    String prefix = cut == -1 ? "" : type.substring(0, cut);
                    namespace = parser.getNamespace(prefix);
                }
                else if (name == null && namespace == null) {
                    if (parser.getAttributeValue(classMap.enc, "arrayType")
                        != null) {
                        namespace = classMap.enc;
                        name = "Array";
                    }
                    else {
                        Object[] names = classMap.getInfo(expected.type, null);

                        namespace = (String) names[0];
                        name = (String) names[1];

                        //	System.out.println ("getInfo for "+expected.type+": {"+namespace+"}"+name);
                    }
                }

                obj = classMap.readInstance(this, namespace, name, expected);

                if (obj == null)
                    obj = readUnknown(namespace, name);
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

        //	System.out.println ("leaving read");

        return obj;
    }

    protected void readSerializable(KvmSerializable obj) throws IOException, XmlPullParserException {

        int testIndex = -1; // inc at beg. of loop for perf. reasons
        int sourceIndex = 0;
        int cnt = obj.getPropertyCount();
        PropertyInfo info = new PropertyInfo();

        while (true) {
            if (parser.nextTag() == parser.END_TAG)
                break;

            String name = parser.getName();
            //	    System.out.println ("tagname:"+name);

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

            obj.setProperty(testIndex, read(obj, testIndex, null, null, info));

            sourceIndex = 0;
        }

        parser.require(parser.END_TAG, null, null);
    }

    public Object readUnknown(String namespace, String name)
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

    public void readVector(Vector v, ElementType elementType)
        throws IOException, XmlPullParserException {

        String namespace = null;
        String name = null;
        int size = v.size();
        boolean dynamic = true;

        String type = parser.getAttributeValue(classMap.enc, "arrayType");
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

        int position =
            getIndex(parser.getAttributeValue(classMap.enc, "offset"), 0, 0);

        while (parser.getEventType() != parser.END_TAG) {
            // handle position

            position =
                getIndex(
                    parser.getAttributeValue(classMap.enc, "position"),
                    0,
                    position);

            if (dynamic && position >= size) {
                size = position + 1;
                v.setSize(size);
            }

            // implicit handling of position exceeding specified size
            v.setElementAt(
                read(v, position, namespace, name, elementType),
                position);

            position++;
            parser.nextTag();
        }

        parser.require(parser.END_TAG, null, null);
    }
}
