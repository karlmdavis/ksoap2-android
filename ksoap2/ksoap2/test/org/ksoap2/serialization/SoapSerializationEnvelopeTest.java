/* Copyright (c) 2006, James Seigel, Calgary, AB., Canada
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or
 * sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The  above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 * IN THE SOFTWARE. */

package org.ksoap2.serialization;

import java.io.*;

import junit.framework.*;
import org.ksoap2.*;
import org.ksoap2.transport.*;
import org.ksoap2.transport.mock.*;
import org.kxml2.io.*;
import org.xmlpull.v1.*;

public class SoapSerializationEnvelopeTest extends TestCase {
    private static final String PARAMETER_NAME = "aParameter";
    private static String FUNCTION_NAME = "FunctionName";
    private static String NAMESPACE_NAME = ServiceConnectionFixture.NAMESPACE;
    private static final String BODY_XML_STRING = "<n0:" + FUNCTION_NAME + " id=\"o0\" n1:root=\"1\" xmlns:n0=\"" + NAMESPACE_NAME + "\" xmlns:n1=\"http://schemas.xmlsoap.org/soap/encoding/\"";
    private static final String END_XML_STRING = " />";
    private static final String END_XML_FUNCTION_STRING = "</n0:" + FUNCTION_NAME + ">";
    private KXmlSerializer xmlWriter;
    private SoapSerializationEnvelope envelope;
    private ByteArrayOutputStream outputStream;
    private SoapObject soapObject;
    private MockTransport myTransport;

    protected void setUp() throws Exception {
        super.setUp();
        xmlWriter = new KXmlSerializer();
        outputStream = new ByteArrayOutputStream();
        xmlWriter.setOutput(outputStream, "UTF-8");
        envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.addMapping(NAMESPACE_NAME, ServiceConnectionFixture.RESPONSE_CLASS_NAME, ServiceConnectionFixture.RESPONSE_CLASS);
        soapObject = new SoapObject(NAMESPACE_NAME, FUNCTION_NAME);
        myTransport = new MockTransport();
    }

    public void xx_testTwoDimensionalStringArrays() throws Throwable {
        // can't handle two dimensional arrays.
        myTransport.parseResponse(envelope, ServiceConnectionFixture.createTwoDimensionalStringArrayResponseAsStream());
        Object result = envelope.getResult();
        ServiceConnectionFixture.assertComplexResponseCorrect((ComplexResponse) result);
    }

    public void testInbound() throws Throwable {
        myTransport.parseResponse(envelope, ServiceConnectionFixture.createWorkingNoMultirefAsStream());
        Object result = envelope.getResult();
        ServiceConnectionFixture.assertComplexResponseCorrect((ComplexResponse) result);

        myTransport.parseResponse(envelope, ServiceConnectionFixture.createWorkingAsStream());
        result = envelope.getResult();
        ServiceConnectionFixture.assertComplexResponseCorrect((ComplexResponse) result);

        myTransport.parseResponse(envelope, ServiceConnectionFixture.createWorkingNoMultirefAsStream_reversedResponseParameters());
        result = envelope.getResult();
        ServiceConnectionFixture.assertComplexResponseCorrect((ComplexResponse) result);

        // Can't handle multirefs yet
        //
        // myTransport.parseResponse(envelope,
        // ServiceConnectionFixture.createMultirefAsStream());
        // result = envelope.getResult();
        // ServiceConnectionFixture.assertComplexResponseCorrect((ComplexResponse)
        // result);

    }

    public void testReadInstance_SoapObject_Reversed() throws Throwable {
        KXmlParser parser = primedParserForSerializableParameterTest(ServiceConnectionFixture.createWorkingNoMultirefAsStream_reversedResponseParameters());
        SoapSerializationEnvelope localEnvelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        SoapObject aSoapObject = (SoapObject) localEnvelope.readUnknown(parser, NAMESPACE_NAME, ServiceConnectionFixture.RESPONSE_CLASS_NAME);
        assertEquals(ServiceConnectionFixture.theStringResponse, aSoapObject.getProperty(0));
        assertEquals("" + ServiceConnectionFixture.theLongResponse, aSoapObject.getProperty(1).toString());
    }

    public void testReadInstance_SoapObject() throws Throwable {
        KXmlParser parser = primedParserForSerializableParameterTest(ServiceConnectionFixture.createWorkingNoMultirefAsStream());
        SoapSerializationEnvelope localEnvelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        SoapObject aSoapObject = (SoapObject) localEnvelope.readUnknown(parser, NAMESPACE_NAME, ServiceConnectionFixture.RESPONSE_CLASS_NAME);
        assertEquals(ServiceConnectionFixture.theStringResponse, aSoapObject.getProperty(1));
        assertEquals("" + ServiceConnectionFixture.theLongResponse, aSoapObject.getProperty(0).toString());
    }

