/* Copyright (c) 2003,2004, Stefan Haustein, Oberhausen, Rhld., Germany
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or
 * sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The  above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 * IN THE SOFTWARE. */

package org.ksoap2.servlet;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Hashtable;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ksoap2.SoapFault;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.kxml2.io.KXmlParser;
import org.kxml2.io.KXmlSerializer;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

/**
 * copy-paste seans interop server orb here as needed....
 * 
 * some design issues: - path and soapaction are not considered. soapaction is
 * deprecated; for multiple paths, please use multiple servlets.
 */

public class SoapServlet extends HttpServlet {

    SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapSerializationEnvelope.VER12);
    /** static mapping paths -> objects */
    Hashtable instanceMap = new Hashtable();

    /**
     * the default operation is to map request.getPathInfo to an instance using
     * the information given by buildInstance. The returned instance is used as
     * target object for the method invocation. Please overwrite this method in
     * order to define your own (generic) mapping. If no mapping is found, the
     * servlet itself is returned.
     */
    protected Object getInstance(HttpServletRequest request) {
        if (request.getPathInfo() == null) {
            return this;
        }
        Object result = instanceMap.get(request.getPathInfo());
        return (result != null) ? result : this;
    }

    /** Publish all public methods of the given class */
    public void publishClass(Class service, String namespace) {
        Method[] methods = service.getMethods();
        for (int i = 0; i < methods.length; i++) {
            if (Modifier.isPublic(methods[i].getModifiers())) {
                Class[] types = methods[i].getParameterTypes();
                PropertyInfo[] info = new PropertyInfo[types.length];
                for (int j = 0; j < types.length; j++) {
                    info[j] = new PropertyInfo();
                    info[j].type = types[j];
                }
                publishMethod(service, namespace, methods[i].getName(), info);
            }
        }
    }

    /**
     * publish an instance by associating the instance with the given local
     * path. Please note that (currently) also the methods need to be published
     * separateley. Alternatively to this call, it is also possible to overwrite
     * the getObject (HttpRequest request) method
     */
    public void publishInstance(String path, Object instance) {
        instanceMap.put(path, instance);
    }

    /**
     * publish a method. Please note that also a corresponding instance needs to
     * be published, either calling publishInstance or by overwriting
     * getInstance (), except when the method is a method of the servlet itself.
     */

    public void publishMethod(Class service, String namespace, String name, PropertyInfo[] parameters) {
        SoapObject template = new SoapObject(namespace, name);
        for (int i = 0; i < parameters.length; i++) {
            template.addProperty(parameters[i]);
        }
        envelope.addTemplate(template);
    }

    /**
     * convenience method; use this method if the paremeter types can be
     * obtained via reflection
     */
    public void publishMethod(Class service, String namespace, String name, String[] parameterNames) {
        // find a fitting method
        Method[] methods = service.getMethods();
        for (int i = 0; i < methods.length; i++) {
            if (methods[i].getName().equals(name) && methods[i].getParameterTypes().length == parameterNames.length) {
                Class[] types = methods[i].getParameterTypes();
                PropertyInfo[] info = new PropertyInfo[types.length];
                for (int j = 0; j < types.length; j++) {
                    info[j] = new PropertyInfo();
                    info[j].name = parameterNames[j];
                    info[j].type = types[j];
                }
                publishMethod(service, namespace, name, info);
                return;
            }
        }
        throw new RuntimeException("Method not found!");
    }

    public SoapSerializationEnvelope getEnvelope() {
        return envelope;
    }

    /**
     * Please note: The classMap should not be set after publishing methods,
     * because parameter type information may get lost!
     */
    public void setEnvelope(SoapSerializationEnvelope envelope) {
        this.envelope = envelope;
    }

    /**
     * In order to filter requests, please overwrite doPost and call super for
     * soap requests only
     */
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
      try {
        Object service = getInstance(req);
        XmlPullParser parser = new KXmlParser();
        if ( false ) {
          //parser.setInput(req.getInputStream(), req.getCharacterEncoding());
        }
        else {
          byte[] inputRequest = new byte[req.getInputStream().available()];
          req.getInputStream().read(inputRequest);
          System.out.println ("Request: " + new String(inputRequest));
          ByteArrayInputStream bas = new ByteArrayInputStream(inputRequest);
          parser.setInput(bas, null);
        }
        parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, true);
        envelope.parse(parser);
        SoapObject soapReq = (SoapObject) envelope.bodyIn;
        SoapObject result = invoke(service, soapReq);
        System.out.println("result: " + result);
        envelope.bodyOut = result;
      } catch (SoapFault f) {
        f.printStackTrace();
        envelope.bodyOut = f;
        res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
      } catch (Throwable t) {
        t.printStackTrace();
        SoapFault fault = new SoapFault();
        fault.faultcode = "Server";
        fault.faultstring = t.getMessage();
        envelope.bodyOut = fault;
        res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
      } finally {
        res.setContentType("text/xml; charset=utf-8");
        res.setHeader("Connection", "close");
        StringWriter sw = new StringWriter();
        XmlSerializer writer = new KXmlSerializer();
        writer.setOutput(sw);
        try {
          envelope.write(writer);
        } catch (Exception e) {
          e.printStackTrace();
        }
        writer.flush();
        System.out.println("result xml: " + sw);
        Writer w = res.getWriter();
        w.write(sw.toString());
        w.close();
      }
      res.flushBuffer();
    }

    protected SoapObject invoke(Object service, SoapObject soapReq)
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        String name = soapReq.getName();
        Class types[] = new Class[soapReq.getPropertyCount()];
        Object[] args = new Object[soapReq.getPropertyCount()];
        PropertyInfo arg = new PropertyInfo();
        Hashtable properties = new Hashtable();
        for (int i = 0; i < types.length; i++) {
            soapReq.getPropertyInfo(i, properties, arg);
            types[i] = (Class) arg.type;
            args[i] = soapReq.getProperty(i);
        }
        // expensive invocation here.. optimize with method cache,
        // want to support method overloading so need to figure in
        // the arg types..
        Method method = service.getClass().getMethod(name, types);
        Object result = method.invoke(service, args);
        System.out.println("result:" + result);
        SoapObject response = new SoapObject(soapReq.getNamespace(), name + "Response");
        if (result != null) {
            response.addProperty("return", result);
        }
        return response;
    }
}
