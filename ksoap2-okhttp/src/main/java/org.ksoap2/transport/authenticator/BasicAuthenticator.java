package org.ksoap2.transport.authenticator;

import okhttp3.*;

import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.UnsupportedCharsetException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A basic authenticator implementation of {@link Authenticator} interface to use with OkHttpTransport.
 */
public class BasicAuthenticator implements Authenticator {
    private static final String DEFAULT_CHARSET = "ISO-8859-1";
    private static Pattern charsetMatcher = Pattern.compile("(?i)charset=(?-i)\"([a-zA-Z0-9-]+)\"");

    private final String userName;
    private final String password;

    /**
     * @param userName User name
     * @param password Password
     */
    public BasicAuthenticator(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }

    /**
     * @throws IllegalCharsetNameException If the given charset name is illegal
     * @throws UnsupportedCharsetException If no support for the named charset is available
     *                                     in this instance of the Java virtual machine
     */
    @Override
    public Request authenticate(Route route, Response response) {
        String charset = DEFAULT_CHARSET;

        List<String> authHeaders = response.headers("WWW-Authenticate");
        if (authHeaders == null) {
            return null;
        }

        for (String authHeader : authHeaders) {
            if (!authHeader.startsWith("Basic")) {
                continue;
            }

            if (AuthenticatorHelper.responseCount(response) > 3) {
                throw new AuthenticatorException("Failed Basic Authentication.");
            }

            final Matcher matcher = charsetMatcher.matcher(authHeader);
            if (matcher.find()) {
                charset = matcher.group(1);
            }

            final String credential = Credentials.basic(userName, password, Charset.forName(charset));
            return response.request().newBuilder().header("Authorization", credential).build();
        }

        return null;
    }
}
