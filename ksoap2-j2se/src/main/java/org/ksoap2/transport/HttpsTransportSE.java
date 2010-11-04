package org.ksoap2.transport;

import java.io.IOException;

/**
 * HttpsTransportSE is a simple transport for https protocal based connections. It creates a #HttpsServiceConnectionSE
 * with the provided parameters.
 *
 * @author Manfred Moser <manfred@simpligility.com>
 */
public class HttpsTransportSE extends HttpTransportSE{

    static final String PROTOCOL = "https";

	private HttpsServiceConnectionSE conn = null;
    private final String host;
    private final int port;
    private final String file;
    private final int timeout;

    public HttpsTransportSE (String host, int port, String file, int timeout) {
        super(HttpsTransportSE.PROTOCOL + "://" + host + ":" + port + file);
        this.host = host;
        this.port = port;
        this.file = file;
        this.timeout = timeout;
    }

	/**
	 * Returns the HttpsServiceConnectionSE that was created in getServiceConnection or null
	 * if getServiceConnection was not called or failed.
	 * @return ServiceConnection
	 */
    public ServiceConnection getConnection() {
		return (HttpsServiceConnectionSE) conn;
	}

	/**
	 * Get a https service connection.
     * @see org.ksoap2.transport.HttpsTransportSE#getServiceConnection()
	 */
	//@Override
	protected ServiceConnection getServiceConnection() throws IOException
	{
        conn = new HttpsServiceConnectionSE(host, port, file, timeout);
        return conn;
	}
}
