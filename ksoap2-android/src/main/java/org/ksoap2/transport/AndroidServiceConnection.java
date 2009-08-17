package org.ksoap2.transport;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;

/**
 * Connection using apache HttpComponent
 */
public class AndroidServiceConnection implements ServiceConnection
{
	private final HttpPost httpPost;
	private final HttpClient httpClient;
	private java.io.ByteArrayOutputStream bufferStream = null;

	/**
	 * Constructor taking the {@link URI} to the endpoint for this soap communication.
	 * 
	 * @param endpointUri
	 *            the {@link URI} to open a connection to
	 */
	public AndroidServiceConnection(URI endpointUri) throws IOException
	{
		this.httpPost = new HttpPost(endpointUri);
		this.httpClient = new DefaultHttpClient();
	}

	/**
	 * @see org.ksoap2.transport.ServiceConnection#connect()
	 */
	public void connect() throws IOException
	{
		// Do nothing; connection will be made when request is executed
	}

	/**
	 * @see org.ksoap2.transport.ServiceConnection#disconnect()
	 */
	public void disconnect()
	{
		this.httpClient.getConnectionManager().shutdown();
	}

	/**
	 * @see org.ksoap2.transport.ServiceConnection#setRequestProperty(java.lang.String, java.lang.String)
	 */
	public void setRequestProperty(String name, String value)
	{
		httpPost.setHeader(name, value);
	}

	/**
	 * @see org.ksoap2.transport.ServiceConnection#setRequestMethod(java.lang.String)
	 */
	public void setRequestMethod(String requestMethod) throws IOException
	{
		if (!requestMethod.toLowerCase().equals("post"))
		{
			throw (new IOException("Only POST method is supported"));
		}
	}

	/**
	 * @see org.ksoap2.transport.ServiceConnection#openOutputStream()
	 */
	public OutputStream openOutputStream() throws IOException
	{
		bufferStream = new java.io.ByteArrayOutputStream();
		return bufferStream;
	}

	/**
	 * @see org.ksoap2.transport.ServiceConnection#openInputStream()
	 */
	public InputStream openInputStream() throws IOException
	{
		HttpEntity entity = new ByteArrayEntity(bufferStream.toByteArray());
		httpPost.setEntity(entity);
		HttpResponse response = this.httpClient.execute(this.httpPost);
		return response.getEntity().getContent();
	}

	/**
	 * @see org.ksoap2.transport.ServiceConnection#getErrorStream()
	 */
	public InputStream getErrorStream()
	{
		return null;
	}

}