    public void testReadSerializable_ParameterOrderReverse() throws Throwable {
        ComplexResponse complexResponse = new ComplexResponse();
        KXmlParser parser = primedParserForSerializableParameterTest(ServiceConnectionFixture.createWorkingNoMultirefAsStream_reversedResponseParameters());
        envelope.readSerializable(parser, complexResponse);
        ServiceConnectionFixture.assertComplexResponseCorrect(complexResponse);
    }

    public void testReadSerializable_ParameterOrderNormal() throws Throwable {
        ComplexResponse complexResponse = new ComplexResponse();
        KXmlParser parser = primedParserForSerializableParameterTest(ServiceConnectionFixture.createWorkingNoMultirefAsStream());
        envelope.readSerializable(parser, complexResponse);
        ServiceConnectionFixture.assertComplexResponseCorrect(complexResponse);
    }

    public void testReadSerializable_ParameterOrderNormal_NullNamespace() throws Throwable {
        ComplexResponse complexResponse = new ComplexResponse();
        complexResponse.namespace = null;
        KXmlParser parser = primedParserForSerializableParameterTest(ServiceConnectionFixture.createWorkingNoMultirefAsStream());
        envelope.readSerializable(parser, complexResponse);
        ServiceConnectionFixture.assertComplexResponseCorrect(complexResponse);
    }

    public void testReadSerializable_ParameterOrderNormal_NullNamespace_NullName() {
        // SF Bug # 1442028 
        try {
            ComplexResponse complexResponse = new ComplexResponse();
            complexResponse.namespace = null;
            complexResponse.parameterCount = 2;
            complexResponse.responseOne_Name = null;
            KXmlParser parser = primedParserForSerializableParameterTest(ServiceConnectionFixture.createWorkingNoMultirefAsStream());
            envelope.readSerializable(parser, complexResponse);
            ServiceConnectionFixture.assertComplexResponseCorrect(complexResponse);
        } catch (Throwable e) {
            assertFalse(e instanceof NullPointerException);
        }
    }

    private KXmlParser primedParserForSerializableParameterTest(InputStream inputStream) throws Throwable {
        KXmlParser parser = new KXmlParser();
        parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, true);
        parser.setInput(inputStream, null);
        parser.nextTag();
        parser.nextTag();
        parser.nextTag();
        parser.nextTag();
        return parser;
    }

    public void testWritingBody_WithNullParameter() throws Exception {
        soapObject.addProperty(PARAMETER_NAME, null);
        writeBodyWithSoapObject(soapObject);
        assertEquals(BODY_XML_STRING + ">" + "<" + PARAMETER_NAME + " n2:null=\"true\" xmlns:n2=\"" + envelope.xsi + "\" />" + END_XML_FUNCTION_STRING, new String(outputStream.toByteArray()));
    }

    public void testWritingBody_WithPrimitiveBooleanParameters() throws Exception {
        assertPrimitiveParameterCorrect(Boolean.TRUE, "boolean");
    }

    public void testWritingBody_WithPrimitiveStringParameters() throws Exception {
        assertPrimitiveParameterCorrect("aStringValue", "string");
    }

    public void testWritingBody_WithPrimitiveIntegerParameters() throws Exception {
        assertPrimitiveParameterCorrect(new Integer(2), "int");
    }

    public void testWritingBody_WithPrimitiveLongParameters() throws Exception {
        assertPrimitiveParameterCorrect(new Long(2), "long");
    }

    private void assertPrimitiveParameterCorrect(Object primtiveValue, String type) throws IOException {
        soapObject.addProperty(PARAMETER_NAME, primtiveValue);
        writeBodyWithSoapObject(soapObject);
        assertEquals(BODY_XML_STRING + ">" + getParameterBody(type, primtiveValue) + END_XML_FUNCTION_STRING, new String(outputStream.toByteArray()));
    }

    public void testWritingBody_NullBody() throws IOException {
        envelope.setOutputSoapObject(null);
        try {
            envelope.writeBody(xmlWriter);
            fail();
        } catch (NullPointerException e) {
            // TODO: This should probably do something intelligent instead of
            // throwing a null pointer exception
        }
    }

    public void testWritingBody_EmptyBody() throws Exception {
        writeBodyWithSoapObject(soapObject);
        assertEquals(BODY_XML_STRING + END_XML_STRING, new String(outputStream.toByteArray()));
    }

    private String getParameterBody(String type, Object aValue) {
        return "<" + PARAMETER_NAME + " n3:type=\"n2:" + type + "\" xmlns:n2=\"" + envelope.xsd + "\" xmlns:n3=\"" + envelope.xsi + "\">" + aValue + "</" + PARAMETER_NAME + ">";
    }

    private void writeBodyWithSoapObject(SoapObject soapObject) throws IOException {
        envelope.setOutputSoapObject(soapObject);
        envelope.writeBody(xmlWriter);
        xmlWriter.flush();
    }

}
