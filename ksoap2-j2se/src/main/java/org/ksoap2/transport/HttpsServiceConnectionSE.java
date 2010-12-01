package org.ksoap2.transport;

import javax.net.ssl.HttpsURLConnection;

import org.apache.http.Header;
import org.apache.http.cookie.CookieOrigin;
import org.apache.http.message.BasicHeader;
import org.ksoap2.cookiemanagement.CookieJar;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;
import java.util.List;
import java.util.Set;

/**
 * HttpsServiceConnectionSE is a service connection that uses a https url connection and requires explicit setting of
 * host, port and file.
 *
 * The explicit setting is necessary since pure url passing and letting the Java URL class parse the string does not
 * work properly on Android.
 *
 * Links for reference:
 * @see "http://stackoverflow.com/questions/2820284/ssl-on-android-strange-issue"
 * @see "http://stackoverflow.com/questions/2899079/custom-ssl-handling-stopped-working-on-android-2-2-froyo"
 * @see "http://code.google.com/p/android/issues/detail?id=2690"
 * @see "http://code.google.com/p/android/issues/detail?id=2764"
 *
 * @author Manfred Moser <manfred@simpligility.com>
 */
public class HttpsServiceConnectionSE implements ServiceConnection {

    private HttpsURLConnection connection;

    public HttpsServiceConnectionSE(String host, int port, String file, int timeout) throws IOException {
        connection = (HttpsURLConnection) new URL(HttpsTransportSE.PROTOCOL, host, port, file).openConnection();
        updateConnectionParameters(timeout);
    }

    private void updateConnectionParameters(int timeout) {
        connection.setConnectTimeout(timeout); // 20 seconds like in iphone app
        connection.setReadTimeout(timeout); // even if we connect fine we want to time out if we cant read anything..
        connection.setUseCaches(false);
        connection.setDoOutput(true);
        connection.setDoInput(true);

        // Allowing any hostname  through with https:// so basically disabling host checking when https is used.
        //Attention! This is not secure against man in the middle attacks.
        // supposed to be a workaround for https problems.
        // @see "http://android.amberfog.com/?p=45"
//        connection.setHostnameVerifier(new AllowAllHostnameVerifier());

        // when the url is created with protocol, host, port and file this workaround is not necessary
    }

    public void connect() throws IOException {
        connection.connect();
    }

    public void disconnect() {
        connection.disconnect();
    }

    public void setRequestProperty(String key, String value) {
        connection.setRequestProperty(key, value);
    }

    public void setRequestMethod(String requestMethod) throws IOException {
        connection.setRequestMethod(requestMethod);
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

    @Override
    public CookieJar saveCookies(CookieJar cookieJar) {
    	
    	Map<String, List<String>> headers = connection.getHeaderFields();
    	Set<String> keys = headers.keySet();
    	CookieOrigin origin = new CookieOrigin(
    			connection.getURL().getHost(), 
    			connection.getURL().getPort(), 
    			connection.getURL().getPath(), 
    			true);
    	
    	for (Iterator<String> iter = keys.iterator(); iter.hasNext();) {
    		String key = iter.next();
    		
    		if (key.equalsIgnoreCase("set-cookie") || key.equalsIgnoreCase("set-cookie2")) {
    			
    			List<String> values = headers.get(key);
    			
    			for (int i = 0; i < values.size(); i++) {
        			cookieJar.saveCookies(new BasicHeader(key, values.get(i)), origin);
    			}
    		}
    	}
    	
    	return cookieJar;
    }
    
    @Override
    public void sendCookies(CookieJar cookieJar) {

    	CookieOrigin origin = new CookieOrigin(
    			connection.getURL().getHost(), 
    			connection.getURL().getPort(), 
    			connection.getURL().getPath(), 
    			true);
    	List<Header> cookies = cookieJar.sendCookies(origin);
    	
    	for (int i = 0; i < cookies.size(); i++) {
    		Header cookie = cookies.get(i);
    		connection.addRequestProperty(cookie.getName(), cookie.getValue());
    	}
    }
}
