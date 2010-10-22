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

import junit.framework.TestCase;
import org.ksoap2.transport.ServiceConnectionFixture;
import org.kxml2.io.KXmlParser;
import org.kxml2.io.KXmlSerializer;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;

public class SoapFaultTest extends TestCase {
    private static final String FAULT_STRING = "<faultstring>The ISBN value contains invalid characters</faultstring>";

    public void testPlaceHolder() {
        // Just here so JUnit doesn't complain.
        return;
    }

    public void testFaultDeserialize() throws Throwable {
        SoapFault fault = generateFaultFromFaultString(ServiceConnectionFixture.FAULT_STRING);
        assertEquals(ServiceConnectionFixture.FAULT_MESSAGE_STRING, fault.faultstring);
    }

    public void testFaultSerialize() throws Throwable {
        KXmlSerializer xmlWriter = new KXmlSerializer();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        xmlWriter.setOutput(outputStream, "UTF-8");
        SoapFault fault = generateFaultFromFaultString(ServiceConnectionFixture.FAULT_STRING);
        fault.write(xmlWriter);

        String possibleOutputString = "<n0:Fault xmlns:n0=\"http://schemas.xmlsoap.org/soap/envelope/\"><faultcode>soap:Client</faultcode>"+FAULT_STRING+"<detail><detail>                <n1:InvalidIsbnFaultDetail xmlns:n1=\"http://www.Monson-Haefel.com/jwsbook/BookQuote\">                <offending-value>19318224-D</offending-value>                <conformance-rules>                    The first nine characters must be digits. The last                    character may be a digit or the letter 'X'. Case is                    not important.                </conformance-rules>                </n1:InvalidIsbnFaultDetail>            </detail>        ";
        String faultString = new String(outputStream.toByteArray());
        assertEquals(possibleOutputString, faultString);
    }

    private SoapFault generateFaultFromFaultString(String faultString) throws XmlPullParserException, IOException {
        SoapFault fault = new SoapFault();
        XmlPullParser parser = new KXmlParser();
        parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, true);
        parser.setInput(new StringReader(faultString));
        parser.nextTag();
        parser.nextTag();
        parser.nextTag();
        fault.parse(parser);
        return fault;
    }
    

}
