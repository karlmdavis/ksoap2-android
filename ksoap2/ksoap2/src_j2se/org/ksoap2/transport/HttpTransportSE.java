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
import java.net.HttpURLConnection;
import java.net.URL;

import org.ksoap2.SoapEnvelope;
import org.kxml2.io.KXmlParser;
import org.kxml2.io.KXmlSerializer;
import org.xmlpull.v1.*;



public class HttpTransportSE {

	String url;

	HttpURLConnection connection;

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
	 * @param url
	 *            the destination to POST SOAP data
	 * @param soapAction
	 *            the desired SOAP action (for HTTP headers)
	 */

	public HttpTransportSE(String url) {
		this.url = url;
	}

	/**
	 * Set the target url.
	 * 
	 * @param url
	 *            the target url.
	 */

	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * set the desired soapAction header field
	 * 
	 * @param soapAction
	 *            the desired soapAction
	 */

	public void call(String soapAction, SoapEnvelope envelope)
			throws IOException, XmlPullParserException {

		if (soapAction == null)
			soapAction = "\"\"";

		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		XmlSerializer xw = new KXmlSerializer();
		xw.setOutput(bos, null);
		envelope.write(xw);
		xw.flush();
		bos.write('\r');
		bos.write('\n');
		byte[] requestData = bos.toByteArray();
		bos = null;
		xw = null;

		requestDump = debug ? new String(requestData) : null;
		responseDump = null;

		connected = true;
		HttpURLConnection connection = (HttpURLConnection) new URL(url)
				.openConnection();

		connection.setUseCaches(false);
		connection.setDoOutput(true);
		connection.setDoInput(true);
		connection.setRequestProperty("User-Agent", "kSOAP/2.0");
		connection.setRequestProperty("SOAPAction", soapAction);
		connection.setRequestProperty("Content-Type", "text/xml");
		connection.setRequestProperty("Connection", "close");

		connection
				.setRequestProperty("Content-Length", "" + requestData.length);

		connection.setRequestMethod("POST");

		OutputStream os = connection.getOutputStream();
		os.write(requestData, 0, requestData.length);
		os.close();

		requestData = null;

		InputStream is;
		try {
			connection.connect();
			is = connection.getInputStream();
		} catch (IOException e) {
			is = connection.getErrorStream();
			if (is == null) {
				connection.disconnect();
				throw (e);
			}
		}

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
		xp.setInput(is, null);

		envelope.parse(xp);

	}

	/*
	 * Executes a SOAP Method and returns a response
	 * 
	 * @param method the remote soap method to be executed @return the result of
	 * the soap method @exception IOException if an error occurs
	 * 
	 * public Object call(SoapObject method) throws IOException {
	 * 
	 * requestEnvelope.setBody(method); call();
	 * 
	 * if (responseEnvelope.getBody() instanceof SoapFault) throw ((SoapFault)
	 * responseEnvelope.getBody());
	 * 
	 * return responseEnvelope.getResult(); }
	 * 
	 * public void call(XmlIO request, XmlIO result) throws IOException {
	 * requestEnvelope.setBody(request); responseEnvelope.setBody(result);
	 * 
	 * if (responseEnvelope.getBody() instanceof SoapFault) throw ((SoapFault)
	 * responseEnvelope.getBody()); }
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
				connection.disconnect ();
			} catch (Throwable e) {
			}
			connection = null;
		}
	}

}