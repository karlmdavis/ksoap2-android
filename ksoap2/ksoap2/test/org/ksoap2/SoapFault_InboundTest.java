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

package org.ksoap2;

import java.io.*;

import org.ksoap2.serialization.*;
import org.ksoap2.transport.*;
import org.xmlpull.v1.*;

import junit.framework.*;

public class SoapFault_InboundTest extends TestCase {

    private MyTransport transport;
    private SoapSerializationEnvelope envelope;

    protected void setUp() throws Exception {
        super.setUp();
        transport = new MyTransport();
        envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
    }

    public void testFaultSerialize() throws Throwable {
        transport.parseResponse(envelope, ServiceConnectionFixture.faultStringAsStream());
        SoapFault fault = (SoapFault) envelope.bodyIn;
        assertTrue(fault instanceof SoapFault);
        assertEquals(ServiceConnectionFixture.FAULT_MESSAGE_STRING, fault.faultstring);
    }

    public class MyTransport extends Transport {
        protected void parseResponse(SoapEnvelope envelope, InputStream is) throws XmlPullParserException, IOException {
            super.parseResponse(envelope, is);
        }
    }

}
