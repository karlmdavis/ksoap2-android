package org.ksoap2.transport;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpConnection;
import org.apache.commons.httpclient.HttpConnectionManager;
import org.apache.commons.httpclient.HttpState;
import org.apache.commons.httpclient.HttpURL;
import org.apache.commons.httpclient.SimpleHttpConnectionManager;
import org.apache.commons.httpclient.methods.ByteArrayRequestEntity;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.ksoap2.transport.ServiceConnection;


/**
 * Connection using apache HttpComponent
 */
public class AndroidServiceConnection implements ServiceConnection {
	private static HttpConnectionManager connectionManager = new SimpleHttpConnectionManager();
    private HttpConnection connection;
    private PostMethod postMethod;
    private java.io.ByteArrayOutputStream bufferStream = null;
    
    /**
     * Constructor taking the url to the endpoint for this soap communication
     * @param url the url to open the connection to.
     */
    public AndroidServiceConnection(String url) throws IOException {
    	HttpURL httpURL = new HttpURL(url);
    	HostConfiguration host = new HostConfiguration();
    	host.setHost(httpURL.getHost(), httpURL.getPort());
        connection = connectionManager.getConnection(host);
        postMethod = new PostMethod(url);
    }

    public void connect() throws IOException {
        if (!connection.isOpen()) {
        	connection.open();
        }
    }

    public void disconnect() {
        connection.releaseConnection();
    }

    public void setRequestProperty(String name, String value) {
    	postMethod.setRequestHeader(name, value);
    }

    public void setRequestMethod(String requestMethod) throws IOException {
        if (!requestMethod.toLowerCase().equals("post")) {
        	throw(new IOException("Only POST method is supported"));
        }
    }

    public OutputStream openOutputStream() throws IOException {
    	bufferStream = new java.io.ByteArrayOutputStream();
    	return bufferStream;
    }

    public InputStream openInputStream() throws IOException {
    	RequestEntity re = new ByteArrayRequestEntity(bufferStream.toByteArray());
    	postMethod.setRequestEntity(re);
    	postMethod.execute(new HttpState(), connection);
    	return postMethod.getResponseBodyAsStream();
    }

    public InputStream getErrorStream() {
    	return null;
    }
    
}
