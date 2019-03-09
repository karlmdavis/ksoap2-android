package org.ksoap2.transport;

import java.io.IOException;
import java.util.List;

/**
 * HttpResponseException is an IOException that is to be thrown when a Http response code is different from 200.
 * It allows for easier retrieval of the Http response code from the connection.
 *
 * @author Rui Pereira <syshex@gmail.com>
 */
public class HttpResponseException extends IOException {

    private int statusCode;
    private List responseHeaders;

    public HttpResponseException(int statusCode) {
        super();
        this.statusCode = statusCode;
    }

    public HttpResponseException(String detailMessage, int statusCode) {
        super(detailMessage);
        this.statusCode = statusCode;
    }

    public HttpResponseException(String detailMessage, int statusCode,List responseHeaders) {
        super(detailMessage);
        this.statusCode = statusCode;
        this.responseHeaders=responseHeaders;
    }

    public HttpResponseException(String message, Throwable cause, int statusCode) {
        super(message, cause);
        this.statusCode = statusCode;
    }

    public HttpResponseException(Throwable cause, int statusCode) {
        super(cause);
        this.statusCode = statusCode;
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
    public List getResponseHeaders() {
        return responseHeaders;
    }
}
