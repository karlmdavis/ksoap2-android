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

import java.io.*;

import org.xmlpull.v1.*;
import org.kobjects.serialization.*;

/** The SOAP envelope. */

public class SoapEnvelope {

    Object body;
    ClassMap classMap;
    String encodingStyle;

    /** deprecated */

    public SoapEnvelope() {
        this(new ClassMap(2));
    }

    public SoapEnvelope(ClassMap classMap) {
        this.classMap = classMap;
    }

    /** Returns the body object of the envelope.  */

    public Object getBody() {
        return body;
    }

    /** Returns the first property of the body object */

    public Object getResult() {
        KvmSerializable ks = (KvmSerializable) body;
        return ks.getPropertyCount() == 0 ? null : ks.getProperty(0);
    }

    /** Parses the SOAP envelope from the given parser */

    public void parse(XmlPullParser parser)
        throws IOException, XmlPullParserException {

        parseHead(parser);
        parseBody(parser);
        parseTail(parser);
    }

    public void parseHead(XmlPullParser parser)
        throws IOException, XmlPullParserException {
        parser.nextTag();
        parser.require(parser.START_TAG, classMap.env, "Envelope");
        encodingStyle = parser.getAttributeValue(classMap.env, "encodingStyle");

        parser.nextTag();

        if (parser.getEventType() == parser.START_TAG
            && parser.getNamespace().equals(classMap.env)
            && parser.getName().equals("Header")) {
            // consume start header

            parser.nextTag();

            // look at all header entries
            while (parser.getEventType() != parser.END_TAG) {
                if ("1"
                    .equals(
                        parser.getAttributeValue(
                            classMap.env,
                            "mustUnderstand")))
                    throw new RuntimeException("mU not supported");

                throw new RuntimeException("Unknown header element; skipping NYI");

                //                parser.ignoreTree();
                //                parser.skip();
            }

            parser.require(parser.END_TAG, classMap.env, "Header");
            parser.nextTag();
        }

        parser.require(parser.START_TAG, classMap.env, "Body");
        encodingStyle = parser.getAttributeValue(classMap.env, "encodingStyle");
    }

    public void parseBody(XmlPullParser parser) throws IOException, XmlPullParserException {

        parser.nextTag();
        // insert fault generation code here

        if (parser.getEventType() == parser.START_TAG
            && parser.getNamespace().equals(classMap.env)
            && parser.getName().equals("Fault")) {
            SoapFault fault = new SoapFault();
            fault.parse(parser);
            body = fault;
        }
//        else if (body != null && body instanceof XmlIO)
//             ((XmlIO) body).parse(parser);
        else
            body = new SoapParser(parser, classMap).read();
    }

    public void parseTail(XmlPullParser parser)
        throws IOException, XmlPullParserException {

        parser.nextTag();
        parser.require(parser.END_TAG, classMap.env, "Body");

        parser.nextTag();
        parser.require(parser.END_TAG, classMap.env, "Envelope");
    }

    /** Sets the encoding style. */

    public void setEncodingStyle(String encodingStyle) {
        this.encodingStyle = encodingStyle;
    }

    /** Writes the envelope and body to the given XML writer. */

    public void write(XmlSerializer writer) throws IOException {
        writeHead(writer);
        writeBody(writer);
        writeTail(writer);
    }

    /** Writes the head including the encoding style attribute and the 
    body start tag */

    public void writeHead(XmlSerializer writer) throws IOException {

        writer.startTag(classMap.env, "Envelope");
        //	writer.attribute (Soap.ENV, "encodingStyle", encodingStyle); 
        writer.startTag(classMap.env, "Body");
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
        writer.attribute(
            classMap.env,
            "encodingStyle",
            encodingStyle == null ? classMap.enc : encodingStyle);

        new SoapWriter(writer, classMap).write(body);
        //        }
    }

    public void writeTail(XmlSerializer writer) throws IOException {
        writer.endTag(classMap.env, "Body");
        writer.endTag(classMap.env, "Envelope");
    }

    public void setBody(Object body) {
        this.body = body;
    }

    public void setClassMap(ClassMap classMap) {
        this.classMap = classMap;
    }

}
