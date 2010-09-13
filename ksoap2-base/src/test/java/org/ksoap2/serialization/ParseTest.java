package org.ksoap2.serialization;

import junit.framework.TestCase;
import org.ksoap2.SoapEnvelope;
import org.kxml2.io.KXmlParser;
import org.xmlpull.v1.XmlPullParser;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class ParseTest extends TestCase {

    public void setUp() {


    }


    public void testAttributesOnPrimitives() throws Exception
    {
        String testXML = "<body><response><result name=\"fred\" anotherAttr=\"anotherValue\">Barney</result></response></body>";
        InputStream inputStream = new ByteArrayInputStream(testXML.getBytes());
        SoapSerializationEnvelope sse = new SoapSerializationEnvelope(SoapEnvelope.VER11);


       KXmlParser parser = new KXmlParser();
		parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, true);
		parser.setInput(inputStream, null);
        parser.nextTag();

        sse.parseBody(parser);

        SoapPrimitive bar = (SoapPrimitive) sse.getResponse();
        assertEquals("fred", bar.safeGetAttribute("name"));
        assertEquals("anotherValue", bar.safeGetAttribute("anotherAttr"));
        assertEquals("Barney", bar.toString());
    }

}