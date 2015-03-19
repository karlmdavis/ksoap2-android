package org.ksoap2.serialization;

import junit.framework.*;

public class SoapObjectTest extends TestCase {

    private static final String ANOTHER_PROPERTY_NAME = "anotherProperty";
    private static final String A_PROPERTY_NAME = "aPropertyName";
    private SoapObject soapObject;
    private SoapObject mojSoapObejObject;

    protected void setUp() throws Exception {
        super.setUp();
        soapObject = new SoapObject("namespace", "name");
        mojSoapObejObject = new SoapObject("nameSpace", "RemoteProject");
    }

    public void testFormattingOfToString() {
        final String localValue = "propertyValue";
        soapObject.addProperty(A_PROPERTY_NAME, "propertyValue");
        assertEquals("name{" + A_PROPERTY_NAME + "=propertyValue; }",
                soapObject.toString());
        soapObject.addProperty(ANOTHER_PROPERTY_NAME, new Integer(12));
        assertEquals("name{" + A_PROPERTY_NAME + "=" + localValue + "; "
                + ANOTHER_PROPERTY_NAME + "=12; }", soapObject.toString());
    }

    public void testEquals() {

        // Different name results in false
        SoapObject differentSoapObject = new SoapObject("namespace", "fred");
        assertFalse(soapObject.equals(differentSoapObject));
        assertFalse(differentSoapObject.equals(soapObject));

        // different namespace results in false
        differentSoapObject = new SoapObject("fred", "name");
        assertFalse(differentSoapObject.equals(soapObject));

        // same results in true
        SoapObject soapObject2 = new SoapObject("namespace", "name");
        assertTrue(soapObject.equals(soapObject2));
        assertTrue(soapObject2.equals(soapObject));

        // missing property results in false.
        soapObject.addProperty(A_PROPERTY_NAME, new Integer(12));
        assertFalse(soapObject.equals(soapObject2));
        assertFalse(soapObject2.equals(soapObject));

        // identical properties results in true
        soapObject2.addProperty(A_PROPERTY_NAME,
                soapObject.getProperty(A_PROPERTY_NAME));
        assertTrue(soapObject.equals(soapObject2));
        assertTrue(soapObject2.equals(soapObject));

        // different properties result in a false
        soapObject.addProperty("anotherProperty", new Integer(12));
        soapObject2.addProperty("anotherPropertyFoo",
                soapObject.getProperty(A_PROPERTY_NAME));
        assertFalse(soapObject.equals(soapObject2));
        assertFalse(soapObject2.equals(soapObject));

        // same properties with different order should be false
        soapObject.addProperty("anotherPropertyFoo", new Integer(12));
        soapObject2.addProperty("anotherProperty",
                soapObject.getProperty(A_PROPERTY_NAME));
        assertFalse(soapObject.equals(soapObject2));
        assertFalse(soapObject2.equals(soapObject));

        soapObject2 = soapObject.newInstance();

        SoapObject multipleAddresses = new SoapObject("namespace", "name");
        multipleAddresses.addProperty("address", "941 Wealthy");
        multipleAddresses.addProperty("address", "942 Wealthy");

        assertTrue(multipleAddresses.equals(multipleAddresses));

        // Different number of attributes should result in equals returning
        // false
        soapObject2.addAttribute("Attribute1", new Integer(14));
        assertFalse(soapObject.equals(soapObject2));

        // Different values of attributes should return false;
        soapObject2.addAttribute("Attribute1", new Integer(19));
        assertFalse(soapObject.equals(soapObject2));

        assertFalse(soapObject.equals("bob"));

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
            assertEquals(RuntimeException.class.getName(), e.getClass()
                    .getName());
            assertEquals("illegal property: Second", e.getMessage());
        }
    }

    public void testHasAttribute_KnowsIfTheAttributeExists() {
        soapObject.addAttribute("Second", "two");
        assertTrue(soapObject.hasAttribute("Second"));
        assertFalse(soapObject.hasAttribute("First"));
    }

    public void testGetAttributeSafely_GivesAttributeWhenItExists() {
        soapObject.addAttribute("First", "one");
        soapObject.addAttribute("Second", "two");

        assertEquals("two", soapObject.getAttributeSafely("Second"));
        assertEquals("one", soapObject.getAttributeSafely("First"));
    }

    public void testGetAttributeSafely_GivesNullWhenTheAttributeDoesNotExist() {
        soapObject.addAttribute("Second", "two");

        assertEquals("two", soapObject.getAttributeSafely("Second"));
        assertNull(soapObject.getAttributeSafely("First"));
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

    public void testHasProperty_namespace() {
        soapObject.addProperty("test_namespace","Prop8", "Eight");
        assertTrue(soapObject.hasProperty("test_namespace","Prop8"));
        assertTrue(soapObject.hasProperty("Prop8"));
        assertFalse(soapObject.hasProperty("wrong_namespace","Prop8"));
    }

    public void testGetProperty_ThrowsWhenIllegalPropertyName() {
        try {
            soapObject.getProperty("blah");
            fail();
        } catch (RuntimeException e) {
            assertEquals(RuntimeException.class.getName(), e.getClass()
                    .getName());
            assertEquals("illegal property: blah", e.getMessage());
        }
    }

    public void testGetProperty_namespace_ThrowsWhenIllegalPropertyName() {
        try {
            Object obj=soapObject.getProperty("namespace", "blah");
            fail();
        } catch (RuntimeException e) {
            assertEquals(RuntimeException.class.getName(), e.getClass()
                    .getName());
            assertEquals("illegal property: blah", e.getMessage());
        }
    }

    public void testGetPropertySafely_GivesPropertyWhenItExists() {
        soapObject.addProperty("Prop1", "One");
        soapObject.addProperty("Prop8", "Eight");

        assertEquals("One", soapObject.getPropertySafely("Prop1"));
        assertEquals("Eight", soapObject.getPropertySafely("Prop8"));
    }

    public void testGetPropertySafely_namespace_GivesPropertyWhenItExists() {
        soapObject.addProperty("namespace","Prop1", "One");
        soapObject.addProperty("namespace","Prop8", "Eight");

        assertEquals("One", soapObject.getPropertyByNamespaceSafely("namespace","Prop1"));
        assertEquals("Eight", soapObject.getPropertyByNamespaceSafely("namespace", "Prop8"));
        assertEquals("One", soapObject.getPropertySafely("Prop1"));
        assertEquals("Eight", soapObject.getPropertySafely("Prop8"));
    }

    public void testGetPropertySafely_GivesANullObjectWhenThePropertyDoesNotExist() {
        Object nullObject = soapObject.getPropertySafely("Prop1");
        assertNotNull(nullObject);
        assertNull(nullObject.toString());
    }

    public void testGetPropertySafely_namespace_GivesANullObjectWhenThePropertyDoesNotExist() {
        Object nullObject = soapObject.getPropertyByNamespaceSafely("namespace", "Prop1");
        assertNotNull(nullObject);
        assertNull(nullObject.toString());
    }

    public void testGetPropertySafely_CanReturnTheGivenObjectWhenThePropertyDoesNotExist() {
        String thinger = "thinger";
        Integer five = new Integer(5);
        assertSame(thinger, soapObject.getPropertySafely("Prop8", thinger));
        assertSame(five, soapObject.getPropertySafely("Prop8", five));
    }

    public void testAddPropertyIfValue() {
        String name = "NotHere";
        String value = null;
        soapObject.addPropertyIfValue(name, value);
        assertFalse(soapObject.hasProperty(name));

        PropertyInfo propertyInfo = new PropertyInfo();
        propertyInfo.name = name;
        propertyInfo.value = value;

        soapObject.addPropertyIfValue(propertyInfo);

        assertFalse(soapObject.hasProperty(name));

        soapObject.addPropertyIfValue(propertyInfo, null);
        assertFalse(soapObject.hasProperty(name));

        soapObject.addPropertyIfValue(propertyInfo, "GotOne");
        assertTrue(soapObject.hasProperty(name));
    }

    public void testAddPropertyIfValue_namespace() {
        String name = "NotHere";
        String value = null;
        soapObject.addPropertyIfValue("namespace",name, value);
        assertFalse(soapObject.hasProperty(name));
        assertFalse(soapObject.hasProperty("namespace",name));

        PropertyInfo propertyInfo = new PropertyInfo();
        propertyInfo.name = name;
        propertyInfo.value = value;
        propertyInfo.namespace = "namespace";

        soapObject.addPropertyIfValue(propertyInfo);

        assertFalse(soapObject.hasProperty(name));
        assertFalse(soapObject.hasProperty("namespace",name));

    }

    public void testAddAttributeIfValue() {
        String name = "NotHere";
        String value = null;
        soapObject.addAttributeIfValue(name, value);
        assertFalse(soapObject.hasAttribute(name));

        AttributeInfo attributeInfo = new AttributeInfo();
        attributeInfo.name = name;
        attributeInfo.value = value;

        soapObject.addAttributeIfValue(attributeInfo);

        assertFalse(soapObject.hasAttribute(name));

        soapObject.addAttribute(name, "GotOne");
        assertTrue(soapObject.hasAttribute(name));
    }

    public void testGetPropertyAsString() {
        String name = "StringProperty";
        String value = "a string";
        soapObject.addProperty(name, value);

        assertEquals(value, soapObject.getPropertyAsString(name));

        String name2 = "NotThere";
        assertEquals("", soapObject.getPropertySafelyAsString(name2));
        assertEquals(value, soapObject.getPropertySafelyAsString(name));

        String anInteger = "AnInteger";
        String integerValue = "12";

        soapObject.addProperty(anInteger, new Integer(12));
        assertEquals(integerValue, soapObject.getPropertyAsString(anInteger));

        mojSoapObejObject.addProperty("jaaa", null);
        assertEquals("",
                mojSoapObejObject.getPropertySafelyAsString("jaaa"));

        assertTrue("".equals(soapObject.getPropertySafelyAsString(null)));

        // tests for later comments on http://code.google.com/p/ksoap2-android/issues/detail?id=94
        assertTrue("".equals(soapObject.getPropertySafelyAsString(null, null)));

        assertTrue("test".equals(soapObject.getPropertySafelyAsString(null, "test")));
    }

    public void getPropertyByNamespaceSafelyAsString() {
        String name = "StringProperty";
        String value = "a string";
        String namespace = "namespace";
        soapObject.addProperty(namespace,name, value);

        assertEquals(value, soapObject.getPropertyByNamespaceSafelyAsString(namespace,name));

        String name2 = "NotThere";
        assertEquals("", soapObject.getPropertyByNamespaceSafelyAsString(namespace,name2));
        assertEquals(value, soapObject.getPropertyByNamespaceSafelyAsString(namespace,name));

        String anInteger = "AnInteger";
        String integerValue = "12";

        soapObject.addProperty(anInteger, new Integer(12));
        assertEquals(integerValue, soapObject.getPropertyByNamespaceSafelyAsString(namespace,anInteger));

        mojSoapObejObject.addProperty(namespace,"jaaa", null);
        assertEquals("",
                mojSoapObejObject.getPropertyByNamespaceSafelyAsString(namespace,"jaaa"));

    }

    public void testGetAttributeAsString() {
        String name = "StringAttribute";
        String value = "a string";
        soapObject.addAttribute(name, value);

        assertEquals(value, soapObject.getAttributeAsString(name));

        String name2 = "NotThere";
        assertEquals("", soapObject.getAttributeSafelyAsString(name2));
        assertEquals(value, soapObject.getAttributeSafelyAsString(name));

        String anInteger = "AnInteger";
        String integerValue = "12";

        soapObject.addAttribute(anInteger, new Integer(12));
        assertEquals(integerValue,
                soapObject.getAttributeSafelyAsString(anInteger));
    }

    public void testGetPrimitiveProperty(){
        PropertyInfo propertyInfo = new PropertyInfo();
        
        propertyInfo.name = "ComplexThing";
        propertyInfo.type = SoapObject.class;
        propertyInfo.value = soapObject;
                
        soapObject.addProperty(propertyInfo);
        
        propertyInfo = new PropertyInfo();
        propertyInfo.name = "PrimitiveThing";
        propertyInfo.type = String.class;
        propertyInfo.value = "thing";
        soapObject.addProperty(propertyInfo);
        
        assertSame("",soapObject.getPrimitiveProperty("ComplexThing").toString());
        assertSame("thing",soapObject.getPrimitiveProperty("PrimitiveThing").toString());
        try {
            soapObject.getPrimitiveProperty("blah");
            fail();
        } catch (RuntimeException e) {
            assertEquals(RuntimeException.class.getName(), e.getClass()
                    .getName());
            assertEquals("illegal property: blah", e.getMessage());
        }
    }
    
    public void testGetPrimitivePropertyAsString(){
        PropertyInfo propertyInfo = new PropertyInfo();
        
        propertyInfo.name = "ComplexThing";
        propertyInfo.type = SoapObject.class;
        propertyInfo.value = soapObject;
                
        soapObject.addProperty(propertyInfo);
        
        propertyInfo = new PropertyInfo();
        propertyInfo.name = "PrimitiveThing";
        propertyInfo.type = String.class;
        propertyInfo.value = "thing";
        soapObject.addProperty(propertyInfo);
        
        assertSame("",soapObject.getPrimitivePropertyAsString("ComplexThing"));
        assertSame("thing",soapObject.getPrimitivePropertyAsString("PrimitiveThing"));
        try {
            soapObject.getPrimitivePropertyAsString("blah");
            fail();
        } catch (RuntimeException e) {
            assertEquals(RuntimeException.class.getName(), e.getClass()
                    .getName());
            assertEquals("illegal property: blah", e.getMessage());
        }
    }

    public void testGetPrimitivePropertySafely(){
        PropertyInfo propertyInfo = new PropertyInfo();
        
        propertyInfo.name = "ComplexThing";
        propertyInfo.type = SoapObject.class;
        propertyInfo.value = soapObject;
                
        soapObject.addProperty(propertyInfo);
        
        propertyInfo = new PropertyInfo();
        propertyInfo.name = "PrimitiveThing";
        propertyInfo.type = String.class;
        propertyInfo.value = "thing";
        soapObject.addProperty(propertyInfo);
        
        assertSame("",soapObject.getPrimitivePropertySafely("ComplexThing").toString());
        assertSame("thing",soapObject.getPrimitivePropertySafely("PrimitiveThing").toString());
        assertEquals(null,soapObject.getPropertySafely("jaaa").toString());
    }

    public void testGetPrimitivePropertySafelyAsString(){
        PropertyInfo propertyInfo = new PropertyInfo();
        
        propertyInfo.name = "ComplexThing";
        propertyInfo.type = SoapObject.class;
        propertyInfo.value = soapObject;
                
        soapObject.addProperty(propertyInfo);
        
        propertyInfo = new PropertyInfo();
        propertyInfo.name = "PrimitiveThing";
        propertyInfo.type = String.class;
        propertyInfo.value = "thing";
        soapObject.addProperty(propertyInfo);
        
        assertSame("",soapObject.getPrimitivePropertySafelyAsString("ComplexThing").toString());
        assertSame("thing",soapObject.getPrimitivePropertySafelyAsString("PrimitiveThing").toString());
        assertEquals("",soapObject.getPropertySafelyAsString("jaaa").toString());
    }

}
