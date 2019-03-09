package org.ksoap2.transport;

import java.io.IOException;
import java.net.Proxy;
import okhttp3.OkHttpClient;

/**
 * A simple Transport based on OkHttp3's URLConnection implementation
 * as a workaround for Android's timeout issues
 * ref. https://code.google.com/p/android/issues/detail?id=40874
 */
public class OkHttpTransportSE extends HttpTransportSE {

    protected final OkHttpClient client;

    public OkHttpTransportSE(Proxy proxy, String url) {
        super(proxy, url);
        this.client = null;
    }

    public OkHttpTransportSE(Proxy proxy, String url, int timeout) {
        super(proxy, url, timeout);
        this.client = null;
    }

    public OkHttpTransportSE(Proxy proxy, String url, int timeout, int contentLength) {
        super(proxy, url, timeout, contentLength);
        this.client = null;
    }

    public OkHttpTransportSE(OkHttpClient client, Proxy proxy, String url, int timeout, int contentLength) {
        super(proxy, url, timeout, contentLength);
        this.client = client;
    }

    public OkHttpTransportSE(String url) {
        super(url);
        this.client = null;
    }

    public OkHttpTransportSE(String url, int timeout) {
        super(url, timeout);
        this.client = null;
    }

    public OkHttpTransportSE(String url, int timeout, int contentLength) {
        super(url, timeout, contentLength);
        this.client = null;
    }

    @Override
    public ServiceConnection getServiceConnection() throws IOException {
        if (client == null) {
            return new OkHttpServiceConnectionSE(proxy, url, timeout);
        } else {
            return new OkHttpServiceConnectionSE(client, proxy, url, timeout);
        }
    }
}
