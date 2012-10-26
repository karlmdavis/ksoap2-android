/**
 *  Copyright (c) 2003,2004, Stefan Haustein, Oberhausen, Rhld., Germany
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

import java.util.List;
import java.util.zip.GZIPInputStream;
import java.io.*;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;

import org.ksoap2.*;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.xmlpull.v1.*;

/**
 * A J2SE based HttpTransport layer.
 */
public class KeepAliveHttpTransportSE extends HttpTransportSE {

    private ServiceConnection serviceConnection;

   

    /**
     * 
     * set the desired soapAction header field
     * 
     * @param soapAction
     *            the desired soapAction
     * @param envelope
     *            the envelope containing the information for the soap call.
     * @param headers
     *              a list of HeaderProperties to be http header properties when establishing the connection
     *            
     * @return <code>CookieJar</code> with any cookies sent by the server
     * @throws IOException
     * @throws XmlPullParserException
     */
    public List call(String soapAction, SoapEnvelope envelope, List headers) 
        throws IOException, XmlPullParserException {
        
        if ( headers == null ) {
            headers = new ArrayList();
        }
        
        HeaderProperty ref = getHeader( headers, "Connection" );
        
        if ( ref == null ) {
            ref = new HeaderProperty();
            headers.add( ref );
        }
        
        hp.setKey("Connection");
        hp.setValue("keep-alive");       
        
        call(soapAction, envelope, headers );
                
    }

    protected HeaderProperty getHeader(List lista, String key) {
        HeaderProperty res = null;
    
        if ( lista != null ) {
            for( int i = 0; i < lista.length; i++ ) {
                HeaderProperty hp = (HeaderProperty)lista.get(i);
                if ( key.equals( hp.getKey() ) ) {
                    res = hp;
                    break;
                }
            }
        }
    
        return res;
    }

    public ServiceConnection getServiceConnection() throws IOException {
        if (serviceConnection == null) {
            serviceConnection = new ServiceConnectionSE(proxy, url, timeout);
        }
        return serviceConnection;
    }

    public String getHost() {
        
        String retVal = null;
        
        retVal = serviceConnection.getHost();
        
        return retVal;
    }
        
    public int getPort() {
        
        int retVal = -1;
                
        retVal = serviceConnection.getPort();
        
        return retVal;
    }
        
    public String getPath() {
        
        String retVal = null;
                
        retVal = serviceConnection.getPath();
                
        return retVal;
    }
}
