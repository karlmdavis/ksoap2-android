package org.ksoap2.marshal;

import java.io.*;
import org.kobjects.serialization.*;
import org.xmlpull.v1.*;
import org.ksoap2.*;

/** This class is not public, so save a few bytes
    by using a short class name (DM stands for DefaultMarshal)... */

class DM implements Marshal {

    public Object readInstance(
        XmlPullParser parser,
        String namespace,
        String name,
        ElementType expected)
        throws IOException, XmlPullParserException {

        //parser.parser.read (); // read start tag;
        String text = parser.nextText();
        //parser.parser.read (); // read end tag
        switch (name.charAt(0)) {
            case 's' :
                return text;
            case 'i' :
                return new Integer(Integer.parseInt(text));
            case 'l' :
                return new Long(Long.parseLong(text));
            case 'b' :
                return new Boolean(SoapEnvelope.stringToBoolean(text));
            default :
                throw new RuntimeException();
        }
    }

    public void writeInstance(XmlSerializer writer, Object instance)
        throws IOException {

        writer.text(instance.toString());
    }

    public void register(SoapSerializationEnvelope cm) {
        cm.addMapping(cm.xsd, "int", ElementType.INTEGER_CLASS, this);
        cm.addMapping(cm.xsd, "long", ElementType.LONG_CLASS, this);
        cm.addMapping(cm.xsd, "string", ElementType.STRING_CLASS, this);
        cm.addMapping(cm.xsd, "boolean", ElementType.BOOLEAN_CLASS, this);
    }
}
