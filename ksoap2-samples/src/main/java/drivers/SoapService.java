package drivers;

import org.ksoap2.*;
import org.ksoap2.serialization.*;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

public class SoapService {

    public SoapSerializationEnvelope envelope(SoapObject request) {
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);
        envelope.dotNet = true;
        return envelope;
    }

    public HttpTransportSE transport(String url) {
        return new HttpTransportSE(url);
    }


    public SoapObject send(SoapObject request, String url, String transportOperation)
            throws IOException, XmlPullParserException {
        SoapSerializationEnvelope envelope = envelope(request);
        HttpTransportSE transport = transport(url);
        transport.debug = true;
        transport.call(transportOperation, envelope);
        System.out.println(transport.responseDump);
        System.out.println("-----");
        SoapObject answer = (SoapObject) envelope.getResponse();

        return answer;
    }



}
