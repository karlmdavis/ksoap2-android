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
 * Contributor(s): John D. Beatty, F. Hunter, Renaud Tognelli
 *
 * */

package org.ksoap2.serialization;

/** This class is used to store information about each property
    an implementation of KvmSerializable exposes. */

public class PropertyInfo {


    public static final Class OBJECT_CLASS =
        new Object().getClass();
    public static final Class STRING_CLASS = "".getClass();
    public static final Class INTEGER_CLASS =
        new Integer(0).getClass();
    public static final Class LONG_CLASS =
        new Long(0).getClass();
    public static final Class BOOLEAN_CLASS =
        new Boolean(true).getClass();
    public static final Class VECTOR_CLASS =
        new java.util.Vector().getClass();

	public static final PropertyInfo OBJECT_TYPE = new PropertyInfo();

	public static final int TRANSIENT = 1;
	public static final int MULTI_REF = 2;
	public static final int REF_ONLY = 4;


    /** Name of the property */

    public String name;


	/** Namespace of this property */

	public String namespace;

	public int flags;


    /** Type of the property/elements. Should usually be
        an instance of Class. */

    public Object type = OBJECT_CLASS;

    /** if a property is multi-referenced, set this flag to true. */

    public boolean multiRef;

    /** Element type for array properties, null if not array prop. */

    public PropertyInfo elementType;

    /*
    public String toString () {
    return "property "+ name + ": " + type + (elementType == null ? "" : "["+elementType+"]");
    }
    */

    public PropertyInfo() {
    }

/*
    public PropertyInfo(String name, Object type) {
        this.name = name;
        this.type = type;
    }

    public PropertyInfo(
        String name,
        Object type,
        int flags,
        ElementType elementType) {
        this.type = type;
        this.flags = flags;
        this.elementType = elementType;
        this.name = name;
    }
*/

    public void clear() {
        type = OBJECT_CLASS;
		flags = 0;
        name = null;
        namespace = null;
    }



}
