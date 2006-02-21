/* Copyright (c) 2006, James Seigel, Calgary, AB., Canada
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

package org.ksoap2.transport.mock;

import java.util.*;

import org.ksoap2.serialization.*;

public class ComplexResponse implements KvmSerializable {

    public String stringResponse;
    public long longResponse;
    public String namespace ="";

    public Object getProperty(int index) {
        if (index == 0)
            return stringResponse;
        else if (index == 1)
            return new Long(longResponse);
        else
            throw new RuntimeException("invalid parameter");
    }

    public int getPropertyCount() {
        return 2;
    }

    public void setProperty(int index, Object value) {
        if (index == 0 && value instanceof String)
            stringResponse = (String) value;
        else if (index == 1 && value instanceof Long)
            longResponse = ((Long) value).longValue();
        else 
            throw new RuntimeException("invalid parameter in set");
    }

    public void getPropertyInfo(int index, Hashtable properties, PropertyInfo info) {
        if (index == 0) {
            info.name = "stringResponse";
            info.type = PropertyInfo.STRING_CLASS;
        } else if (index == 1) {
            info.name = "longResponse";
            info.type = PropertyInfo.LONG_CLASS;
        } else {
            throw new RuntimeException("invalid parameter");
        }
        info.namespace = namespace;
    }

}
