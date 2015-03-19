package org.ksoap2.samples.soccer;

import org.ksoap2.*;
import org.ksoap2.serialization.*;
import org.ksoap2.transport.*;

public class WorldCupSoccer2006Client {
    private static final String SOAP_ACTION = "";
    private static final String METHOD_NAME = "StadiumNames";
    private static final String NAMESPACE = "http://www.dataaccess.nl/wk2006";
    private static final String URL = "http://www.dataaccess.nl/wk2006/footballpoolwebservice.wso";

    WorldCupSoccer2006Client() {
        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);

        addClassMappings(envelope);
        // again this example is designed to run with j2se.  Just change the transport
        // layer to make it work with j2me
        HttpTransportSE httpTransportSE = new HttpTransportSE(URL);
        try {
            httpTransportSE.call(SOAP_ACTION, envelope);

            // Show the elements of the resultant vector.
            StadiumNamesResult response = (StadiumNamesResult) envelope.getResponse();
            for (int i = 0; i < response.size(); i++) {
                System.out.println(response.elementAt(i));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addClassMappings(SoapSerializationEnvelope envelope) {
        createWrappingResultTemplate(envelope);
        new StadiumNamesResult().register(envelope, NAMESPACE, "StadiumNamesResult");
    }

    private void createWrappingResultTemplate(SoapSerializationEnvelope envelope) {
        // tell ksoap what type of attribute the Resulting Object as.
        PropertyInfo info = new PropertyInfo();
        info.name = "StadiumNamesResult";
        info.type = new StadiumNamesResult().getClass();

        // Demonstration of using a template to describe what the result will
        // look like. KSOAP will actually clone this object and fill it with
        // the resulting information.
        // You could use a real object here as well, your choice.
        // However, If you fail to tell ksoap what to 
        // expect however it will not try to map any
        // contained objects to their classes.

        SoapObject template = new SoapObject(NAMESPACE, "StadiumNamesResponse");
        template.addProperty(info);

        envelope.addTemplate(template);
    }

    public static void main(String[] args) {
        new WorldCupSoccer2006Client();
    }

}
