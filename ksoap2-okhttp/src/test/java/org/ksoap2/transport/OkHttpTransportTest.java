package org.ksoap2.transport;

import okhttp3.Headers;
import org.junit.Test;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;

import static org.junit.Assert.*;
import org.junit.Ignore;

public class OkHttpTransportTest {

    @Ignore
    @Test
    public void testCallGlobalWeatherService() throws Throwable {
        OkHttpTransport transport =
                new OkHttpTransport.Builder("http://www.webservicex.net/globalweather.asmx")
                        .build();

        SoapObject request = new SoapObject("http://www.webserviceX.NET", "GetWeather");
        request.addProperty(getStringPropertyInfoEnvelope("CountryName", "Turkey"));
        request.addProperty(getStringPropertyInfoEnvelope("CityName", "Ankara"));

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);

        Headers responseHeaders = transport.call("http://www.webserviceX.NET/GetWeather", envelope);

        assertNotNull(responseHeaders);

        SoapPrimitive response = (SoapPrimitive) envelope.getResponse();

        assertNotNull(response);

        assertTrue(response.getValue().toString().length() > 0);
    }

    @Ignore
    @Test(expected = HttpResponseException.class)
    public void testCallGlobalWeatherServiceForFault() throws Throwable {
        OkHttpTransport transport =
                new OkHttpTransport.Builder("http://www.webservicex.net/globalweather.asmx")
                        .build();

        SoapObject request = new SoapObject("http://www.webserviceX.NET", "GetWeather");
        // Missing values should generate a SoapFault with 500 status

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);

        try {
            transport.call("http://www.webserviceX.NET/GetWeather", envelope);
        } catch (HttpResponseException e) {
            assertNotNull(envelope.bodyIn);
            assertTrue(envelope.bodyIn instanceof SoapFault);

            throw e;
        }
    }

    private PropertyInfo getStringPropertyInfoEnvelope(String key, String value) {
        PropertyInfo pi = new PropertyInfo();
        pi.setName(key);
        pi.setValue(value);
        pi.setType(value.getClass());

        return pi;
    }
}
