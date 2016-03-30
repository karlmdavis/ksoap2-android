package org.ksoap2.transport;

import java.io.IOException;
import java.net.Proxy;

/**
 * A simple Transport based on OkHttp3's URLConnection implementation
 * as a workaround for Android's timeout issues
 * ref. https://code.google.com/p/android/issues/detail?id=40874
 */
public class OkHttpTransportSE extends HttpTransportSE {
    public OkHttpTransportSE(Proxy proxy, String url) {
        super(proxy, url);
    }

    public OkHttpTransportSE(Proxy proxy, String url, int timeout) {
        super(proxy, url, timeout);
    }

    public OkHttpTransportSE(Proxy proxy, String url, int timeout, int contentLength) {
        super(proxy, url, timeout, contentLength);
    }

    public OkHttpTransportSE(String url) {
        super(url);
    }

    public OkHttpTransportSE(String url, int timeout) {
        super(url, timeout);
    }

    public OkHttpTransportSE(String url, int timeout, int contentLength) {
        super(url, timeout, contentLength);
    }

    @Override
    public ServiceConnection getServiceConnection() throws IOException {
        return new OkHttpServiceConnectionSE(proxy, url, timeout);
    }
}
