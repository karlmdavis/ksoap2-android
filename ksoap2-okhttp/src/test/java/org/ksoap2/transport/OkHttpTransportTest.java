package org.ksoap2.transport;

import okhttp3.Headers;
import org.junit.Test;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.OkHttpTransport;

import static org.junit.Assert.*;

public class OkHttpTransportTest {

    @Test
    public void testCallGlobalWeatherService() throws Throwable {
        OkHttpTransport transport =
                new OkHttpTransport.Builder("http://www.webservicex.net/globalweather.asmx")
                        .build();

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);

        envelope.dotNet = true;

        SoapObject request = new SoapObject("http://www.webserviceX.NET", "GetWeather");
        request.addProperty(getStringPropertyInfoEnvelope("CountryName", "Turkey"));
        request.addProperty(getStringPropertyInfoEnvelope("CityName", "Ankara"));

        envelope.setOutputSoapObject(request);

        Headers responseHeaders = transport.call("http://www.webserviceX.NET/GetWeather", envelope, null);

        assertNotNull(responseHeaders);

        SoapPrimitive response = (SoapPrimitive) envelope.getResponse();

        assertNotNull(response);

        assertTrue(response.getValue().toString().length() > 0);
    }

    private PropertyInfo getStringPropertyInfoEnvelope(String key, String value) {
        PropertyInfo pi = new PropertyInfo();
        pi.setName(key);
        pi.setValue(value);
        pi.setType(value.getClass());

        return pi;
    }
}
