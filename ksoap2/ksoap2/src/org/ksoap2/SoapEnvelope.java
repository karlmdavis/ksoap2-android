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

package org.ksoap2;

import java.io.*;
import java.util.*;

import org.xmlpull.v1.*;
import org.kxml2.kdom.*;
import org.kobjects.serialization.*;
import org.ksoap2.marshal.*;

/** The SOAP envelope. */

public class SoapEnvelope {

    public static final int VER10 = 10;
    public static final int VER11 = 11;
    public static final int VER12 = 12;

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

    public static final String XSD = "http://www.w3.org/2001/XMLSchema";

    /** Namespace constant: http://www.w3.org/2001/XMLSchema */

    public static final String XSI =
        "http://www.w3.org/2001/XMLSchema-instance";

    /** Namespace constant: http://www.w3.org/1999/XMLSchema */

    public static final String XSD1999 = "http://www.w3.org/1999/XMLSchema";

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

    public static boolean stringToBoolean(String s) {

        if (s == null)
            return false;

        s = s.trim().toLowerCase();
        return (s.equals("1") || s.equals("true"));
    }

    public Object body;
    public String encodingStyle;
    int version;    

    protected String env;
    protected String enc;
    protected String xsi;   
    protected String xsd;

    public SoapEnvelope (int version) {
                this.version = version;
        //  prefixMap = SoapEnvelope.prefixMap[version];

        if (version == SoapEnvelope.VER10) {
            xsi = SoapEnvelope.XSI1999;
            xsd = SoapEnvelope.XSD1999;
        }
        else {
            xsi = SoapEnvelope.XSI;
            xsd = SoapEnvelope.XSD;
        }

        if (version < SoapEnvelope.VER12) {
            enc = SoapEnvelope.ENC;
            env = SoapEnvelope.ENV;
        }
        else {
            enc = SoapEnvelope.ENC2001;
            env = SoapEnvelope.ENV2001;
        }

    }


    /** Parses the SOAP envelope from the given parser */


    public void parse(XmlPullParser parser)
        throws IOException, XmlPullParserException {

        parser.nextTag();
        parser.require(parser.START_TAG, env, "Envelope");
        encodingStyle = parser.getAttributeValue(env, "encodingStyle");

        parser.nextTag();

        if (parser.getEventType() == parser.START_TAG
            && parser.getNamespace().equals(env)
            && parser.getName().equals("Header")) {

            parseHeader(parser);

            parser.require(parser.END_TAG, env, "Header");
            parser.nextTag();
        }

        parser.require(parser.START_TAG, env, "Body");
        encodingStyle = parser.getAttributeValue(env, "encodingStyle");

        parseBody (parser);

        parser.require(parser.END_TAG, env, "Body");

        parser.nextTag();
        parser.require(parser.END_TAG, env, "Envelope");
    }


    public void parseHeader(XmlPullParser parser)
        throws IOException, XmlPullParserException {

        // consume start header

        parser.nextTag();

        // look at all header entries
        while (parser.getEventType() != parser.END_TAG) {
            if ("1"
                .equals(
                    parser.getAttributeValue(env, "mustUnderstand")))
                throw new RuntimeException("mU not supported");

            throw new RuntimeException("Unknown header element; skipping NYI");

            //                parser.ignoreTree();
            //                parser.skip();
        }

    }

    public void parseBody(XmlPullParser parser)
        throws IOException, XmlPullParserException {

        parser.nextTag();
        // insert fault generation code here

        if (parser.getEventType() == parser.START_TAG
            && parser.getNamespace().equals(env)
            && parser.getName().equals("Fault")) {
            SoapFault fault = new SoapFault();
            fault.parse(parser);
            body = fault;
        }
        else {
            Node node = (body instanceof Node) ? (Node) body : new Node ();
            node.parse(parser);
            body = node;
        }
    }


    /** Writes the envelope and body to the given XML writer. */

    public void write(XmlSerializer writer) throws IOException {

        
        writer.startTag(env, "Envelope");

        //  writer.attribute (Soap.ENV, "encodingStyle", encodingStyle); 
        writer.startTag(env, "Body");
        
        writeHeader(writer);

        if (encodingStyle != null) 
           writer.attribute(env, "encodingStyle", encodingStyle);
        
        writeBody(writer);


    }

    /** Writes the head including the encoding style attribute and the 
    body start tag */

    public void writeHeader(XmlSerializer writer) throws IOException {

    }

    /** Overwrite this method for customized writing of
    the soap message body. */

    public void writeBody(XmlSerializer writer) throws IOException {

        /*   if (body instanceof XmlIO) {
               if (encodingStyle != null)
                   writer.attribute(classMap.env, "encodingStyle", encodingStyle);
        
               ((XmlIO) body).write(writer);
           }
           else { */


        ((Node) body).write(writer);
        //        }
    }

}
