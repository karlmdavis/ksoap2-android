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

package org.ksoap2.transport;

import java.io.*;

import junit.framework.*;

import org.ksoap2.*;
import org.ksoap2.serialization.*;
import org.ksoap2.transport.mock.*;
import org.xmlpull.v1.*;

public class HttpTransportSETest extends TestCase {
    ServiceConnectionFixture serviceConnection;
    private SoapSerializationEnvelope envelope;
    static final String containerNameSpaceURI = ServiceConnectionFixture.NAMESPACE;
    private SoapObject soapObject;

    protected void setUp() throws Exception {
        super.setUp();
        serviceConnection = new ServiceConnectionFixture();
        serviceConnection.setInputSring(ServiceConnectionFixture.WORKING_NOMULTIREF);
        envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        soapObject = new SoapObject(containerNameSpaceURI, "performComplexFunctionService");
    }

    public void testOutbound() throws Throwable {
        ComplexParameter complexParameter = new ComplexParameter();
        complexParameter.name = "Serenity";
        complexParameter.count = 56;
        envelope.addMapping(containerNameSpaceURI, "ComplexParameter", complexParameter.getClass());
        envelope.addMapping(containerNameSpaceURI, ServiceConnectionFixture.RESPONSE_CLASS_NAME, ServiceConnectionFixture.RESPONSE_CLASS);
        
        soapObject.addProperty("complexFunction", complexParameter);
        envelope.setOutputSoapObject(soapObject);

        MyTransport ht = new MyTransport("a url");
        ht.debug = true;
        try {
            ht.call(containerNameSpaceURI, envelope);
        } catch (XmlPullParserException e) {
            ht.os.flush();
            byte[] output = serviceConnection.outputStream.toByteArray();
            System.out.println("output " +output.length+" "+output[0] +" "+ output[1]);
        }
        Object resultObject = envelope.getResult();
        assertTrue(resultObject instanceof ComplexResponse);
    }

    class MyTransport extends HttpTransport {
        public MyTransport(String url) {
            super(url);
        }

        protected ServiceConnection getServiceConnection() throws IOException {
            return serviceConnection;
        }
    }
    

}
