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
 * IN THE SOFTWARE. 
 *
 * Contributor(s): John D. Beatty, F. Hunter, Renaud Tognelli
 *
 * */

package org.ksoap2.serialization;

/**
 * This class is used to store information about each property an implementation
 * of KvmSerializable exposes.
 */

public class PropertyInfo {
    public static final Class OBJECT_CLASS = new Object().getClass();
    public static final Class STRING_CLASS = "".getClass();
    public static final Class INTEGER_CLASS = new Integer(0).getClass();
    public static final Class LONG_CLASS = new Long(0).getClass();
    public static final Class BOOLEAN_CLASS = new Boolean(true).getClass();
    public static final Class VECTOR_CLASS = new java.util.Vector().getClass();
    public static final PropertyInfo OBJECT_TYPE = new PropertyInfo();
    public static final int TRANSIENT = 1;
    public static final int MULTI_REF = 2;
    public static final int REF_ONLY = 4;
    /** Name of the property */
    public String name;
    /** Namespace of this property */
    public String namespace;
    public int flags;
    /**
     * Type of the property/elements. Should usually be an instance of Class.
     */
    public Object type = OBJECT_CLASS;
    /** if a property is multi-referenced, set this flag to true. */
    public boolean multiRef;
    /** Element type for array properties, null if not array prop. */
    public PropertyInfo elementType;

    public PropertyInfo() {
    }

    public void clear() {
        type = OBJECT_CLASS;
        flags = 0;
        name = null;
        namespace = null;
    }

}
