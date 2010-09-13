package org.ksoap2.serialization;

import junit.framework.TestCase;
import org.kxml2.io.KXmlParser;
import org.xmlpull.v1.XmlPullParser;

import java.io.InputStream;

public class ParseTest extends TestCase {

    public void setUp() {


    }


    public void testAttributesOnPrimitives() throws Exception
    {
        String testXML = "<foo><bar name=\"fred\">Barney</bar></foo>";
        InputStream inputStream = new IStringStream(textXML);

       KXmlParser parser = new KXmlParser();
		parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, true);
		parser.setInput(inputStream, null);
		parser.nextTag();
		parser.nextTag();
		parser.nextTag();
		parser.nextTag();
		return parser;



    }

}