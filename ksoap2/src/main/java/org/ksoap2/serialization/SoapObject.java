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
 * Contributor(s): John D. Beatty, Dave Dash, Andre Gerard, F. Hunter,
 * Renaud Tognelli
 *
 * */

package org.ksoap2.serialization;

import java.util.*;

/**
 * A simple dynamic object that can be used to build soap calls without
 * implementing KvmSerializable
 * 
 * Essentially, this is what goes inside the body of a soap envelope - it is the
 * direct subelement of the body and all further subelements
 * 
 * Instead of this this class, custom classes can be used if they implement the
 * KvmSerializable interface.
 */

public class SoapObject implements KvmSerializable {

    String namespace;
    String name;
    Vector info = new Vector();
    Vector data = new Vector();

    /**
     * Creates a new <code>SoapObject</code> instance.
     * 
     * @param namespace
     *            the namespace for the soap object
     * @param name
     *            the name of the soap object
     */

    public SoapObject(String namespace, String name) {
        this.namespace = namespace;
        this.name = name;
    }

    public boolean equals(Object o) {
        if (!(o instanceof SoapObject))
            return false;

        SoapObject so = (SoapObject) o;
        int cnt = data.size();

        if (cnt != so.data.size())
            return false;

        try {
            for (int i = 0; i < cnt; i++) {
                if (!data.elementAt(i).equals(so.getProperty(((PropertyInfo) info.elementAt(i)).name))) {
                    return false;
                }
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public String getName() {
        return name;
    }

    public String getNamespace() {
        return namespace;
    }

    /**
     * Returns a specific property at a certain index.
     * 
     * @param index
     *            the index of the desired property
     * @return the desired property
     */
    public Object getProperty(int index) {
        return data.elementAt(index);
    }

    public Object getProperty(String name) {
        for (int i = 0; i < data.size(); i++) {
            if (name.equals(((PropertyInfo) info.elementAt(i)).name)) {
                return data.elementAt(i);
            }
        }
        throw new RuntimeException("illegal property: " + name);
    }

    /**
     * Returns the number of properties
     * 
     * @return the number of properties
     */
    public int getPropertyCount() {
        return data.size();
    }

    /**
     * Places PropertyInfo of desired property into a designated PropertyInfo
     * object
     * 
     * @param index
     *            index of desired property
     * @param propertyInfo
     *            designated retainer of desired property
     */
    public void getPropertyInfo(int index, Hashtable properties, PropertyInfo propertyInfo) {
        PropertyInfo p = (PropertyInfo) info.elementAt(index);
        propertyInfo.name = p.name;
        propertyInfo.namespace = p.namespace;
        propertyInfo.flags = p.flags;
        propertyInfo.type = p.type;
        propertyInfo.elementType = p.elementType;
    }

    /**
     * Creates a new SoapObject based on this, allows usage of SoapObjects as
     * templates. One application is to set the expected return type of a soap
     * call if the server does not send explicit type information.
     * 
     * @return a copy of this.
     */
    public SoapObject newInstance() {
        SoapObject o = new SoapObject(namespace, name);
        for (int i = 0; i < data.size(); i++) {
            PropertyInfo propertyInfo = (PropertyInfo) info.elementAt(i);
            o.addProperty(propertyInfo, data.elementAt(i));
        }
        return o;
    }

    /**
     * Sets a specified property to a certain value.
     * 
     * @param index
     *            the index of the specified property
     * @param value
     *            the new value of the property
     */
    public void setProperty(int index, Object value) {
        data.setElementAt(value, index);
    }

    /**
     * Adds a property (parameter) to the object. This is essentially a sub
     * element.
     * 
     * @param name
     *            The name of the property
     * @param value
     *            the value of the property
     */
    public SoapObject addProperty(String name, Object value) {
        PropertyInfo propertyInfo = new PropertyInfo();
        propertyInfo.name = name;
        propertyInfo.type = value == null ? PropertyInfo.OBJECT_CLASS : value.getClass();
        return addProperty(propertyInfo, value);
    }

    /**
     * Adds a property (parameter) to the object. This is essentially a sub
     * element.
     * 
     * @param propertyInfo
     *            designated retainer of desired property
     * @param value
     *            the value of the property
     */
    public SoapObject addProperty(PropertyInfo propertyInfo, Object value) {
        info.addElement(propertyInfo);
        data.addElement(value);
        return this;
    }

    public String toString() {
        StringBuffer buf = new StringBuffer("" + name + "{");
        for (int i = 0; i < getPropertyCount(); i++) {
            buf.append("" + ((PropertyInfo) info.elementAt(i)).name + "=" + getProperty(i) + "; ");
        }
        buf.append("}");
        return buf.toString();
    }

}
