package org.ksoap2.serialization;

import junit.framework.*;

public class SoapObjectTest extends TestCase {

    private static final String ANOTHER_PROPERTY_NAME = "anotherProperty";
    private static final String A_PROPERTY_NAME = "aPropertyName";
    private SoapObject soapObject;

    protected void setUp() throws Exception {
        super.setUp();
        soapObject = new SoapObject("namespace", "name");
    }

    public void testFormattingOfToString() {
        final String localValue = "propertyValue";
        soapObject.addProperty(A_PROPERTY_NAME, "propertyValue");
        assertEquals("name{" + A_PROPERTY_NAME + "=propertyValue; }", soapObject.toString());
        soapObject.addProperty(ANOTHER_PROPERTY_NAME, new Integer(12));
        assertEquals("name{" + A_PROPERTY_NAME + "=" + localValue + "; " + ANOTHER_PROPERTY_NAME + "=12; }", soapObject.toString());
    }

    public void testEquals() {
        SoapObject soapObject2 = new SoapObject("namespace", "name");
        assertTrue(soapObject.equals(soapObject2));

        soapObject.addProperty(A_PROPERTY_NAME, new Integer(12));
        assertFalse(soapObject.equals(soapObject2));

        soapObject2.addProperty(A_PROPERTY_NAME, soapObject.getProperty(A_PROPERTY_NAME));
        assertTrue(soapObject.equals(soapObject2));

        soapObject.equals("bob");

        assertTrue(soapObject.newInstance().equals(soapObject));

    }

    public void testSameNumberProperties_DifferentNames() {
        SoapObject soapObject2 = soapObject.newInstance();
        soapObject.addProperty(ANOTHER_PROPERTY_NAME, "value");
        soapObject2.addProperty("differentProperty", "differentValue");
        assertFalse(soapObject2.equals(soapObject));
    }

    public void testSameProperties_DifferentValues() {
        SoapObject soapObject2 = soapObject.newInstance();
        soapObject.addProperty(ANOTHER_PROPERTY_NAME, "value");
        soapObject2.addProperty(ANOTHER_PROPERTY_NAME, "differentValue");
        assertFalse(soapObject2.equals(soapObject));
    }

    public void testGetPropertyWithIllegalPropertyName() {
        try {
            soapObject.getProperty("blah");
            fail();
        } catch (RuntimeException e) {
            assertEquals(RuntimeException.class.getName(), e.getClass().getName());
            assertEquals("illegal property: blah", e.getMessage());
        }
    }

}
