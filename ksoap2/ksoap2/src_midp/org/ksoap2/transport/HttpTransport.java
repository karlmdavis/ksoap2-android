/* kSOAP
 *
 * The contents of this file are subject to the Enhydra Public License
 * Version 1.1 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License
 * on the Enhydra web site ( http://www.enhydra.org/ ).
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See
 * the License for the specific terms governing rights and limitations
 * under the License.
 *
 * The Initial Developer of kSOAP is Stefan Haustein. Copyright (C)
 * 2000, 2001 Stefan Haustein, D-46045 Oberhausen (Rhld.),
 * Germany. All Rights Reserved.
 *
 * Contributor(s): John D. Beatty, Dave Dash, F. Hunter, Renaud Tognelli,
 *                 Thomas Strang, Alexander Krebs, Sean McDaniel
 * */

package org.ksoap2.transport;

import java.io.*;
import javax.microedition.io.*;
import org.xmlpull.v1.*;
import org.kxml2.io.*;
import org.ksoap2.*;

/** Methods to facilitate SOAP calls over HTTP using the
    J2ME generic connection framework.
    <p>
    Instances of HttpTransport can be in one of two states: connected
    and not connected.  When an invocation on call is made the
    instance is in a connected state until call returns or throws
    an IOException.  in any case once control is returned to the
    caller the instance is again in the not connected state.  HttpTransport
    is not thread safe and applications should ensure that only one
    thread is inside the call method at any given time.  It is
    designed in such a way that applications can reuse a single instance
    for all soap calls to one, or multiple, target endpoints.<p>

    The underlying HttpConnection is opened with the timeout
    flag set. In the MIDP API this flag is only a hint to the
    underlying protocol handler to throw an InterrruptIOException,
    however, there are no guarantees that it will be handled.  So
    rather than support a timeout mechanism internally the design
    is such that applications can manage timeouts in an environment
    dependent way.<p>

    For example some environments may allow for a timeout parameter
    that can be externally specified in perhaps a system property
    (which?  I don't know. it's in the api).  Others like
    the emulator (ok, who cares) and the Motorola i85s can use
    a simple and effective timeout mechanism that closes the
    connection and associated streams in an asynchronous fashion.
    Calling the close( ) method inside of a separate thread can
    provide for this timeout handling by releasing threads that
    maybe stuck inside of call( ) performing network io.<p>

    Here is some sample code to demonstrate how such a timeout
    mechanism may look:<br>
    <pre>
    private HttpTransport soap;
      ...
    TimerTask task =
      new TimerTask( ) { public void run( ) { soap.close( ); } };

    try {
      new Timer( ).schedule( task, TIMEOUT );
      soap.call( soapobject );  // invoke method
      task.cancel( );           // cancel the timeout

    } catch ( InterruptedIOException e ) {
      // handle timeout here...

    } catch ( IOException e ) {
      // some other io problem...
    }
    </pre><br>
    The call( ) method will throw and InterruptedIOException if
    the instance is no longer in the connected state before
    control is returned to the caller.  The call to soap.close( )
    inside the TimerTask transitions the HttpConnection into
    a not connected state.<p>
    <b>Note</b>:  The InterruptedIOException will be caught by a thread
    waiting on network io, however, it may not be immediate.  It is
    assumed that the protocol handler will gracefully handle the
    lifecycle of the outputstream and therefore it is not closed
    inside the close method.  IOW the waiting thread will be interrupted
    after the outputstream has been flushed.  If the waiting thread
    is hung up waiting for input a call to close from a separate thread
    the exception is observed right away and will return before the
    thread calling close.  <b>At least this is what has been observation
    on the i85s handset.</b>  On this device, if a call to
    outputstream.close( ) is made while the outputstream is
    being flushed it seems to cause a deadlock, ie outputstream will
    never return.
 */

public class HttpTransport {

    String url;

    HttpConnection connection;
    OutputStream os;
    InputStream is;

