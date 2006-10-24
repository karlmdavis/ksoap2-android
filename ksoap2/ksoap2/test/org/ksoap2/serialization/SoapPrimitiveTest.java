package org.ksoap2.serialization;

import junit.framework.*;

public class SoapPrimitiveTest extends TestCase {
    
    public void testEquals() {
        SoapPrimitive primitive = new SoapPrimitive("namespace", "name", "value");
        assertFalse(primitive.equals("something that shouldn't equal"));
        
        SoapPrimitive primitiveTwo = new SoapPrimitive("","","");
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
    
}
