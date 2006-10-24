package org.ksoap2.serialization;

import java.io.*;

import org.ksoap2.transport.mock.*;
import org.xmlpull.v1.*;

import junit.framework.*;

public class DMTest extends TestCase {

    private static final String STRING_XML_PROPERTY_VALUE = "someXmlPropertyValue";
    private MockXmlPullParser pullParser;
    private DM dm;

    protected void setUp() throws Exception {
        super.setUp();
        pullParser = new MockXmlPullParser();
        pullParser.nextText = STRING_XML_PROPERTY_VALUE;
        dm = new DM();
    }
    
    public void testValidTypes() throws IOException, XmlPullParserException {
        assertEquals(STRING_XML_PROPERTY_VALUE, dm.readInstance(pullParser, "", "string", null));
        pullParser.nextText = "12";
        assertEquals(new Long(12), dm.readInstance(pullParser, "", "long", null));
        assertEquals(new Integer(12), dm.readInstance(pullParser, "", "int", null));
        pullParser.nextText = "true";
        assertEquals(Boolean.TRUE, dm.readInstance(pullParser, "", "boolean", null));
    }

    public void testDefaultFailureCase() throws IOException, XmlPullParserException {
        try {
            dm.readInstance(pullParser, "", "unknownType", null);
        } catch (RuntimeException expected) {
            assertEquals(null, expected.getMessage());
        }
    }
}
