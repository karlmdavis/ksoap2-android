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


package org.ksoap;



/** Contains some constant definitions only. */


public class Soap {

    public static final int VER10 = 0;
    public static final int VER11 = 1;
    public static final int VER12 = 2;



    public static final String ENV2001 = 
	"http://www.w3.org/2001/12/soap-envelope";


    public static final String ENC2001 = 
	"http://www.w3.org/2001/12/soap-encoding";

    /** Namespace constant: http://schemas.xmlsoap.org/soap/envelope/ */

    public static final String ENV = 
	"http://schemas.xmlsoap.org/soap/envelope/";

    /** Namespace constant: http://schemas.xmlsoap.org/soap/encoding/ */

    public static final String ENC = 
	"http://schemas.xmlsoap.org/soap/encoding/";

    /** Namespace constant: http://www.w3.org/2001/XMLSchema */

    public static final String XSD =
	"http://www.w3.org/2001/XMLSchema";

    /** Namespace constant: http://www.w3.org/2001/XMLSchema */

    public static final String XSI =
	"http://www.w3.org/2001/XMLSchema-instance";

    /** Namespace constant: http://www.w3.org/1999/XMLSchema */

    public static final String XSD1999 =
	"http://www.w3.org/1999/XMLSchema";

    /** Namespace constant: http://www.w3.org/1999/XMLSchema */

    public static final String XSI1999 =
	"http://www.w3.org/1999/XMLSchema-instance";

    //    static final Class BYTE_ARRAY_CLASS = new byte [0].getClass (); 
    // static final Class OBJECT_CLASS = new Object ().getClass ();

    /** A default prefix map containing all relevant namespaces */

/*    static PrefixMap basePrefixMap = 
	new PrefixMap (new PrefixMap 
	    (PrefixMap.DEFAULT, "SOAP-ENV", ENV), "SOAP-ENC", ENC);
	    
    public static final PrefixMap [] prefixMap = {
	new PrefixMap (new PrefixMap 
	    (basePrefixMap, "xsd", XSD1999), "xsi", XSI1999),
	new PrefixMap (new PrefixMap 
	    (basePrefixMap, "xsd", XSD), "xsi", XSI),
	new PrefixMap (new PrefixMap (new PrefixMap (new PrefixMap 
	    (basePrefixMap, "xsd", XSD), "xsi", XSI), "SOAP-ENV", ENV2001), "SOAP-ENC", ENC2001)};
*/	

    static boolean stringToBoolean (String s) {

	if (s == null) return false;
	
	s = s.trim ().toLowerCase ();
	return (s.equals ("1") || s.equals ("true"));
    }

}

