package org.ksoap2.samples.amazon.search.messages;

import org.ksoap2.serialization.*;

public abstract class BaseObject implements KvmSerializable {

    protected static final String NAMESPACE = "http://webservices.amazon.com/AWSECommerceService/2006-05-17";

    public BaseObject() {
        super();
    }

}