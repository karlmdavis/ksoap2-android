package org.ksoap2.samples.amazon.search.messages;

import java.util.*;

import org.ksoap2.serialization.*;

public abstract class LiteralArrayVector extends Vector implements KvmSerializable {

    public void register(SoapSerializationEnvelope envelope, String namespace, String name) {
        // using this.getClass() everywhere because .class doesn't 
        // exist on j2me
        envelope.addMapping(namespace, name, this.getClass());
        registerElementClass(envelope, namespace);
    }

    private void registerElementClass(SoapSerializationEnvelope envelope, String namespace) {
        final Class elementClass = getElementClass();
        try {
            if (elementClass.newInstance() instanceof KvmSerializable) {
                envelope.addMapping(namespace, "", elementClass);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getPropertyInfo(int index, Hashtable properties, PropertyInfo info) {
        info.name = getItemDescriptor();
        info.type = getElementClass();
    }

    public Object getProperty(int index) {
        return this;
    }

    public int getPropertyCount() {
        return 1;
    }

    public void setProperty(int index, Object value) {
        addElement(value);
    }

    abstract protected Class getElementClass();

    protected String getItemDescriptor() {
        return "item";
    }

}
