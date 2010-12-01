package org.ksoap2.cookiemanagement;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.http.Header;
import org.apache.http.cookie.Cookie;
import org.apache.http.cookie.CookieOrigin;
import org.apache.http.cookie.CookieSpec;
import org.apache.http.cookie.MalformedCookieException;
import org.apache.http.impl.cookie.BestMatchSpec;

import android.util.Log;

public class CookieJar {
	
	private static final String TAG = "COOKIEJAR";
	
	private Map<String, Cookie> _cookieMap;
	private CookieSpec _spec = new BestMatchSpec();
	
	public CookieJar() {
		_cookieMap = new HashMap<String, Cookie>();
	}
	
	public CookieJar(Map<String, Cookie> cookieMap) {

		if (cookieMap == null)
			throw new IllegalArgumentException("Cookie Map cannot be null");

		_cookieMap = cookieMap; 
	}
	
	public void saveCookies(Header header, CookieOrigin origin) {
		
		if (origin == null)
			throw new IllegalArgumentException("CookieOrigin cannot be null");
		
		if (header == null) 
			throw new IllegalArgumentException("Header cannot be null");
		
		List<Cookie> cookies;
		String key;
		
		try {
			
			// Parse the cookies (typically just one)
			cookies = _spec.parse(header, origin);
		} catch (MalformedCookieException e) {
			
			// Absorb invalid cookies
			Log.d(TAG, e.getMessage());
			return;
		}
		
		for (int i = 0; i < cookies.size(); i++) {
			
			try {
				Cookie cookie = cookies.get(i);
				
				// Check if the cookie is valid
				_spec.validate(cookie, origin);
				
				// Cookie is valid - save it
				key = cookie.getDomain() + "$" + cookie.getPath() + "$" + cookie.getName();
				
				// Remove any previous cookie with the same key. This allows the web server
				// to replace a cookie or delete a cookie by replacing it with one that has
				// expired
				_cookieMap.remove(key);
				
				// Save the new cookie
				_cookieMap.put(key, cookie);
			} catch (MalformedCookieException e) {
				
				// Remove invalid cookie
				cookies.remove(i);
			}
		}
	}
	
	public List<Header> sendCookies(CookieOrigin origin) {
		
		if (origin == null)
			throw new IllegalArgumentException("CookieOrigin cannot be null");
		
		List<Cookie> cookiesToSend = new LinkedList<Cookie>();
		Set<String> keys = _cookieMap.keySet();
		Date now = new Date();
		
		for (Iterator<String> i = keys.iterator(); i.hasNext();) {
			
			String key = i.next();
			Cookie cookie = _cookieMap.get(key);
			
			if (cookie.isExpired(now) == true) {
				_cookieMap.remove(key);
			}
			else if (_spec.match(cookie, origin)) {
				cookiesToSend.add(cookie);
			}
		}
		
		// formatCookies will throw an exception if the list is empty
		return (cookiesToSend.size() > 0)? _spec.formatCookies(cookiesToSend) : new LinkedList<Header>();
	}
	
	public List<Cookie> getCookies() {
		return new LinkedList<Cookie>(_cookieMap.values());
	}
}
