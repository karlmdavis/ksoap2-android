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

package org.kobjects.serialization;

/** This class encapsulates type information. */

public class ElementType {

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

    public static final ElementType OBJECT_TYPE =
        new ElementType(OBJECT_CLASS, false, null);

    /** Type of the property/elements. Should usually be
        an instance of class, */

    public Object type;

    /** if a property is multi-referenced, set this flag to true. */

    public boolean multiRef;

    /** Element type for array properties, null if not array prop. */

    public ElementType elementType;

    public ElementType() {
    }

    public ElementType(Object type) {
        this.type = type;
    }

    public ElementType(
        Object type,
        boolean multiRef,
        ElementType elementType) {

        this.type = type;
        this.multiRef = multiRef;
        this.elementType = elementType;
    }

    public void clear() {
        type = null;
        multiRef = false;
        elementType = null;
    }

    public void copy(ElementType t2) {
        type = t2.type;
        multiRef = t2.multiRef;
        elementType = t2.elementType;
    }
}
