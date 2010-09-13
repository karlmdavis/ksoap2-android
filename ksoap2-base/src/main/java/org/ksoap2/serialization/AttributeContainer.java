package org.ksoap2.serialization;

import java.util.Vector;

public class AttributeContainer {


    /**
     * The Vector of attributes.
     */
    private Vector attributes = new Vector();

    /**
     * Places AttributeInfo of desired attribute into a designated AttributeInfo object
     *
     * @param index         index of desired attribute
     * @param attributeInfo designated retainer of desired attribute
     */
    public void getAttributeInfo(int index, AttributeInfo attributeInfo) {
        AttributeInfo p = (AttributeInfo) attributes.elementAt(index);
        attributeInfo.name = p.name;
        attributeInfo.namespace = p.namespace;
        attributeInfo.flags = p.flags;
        attributeInfo.type = p.type;
        attributeInfo.elementType = p.elementType;
        attributeInfo.value = p.getValue();
    }

    /**
     * Get the attribute at the given index
     */
    public Object getAttribute(int index) {
        return ((AttributeInfo) attributes.elementAt(index)).getValue();
    }

    /**
     * Get the attribute with the given name
     *
     * @throws RuntimeException if the attribute does not exist
     */
    public Object getAttribute(String name) {
        Integer i = attributeIndex(name);
        if (i != null) return getAttribute(i);
        else throw new RuntimeException("illegal property: " + name);
    }

    /**
     * Knows whether the given attribute exists
     */
    public boolean hasAttribute(final String name) {
        if (attributeIndex(name) != null) return true;
        else return false;
    }

    /**
     * Get an attribute without chance of throwing an exception
     *
     * @return the value of the attribute if it exists; {@code null} if it does not exist
     */
    public Object safeGetAttribute(String name) {
        Integer i = attributeIndex(name);
        if (i != null) return getAttribute(i);
        else return null;
    }

    private Integer attributeIndex(String name) {
        for (int i = 0; i < attributes.size(); i++) {
            if (name.equals(((AttributeInfo) attributes.elementAt(i)).getName())) return i;
        }
        return null;
    }

    /**
     * Returns the number of attributes
     *
     * @return the number of attributes
     */
    public int getAttributeCount() {
        return attributes.size();
    }

    /**
     * Checks that the two objects have identical sets of attributes.
     *
     * @param other
     * @return {@code true} of the attrubte sets are equal, {@code false} otherwise.
     */
    protected boolean attributesAreEqual(AttributeContainer other) {
        int numAttributes = getAttributeCount();
        if (numAttributes != other.getAttributeCount())
            return false;

        for (int attribIndex = 0; attribIndex < numAttributes; attribIndex++) {
            AttributeInfo thisAttrib = (AttributeInfo) this.attributes.elementAt(attribIndex);
            Object thisAttribValue = thisAttrib.getValue();
            if (!other.hasAttribute(thisAttrib.getName())) return false;
            Object otherAttribValue = other.safeGetAttribute(thisAttrib.getName());
            if (!thisAttribValue.equals(otherAttribValue)) {
                return false;
            }
        }
        return true;

    }


    /**
     * Adds a attribute (parameter) to the object.
     *
     * @param name  The name of the attribute
     * @param value the value of the attribute
     * @return {@code this} object.
     */
    public AttributeContainer addAttribute(String name, Object value) {
        AttributeInfo attributeInfo = new AttributeInfo();
        attributeInfo.name = name;
        attributeInfo.type = value == null ? PropertyInfo.OBJECT_CLASS : value.getClass();
        attributeInfo.value = value;
        return addAttribute(attributeInfo);
    }


    /**
     * Add a new attribute by providing an {@link AttributeInfo} object.  {@code AttributeInfo}
     * contains all data about the attribute, including name and value.}
     *
     * @param attributeInfo the {@code AttributeInfo} object to add.
     * @return {@code this} object.
     */
    public AttributeContainer addAttribute(AttributeInfo attributeInfo) {
        attributes.addElement(attributeInfo);
        return this;
    }



}
