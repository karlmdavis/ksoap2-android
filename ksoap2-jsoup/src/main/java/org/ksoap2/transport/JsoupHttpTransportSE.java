package org.ksoap2.transport;

import org.jsoup.Jsoup;
import org.jsoup.parser.Parser;
import org.ksoap2.SoapEnvelope;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.Proxy;

public class JsoupHttpTransportSE extends HttpTransportSE {

    public JsoupHttpTransportSE(String url) {
        super(url);
    }

    public JsoupHttpTransportSE(String url, int timeout) {
        super(url, timeout);
    }

    public JsoupHttpTransportSE(String url, int timeout, int contentLength) {
        super(url, timeout, contentLength);
    }

    public JsoupHttpTransportSE(Proxy proxy, String url) {
        super(proxy, url);
    }

    public JsoupHttpTransportSE(Proxy proxy, String url, int timeout) {
        super(proxy, url, timeout);
    }

    public JsoupHttpTransportSE(Proxy proxy, String url, int timeout,
        int contentLength) {
        super(proxy, url, timeout, contentLength);
    }

    @Override
    protected void parseResponse(SoapEnvelope envelope, InputStream is) throws XmlPullParserException, IOException {
        envelope.bodyIn = Jsoup.parse(is, "UTF-8", "", Parser.xmlParser());
    }

}
