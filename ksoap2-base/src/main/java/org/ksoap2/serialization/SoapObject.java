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
 * 
 * Essentially, this is what goes inside the body of a soap envelope - it is the direct subelement of the body
 * and all further subelements
 * 
 * Instead of this this class, custom classes can be used if they implement the KvmSerializable interface.
 */

public class SoapObject implements KvmSerializable
{
	/** The namespace of this soap object. */
	protected String namespace;
	/** The name of this soap object. */
	protected String name;
	/** The Vector of properties. */
	protected Vector properties = new Vector();
	/** The Vector of attributes. */
	protected Vector attributes = new Vector();

	/**
	 * Creates a new <code>SoapObject</code> instance.
	 * 
	 * @param namespace
	 *            the namespace for the soap object
	 * @param name
	 *            the name of the soap object
	 */

	public SoapObject(String namespace, String name)
	{
		this.namespace = namespace;
		this.name = name;
	}

	public boolean equals(Object o)
	{
		if (!(o instanceof SoapObject))
			return false;

		SoapObject so = (SoapObject) o;
		int cnt = properties.size();

		if (cnt != so.properties.size())
			return false;

		try
		{
			for (int i = 0; i < cnt; i++)
			{
				if (!((PropertyInfo) properties.elementAt(i)).getValue().equals(
						so.getProperty(((PropertyInfo) properties.elementAt(i)).getName())))
				{
					return false;
				}
				if (!((PropertyInfo) attributes.elementAt(i)).getValue().equals(
						so.getProperty(((PropertyInfo) attributes.elementAt(i)).getName())))
				{
					return false;
				}
			}
		}
		catch (Exception e)
		{
			return false;
		}
		return true;
	}

	public String getName()
	{
		return name;
	}

	public String getNamespace()
	{
		return namespace;
	}

	/**
	 * Returns a specific property at a certain index.
	 * 
	 * @param index
	 *            the index of the desired property
	 * @return the desired property
	 */
	public Object getProperty(int index)
	{
		return ((PropertyInfo) properties.elementAt(index)).getValue();
	}

	public Object getProperty(String name)
	{
		for (int i = 0; i < properties.size(); i++)
		{
			if (name.equals(((PropertyInfo) properties.elementAt(i)).getName()))
				return getProperty(i);
		}
		throw new RuntimeException("illegal property: " + name);
	}

	/**
	 * Returns the number of properties
	 * 
	 * @return the number of properties
	 */
	public int getPropertyCount()
	{
		return properties.size();
	}

	/**
	 * Places AttributeInfo of desired attribute into a designated AttributeInfo object
	 * 
	 * @param index
	 *            index of desired attribute
	 * @param propertyInfo
	 *            designated retainer of desired attribute
	 */
	public void getAttributeInfo(int index, AttributeInfo attributeInfo)
	{
		AttributeInfo p = (AttributeInfo) attributes.elementAt(index);
		attributeInfo.name = p.name;
		attributeInfo.namespace = p.namespace;
		attributeInfo.flags = p.flags;
		attributeInfo.type = p.type;
		attributeInfo.elementType = p.elementType;
		attributeInfo.value = p.getValue();
	}

	/**
	 * Returns a specific attribute at a certain index.
	 * 
	 * @param index
	 *            the index of the desired attribute
	 * @return the value of the desired attribute
	 * 
	 */
	public Object getAttribute(int index)
	{
		return ((AttributeInfo) attributes.elementAt(index)).getValue();
	}

	/** Returns a property with the given name. */
	public Object getAttribute(String name)
	{
		for (int i = 0; i < attributes.size(); i++)
		{
			if (name.equals(((AttributeInfo) attributes.elementAt(i)).getName()))
				return getAttribute(i);
		}
		throw new RuntimeException("illegal property: " + name);
	}

