package org.ksoap2.serialization;

/** 
 * A class that is used to encapsulate primitive types
 *  (represented by a string in XML serialization).
 *
 * Basically, the SoapPrimitive class encapsulates "unknown"
 * primitive types (similar to SoapObject encapsulating unknown
 * complex types). For example, new SoapPrimitive (classMap.xsd,
 * "float", "12.3") allows you to send a float from a MIDP device to
 * a server although MIDP does not support floats.  In the other
 * direction, kSOAP will deserialize any primitive type (=no
 * subelements) that are not recognized by the ClassMap to
 * SoapPrimitive, preserving the namespace, name and string value
 * (this is how the stockquote example works).  */

public class SoapPrimitive {

    String namespace;
    String name;
    String value;

    public SoapPrimitive(String namespace, String name, String value) {
        this.namespace = namespace;
        this.name = name;
        this.value = value;
    }

    public boolean equals(Object o) {
        if (!(o instanceof SoapPrimitive))
            return false;

        SoapPrimitive p = (SoapPrimitive) o;

        return name.equals(p.name)
            && namespace.equals(p.namespace)
            && (value == null ? (p.value == null) : value.equals(p.value));
    }

    public int hashCode() {
        return name.hashCode() ^ namespace.hashCode();
    }

    public String toString() {
        return value;
    }

    public String getNamespace() {
        return namespace;
    }

    public String getName() {
        return name;
    }

}
