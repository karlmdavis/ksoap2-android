package org.ksoap2.serialization;

import java.io.*;
import java.math.*;

import org.junit.*;

import org.ksoap2.*;
import org.ksoap2.transport.mock.*;
import org.xmlpull.v1.*;

import static junit.framework.TestCase.*;

public class MarshalFloatTest {
    private static final String FLOAT_LABEL = "float";

    private static final String FLOATING_POINT_VALUE = "12.0";

    private MarshalFloat marshalFloat;

    @Before
    public void setUp() throws Exception {
        marshalFloat = new MarshalFloat();
    }

    @Test
    public void testMarshalDateInbound() throws IOException, XmlPullParserException {
        MockXmlPullParser mockXmlPullParser = new MockXmlPullParser();
        mockXmlPullParser.nextText = FLOATING_POINT_VALUE;

        Number floatingPointValue = (Number) marshalFloat.readInstance(mockXmlPullParser, null, FLOAT_LABEL, null);
        assertTrue(floatingPointValue instanceof Float);
        assertEquals(Float.valueOf(FLOATING_POINT_VALUE), floatingPointValue.floatValue(), 0.01f);

        floatingPointValue = (Number) marshalFloat.readInstance(mockXmlPullParser, null, "double", null);
        assertTrue(floatingPointValue instanceof Double);
        assertEquals(Double.valueOf(FLOATING_POINT_VALUE), floatingPointValue.doubleValue(), 0.01d);

        floatingPointValue = (Number) marshalFloat.readInstance(mockXmlPullParser, null, "decimal", null);
        assertTrue(floatingPointValue instanceof BigDecimal);
        assertEquals(new BigDecimal(FLOATING_POINT_VALUE).doubleValue(), floatingPointValue.doubleValue(), 0.01d);

        try {
            floatingPointValue = (Number) marshalFloat.readInstance(mockXmlPullParser, null, "unknown type", null);
            fail();
        } catch (RuntimeException e) {
            assertNotNull(e.getMessage());
        }

    }

    @Test
    public void testMarshalDateOutbound_Float() throws IOException {
        MockXmlSerializer writer = new MockXmlSerializer();
        marshalFloat.writeInstance(writer, 12.0f);
        assertEquals(FLOATING_POINT_VALUE, writer.getOutputText());
    }

    @Test
    public void testMarshalDateOutbound_Double() throws IOException {
        MockXmlSerializer writer = new MockXmlSerializer();
        marshalFloat.writeInstance(writer, 12.0d);
        assertEquals(FLOATING_POINT_VALUE, writer.getOutputText());
    }

    @Test
    public void testMarshalDateOutbound_Decimal() throws IOException {
        MockXmlSerializer writer = new MockXmlSerializer();
        marshalFloat.writeInstance(writer, new BigDecimal(12.0d));
        assertEquals("12", writer.getOutputText());
    }

    @Test
    public void testRegistration_moreIntegrationLike() throws IOException, XmlPullParserException {
        MockXmlPullParser pullParser = new MockXmlPullParser();
        pullParser.nextText = FLOATING_POINT_VALUE;

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        marshalFloat.register(envelope);
        assertTrue(envelope.classToQName.containsKey(Float.class.getName()));
        assertTrue(envelope.classToQName.containsKey(Double.class.getName()));
        assertTrue(envelope.classToQName.containsKey(BigDecimal.class.getName()));

        Float floatingPointValue = (Float) envelope.readInstance(pullParser, envelope.xsd, FLOAT_LABEL, null);
        assertEquals(Float.valueOf(FLOATING_POINT_VALUE), floatingPointValue, 0.01f);
    }

}
