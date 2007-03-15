package org.ksoap2.serialization;

import junit.framework.*;

public class SoapPrimitiveTest extends TestCase {

    public void testEquals() {
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

}
