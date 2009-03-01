package org.ksoap2.transport;

import java.io.*;

import org.ksoap2.transport.*;
import org.ksoap2.*;
import org.xmlpull.v1.*;

/**
 * Apache HttpComponent based HttpTransport layer.
 */
public class AndroidHttpTransport extends Transport {

    /**
     * Creates instance of HttpTransport with set url
     * 
     * @param url
     *            the destination to POST SOAP data
     */
    public AndroidHttpTransport(String url) {
        super(url);
    }

    /**
     * set the desired soapAction header field
     * 
     * @param soapAction
     *            the desired soapAction
     * @param envelope
     *            the envelope containing the information for the soap call.
     */
    public void call(String soapAction, SoapEnvelope envelope) throws IOException, XmlPullParserException {
        if (soapAction == null)
            soapAction = "\"\"";
        byte[] requestData = createRequestData(envelope);
        requestDump = debug ? new String(requestData) : null;
        responseDump = null;
        ServiceConnection connection = getServiceConnection();
        connection.connect();
        try {
	        connection.setRequestProperty("User-Agent", "kSOAP/2.0");
	        connection.setRequestProperty("SOAPAction", soapAction);
	        connection.setRequestProperty("Content-Type", "text/xml");
	        connection.setRequestProperty("Connection", "close");
	        connection.setRequestProperty("Content-Length", "" + requestData.length);
	        connection.setRequestMethod("POST");	        
	        OutputStream os = connection.openOutputStream();
	        os.write(requestData, 0, requestData.length);
	        os.flush();
	        os.close();
		    requestData = null;
	        
	        InputStream is;
	        try {
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
	            bos.flush();
	            buf = bos.toByteArray();
	            responseDump = new String(buf);
	            is.close();
	            is = new ByteArrayInputStream(buf);
	            if (debug) {
		            System.out.println("DBG:request:" + requestDump);
		            System.out.println("DBG:response:" + responseDump);
	            }
	        }	        
	        parseResponse(envelope, is);
        } finally {
        	connection.disconnect();
        }
    }

    protected ServiceConnection getServiceConnection() throws IOException {
        return new AndroidServiceConnection(url);
    }
}
