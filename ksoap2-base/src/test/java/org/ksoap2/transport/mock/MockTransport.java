package org.ksoap2.transport.mock;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.transport.Transport;
import org.xmlpull.v1.XmlPullParserException;

// makes the parse more visible
public class MockTransport extends Transport
{
	public void parseResponse(SoapEnvelope envelope, InputStream is) throws XmlPullParserException,
			IOException
	{
		super.parseResponse(envelope, is);
	}

	/**
	 * Overriding method to make it public.
	 * 
	 * @see org.ksoap2.transport.Transport#createRequestData(org.ksoap2.SoapEnvelope)
	 */
	public byte[] createRequestData(SoapEnvelope envelope) throws IOException
	{
		return super.createRequestData(envelope);
	}

	public void call(String targetNamespace, SoapEnvelope envelope) throws IOException,
			XmlPullParserException
	{
		throw new RuntimeException("call not currently implemented");
	}

	/** Invoke - from SoapServlet. */
	public static SoapObject invoke(Object service, SoapObject soapReq) throws NoSuchMethodException,
			InvocationTargetException, IllegalAccessException
	{
		String name = soapReq.getName();
		Class types[] = new Class[soapReq.getPropertyCount()];
		Object[] args = new Object[soapReq.getPropertyCount()];
		PropertyInfo arg = new PropertyInfo();
		for (int i = 0; i < types.length; i++)
		{
			soapReq.getPropertyInfo(i, arg);
			types[i] = (Class) arg.type;
			args[i] = soapReq.getProperty(i);
		}
		// expensive invocation here.. optimize with method cache,
		// want to support method overloading so need to figure in
		// the arg types..
		Object result = null;
		try
		{
			Method method = service.getClass().getMethod(name, types);
			result = method.invoke(service, args);
		}
		catch (NoSuchMethodException nsme)
		{
			// since the properties do not match the required method calls when
			// attributes are present
			// we will also search for a call that takes a single "SoapObject"
			// as the input.
			Method method = service.getClass().getMethod(name, new Class[] { SoapObject.class });
			result = method.invoke(service, new Object[] { soapReq });
		}
		SoapObject response = new SoapObject(soapReq.getNamespace(), name + "Response");
		if (result != null)
			response.addProperty("return", result);
		return response;
	}
}