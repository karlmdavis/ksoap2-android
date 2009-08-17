package org.ksoap2.transport;

import java.io.IOException;

/**
 * This is a simple extension of the {@link HttpTransportSE} class. It provides the exact same functionality
 * as that class and is provided purely for backwards-compatibility purposes. It will likely be deprecated at
 * some point in the near future.
 */
public class AndroidServiceConnection extends ServiceConnectionSE
{
	/**
	 * @see ServiceConnectionSE#ServiceConnectionSE(String)
	 */
	public AndroidServiceConnection(String url) throws IOException
	{
		super(url);
	}
}
