/* Copyright (c) 2003,2004, Stefan Haustein, Oberhausen, Rhld., Germany
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
 * IN THE SOFTWARE. 
 *
 * Contributor(s): John D. Beatty, Dave Dash, F. Hunter, Alexander Krebs, 
 *                 Lars Mehrmann, Sean McDaniel, Thomas Strang, Renaud Tognelli 
 * */
package org.ksoap2.transport;

import java.io.*;

import org.ksoap2.*;
import org.xmlpull.v1.*;

public class HttpTransportSE extends Transport {
    OutputStream os;
    InputStream is;
    private boolean connected;

    /**
     * Creates instance of HttpTransport with set url and SoapAction
     * 
     * @param url
     *            the destination to POST SOAP data
     * @param soapAction
     *            the desired SOAP action (for HTTP headers)
     */

    public HttpTransportSE(String url) {
        this.url = url;
    }

    /**
     * set the desired soapAction header field
     * 
     * @param soapAction
     *            the desired soapAction
     */

    public void call(String soapAction, SoapEnvelope envelope) throws IOException, XmlPullParserException {
        if (soapAction == null)
            soapAction = "\"\"";
        byte[] requestData = createRequestData(envelope);
        requestDump = debug ? new String(requestData) : null;
        responseDump = null;
        connected = true;
        ServiceConnection connection = getServiceConnection();
        connection.setRequestProperty("User-Agent", "kSOAP/2.0");
        connection.setRequestProperty("SOAPAction", soapAction);
        connection.setRequestProperty("Content-Type", "text/xml");
        connection.setRequestProperty("Connection", "close");
        connection.setRequestProperty("Content-Length", "" + requestData.length);
        connection.setRequestMethod("POST");
        OutputStream os = connection.openOutputStream();
        os.write(requestData, 0, requestData.length);
        os.close();
        requestData = null;
        InputStream is;
        try {
            connection.connect();
            is = connection.openInputStream();
        } catch (IOException e) {
            is = connection.getErrorStream();
            if (is == null) {
                connection.disconnect();
                throw (e);
            }
        }
        if (debug) {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] buf = new byte[256];
            while (true) {
                int rd = is.read(buf, 0, 256);
                if (rd == -1)
                    break;
                bos.write(buf, 0, rd);
            }
            buf = bos.toByteArray();
            responseDump = new String(buf);
            is.close();
            is = new ByteArrayInputStream(buf);
        }
        parseResponse(envelope, is);
    }

    protected ServiceConnection getServiceConnection() throws IOException {
        return new ServiceConnectionSE(url);
    }
}