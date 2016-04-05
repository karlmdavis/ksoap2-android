package org.ksoap2.transport;

import org.ksoap2.HeaderProperty;
import org.ksoap2.transport.ServiceConnection;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.internal.huc.HttpURLConnectionImpl;

/**
 * A simple ServiceConnection based on OkHttp3's URLConnection implementation
 * as a workaround for Android's timeout issues
 * ref. https://code.google.com/p/android/issues/detail?id=40874
 */
public class OkHttpServiceConnectionSE implements ServiceConnection {
    private HttpURLConnection connection;
    private OkHttpClient client;

    /**
     * Constructor taking the url to the endpoint for this soap communication
     * @param url the url to open the connection to.
     * @throws IOException
     */
    public OkHttpServiceConnectionSE(String url) throws IOException {
        this(null, url, ServiceConnection.DEFAULT_TIMEOUT);
    }

    public OkHttpServiceConnectionSE(Proxy proxy, String url) throws IOException {
        this(proxy, url, ServiceConnection.DEFAULT_TIMEOUT);
    }

    /**
     * Constructor taking the url to the endpoint for this soap communication
     * @param url the url to open the connection to.
     * @param timeout the connection and read timeout for the http connection in milliseconds
     * @throws IOException                            // 20 seconds
     */
    public OkHttpServiceConnectionSE(String url, int timeout) throws IOException {
        this(null, url, timeout);
    }

    public OkHttpServiceConnectionSE(Proxy proxy, String url, int timeout) throws IOException {
        client = new OkHttpClient.Builder()
                .connectTimeout(timeout, TimeUnit.MILLISECONDS)
                .readTimeout(timeout, TimeUnit.MILLISECONDS)
                .build();
        connection = new HttpURLConnectionImpl(new URL(url), client);
//        connection = (proxy == null)
//                ? new HttpURLConnectionImpl(new URL(url), client)
//                : (HttpURLConnectionImpl) new URL(url).openConnection(proxy);
        connection.setUseCaches(false);
        connection.setDoOutput(true);
        connection.setDoInput(true);
        connection.setConnectTimeout(timeout);
        connection.setReadTimeout(timeout); // even if we connect fine we want to time out if we cant read anything..
    }

    public void connect() throws IOException {
        connection.connect();
    }

    public void disconnect() {
        connection.disconnect();
    }

    public List getResponseProperties() throws IOException {
        List retList = new LinkedList();

        Map properties = connection.getHeaderFields();
        if(properties != null) {
            Set keys = properties.keySet();
            for (Iterator i = keys.iterator(); i.hasNext();) {
                String key = (String) i.next();
                List values = (List) properties.get(key);

                for (int j = 0; j < values.size(); j++) {
                    retList.add(new HeaderProperty(key, (String) values.get(j)));
                }
            }
        }

        return retList;
    }

    public int getResponseCode() throws IOException {
        return connection.getResponseCode();
    }

    public void setRequestProperty(String string, String soapAction) {
        connection.setRequestProperty(string, soapAction);
    }

    public void setRequestMethod(String requestMethod) throws IOException {
        connection.setRequestMethod(requestMethod);
    }

    /**
     * If the length of a HTTP request body is known ahead, sets fixed length
     * to enable streaming without buffering. Sets after connection will cause an exception.
     *
     * @param contentLength the fixed length of the HTTP request body
     * @see http://developer.android.com/reference/java/net/HttpURLConnection.html
     **/
    public void setFixedLengthStreamingMode(int contentLength) {
        connection.setFixedLengthStreamingMode(contentLength);
    }

    public void setChunkedStreamingMode() {
        connection.setChunkedStreamingMode(0);
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

    public String getHost() {
        return connection.getURL().getHost();
    }

    public int getPort() {
        return connection.getURL().getPort();
    }

    public String getPath() {
        return connection.getURL().getPath();
    }
}
