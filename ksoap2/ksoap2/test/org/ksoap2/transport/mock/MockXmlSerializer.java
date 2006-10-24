package org.ksoap2.transport.mock;

import java.io.*;

import org.xmlpull.v1.*;

public class MockXmlSerializer implements XmlSerializer {

    public String outputText;

    public XmlSerializer attribute(String arg0, String arg1, String arg2) throws IOException, IllegalArgumentException, IllegalStateException {
        throw new UnsupportedOperationException("MockXmlSerializer.attribute is not implemented yet");
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

    public XmlSerializer endTag(String arg0, String arg1) throws IOException, IllegalArgumentException, IllegalStateException {
        throw new UnsupportedOperationException("MockXmlSerializer.endTag is not implemented yet");
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
        throw new UnsupportedOperationException("MockXmlSerializer.getPrefix is not implemented yet");
    }

    public Object getProperty(String arg0) {
        throw new UnsupportedOperationException("MockXmlSerializer.getProperty is not implemented yet");
    }

    public void ignorableWhitespace(String arg0) throws IOException, IllegalArgumentException, IllegalStateException {
        throw new UnsupportedOperationException("MockXmlSerializer.ignorableWhitespace is not implemented yet");
    }

    public void processingInstruction(String arg0) throws IOException, IllegalArgumentException, IllegalStateException {
        throw new UnsupportedOperationException("MockXmlSerializer.processingInstruction is not implemented yet");
    }

    public void setFeature(String arg0, boolean arg1) throws IllegalArgumentException, IllegalStateException {
        throw new UnsupportedOperationException("MockXmlSerializer.setFeature is not implemented yet");
    }

    public void setOutput(Writer arg0) throws IOException, IllegalArgumentException, IllegalStateException {
        throw new UnsupportedOperationException("MockXmlSerializer.setOutput is not implemented yet");
    }

    public void setOutput(OutputStream arg0, String arg1) throws IOException, IllegalArgumentException, IllegalStateException {
        throw new UnsupportedOperationException("MockXmlSerializer.setOutput is not implemented yet");
    }

    public void setPrefix(String arg0, String arg1) throws IOException, IllegalArgumentException, IllegalStateException {
        throw new UnsupportedOperationException("MockXmlSerializer.setPrefix is not implemented yet");
    }

    public void setProperty(String arg0, Object arg1) throws IllegalArgumentException, IllegalStateException {
        throw new UnsupportedOperationException("MockXmlSerializer.setProperty is not implemented yet");
    }

    public void startDocument(String arg0, Boolean arg1) throws IOException, IllegalArgumentException, IllegalStateException {
        throw new UnsupportedOperationException("MockXmlSerializer.startDocument is not implemented yet");
    }

    public XmlSerializer startTag(String arg0, String arg1) throws IOException, IllegalArgumentException, IllegalStateException {
        throw new UnsupportedOperationException("MockXmlSerializer.startTag is not implemented yet");
    }

    public XmlSerializer text(String text) throws IOException, IllegalArgumentException, IllegalStateException {
        outputText = text;
        return this;
    }

    public XmlSerializer text(char[] arg0, int arg1, int arg2) throws IOException, IllegalArgumentException, IllegalStateException {
        throw new UnsupportedOperationException("MockXmlSerializer.text is not implemented yet");
    }

}
