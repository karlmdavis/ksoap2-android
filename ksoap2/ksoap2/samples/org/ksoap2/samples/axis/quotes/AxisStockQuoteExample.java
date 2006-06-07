package org.ksoap2.samples.axis.quotes;

import org.ksoap2.*;
import org.ksoap2.serialization.*;
import org.ksoap2.transport.*;
/*
 * Example that uses one of the built in axis .jws examples
 * Install apache tomcat and the default version of axis and 
 * start the server verify you have all of the needed .jars
 * installed correctly and you should be able to run this 
 * Example.
 */
public class AxisStockQuoteExample {

    public AxisStockQuoteExample() {
        // Create the outgoing message
        SoapObject requestObject = new SoapObject("x", "getQuote");
        // ask for the specially encoded symbol in the included service
        requestObject.addProperty("symbol", "XXX");

        // use version 1.1 of soap
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        // add the outgoing object as the request
        envelope.setOutputSoapObject(requestObject);
        new MarshalFloat().register(envelope); // not really needed for j2se version

        // Create a transport layer for the J2SE platform. You should change this for
        // another transport on midp or j2me devices.
        HttpTransportSE transportSE = new HttpTransportSE("http://localhost:8080/axis/StockQuoteService.jws");
        // turn on debug mode if you want to see what is happening over the wire.
        transportSE.debug  = true;
        try {
            // call and print out the result
            transportSE.call("getQuote", envelope);
            System.out.println(envelope.getResponse());
        } catch (Exception e) {
            // if we get an error print the stacktrace and dump the response out.
            e.printStackTrace();
            System.out.println(transportSE.responseDump);
        }
    }

    public static void main(String[] args) {
        new AxisStockQuoteExample();
    }

}
