/* kSOAP
 *
 * The contents of this file are subject to the "Lesser" Gnu Public License
 * (LGPL) (the "License"); you may not use this file except in
 * compliance with the License. 
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See
 * the License for the specific terms governing rights and limitations
 * under the License.
 *
 * The Initial Developer of kSOAP is Stefan Haustein. Copyright (C)
 * 2000, 2001, 2002 Stefan Haustein, D-46045 Oberhausen (Rhld.),
 * Germany. All Rights Reserved.
 *
 * Contributor(s): Mark Sanguinetti, Sean McDaniel
 *
 * */

package org.ksoap2.marshal;

import java.io.*;
import java.util.*;

import org.xmlpull.v1.*;
import org.kobjects.serialization.*;

/** A writer that is able to write objects wrt. the SOAP section five
    encoding rules. */

public class SoapWriter {

    public XmlSerializer writer;
    ClassMap classMap;


    /** The SoapWriter is initialized with an AbstractXmlWriter and a
        class map.  */

    public SoapWriter(XmlSerializer writer, ClassMap classMap) {

        this.writer = writer;
        this.classMap = classMap;
    }

    /** Serializes the given object */


}