	/**
	 * Returns the number of attributes
	 * 
	 * @return the number of attributes
	 */
	public int getAttributeCount()
	{
		return attributes.size();
	}

	/**
	 * Places PropertyInfo of desired property into a designated PropertyInfo object
	 * 
	 * @param index
	 *            index of desired property
	 * @param propertyInfo
	 *            designated retainer of desired property
	 * @deprecated
	 */
	public void getPropertyInfo(int index, Hashtable properties, PropertyInfo propertyInfo)
	{
		getPropertyInfo(index, propertyInfo);
	}

	/**
	 * Places PropertyInfo of desired property into a designated PropertyInfo object
	 * 
	 * @param index
	 *            index of desired property
	 * @param propertyInfo
	 *            designated retainer of desired property
	 */
	public void getPropertyInfo(int index, PropertyInfo propertyInfo)
	{
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
	public SoapObject newInstance()
	{
		SoapObject o = new SoapObject(namespace, name);
		for (int i = 0; i < properties.size(); i++)
		{
			PropertyInfo propertyInfo = (PropertyInfo) properties.elementAt(i);
			o.addProperty(propertyInfo);
		}
		for (int i = 0; i < properties.size(); i++)
		{
			AttributeInfo attributeInfo = (AttributeInfo) attributes.elementAt(i);
			o.addAttribute(attributeInfo);
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
	public void setProperty(int index, Object value)
	{
		((PropertyInfo) properties.elementAt(index)).setValue(value);
	}

	/**
	 * Adds a property (parameter) to the object. This is essentially a sub element.
	 * 
	 * @param name
	 *            The name of the property
	 * @param value
	 *            the value of the property
	 */
	public SoapObject addProperty(String name, Object value)
	{
		PropertyInfo propertyInfo = new PropertyInfo();
		propertyInfo.name = name;
		propertyInfo.type = value == null ? PropertyInfo.OBJECT_CLASS : value.getClass();
		propertyInfo.value = value;
		return addProperty(propertyInfo);
	}

	/**
	 * Adds a property (parameter) to the object. This is essentially a sub element.
	 * 
	 * @param propertyInfo
	 *            designated retainer of desired property
	 * @param value
	 *            the value of the property
	 * @deprecated property info now contains the value
	 */
	public SoapObject addProperty(PropertyInfo propertyInfo, Object value)
	{
		propertyInfo.setValue(value);
		addProperty(propertyInfo);
		return this;
	}

	/**
	 * Adds a property (parameter) to the object. This is essentially a sub element.
	 * 
	 * @param propertyInfo
	 *            designated retainer of desired property
	 */
	public SoapObject addProperty(PropertyInfo propertyInfo)
	{
		properties.addElement(propertyInfo);
		return this;
	}

	/**
	 * Adds a attribute (parameter) to the object. This is essentially a sub element.
	 * 
	 * @param name
	 *            The name of the attribute
	 * @param value
	 *            the value of the attribute
	 */
	public SoapObject addAttribute(String name, Object value)
	{
		AttributeInfo attributeInfo = new AttributeInfo();
		attributeInfo.name = name;
		attributeInfo.type = value == null ? PropertyInfo.OBJECT_CLASS : value.getClass();
		attributeInfo.value = value;
		return addAttribute(attributeInfo);
	}

	/**
	 * Adds a attribute (parameter) to the object. This is essentially a sub element.
	 * 
	 * @param propertyInfo
	 *            designated retainer of desired attribute
	 */
	public SoapObject addAttribute(AttributeInfo attributeInfo)
	{
		attributes.addElement(attributeInfo);
		return this;
	}

	public String toString()
	{
		StringBuffer buf = new StringBuffer("" + name + "{");
		for (int i = 0; i < getPropertyCount(); i++)
		{
			buf.append("" + ((PropertyInfo) properties.elementAt(i)).getName() + "=" + getProperty(i) + "; ");
		}
		buf.append("}");
		return buf.toString();
	}

}
