/*
 * Copyright (c) 2003,2004, Stefan Haustein, Oberhausen, Rhld., Germany
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the
 * following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all copies or substantial
 * portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT
 * LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO
 * EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE
 * USE OR OTHER DEALINGS IN THE SOFTWARE.
 * 
 * Contributor(s): John D. Beatty, Dave Dash, Andre Gerard, F. Hunter, Renaud Tognelli
 */

package org.ksoap2.serialization;

import java.util.*;

/**
 * A simple dynamic object that can be used to build soap calls without implementing KvmSerializable
 * <p/>
 * Essentially, this is what goes inside the body of a soap envelope - it is the direct subelement of the body
 * and all further subelements
 * <p/>
 * Instead of this this class, custom classes can be used if they implement the KvmSerializable interface.
 */

public class SoapObject extends AttributeContainer implements KvmSerializable {
    /**
     * The namespace of this soap object.
     */
    protected String namespace;
    /**
     * The name of this soap object.
     */
    protected String name;
    /**
     * The Vector of properties.
     */
    protected Vector properties = new Vector();

    // TODO: accessing properties and attributes would work much better if we kept a list of known properties instead of iterating through the list each time

    /**
     * Creates a new <code>SoapObject</code> instance.
     *
     * @param namespace the namespace for the soap object
     * @param name      the name of the soap object
     */

