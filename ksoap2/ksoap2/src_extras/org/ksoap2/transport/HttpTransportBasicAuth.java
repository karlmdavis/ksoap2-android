/** 
 * Copyright (c) 2006, James Seigel, Calgary, AB., Canada
 * Copyright (c) 2003,2004, Stefan Haustein, Oberhausen, Rhld., Germany
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
 * Contributor(s): Paul Spencer, John D. Beatty, Dave Dash, F. Hunter, 
 *                  Renaud Tognelli, Thomas Strang, Alexander Krebs, Sean McDaniel
 */
package org.ksoap2.transport;

import java.io.*;
import javax.microedition.io.*;
import org.ksoap2.*;
import org.xmlpull.v1.*;

/**
 * An Http transport layer class which provides a mechanism to login to
 * webservices using Basic Authentication
 */
public class HttpTransportBasicAuth extends Transport {
    ServiceConnection connection;
    OutputStream os;
    InputStream is;
    private String username;
    private String password;
    /** state info */
    private boolean connected = false;

    /**
     * Constructor with username and password
     * 
     * @param url
     *            The url address of the webservice endpoint
     * @param username
     *            Username for the Basic Authentication challenge RFC 2617 *
     * @param password
     *            Password for the Basic Authentication challenge RFC 2617
     */
    public HttpTransportBasicAuth(String url, String username, String password) {
        super(url);
        this.username = username;
        this.password = password;
    }

    /**
     * Set the target url.
     * 
     * @param url
     *            the target url.
     */

    public void setUrl(String url) {
        super.setUrl(url);
        this.username = null;
        this.password = null;
    }

    /**
     * Call the webservice endpoint with the specific soap action specified and
     * the envelope containing the request and where the result will be returned
     * to.
     * 
     * @param soapAction
     *            the desired soapAction
     * @param envelope
     *            the envelope containing the request and where the result will
     *            be deserialized into.
     */

    public void call(String soapAction, SoapEnvelope envelope) throws IOException, XmlPullParserException {
        if (soapAction == null)
            soapAction = "\"\"";
        byte[] requestData = createRequestData(envelope);

        requestDump = debug ? new String(requestData) : null;
        responseDump = null;

        try {
            connected = true;
            connection = getServiceConnection();
            connection.setRequestProperty("SOAPAction", soapAction);
            connection.setRequestProperty("Content-Type", "text/xml");
            connection.setRequestProperty("Content-Length", "" + requestData.length);

            connection.setRequestProperty("User-Agent", "kSOAP/2.0");
            if (username != null && password != null) {
                StringBuffer buf = new StringBuffer(username);
                buf.append(':').append(password);
                byte[] raw = buf.toString().getBytes();
                buf.setLength(0);
                buf.append("Basic ");
                org.kobjects.base64.Base64.encode(raw, 0, raw.length, buf);
                connection.setRequestProperty("Authorization", buf.toString());
            }

            connection.setRequestMethod(HttpConnection.POST);

            os = connection.openOutputStream();
            os.write(requestData, 0, requestData.length);
            os.close();

            requestData = null;

            is = connection.openInputStream();

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
        } finally {
            if (!connected)
                throw new InterruptedIOException();
            reset();
        }

        if (envelope.bodyIn instanceof SoapFault)
            throw ((SoapFault) envelope.bodyIn);

    }

    /**
     * Closes the connection and associated streams. This method does not need
     * to be explictly called since the uderlying connections and streams are
     * only opened and valid inside of the call method. Close can be called
     * ansynchronously, from another thread to potentially release another
     * thread that is hung up doing network io inside of call. Caution should be
     * taken, however when using this as a psedu timeout mechanism. it is a
     * valid and suggested approach for the motorola handsets. oh, and it works
     * in the emulator...
     */
    public void reset() {
        connected = false;
        if (is != null) {
            try {
                is.close();
            } catch (Throwable e) {
            }
            is = null;
        }
        if (connection != null) {
            try {
                connection.disconnect();
            } catch (Throwable e) {
            }
            connection = null;
        }
    }

    protected ServiceConnection getServiceConnection() throws IOException {
        return new ServiceConnectionMidp(url);
    }

}
