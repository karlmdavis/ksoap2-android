package org.ksoap2;

/**
 * HeaderProperty is a key - value pojo for storing http header properties.
 */
public class HeaderProperty {
    private String key;
    private String value;

    public HeaderProperty(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }
    public void setKey(String key) {
        this.key = key;
    }
    public String getValue() {
        return value;
    }
    public void setValue(String value) {
        this.value = value;
    }
}
