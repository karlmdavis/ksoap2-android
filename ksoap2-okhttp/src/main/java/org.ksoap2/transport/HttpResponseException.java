package org.ksoap2.transport;

import okhttp3.Headers;

import java.io.IOException;

/**
 * HttpResponseException is an IOException that is to be thrown when a Http response code is different from 200.
 * It allows for easier retrieval of the Http response code from the connection.
 *
 * @author Rui Pereira <syshex@gmail.com>
 */
public class HttpResponseException extends IOException {

    private int statusCode;
    private Headers responseHeaders;

    HttpResponseException(int statusCode) {
        this(null, statusCode);
    }

    HttpResponseException(String message, int statusCode) {
        this(message, statusCode, null);
    }

    HttpResponseException(String message, int statusCode, Headers responseHeaders) {
        super(message);
        this.statusCode = statusCode;
        this.responseHeaders = responseHeaders;
    }

    /**
     * Returns the unexpected Http response code
     *
     * @return response code
     */
    public int getStatusCode() {
        return statusCode;
    }

    /**
     * Returns all http headers from this response
     *
     * @return response code
     */
    public Headers getResponseHeaders() {
        return responseHeaders;
    }
}
