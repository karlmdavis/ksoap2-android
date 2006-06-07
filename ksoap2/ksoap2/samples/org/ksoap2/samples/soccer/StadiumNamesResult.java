package org.ksoap2.samples.soccer;

import java.util.*;

import org.ksoap2.serialization.*;

public class StadiumNamesResult extends Vector implements KvmSerializable {

        public void register(SoapSerializationEnvelope envelope, String namespace, String name) {
            envelope.addMapping(namespace, name, this.getClass());
        }

        public void getPropertyInfo(int index, Hashtable properties, PropertyInfo info) {
            info.name = getItemDescriptor();
            info.type = PropertyInfo.STRING_CLASS;
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

        protected String getItemDescriptor() {
            return "string";
        }

}
