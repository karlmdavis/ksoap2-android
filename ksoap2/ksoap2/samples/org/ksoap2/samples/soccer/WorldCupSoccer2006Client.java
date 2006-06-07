package org.ksoap2.samples.soccer;

import org.ksoap2.*;
import org.ksoap2.serialization.*;
import org.ksoap2.transport.*;

public class WorldCupSoccer2006Client {
    private static final String NAMESPACE = "http://www.dataaccess.nl/wk2006";
    private static final String URL = "http://www.dataaccess.nl/wk2006/footballpoolwebservice.wso";

    WorldCupSoccer2006Client() {
        SoapObject request = new SoapObject(NAMESPACE, "StadiumNames");
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);
        
        mapClasses(envelope);
        HttpTransportSE httpTransportSE = new HttpTransportSE(URL);
        try {
            httpTransportSE.call("StadiumNames", envelope);
            StadiumNamesResult response = (StadiumNamesResult) envelope.getResponse();
            for (int i = 0; i < response.size(); i++) {
                System.out.println(response.elementAt(i));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void mapClasses(SoapSerializationEnvelope envelope) {
        PropertyInfo info = new PropertyInfo();
        info.name="StadiumNamesResult";
        info.type=new StadiumNamesResult().getClass();
        SoapObject template = new SoapObject(NAMESPACE,"StadiumNamesResponse");
        template.addProperty(info, "");
        envelope.addTemplate(template);
        new StadiumNamesResult().register(envelope, NAMESPACE, "StadiumNamesResult");
    }

    public static void main(String[] args) {
        new WorldCupSoccer2006Client();
    }

}
