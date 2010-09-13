package drivers;

import org.ksoap2.serialization.SoapObject;


public class RequestBuilder {

     public static final String PHYSICIAN_GUID = "66AEBDEE-A139-403d-AEE4-B343A46167F4";
    public static final String MYSPECTRUM_GUID = "39083672-6d49-4705-8f5d-a63c492ac5be";


    public static final String URGENT_CARE_QUEUE_LENGTH_URL = "https://integration-wspublic.spectrum-health.org/ws/public/Spectrum.WebServices.Public.PublicWebService.cls/Spectrum.WebServices.Public.PublicWebService.cls";
    public static final String URGENT_CARE_QUEUE_LENGTH_NAMESPACE = "";
    public static final String URGENT_CARE_QUEUE_LENGTH_OPERATION = "GetCensusByUrgentCarePROD2";
    public static final String URGENT_CARE_QUEUE_LENGTH_SOAPACTION = "http://default.org/Spectrum.WebServices.Public.PublicWebService.GetCensusByUrgentCarePROD2";


    public static SoapObject queueLengthRequest() {
        return baseRequest(URGENT_CARE_QUEUE_LENGTH_NAMESPACE, URGENT_CARE_QUEUE_LENGTH_OPERATION);
    }

    private static SoapObject baseRequest(String namespace, String operation) {
        SoapObject request = new SoapObject(namespace, operation);
        request.addProperty("GUID", MYSPECTRUM_GUID);
        return request;
    }

}
