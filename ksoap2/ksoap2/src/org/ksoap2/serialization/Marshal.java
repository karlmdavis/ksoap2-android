package org.ksoap2.serialization;

import java.io.*;
import org.xmlpull.v1.*;

/** Abstract class for custom (de)serialization. */

public interface Marshal {

    /** This methods reads an instance from the given parser. For
        implementation, please note that the start and and tag must be
        consumed. This is not symmetric to writeInstance, but
        otherwise it would not be possible to access the attributes of
        the start tag here.  */

    public Object readInstance(
        XmlPullParser parser,
        String namespace,
        String name,
        PropertyInfo expected)
        throws IOException, XmlPullParserException;

    /** Write the instance to the given XmlSerializer. In contrast to 
        readInstance, it is not neccessary to care about the
        sorrounding start and end tags. Additional attributes must be
        writen before anything else is written. */

    public void writeInstance(
        XmlSerializer writer,
        Object instance)
        throws IOException;

    /** Register this Marshal with Envelope */

    public void register(SoapSerializationEnvelope cm);
}
