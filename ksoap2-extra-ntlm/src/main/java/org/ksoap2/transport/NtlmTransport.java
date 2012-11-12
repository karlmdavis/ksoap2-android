package org.ksoap2.transport;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScheme;
import org.apache.http.auth.AuthSchemeFactory;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.NTCredentials;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.auth.NTLMScheme;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.ksoap2.HeaderProperty;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.xmlpull.v1.XmlPullParserException;

/**
 * A transport to be used with NTLM.
 *
 * Inspired by http://hc.apache.org/httpcomponents-client-ga/ntlm.html
 * @author Lian Hwang lian_hwang@hotmail.com
 * @author Manfred Moser <manfred@simpligity.com>
 */
public class NtlmTransport extends Transport {

    static final String ENCODING = "utf-8";

    private final DefaultHttpClient client = new DefaultHttpClient();
    private final HttpContext localContext = new BasicHttpContext();
    private String urlString;
    private String user;
    private String password;
    private String ntDomain;
    private String ntWorkstation;

    public void setCredentials(String url, String user, String password,
                               String domain, String workStation) {
        this.urlString = url;
        this.user = user;
        this.password = password;
        this.ntDomain = domain;
        this.ntWorkstation = workStation;

    }

    public List call(String targetNamespace, SoapEnvelope envelope, List headers)
            throws IOException, XmlPullParserException {
        return call(targetNamespace, envelope, headers, null);
    }

    public List call(String soapAction, SoapEnvelope envelope, List headers, File outputFile)
            throws IOException, XmlPullParserException {
        if (outputFile != null) {
            // implemented in HttpTransportSE if you are willing to port..
            throw new RuntimeException("Writing to file not supported");
        }
        HttpResponse resp = null;

        setupNtlm(urlString, user, password);

        try {
            // URL url = new URL(urlString);
            HttpPost httppost = new HttpPost(urlString);
//            UrlEncodedFormEntity byteArrayEntity =
//              new UrlEncodedFormEntity(new ArrayList<BasicNameValuePair>());
//            httppost.setEntity(byteArrayEntity);
            setHeaders(soapAction, envelope, httppost, headers);

            resp = client.execute(httppost, localContext);
            HttpEntity respEntity = resp.getEntity();

            InputStream is = respEntity.getContent();
            parseResponse(envelope, is);

        } catch (Exception ex) {
            // ex.printStackTrace();
        }

        if (resp != null) {
            return Arrays.asList(resp.getAllHeaders());
        } else {
            return null;
        }
    }

    private void setHeaders(String soapAction, SoapEnvelope envelope, HttpPost httppost, List headers) {
        byte[] requestData = null;
        try {
            requestData = createRequestData(envelope);
        } catch (IOException iOException) {
        }
        ByteArrayEntity byteArrayEntity = new ByteArrayEntity(requestData);
        httppost.setEntity(byteArrayEntity);
        httppost.addHeader("User-Agent", org.ksoap2.transport.Transport.USER_AGENT);
        // SOAPAction is not a valid header for VER12 so do not add
        // it
        // @see "http://code.google.com/p/ksoap2-android/issues/detail?id=67
        if (envelope.version != SoapSerializationEnvelope.VER12) {
            httppost.addHeader("SOAPAction", soapAction);
        }

        if (envelope.version == SoapSerializationEnvelope.VER12) {
            httppost.addHeader("Content-Type", Transport.CONTENT_TYPE_SOAP_XML_CHARSET_UTF_8);
        } else {
            httppost.addHeader("Content-Type", Transport.CONTENT_TYPE_XML_CHARSET_UTF_8);
        }

        // Pass the headers provided by the user along with the call
        if (headers != null) {
            for (int i = 0; i < headers.size(); i++) {
                HeaderProperty hp = (HeaderProperty) headers.get(i);
                httppost.addHeader(hp.getKey(), hp.getValue());
            }
        }
    }

    // Try to execute a cheap method first. This will trigger NTLM authentication
    public void setupNtlm(String dummyUrl, String userId, String password) {
        try {

            ((AbstractHttpClient) client).getAuthSchemes().register("ntlm", new NTLMSchemeFactory());

            NTCredentials creds = new NTCredentials(userId, password, ntWorkstation, ntDomain);
            client.getCredentialsProvider().setCredentials(AuthScope.ANY, creds);

            HttpGet httpget = new HttpGet(dummyUrl);

            HttpResponse response1 = client.execute(httpget, localContext);
            HttpEntity entity1 = response1.getEntity();

            Header[] hArray = response1.getAllHeaders();
            int size = hArray.length;
            for (int i = 0; i < size; i ++) {
                Header h = hArray[i];
                if (h.getName().equals("WWW-Authenticate")) {
                    entity1.consumeContent();
                    throw new Exception("Failed Authentication");
                }
            }

            entity1.consumeContent();
        } catch (Exception ex) {
            // swallow
        }
    }

        //NTLM Scheme factory
    private class NTLMSchemeFactory implements AuthSchemeFactory {
        public AuthScheme newInstance(final HttpParams params) {
        // see http://www.robertkuzma.com/2011/07/
        // manipulating-sharepoint-list-items-with-android-java-and-ntlm-authentication/
            return new NTLMScheme(new JCIFSEngine());
        }
    }

    public ServiceConnection getServiceConnection() throws IOException
    {
        throw new IOException("Not using ServiceConnection in transport");
    }

    public String getHost() {
        String retVal = null;
        try {
            retVal = new URL(url).getHost();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return retVal;
    }

    public int getPort() {
        int retVal = -1;
        try {
            retVal = new URL(url).getPort();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return retVal;
    }

    public String getPath() {
        String retVal = null;
        try {
            retVal = new URL(url).getPath();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return retVal;
    }
}
