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
 * Contributor(s): John D. Beatty, Dave Dash, Andre Gerard, F. Hunter,
 * Renaud Tognelli
 *
 * */
 

package org.ksoap;

import java.util.Vector;
import org.kobjects.serialization.*;


/** A simple dynamic object that can be used to build
    soap calls without implementing KvmSerializable */ 

/** 
 * A simple dynamic object that can be used to build soap calls without 
 * implementing KvmSerializable 
 * <p>Essentially, this is what goes inside the body of a soap envelope - 
 * it is the direct subelement of the body and all further subelements</p>
 * <p>Instead of this this class, custom classes
 * can be used if they implement the KvmSerializable interface. </p>
 */ 

public class SoapObject implements KvmSerializable {

    String namespace;
    String name;
    Vector info = new Vector ();
    Vector data = new Vector ();
    /**
     * Creates a new <code>SoapObject</code> instance.
     *
     * @param namespace the namespace for the soap object
     * @param name the name of the soap object
     */

    public SoapObject (String namespace, String name) {
	this.namespace = namespace;
	this.name = name;
    }


    public boolean equals (Object o) {
	if (!(o instanceof SoapObject)) return false;
	
	SoapObject so = (SoapObject) o;
	int cnt = data.size ();

	if (cnt != so.data.size ()) return false;

	try {
	    for (int i = 0; i < cnt; i++) 
		if (!data.elementAt (i).equals 
		    (so.getProperty (((PropertyInfo) info.elementAt (i)).name))) 
		    return false;
	}
	catch (Exception e) {
	    return false;
	}
	return true;
    }
    
    
    public String getName () {
	return name;
    }


    public String getNamespace () {
	return namespace;
    }


     /**
     * Returns a specific property at a certain index. 
     *  
     * @param index the index of the desired property
     * @return the desired property
     */
    public Object getProperty (int index) {
	return data.elementAt (index);
    }
    

    public Object getProperty (String name) {

	for (int i = 0; i < data.size (); i++) {
	    if (name.equals (((PropertyInfo) info.elementAt (i)).name))
		return data.elementAt (i);
	}

	throw new RuntimeException ("illegal property: "+name);
    }


     /**
     * Returns the number of properties
     *
     * @return the number of properties
     */

    public int getPropertyCount () {
	return data.size ();
    }

     /**
     * Places PropertyInfo of desired property into a designated PropertyInfo
     * object
     *
     * @param index index of desired property
     * @param info designated retainer of desired property
     */

    public void getPropertyInfo (int index, PropertyInfo pi) {
	PropertyInfo p = (PropertyInfo) info.elementAt (index);
	pi.name = p.name;
	pi.copy (p);
    }

    
  
    /**
     * Creates a new SoapObject based on this, allows usage of SoapObjects 
     * as templates. One application is to set the expected return type 
     * of a soap call if the server does not send explicit type information.
     *
     * @return a copy of this.
     */ 
 
    public SoapObject newInstance () {
	SoapObject o = new SoapObject (namespace, name);
	for (int i = 0; i < data.size (); i++) {
	    PropertyInfo p = (PropertyInfo) info.elementAt (i);
	    o.addProperty (p.name, p, data.elementAt (i));
	}
	return o;
    }
    

    /**
     * Sets a specified property to a certain value.
     *
     * @param index the index of the specified property
     * @param value the new value of the property
     */
    public void setProperty (int index, Object value) {
	data.setElementAt (value, index);
    }
    
    
    /**
     * Adds a property (parameter) to the object.  This is essentially
     * a sub element.
     *
     * @param name The name of the property
     * @param value the value of the property
     */
    public SoapObject addProperty (String name, Object value) {

	return addProperty 
	    (new PropertyInfo 
		(name, value == null 
		 ? ElementType.OBJECT_CLASS 
		 : value.getClass ()), 
	     value);
    }
    
    
    /** @deprecated
	Adds a property (parameter) to the object.  This is essentially
	a sub element.
	
	@param name the name of the property
	@param type the type or class of the element
	@param value the value of the property
    */

    public SoapObject addProperty (String name, ElementType type, 
				   Object value) {

	PropertyInfo p = new PropertyInfo ();
	p.copy (type);
	p.name = name;
	return addProperty (p, value);
    }


    /** Adds a property (parameter) to the object.  This is
	essentially a sub element.
	
	@param name the name of the property
	@param type the type or class of the element
	@param value the value of the property */

    public SoapObject addProperty (PropertyInfo pi, Object value) {
	info.addElement (pi);
	data.addElement (value);
	return this;
    }

}
