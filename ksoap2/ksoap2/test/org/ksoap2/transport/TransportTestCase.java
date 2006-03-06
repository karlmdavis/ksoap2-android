package org.ksoap2.transport;

import org.ksoap2.*;
import org.ksoap2.serialization.*;
import org.ksoap2.transport.mock.*;

import junit.framework.*;

public abstract class TransportTestCase extends TestCase {

    protected static final String containerNameSpaceURI = ServiceConnectionFixture.NAMESPACE;
    protected static final String soapAction = "SoapActionString";
    ServiceConnectionFixture serviceConnection;
    protected SoapSerializationEnvelope envelope;
    protected SoapObject soapObject;
    protected ComplexParameter complexParameter;

    protected void setUp() throws Exception {
        super.setUp();
        serviceConnection = new ServiceConnectionFixture();
        serviceConnection.setInputSring(ServiceConnectionFixture.WORKING_NOMULTIREF);
        envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        soapObject = new SoapObject(containerNameSpaceURI, "performComplexFunctionService");
        complexParameter = new ComplexParameter();
        complexParameter.name = "Serenity";
        complexParameter.count = 56;
        envelope.addMapping(containerNameSpaceURI, "ComplexParameter", complexParameter.getClass());
        envelope.addMapping(containerNameSpaceURI, ServiceConnectionFixture.RESPONSE_CLASS_NAME, ServiceConnectionFixture.RESPONSE_CLASS);
        soapObject.addProperty("complexFunction", complexParameter);
        envelope.setOutputSoapObject(soapObject);
    }

    protected void assertHeaderCorrect(ServiceConnectionFixture aServiceConnection, String aSoapAction) {
        assertEquals(aSoapAction, aServiceConnection.requestPropertyMap.get("SOAPAction"));
        assertEquals("text/xml", aServiceConnection.requestPropertyMap.get("Content-Type"));
        assertNotNull(aServiceConnection.requestPropertyMap.get("Content-Length"));
        assertEquals("kSOAP/2.0", aServiceConnection.requestPropertyMap.get("User-Agent"));
    }

    protected void assertSerializationDeserialization() throws SoapFault {
        String outputString = new String(serviceConnection.outputStream.toByteArray());
        assertTrue(outputString.indexOf(complexParameter.name) > 0);
        assertTrue(outputString.indexOf(""+complexParameter.count) > 0);
        assertTrue(envelope.getResponse() instanceof ComplexResponse);
        assertHeaderCorrect(serviceConnection,soapAction);
    }

}
