package org.ksoap;

import java.util.Vector;
import java.io.*;
import org.xmlpull.v1.*;

public class SoapFault extends IOException { //implements XmlIO {

    public String faultcode;
    public String faultstring;
    public String faultactor;
    public Vector detail;

    public void parse(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(parser.START_TAG, Soap.ENV, "Fault");

        parser.nextTag();
        while (parser.getEventType() == parser.START_TAG) {

            String name = parser.getName();

            if (name.equals("detail")) {
                detail = new Vector();
        
                throw new RuntimeException ("NYI: parser.readTree(detail);");
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
        xw.startTag(Soap.ENV, "Fault");
        xw.startTag(null, "faultcode");
        xw.text("" + faultcode);
        xw.endTag(null, "falutcode");
        xw.startTag(null, "faultstring");
        xw.text("" + faultstring);
        xw.endTag(null, "faultstring");

        xw.startTag(null, "detail");

        if (detail != null)
            for (int i = 0; i < detail.size(); i++) {
                xw.startTag(null, "item");
                xw.text("" + detail.elementAt(i));
                xw.endTag(null, "item");
            }

        xw.endTag(null, "detail");
        xw.endTag(Soap.ENV, "Fault");
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
