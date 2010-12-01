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
import java.net.*;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.http.Header;
import org.apache.http.cookie.CookieOrigin;
import org.apache.http.message.BasicHeader;
import org.ksoap2.cookiemanagement.*;

/**
 * Connection for J2SE environments.
 */
public class ServiceConnectionSE implements ServiceConnection {

    private HttpURLConnection connection;

    /**
     * Constructor taking the url to the endpoint for this soap communication
     * @param url the url to open the connection to.
     */
    public ServiceConnectionSE(String url) throws IOException {
    	this(null, url);
    }

    public ServiceConnectionSE(Proxy proxy, String url) throws IOException {
    	
    	connection = (proxy == null)
    		? (HttpURLConnection) new URL(url).openConnection()
    		: (HttpURLConnection) new URL(url).openConnection(proxy);
        connection.setUseCaches(false);
        connection.setDoOutput(true);
        connection.setDoInput(true);
    }

    public void connect() throws IOException {
        connection.connect();
    }

    public void disconnect() {
        connection.disconnect();
    }

    public void setRequestProperty(String string, String soapAction) {
        connection.setRequestProperty(string, soapAction);
    }

    public void setRequestMethod(String requestMethod) throws IOException {
        connection.setRequestMethod(requestMethod);
    }

    @Override
    public CookieJar saveCookies(CookieJar cookieJar) {
    	
		if (cookieJar == null)
			throw new IllegalArgumentException("CookieJar cannot be null");
		
    	Map<String, List<String>> headers = connection.getHeaderFields();
    	Set<String> keys = headers.keySet();
    	CookieOrigin origin = new CookieOrigin(
    			connection.getURL().getHost(), 
    			connection.getURL().getPort(), 
    			connection.getURL().getPath(), 
    			false);
    	
    	for (Iterator<String> iter = keys.iterator(); iter.hasNext();) {
    		String key = iter.next();
    		
    		if (key.equalsIgnoreCase("set-cookie") || key.equalsIgnoreCase("set-cookie2")) {
    			
    			List<String> values = headers.get(key);
    			
    			for (int i = 0; i < values.size(); i++) {
        			cookieJar.saveCookies(new BasicHeader(key, values.get(i)), origin);
    			}
    		}
    	}
    	
    	return cookieJar;
    }
    
    @Override
    public void sendCookies(CookieJar cookieJar) {

		if (cookieJar == null)
			throw new IllegalArgumentException("CookieJar cannot be null");
		
    	CookieOrigin origin = new CookieOrigin(
    			connection.getURL().getHost(), 
    			connection.getURL().getPort(), 
    			connection.getURL().getPath(), 
    			true);
    	List<Header> cookies = cookieJar.sendCookies(origin);
    	
    	for (int i = 0; i < cookies.size(); i++) {
    		Header cookie = cookies.get(i);
    		connection.addRequestProperty(cookie.getName(), cookie.getValue());
    	}
    }

    public OutputStream openOutputStream() throws IOException {
        return connection.getOutputStream();
    }

    public InputStream openInputStream() throws IOException {
        return connection.getInputStream();
    }

    public InputStream getErrorStream() {
        return connection.getErrorStream();
    }

}
