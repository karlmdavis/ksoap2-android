package org.ksoap2;

import java.util.Vector;
import java.io.*;
import org.xmlpull.v1.*;
import org.kxml2.kdom.*;

public class SoapFault extends IOException { //implements XmlIO {

    public String faultcode;
    public String faultstring;
    public String faultactor;
    public Node detail;

    public void parse(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(parser.START_TAG, SoapEnvelope.ENV, "Fault");

        parser.nextTag();
        while (parser.getEventType() == parser.START_TAG) {

            String name = parser.getName();

            if (name.equals("detail")) {
                detail = new Node();
                detail.parse(parser);        
            }
            else if (name.equals("faultcode"))
                faultcode = parser.nextText();
            else if (name.equals("faultstring"))
                faultstring = parser.nextText();
            else if (name.equals("faultactor"))
                faultactor = parser.nextText();
            else throw new RuntimeException ("unexpected tag:" +name);

            parser.require(parser.END_TAG, null, name);
        }
    }

    public void write(XmlSerializer xw) throws IOException {
        xw.startTag(SoapEnvelope.ENV, "Fault");
        xw.startTag(null, "faultcode");
        xw.text("" + faultcode);
        xw.endTag(null, "falutcode");
        xw.startTag(null, "faultstring");
        xw.text("" + faultstring);
        xw.endTag(null, "faultstring");
        
        xw.startTag(null, "detail");

        if (detail != null)
            detail.write(xw);

        xw.endTag(null, "detail");
        xw.endTag(SoapEnvelope.ENV, "Fault");
    }

    public String toString() {
        return "SoapFault - faultcode: '"
            + faultcode
            + "' faultstring: '"
            + faultstring
            + "' faultactor: '"
            + faultactor
            + "' detail: "
            + detail;
    }
}
