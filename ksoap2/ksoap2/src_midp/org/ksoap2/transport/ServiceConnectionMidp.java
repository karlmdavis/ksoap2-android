/* Copyright (c) 2003,2004, Stefan Haustein, Oberhausen, Rhld., Germany
 * Copyright (c) 2006, James Seigel, Calgary, AB., Canada
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

import javax.microedition.io.*;

public class ServiceConnectionMidp implements ServiceConnection {
    private HttpConnection connection;

    public ServiceConnectionMidp(String url) throws IOException {
        connection = (HttpConnection) Connector.open(url, Connector.READ_WRITE, true);
    }

    public void disconnect() throws IOException {
        connection.close();
    }

    public void setRequestProperty(String string, String soapAction) throws IOException {
        connection.setRequestProperty(string, soapAction);
    }

    public void setRequestMethod(String post) throws IOException {
        connection.setRequestMethod(post);
    }

    public OutputStream openOutputStream() throws IOException {
        return connection.openOutputStream();
    }

    public InputStream openInputStream() throws IOException {
        return connection.openInputStream();
    }

    public void connect() throws IOException {
    }

    public InputStream getErrorStream() {
        throw new RuntimeException("ServiceConnectionMidp.getErrorStream is not available.");
    }

}
