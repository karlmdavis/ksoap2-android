package org.ksoap2.transport.mock;

import java.io.*;

import org.ksoap2.*;
import org.ksoap2.transport.*;
import org.xmlpull.v1.*;

// makes the parse more visible
public class MockTransport extends Transport {
    public void parseResponse(SoapEnvelope envelope, InputStream is) throws XmlPullParserException, IOException {
        super.parseResponse(envelope, is);
    }

    public void call(String targetNamespace, SoapEnvelope envelope) throws IOException, XmlPullParserException {
        throw new RuntimeException("call not currently implemented");
    }
}