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

    public void testGetAttribute_AttributesExist() {
        soapObject.addAttribute("First", "one");
        soapObject.addAttribute("Second", "two");

        assertEquals("two", soapObject.getAttribute("Second"));
        assertEquals("one", soapObject.getAttribute("First"));
    }

    public void testGetAttribute_AttributeDoesNotExist() {
        soapObject.addAttribute("First", "one");

        try {
          soapObject.getAttribute("Second");
          fail("should have thrown");
        } catch (RuntimeException e) {
          assertEquals(RuntimeException.class.getName(), e.getClass().getName());
          assertEquals("illegal property: Second", e.getMessage());
        }
    }

    public void testHasAttribute_KnowsIfTheAttributeExists() {
        soapObject.addAttribute("Second", "two");
        assertTrue(soapObject.hasAttribute("Second"));
        assertFalse(soapObject.hasAttribute("First"));
    }

    public void testSafeGetAttribute_GivesAttributeWhenItExists() {
        soapObject.addAttribute("First", "one");
        soapObject.addAttribute("Second", "two");

        assertEquals("two", soapObject.safeGetAttribute("Second"));
        assertEquals("one", soapObject.safeGetAttribute("First"));
    }

    public void testSafeGetAttribute_GivesNullWhenTheAttributeDoesNotExist() {
        soapObject.addAttribute("Second", "two");

        assertEquals("two", soapObject.safeGetAttribute("Second"));
        assertNull(soapObject.safeGetAttribute("First"));
    }

    public void testGetProperty_GivesPropertyWhenItExists() {
        soapObject.addProperty("Prop1", "One");
        soapObject.addProperty("Prop8", "Eight");

        assertEquals("One", soapObject.getProperty("Prop1"));
        assertEquals("Eight", soapObject.getProperty("Prop8"));
    }

    public void testHasProperty_KnowsWhenThePropertyExists() {
        soapObject.addProperty("Prop8", "Eight");
        assertTrue(soapObject.hasProperty("Prop8"));
        assertFalse(soapObject.hasProperty("Prop1"));
    }

    public void testGetProperty_ThrowsWhenIllegalPropertyName() {
        try {
            soapObject.getProperty("blah");
            fail();
        } catch (RuntimeException e) {
            assertEquals(RuntimeException.class.getName(), e.getClass().getName());
            assertEquals("illegal property: blah", e.getMessage());
        }
    }

    public void testSafeGetProperty_GivesPropertyWhenItExists() {
        soapObject.addProperty("Prop1", "One");
        soapObject.addProperty("Prop8", "Eight");

        assertEquals("One", soapObject.safeGetProperty("Prop1"));
        assertEquals("Eight", soapObject.safeGetProperty("Prop8"));
    }

    public void testSafeGetProperty_GivesANullObjectWhenThePropertyDoesNotExist() {
        Object nullObject = soapObject.safeGetProperty("Prop1");
        assertNotNull(nullObject);
        assertNull(nullObject.toString());
    }

    public void testSafeGetProperty_CanReturnTheGivenObjectWhenThePropertyDoesNotExist() {
        String thinger = "thinger";
        Integer five = 5;
        assertSame(thinger, soapObject.safeGetProperty("Prop8", thinger));
        assertSame(five, soapObject.safeGetProperty("Prop8", five));
    }
}
