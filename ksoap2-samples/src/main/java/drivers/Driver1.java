package drivers;

import org.ksoap2.serialization.SoapObject;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;


public class Driver1
{
    private SoapService service = new SoapService();

    private void runUC ()  {

        SoapObject request = RequestBuilder.queueLengthRequest();
        SoapObject response = null;
        try {
            response = service.send(request, RequestBuilder.URGENT_CARE_QUEUE_LENGTH_URL, RequestBuilder.URGENT_CARE_QUEUE_LENGTH_SOAPACTION);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }
        System.out.println(response);

    }

    public static void main(String[] args)  {
        new Driver1().runUC();
    }
    



}
