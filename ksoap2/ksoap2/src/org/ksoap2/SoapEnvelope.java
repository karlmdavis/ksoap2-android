/* kSOAP
 *
 */ 

package org.ksoap2;

import java.io.*;
import java.util.*;

import org.xmlpull.v1.*;
import org.kxml2.kdom.*;
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

    public Object bodyIn;
    public Object bodyOut;
    public Element [] headerIn;
    public Element [] headerOut;
    public String encodingStyle;
    public int version;    

    /** Envelope namespace */

    public String env;

    /** Encoding namespace */

    public String enc;

    /** Xml Schema instance namespace */
    
    public String xsi;   

    /** Xml Schema data namespace */

    public String xsd;

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
        
   /*     System.out.println ("name-r: '"+parser.getName()+"'");
        System.out.println ("name-x: 'Envelope'");
        System.out.println ("namesp-r: '"+parser.getNamespace()+"'");
        System.out.println ("namesp-x: '"+env+"'"); */
        
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

        Node headers = new Node ();
        headers.parse (parser);
    
        int count = 0;
        for (int i = 0; i < headers.getChildCount(); i++) {
            Element child = headers.getElement(i);
            if (child != null) count++;
        }
        
        headerIn = new Element [count];
        count = 0;
        for (int i = 0; i < headers.getChildCount(); i++) {
            Element child = headers.getElement(i);
            if (child != null) 
                headerIn [count++] = child;
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
            bodyIn = fault;
        }
        else {
            Node node = (bodyIn instanceof Node) ? (Node) bodyIn : new Node ();
            node.parse(parser);
            bodyIn = node;
        }
    }


    /** Writes the envelope and body to the given XML writer. */

    public void write(XmlSerializer writer) throws IOException {

        writer.setPrefix("i", xsi);  
        writer.setPrefix("d", xsd);  
        writer.setPrefix("c", enc);      
        writer.setPrefix("v", env);      
        writer.startTag(env, "Envelope");

        //  writer.attribute (Soap.ENV, "encodingStyle", encodingStyle); 
        
        writer.startTag(env, "Header");
        writeHeader(writer);
        writer.endTag(env, "Header");

        writer.startTag(env, "Body");
        writeBody(writer);
        writer.endTag(env, "Body");


        writer.endTag(env, "Envelope");
    }

    /** Writes the head including the encoding style attribute and the 
    body start tag */

    public void writeHeader(XmlSerializer writer) throws IOException {
        if (headerOut != null) {
            for (int i = 0; i < headerOut.length; i++) {
                headerOut[i].write (writer);
            }   
        }
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

		if (encodingStyle != null) 
           writer.attribute(env, "encodingStyle", encodingStyle);
        
        ((Node) bodyOut).write(writer);
        //        }
    }

}
