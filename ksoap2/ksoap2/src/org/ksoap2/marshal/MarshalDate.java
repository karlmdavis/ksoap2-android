package org.ksoap2.marshal;

import java.util.Date;
import java.io.*;
import org.xmlpull.v1.*;
import org.kobjects.isodate.*;
import org.kobjects.serialization.*;

/** Marshal class for Dates. */

public class MarshalDate implements Marshal {

    public static Class DATE_CLASS = new Date().getClass();

    public Object readInstance(
        XmlPullParser parser,
        String namespace,
        String name,
        ElementType expected)
        throws IOException, XmlPullParserException {

        Object result =
            IsoDate.stringToDate(parser.nextText(), IsoDate.DATE_TIME);

        return result;
    }

    public void writeInstance(XmlSerializer writer, Object obj)
        throws IOException {

        writer.text(IsoDate.dateToString((Date) obj, IsoDate.DATE_TIME));
    }

    public void register(SoapSerializationEnvelope cm) {
        cm.addMapping(cm.xsd, "dateTime", MarshalDate.DATE_CLASS, this);
    }

}
