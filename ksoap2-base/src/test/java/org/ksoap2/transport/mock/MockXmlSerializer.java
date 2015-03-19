package org.ksoap2.transport.mock;

import java.io.*;

import org.xmlpull.v1.*;

public class MockXmlSerializer implements XmlSerializer {

    public static final String PREFIX = "PREFIX";
    public String outputText = "";
    public String propertyType = "";
    public String endtag = "";
    public String startTag = "";

    public XmlSerializer attribute(String arg0, String label, String type)
            throws IOException, IllegalArgumentException, IllegalStateException {
        propertyType += (type + ";");
        return this;
    }

    public void cdsect(String arg0) throws IOException, IllegalArgumentException, IllegalStateException {
        throw new UnsupportedOperationException("MockXmlSerializer.cdsect is not implemented yet");
    }

    public void comment(String arg0) throws IOException, IllegalArgumentException, IllegalStateException {
        throw new UnsupportedOperationException("MockXmlSerializer.comment is not implemented yet");
    }

    public void docdecl(String arg0) throws IOException, IllegalArgumentException, IllegalStateException {
        throw new UnsupportedOperationException("MockXmlSerializer.docdecl is not implemented yet");
    }

    public void endDocument() throws IOException, IllegalArgumentException, IllegalStateException {
        throw new UnsupportedOperationException("MockXmlSerializer.endDocument is not implemented yet");
    }

    public XmlSerializer endTag(String arg0, String tag)
            throws IOException, IllegalArgumentException, IllegalStateException {
        endtag += (tag + ";");
        return this;
    }

    public void entityRef(String arg0) throws IOException, IllegalArgumentException, IllegalStateException {
        throw new UnsupportedOperationException("MockXmlSerializer.entityRef is not implemented yet");
    }

    public void flush() throws IOException {
        throw new UnsupportedOperationException("MockXmlSerializer.flush is not implemented yet");
    }

    public int getDepth() {
        throw new UnsupportedOperationException("MockXmlSerializer.getDepth is not implemented yet");
    }

    public boolean getFeature(String arg0) {
        throw new UnsupportedOperationException("MockXmlSerializer.getFeature is not implemented yet");
    }

    public String getName() {
        throw new UnsupportedOperationException("MockXmlSerializer.getName is not implemented yet");
    }

    public String getNamespace() {
        throw new UnsupportedOperationException("MockXmlSerializer.getNamespace is not implemented yet");
    }

    public String getPrefix(String arg0, boolean arg1) throws IllegalArgumentException {
        return PREFIX;
    }

    public Object getProperty(String arg0) {
        throw new UnsupportedOperationException("MockXmlSerializer.getProperty is not implemented yet");
    }

    public void ignorableWhitespace(String arg0)
            throws IOException, IllegalArgumentException, IllegalStateException {
        throw new UnsupportedOperationException("MockXmlSerializer.ignorableWhitespace is not implemented yet");
    }

    public void processingInstruction(String arg0)
            throws IOException, IllegalArgumentException, IllegalStateException {
        throw new UnsupportedOperationException("MockXmlSerializer.processingInstruction is not implemented yet");
    }

    public void setFeature(String arg0, boolean arg1) throws IllegalArgumentException, IllegalStateException {
        throw new UnsupportedOperationException("MockXmlSerializer.setFeature is not implemented yet");
    }

    public void setOutput(Writer arg0) throws IOException, IllegalArgumentException, IllegalStateException {
        throw new UnsupportedOperationException("MockXmlSerializer.setOutput is not implemented yet");
    }

    public void setOutput(OutputStream arg0, String arg1)
            throws IOException, IllegalArgumentException, IllegalStateException {
        throw new UnsupportedOperationException("MockXmlSerializer.setOutput is not implemented yet");
    }

    public void setPrefix(String arg0, String arg1)
            throws IOException, IllegalArgumentException, IllegalStateException {
        throw new UnsupportedOperationException("MockXmlSerializer.setPrefix is not implemented yet");
    }

    public void setProperty(String arg0, Object arg1) throws IllegalArgumentException, IllegalStateException {
        throw new UnsupportedOperationException("MockXmlSerializer.setProperty is not implemented yet");
    }

    public void startDocument(String arg0, Boolean arg1)
            throws IOException, IllegalArgumentException, IllegalStateException {
        throw new UnsupportedOperationException("MockXmlSerializer.startDocument is not implemented yet");
    }

    public XmlSerializer startTag(String arg0, String key)
            throws IOException, IllegalArgumentException, IllegalStateException {
        startTag += (key + ";");
        return this;
    }

    public XmlSerializer text(String text) throws IOException, IllegalArgumentException, IllegalStateException {
        outputText += (text + ";");
        return this;
    }

    public XmlSerializer text(char[] arg0, int arg1, int arg2)
            throws IOException, IllegalArgumentException, IllegalStateException {
        throw new UnsupportedOperationException("MockXmlSerializer.text is not implemented yet");
    }

    public String getOutputText() {
        return outputText.substring(0, outputText.length() - 1);
    }

    public String getEndtag() {
        return endtag.substring(0, endtag.length() - 1);
    }

    public String getPropertyType() {
        return propertyType.substring(0, propertyType.length() - 1);
    }

    public String getStartTag() {
        return startTag.substring(0, startTag.length() - 1);
    }

}