    public SoapObject(String namespace, String name) {
        this.namespace = namespace;
        this.name = name;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof SoapObject))
            return false;

        SoapObject otherSoapObject = (SoapObject) obj;

        if (!name.equals(otherSoapObject.name) || !namespace.equals(otherSoapObject.namespace)) {
            return false;
        }


        int numProperties = properties.size();
        if (numProperties != otherSoapObject.properties.size())
            return false;


        // TODO:  The code below doesn't correctly compare the following <name> element to itself.
        // calling otherSoapObject.getProperty(thisProp.getName()) will return the first child with the given name
        // Perhaps we need to define two SoapObject to be equal if it has the same children *in the same order*
        /*<name>
           <address>941 Wealthy</address>
           <address>942 Wealthy</address>
         </name>
        */

        for (int propIndex = 0; propIndex < numProperties; propIndex++) {
            PropertyInfo thisProp = (PropertyInfo) this.properties.elementAt(propIndex);
            Object thisPropValue = thisProp.getValue();
            if (!otherSoapObject.hasProperty(thisProp.getName())) {
                return false;
            }
            Object otherPropValue = otherSoapObject.getProperty(thisProp.getName());
            if (!thisPropValue.equals(otherPropValue)) {
                return false;
            }
        }

        return attributesAreEqual(otherSoapObject);

    }

    public String getName() {
        return name;
    }

    public String getNamespace() {
        return namespace;
    }

    /**
     * @inheritDoc
     */
    public Object getProperty(int index) {
        return ((PropertyInfo) properties.elementAt(index)).getValue();
    }

    /**
     * Get the property with the given name
     *
     * @throws java.lang.RuntimeException if the property does not exist
     */
    public Object getProperty(String name) {
        Integer index = propertyIndex(name);
        if (index != null) return getProperty(index);
        else throw new RuntimeException("illegal property: " + name);
    }

    /**
     * Knows whether the given property exists
     */
    public boolean hasProperty(final String name) {
        if (propertyIndex(name) != null) return true;
        else return false;
    }

    /**
     * Get a property without chance of throwing an exception
     *
     * @return the property if it exists; if not, {@link NullSoapObject} is returned
     */
    public Object safeGetProperty(final String name) {
        Integer i = propertyIndex(name);
        if (i != null) return getProperty(i);
        else return new NullSoapObject();
    }

    /**
     * Get a property without chance of throwing an exception. An object can be provided
     * to this method; if the property is not found, this object will be returned.
     *
     * @param defaultThing the object to return if the property is not found
     * @return the property if it exists; defaultThing if the property does not exist
     */
    public Object safeGetProperty(final String name, final Object defaultThing) {
        Integer i = propertyIndex(name);
        if (i != null) return getProperty(i);
        else return defaultThing;
    }

    private Integer propertyIndex(String name) {
        for (int i = 0; i < properties.size(); i++) {
            if (name.equals(((PropertyInfo) properties.elementAt(i)).getName())) return i;
        }
        return null;
    }

    /**
     * Returns the number of properties
     *
     * @return the number of properties
     */
    public int getPropertyCount() {
        return properties.size();
    }

    /**
     * Places PropertyInfo of desired property into a designated PropertyInfo object
     *
     * @param index        index of desired property
     * @param propertyInfo designated retainer of desired property
     * @deprecated
     */
    public void getPropertyInfo(int index, Hashtable properties, PropertyInfo propertyInfo) {
        getPropertyInfo(index, propertyInfo);
    }

    /**
     * Places PropertyInfo of desired property into a designated PropertyInfo object
     *
     * @param index        index of desired property
     * @param propertyInfo designated retainer of desired property
     */
    public void getPropertyInfo(int index, PropertyInfo propertyInfo) {
        PropertyInfo p = (PropertyInfo) properties.elementAt(index);
        propertyInfo.name = p.name;
        propertyInfo.namespace = p.namespace;
        propertyInfo.flags = p.flags;
        propertyInfo.type = p.type;
        propertyInfo.elementType = p.elementType;
    }

    /**
     * Creates a new SoapObject based on this, allows usage of SoapObjects as templates. One application is to
     * set the expected return type of a soap call if the server does not send explicit type information.
     *
     * @return a copy of this.
     */
    public SoapObject newInstance() {
        SoapObject o = new SoapObject(namespace, name);
        for (int propIndex = 0; propIndex < properties.size(); propIndex++) {
            PropertyInfo propertyInfo = (PropertyInfo) properties.elementAt(propIndex);
            o.addProperty(propertyInfo);
        }
        for (int attribIndex = 0; attribIndex < getAttributeCount(); attribIndex++) {
            AttributeInfo newAI = new AttributeInfo();
            getAttributeInfo(attribIndex, newAI);
            AttributeInfo attributeInfo = newAI; //(AttributeInfo) attributes.elementAt(attribIndex);
            o.addAttribute(attributeInfo);
        }
        return o;
    }

    /**
     * Sets a specified property to a certain value.
     *
     * @param index the index of the specified property
     * @param value the new value of the property
     */
    public void setProperty(int index, Object value) {
        ((PropertyInfo) properties.elementAt(index)).setValue(value);
    }

    /**
     * Adds a property (parameter) to the object. This is essentially a sub element.
     *
     * @param name  The name of the property
     * @param value the value of the property
     */
    public SoapObject addProperty(String name, Object value) {
        PropertyInfo propertyInfo = new PropertyInfo();
        propertyInfo.name = name;
        propertyInfo.type = value == null ? PropertyInfo.OBJECT_CLASS : value.getClass();
        propertyInfo.value = value;
        return addProperty(propertyInfo);
    }

    /**
     * Adds a property (parameter) to the object. This is essentially a sub element.
     *
     * @param propertyInfo designated retainer of desired property
     * @param value        the value of the property
     * @deprecated property info now contains the value
     */
    public SoapObject addProperty(PropertyInfo propertyInfo, Object value) {
        propertyInfo.setValue(value);
        addProperty(propertyInfo);
        return this;
    }

    /**
     * Adds a property (parameter) to the object. This is essentially a sub element.
     *
     * @param propertyInfo designated retainer of desired property
     */
    public SoapObject addProperty(PropertyInfo propertyInfo) {
        properties.addElement(propertyInfo);
        return this;
    }

    /**
     * Adds a attribute (parameter) to the object. This is essentially a sub element.
     *
     * @param attributeInfo designated retainer of desired attribute
     */
    public SoapObject addAttribute(AttributeInfo attributeInfo) {
        super.addAttribute(attributeInfo);
        return this;
    }


    public SoapObject addAttribute(String name, Object value) {
        super.addAttribute(name, value);
        return this;
    }


    public String toString() {
        StringBuffer buf = new StringBuffer("" + name + "{");
        for (int i = 0; i < getPropertyCount(); i++) {
            buf.append("" + ((PropertyInfo) properties.elementAt(i)).getName() + "=" + getProperty(i) + "; ");
        }
        buf.append("}");
        return buf.toString();
    }
}
