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


package org.ksoap2.marshal;


/** provides get and set methods for properties. Can be used to
    replace reflection (to some extend) for "serialization-aware"
    classes. Currently used in kSOAP and the RMS based kobjects object
    repository */



public interface KvmSerializable {


    /**
     * Returns the property at a specified index (for serialization)
     *
     * @param index the specified index 
     * @return the serialized property
     */
 
   Object getProperty (int index);

    /** returns the number of serializable properties */

    int getPropertyCount (); 



    /**
     * sets the property with the given index to the given value.
     * @param index the index to be set
     * @param value the value of the property
     */
    void setProperty (int index, Object value);


 
    /** Fills the given property info record */
    
    void getPropertyInfo (int index, PropertyInfo info);

    
}
