package org.ksoap2.serialization;

import junit.framework.*;

public class SoapPrimitiveTest extends TestCase {

    private SoapPrimitive soapPrimitive;

    protected void setUp() throws Exception {
        super.setUp();
        soapPrimitive = new SoapPrimitive("namespace", "name", "theValue");
    }


    public void testEqualsWithoutAttributes() {
        SoapPrimitive primitive = new SoapPrimitive("namespace", "name", "value");
        assertFalse(primitive.equals("something that shouldn't equal"));

        SoapPrimitive primitiveTwo = new SoapPrimitive("", "", "");
        assertFalse(primitive.equals(primitiveTwo));

        primitiveTwo.namespace = primitive.getNamespace();
        assertFalse(primitive.equals(primitiveTwo));

        primitiveTwo.name = primitive.getName();
        assertFalse(primitive.equals(primitiveTwo));

        primitiveTwo.value = primitive.toString();
        assertTrue(primitive.equals(primitiveTwo));

        primitiveTwo.value = null;
        assertFalse(primitive.equals(primitiveTwo));

        primitive.value = null;
        assertTrue(primitive.equals(primitiveTwo));
    }

    public void testHashCode_NullNamespace() {
        SoapPrimitive primitive = new SoapPrimitive(null, "name", "value");
        assertTrue(primitive.hashCode() == primitive.hashCode());
        assertFalse(primitive.hashCode() == new SoapPrimitive("weeee", "name", "value").hashCode());
    }

    public void testEquals_NullNamespace() {
        SoapPrimitive primitive = new SoapPrimitive(null, "name", "value");
        assertTrue(primitive.equals(primitive));
        assertFalse(primitive.equals(new SoapPrimitive("weeee", "name", "value")));
    }

    public void testGetAttribute_AttributesExist() {
        soapPrimitive.addAttribute("First", "one");
        soapPrimitive.addAttribute("Second", "two");

        assertEquals("two", soapPrimitive.getAttribute("Second"));
        assertEquals("one", soapPrimitive.getAttribute("First"));
    }

    public void testGetAttribute_AttributeDoesNotExist() {
        soapPrimitive.addAttribute("First", "one");

        try {
            soapPrimitive.getAttribute("Second");
            fail("should have thrown");
        } catch (RuntimeException e) {
            assertEquals(RuntimeException.class.getName(), e.getClass().getName());
            assertEquals("illegal property: Second", e.getMessage());
        }
    }

    public void testHasAttribute_KnowsIfTheAttributeExists() {
        soapPrimitive.addAttribute("Second", "two");
        assertTrue(soapPrimitive.hasAttribute("Second"));
        assertFalse(soapPrimitive.hasAttribute("First"));
    }

    public void testSafeGetAttribute_GivesAttributeWhenItExists() {
        soapPrimitive.addAttribute("First", "one");
        soapPrimitive.addAttribute("Second", "two");

        assertEquals("two", soapPrimitive.safeGetAttribute("Second"));
        assertEquals("one", soapPrimitive.safeGetAttribute("First"));
    }

    public void testSafeGetAttribute_GivesNullWhenTheAttributeDoesNotExist() {
        soapPrimitive.addAttribute("Second", "two");

        assertEquals("two", soapPrimitive.safeGetAttribute("Second"));
        assertNull(soapPrimitive.safeGetAttribute("First"));
    }


    public void testReturnTypeOfAddAttribute() {
        SoapPrimitive result = soapPrimitive.addAttribute("Key", "Value");
        assertTrue(result instanceof SoapPrimitive);
        assertTrue(result == soapPrimitive);
    }



}
