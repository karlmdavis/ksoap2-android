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

/** This class is used to store information about each property
    an implementation of KvmSerializable exposes. */


public class PropertyInfo extends ElementType {
    

    /** Name of the property */

    public String name;


    /** the equivalent to transient, but named differently because
        transient is a reserved keyword */

    public boolean nonpermanent;

    /*
    public String toString () {
	return "property "+ name + ": " + type + (elementType == null ? "" : "["+elementType+"]");
    }
    */

    public PropertyInfo () {
    }

    
    public PropertyInfo (String name, Object type) {
	super (type);
	this.name = name;
    }


    public PropertyInfo (String name, Object type, boolean multiRef, 
			ElementType elementType) {
	super (type, multiRef, elementType);
	this.name = name;
    }


    public void clear () {
	name = null;
	nonpermanent = false;
	super.clear ();
    }



}
