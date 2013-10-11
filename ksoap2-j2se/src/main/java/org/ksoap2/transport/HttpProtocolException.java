package org.ksoap2.transport;

import java.io.IOException;

/**
 * Created by rui on 10/11/13.
 */
public class HttpProtocolException extends IOException {

    private int statusCode;

    public HttpProtocolException(int statusCode) {
        super();
        this.statusCode = statusCode;
    }

    public HttpProtocolException(String detailMessage, int statusCode) {
        super(detailMessage);
        this.statusCode = statusCode;
    }

    public HttpProtocolException(String message, Throwable cause, int statusCode) {
        super(message, cause);
        this.statusCode = statusCode;
    }

    public HttpProtocolException(Throwable cause, int statusCode) {
        super(cause);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