    /** state info */
    private boolean connected = false;

    /** Set to true if debugging */
    public boolean debug;

    /** String dump of request for debugging. */
    public String requestDump;

    /** String dump of response for debugging */
    public String responseDump;


    /**
     * Creates instance of HttpTransport with set url and SoapAction
     *
     * @param url the destination to POST SOAP data
     * @param soapAction the desired SOAP action (for HTTP headers)
     */

    public HttpTransport(String url) {
        this.url = url;
    }

    /**
     * Set the target url.
     *
     * @param url the target url.
     */

    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * set the desired soapAction header field
     *
     * @param soapAction the desired soapAction
     */


    public void call(String soapAction, SoapEnvelope envelope) throws IOException, XmlPullParserException {
        call (soapAction, envelope, envelope);
    }

    public void call(String soapAction, SoapEnvelope requestEnvelope, SoapEnvelope responseEnvelope) throws IOException, XmlPullParserException  {

        if (soapAction == null) 
            soapAction = "\"\"";

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        XmlSerializer xw = new KXmlSerializer();
        xw.setOutput(bos, null);
        requestEnvelope.write(xw);
        xw.flush();
        bos.write('\r');
        bos.write('\n');
        byte[] requestData = bos.toByteArray();
        bos = null;
        xw = null;

        requestDump = debug ? new String(requestData) : null;
        responseDump = null;

        try {
            connected = true;
            connection =
                (HttpConnection) Connector.open(
                    url,
                    Connector.READ_WRITE,
                    true);

            connection.setRequestProperty("SOAPAction", soapAction);
            connection.setRequestProperty("Content-Type", "text/xml");
            connection.setRequestProperty(
                "Content-Length",
                "" + requestData.length);

            connection.setRequestProperty("User-Agent", "kSOAP/2.0");

            connection.setRequestMethod(HttpConnection.POST);

            os = connection.openOutputStream();
            os.write(requestData, 0, requestData.length);
            //            os.flush ();  // removed in order to avoid chunked encoding
            os.close();

            requestData = null;

            is = connection.openInputStream();

            if (debug) {
                bos = new ByteArrayOutputStream();
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

            XmlPullParser xp = new KXmlParser();
            xp.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, true);
            xp.setInput (is, null);

            responseEnvelope.parse(xp);

        }
        finally {
            if (!connected)
                throw new InterruptedIOException();
            reset();
        }
    }

    /*
     * Executes a SOAP Method and returns a response
     *
     * @param method the remote soap method to be executed
     * @return the result of the soap method
     * @exception IOException if an error occurs

    public Object call(SoapObject method) throws IOException {

        requestEnvelope.setBody(method);
        call();

        if (responseEnvelope.getBody() instanceof SoapFault)
            throw ((SoapFault) responseEnvelope.getBody());

        return responseEnvelope.getResult();
    }

    public void call(XmlIO request, XmlIO result) throws IOException {
        requestEnvelope.setBody(request);
        responseEnvelope.setBody(result);

        if (responseEnvelope.getBody() instanceof SoapFault)
            throw ((SoapFault) responseEnvelope.getBody());
    }
     */



    /**
     * Closes the connection and associated streams.  This method
     * does not need to be explictly called since the uderlying
     * connections and streams are only opened and valid inside of
     * the call method.  Close can be called ansynchronously,
     * from another thread to potentially release another thread
     * that is hung up doing network io inside of call.  Caution
     * should be taken, however when using this as a psedu timeout
     * mechanism.  it is a valid and suggested approach for the
     * motorola handsets.  oh, and it works in the emulator...
     */

    public void reset() {
        connected = false;

        if (is != null) {
            try {
                is.close();
            }
            catch (Throwable e) {
            }
            is = null;
        }

        if (connection != null) {
            try {
                connection.close();
            }
            catch (Throwable e) {
            }
            connection = null;
        }
    }

}
