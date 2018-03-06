/**
 * Copyright (c) 2003,2004, Stefan Haustein, Oberhausen, Rhld., Germany
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or
 * sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The  above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 * IN THE SOFTWARE.
 * <p>
 * Contributor(s): John D. Beatty, Dave Dash, F. Hunter, Alexander Krebs,
 * Lars Mehrmann, Sean McDaniel, Thomas Strang, Renaud Tognelli
 */
package org.ksoap2.transport;

import okhttp3.*;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.kxml2.io.KXmlParser;
import org.kxml2.io.KXmlSerializer;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Proxy;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A OkHttp based HttpTransport layer. Keep in mind that OkHttp supports Android 2.3 and above.
 * For Java, the minimum requirement is 1.7. (see https://square.github.io/okhttp/ )
 * <p>
 * You should also add okhttp dependency to your android projects.
 * compile 'com.squareup.okhttp3:okhttp:3.9.1'
 * <p>
 * If you use NTLM authentication, you should also add jcifs.
 * compile 'jcifs:jcifs:1.3.17'
 */
public class OkHttpTransport {
    public static final int DEFAULT_TIMEOUT = 20000;
    protected static final String USER_AGENT_PREFIX = "ksoap2-okhttp/3.6.3";

    private final String userAgent;
    private final OkHttpClient client;
    private final HttpUrl url;
    private final Headers headers;

    private OkHttpTransport(OkHttpTransport.Builder builder) {
        OkHttpClient.Builder clientBuilder;
        if (null != builder.client) {
            clientBuilder = builder.client.newBuilder();
        } else {
            clientBuilder = new OkHttpClient.Builder();
        }

        clientBuilder
                .connectTimeout(builder.timeout, TimeUnit.MILLISECONDS)
                .readTimeout(builder.timeout, TimeUnit.MILLISECONDS);

        if (null != builder.proxy) {
            clientBuilder.proxy(builder.proxy);

            if (null != builder.proxyAuthenticator) {
                clientBuilder.proxyAuthenticator(builder.proxyAuthenticator);
            }
        }

        if (null != builder.sslSocketFactory) {
            if (null == builder.trustManager) {
                throw new NullPointerException("TrustManager = null");
            }

            clientBuilder.sslSocketFactory(builder.sslSocketFactory, builder.trustManager);
        }

        if (null != builder.authenticator) {
            clientBuilder.authenticator(builder.authenticator);
        }

        client = clientBuilder.build();
        userAgent = buildUserAgent(builder);
        url = builder.url;
        headers = builder.headers;
    }

    private String buildUserAgent(Builder builder) {
        if (null != builder.userAgent) {
            return builder.userAgent;
        } else {
            // Try to get default agent to not loose environment definitions.
            final String agent = System.getProperty("http.agent");

            if (null != agent) {
                Matcher m = Pattern.compile("(\\s\\(.*\\))").matcher(agent);

                if (m.find() && m.groupCount() > 0 && m.group(1).length() > 0) {
                    return USER_AGENT_PREFIX + m.group(1);
                }
            }
        }

        return USER_AGENT_PREFIX;
    }

    /**
     * Perform a soap call with a given namespace and the given envelope providing
     * any extra headers that the user requires such as cookies. Headers that are
     * returned by the web service will be returned to the caller in the form of a
     * <code>List</code> of <code>HeaderProperty</code> instances.
     *
     * @param soapAction the namespace with which to perform the call in.
     * @param envelope   the envelope the contains the information for the call.
     * @return Headers returned by the web service as a <code>List</code> of
     * <code>HeaderProperty</code> instances.
     * @throws HttpResponseException  an IOException when Http response code is different from 200
     * @throws IOException            an IOException when XML Serialization fails or HTTP fails
     * @throws XmlPullParserException an XmlPullParserException when XML Parse fails
     */
    public Headers call(String soapAction, SoapEnvelope envelope)
            throws IOException, XmlPullParserException {
        return call(soapAction, envelope, null);
    }

    /**
     * Perform a soap call with a given namespace and the given envelope providing
     * any extra headers that the user requires such as cookies. Headers that are
     * returned by the web service will be returned to the caller in the form of a
     * <code>List</code> of <code>HeaderProperty</code> instances.
     *
     * @param soapAction the namespace with which to perform the call in.
     * @param envelope   the envelope the contains the information for the call.
     * @param headers    <code>List</code> of <code>HeaderProperty</code> headers to send with the SOAP request.
     * @return Headers returned by the web service as a <code>List</code> of
     * <code>HeaderProperty</code> instances.
     * @throws HttpResponseException  an IOException when Http response code is different from 200
     * @throws IOException            an IOException when XML Serialization fails or HTTP fails
     * @throws XmlPullParserException an XmlPullParserException when XML Parse fails
     */
    public Headers call(String soapAction, SoapEnvelope envelope, Headers headers)
            throws IOException, XmlPullParserException {

        if (soapAction == null) {
            soapAction = "\"\"";
        }

        MediaType contentType;
        if (envelope.version == SoapSerializationEnvelope.VER12) {
            contentType = MediaType.parse(Transport.CONTENT_TYPE_SOAP_XML_CHARSET_UTF_8);
        } else {
            contentType = MediaType.parse(Transport.CONTENT_TYPE_XML_CHARSET_UTF_8);
        }

        byte[] requestData = createRequestData(envelope);

        RequestBody body = RequestBody.create(contentType, requestData);

        Request.Builder builder = new Request.Builder()
                .url(url)
                .cacheControl(CacheControl.FORCE_NETWORK)
                .post(body);

        builder.addHeader("User-Agent", userAgent);

        // SOAPAction is not a valid header for VER12 so do not add it
        // @see "http://code.google.com/p/ksoap2-android/issues/detail?id=67
        if (envelope.version != SoapSerializationEnvelope.VER12) {
            builder.addHeader("SOAPAction", soapAction);
        }

        if (null != this.headers) {
            for (int i = 0; i < this.headers.size(); i++) {
                builder.addHeader(this.headers.name(i), this.headers.value(i));
            }
        }

        if (null != headers) {
            for (int i = 0; i < headers.size(); i++) {
                builder.addHeader(headers.name(i), headers.value(i));
            }
        }

        final Request request = builder.build();
        final Response response = client.newCall(request).execute();
        ResponseBody responseBody = null;
        try {
            if (response == null) {
                throw new HttpResponseException("Null response.", -1);
            }

            responseBody = response.body();
            if (responseBody == null) {
                throw new HttpResponseException("Null response body.", response.code());
            }

            final Headers resHeaders = response.headers();
            if (!response.isSuccessful()) {
                throw new HttpResponseException("HTTP request failed, HTTP status: " + response.code(),
                        response.code(), resHeaders);
            }

            parseResponse(envelope, responseBody.byteStream());

            return resHeaders;
        } catch (HttpResponseException e) {
            if (null != responseBody) { // Try to get soap fault
                try {
                    parseResponse(envelope, responseBody.byteStream());
                } catch (XmlPullParserException ignore) { }
            }

            throw e;
        } finally {
            if (null != responseBody) {
                responseBody.close(); // Release resources..
            }
        }
    }

    /**
     * Serializes the request.
     */
    private byte[] createRequestData(SoapEnvelope envelope)
            throws IOException {
        final ByteArrayOutputStream bos = new ByteArrayOutputStream(8 * 1024);
        final XmlSerializer xw = new KXmlSerializer();
        xw.setOutput(bos, "UTF-8");
        envelope.write(xw);
        xw.flush();
        bos.write('\r');
        bos.write('\n');
        bos.flush();
        return bos.toByteArray();
    }

    /**
     * Sets up the parsing to hand over to the envelope to deserialize.
     */
    private void parseResponse(SoapEnvelope envelope, InputStream is)
            throws XmlPullParserException, IOException {
        final XmlPullParser xp = new KXmlParser();
        xp.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, true);
        xp.setInput(is, null);
        envelope.parse(xp);
    }

    public static class Builder {
        private final HttpUrl url;
        private Proxy proxy = null;
        private int timeout = DEFAULT_TIMEOUT;
        private String userAgent = null;
        private Headers headers = null;
        private OkHttpClient client = null;
        private SSLSocketFactory sslSocketFactory = null;
        private X509TrustManager trustManager = null;
        private Authenticator authenticator = null;
        private Authenticator proxyAuthenticator = null;

        /**
         * Transport builder for OkHttp client.
         *
         * @param url the destination to POST SOAP data.
         */
        public Builder(HttpUrl url) {
            this.url = url;
        }

        /**
         * Transport builder for OkHttp client.
         *
         * @param url the destination to POST SOAP data.
         */
        public Builder(String url) {
            this.url = HttpUrl.parse(url);
        }

        /**
         * @param client User defined OkHttpClient
         * @return builder chain
         */
        public Builder client(OkHttpClient client) {
            this.client = client;
            return this;
        }

        /**
         * @param proxy Proxy server
         * @return builder chain
         */
        public Builder proxy(Proxy proxy) {
            this.proxy = proxy;
            return this;
        }

        /**
         * @param timeout Connection and Read timeout
         * @return builder chain
         */
        public Builder timeout(int timeout) {
            this.timeout = timeout;
            return this;
        }

        /**
         * @param userAgent User defined http.agent
         * @return builder chain
         */
        public Builder userAgent(String userAgent) {
            this.userAgent = userAgent;
            return this;
        }

        /**
         * @param headers HTTP headers those will be added as default.
         * @return builder chain
         */
        public Builder headers(Headers headers) {
            this.headers = headers;
            return this;
        }

        /**
         * SSLSocketFactory and X509TrustManager objects to verify SSL/TLS certificate.
         *
         * Example:
         * <code>
         * KeyStore trustStore = KeyStore.getInstance("BKS");
         * InputStream trustStoreStream = getResources().openRawResource(R.raw.truststore);
         * trustStore.load(trustStoreStream, null);
         * trustStoreStream.close();
         *
         * TrustManagerFactory trustManagerFactory =
         *         TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
         * trustManagerFactory.init(trustStore);
         * X509TrustManager x509TrustManager = (X509TrustManager) trustManagerFactory.getTrustManagers()[0];
         *
         * KeyManagerFactory keyManagerFactory =
         *         KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
         * keyManagerFactory.init(trustStore, null);     // BouncyCastle keystore don't
         *                                               // require password to load certificates.
         *
         * SSLContext sslContext = SSLContext.getInstance("TLS");
         * sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), null);
         *
         * SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
         * OkHttpTransport transport = new OkHttpTransport.Builder(webServiceUrl)
         *         .sslSocketFactory(sslSocketFactory, x509TrustManager)
         * </code>
         *
         * @param sslSocketFactory {@link SSLSocketFactory} object
         * @param trustManager     {@link X509TrustManager} object
         * @return builder chain
         */
        public Builder sslSocketFactory(SSLSocketFactory sslSocketFactory, X509TrustManager trustManager) {
            this.sslSocketFactory = sslSocketFactory;
            this.trustManager = trustManager;
            return this;
        }

        /**
         * @param authenticator An implementation of {@link okhttp3.Authenticator} interface.
         * @return builder chain
         */
        public Builder authenticator(Authenticator authenticator) {
            this.authenticator = authenticator;
            return this;
        }

        /**
         * @param authenticator An implementation of {@link okhttp3.Authenticator} interface.
         * @return builder chain
         */
        public Builder proxyAuthenticator(Authenticator authenticator) {
            this.proxyAuthenticator = authenticator;
            return this;
        }

        /**
         * @return OkHttpTransport object.
         */
        public OkHttpTransport build() {
            return new OkHttpTransport(this);
        }
    }
}
