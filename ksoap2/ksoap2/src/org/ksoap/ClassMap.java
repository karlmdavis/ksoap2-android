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
 * Contributor(s): F. Hunter
 *
 * */

package org.ksoap;

import java.io.*;
import java.util.Vector;
import java.util.Hashtable;
import org.kobjects.serialization.*;
import org.xmlpull.v1.*;


/** This class provides various soap properties relevant for
    (de)serialization, including a method for defining mappings 
    between java classes and XML element names.    */

public class ClassMap {

    /** Determines if type attributes are included for all objects
    written. If true, the type attribute is only written if the
    actual type differs from the declared type. */

    public boolean implicitTypes;
    public int version;
    //   public PrefixMap prefixMap;
    public String xsi;
    public String xsd;
    public String env;
    public String enc;

    protected int cnt;

    static final Marshal DEFAULT_MARSHAL = new DM();

    /** Map from XML qualified names to Java classes */

    protected Hashtable qNameToClass = new Hashtable();

    /** Map from Java class names to XML name and namespace pairs */

    protected Hashtable classToQName = new Hashtable();

    /** deprecated 
    
    Create a new class map using the 2001 version of the 
    XML schema namespace. */

    public ClassMap() {
        this(Soap.VER11);
    }

    public ClassMap(int version) {
        this.version = version;
     //  prefixMap = Soap.prefixMap[version];

        if (version == Soap.VER10) {
            xsi = Soap.XSI1999;
            xsd = Soap.XSD1999;
        }
        else {
            xsi = Soap.XSI;
            xsd = Soap.XSD;
        }

        if (version < Soap.VER12) {
            enc = Soap.ENC;
            env = Soap.ENV;
        }
        else {
            enc = Soap.ENC2001;
            env = Soap.ENV2001;
        }

        addMapping(enc, "Array", ElementType.VECTOR_CLASS);
        DEFAULT_MARSHAL.register(this);
    }

    /** deprecated
    
    Creates a new Class map. If the legacy flag is set to true,
    the 1999 version of the XML Schema namespace is used, otherwise
    the 2001 version. */

    public ClassMap(boolean legacy) {
        this(legacy ? Soap.VER10 : Soap.VER11);
    }

    /** Returns a new object read from the given parser.  If no
    mapping is found, null is returned.  This method is used by
    the SoapParser in order to convert the XML code to Java
    objects. */

    public Object readInstance(
        SoapParser parser,
        String namespace,
        String name,
        ElementType expected)
        throws IOException, XmlPullParserException {

        /*	if (xsdNamespace.equals (namespace)) {
            if ("int".equals (name)) 
        	return new Integer (Integer.parseInt (readText (parser)));
            else if ("long".equals (name))
        	return new Long (Long.parseLong (readText (parser)));
            else if ("string".equals (name))
        	return readText (parser);
            else if ("boolean".equals (name)) 
        	return new Boolean (Soap.stringToBoolean (readText (parser)));
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
            parser.readSerializable((KvmSerializable) obj);
        else if (obj instanceof Vector)
            parser.readVector((Vector) obj, expected.elementType);
        else
            throw new RuntimeException("no deserializer for " + obj.getClass());

        return obj;
    }

    /** Returns a string array containing the namespace, name, id and
        Marshal object for the given java object. This method is used
        by the SoapWriter in order to map Java objects to the
        corresponding SOAP section five XML code.*/

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

    /** Defines a direct mapping from a namespace and name to a java
        class (and vice versa), using the given marshal mechanism */

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

    /** Adds a SoapObject to the class map. During parsing,
    objects of the given type (namespace/name) will be
    mapped to corresponding copies of the given SoapObject,
    maintaining the structure of the template. */

    public void addTemplate(SoapObject so) {

        qNameToClass.put(new SoapPrimitive(so.namespace, so.name, null), so);

   //     if (prefixMap.getPrefix(so.namespace) == null)
    //        prefixMap = new PrefixMap(prefixMap, "n" + (cnt++), so.namespace);
    }

}
