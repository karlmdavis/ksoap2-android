package org.ksoap2.transport;

import java.io.IOException;

/**
 * This is a simple extension of the {@link HttpTransportSE} class. It provides the exact same functionality
 * as that class and is provided purely for backwards-compatibility purposes. It will likely be deprecated at
 * some point in the near future.
 */
public class AndroidHttpTransport extends HttpTransportSE
{
	/**
	 * @see HttpTransportSE#HttpTransportSE(String)
	 */
	public AndroidHttpTransport(String url)
	{
		super(url);
	}

	/**
	 * @see org.ksoap2.transport.HttpTransportSE#getServiceConnection()
	 */
	@Override
	protected ServiceConnection getServiceConnection() throws IOException
	{
		return new AndroidServiceConnection(super.url);
	}
}
