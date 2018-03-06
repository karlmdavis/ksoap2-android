package org.ksoap2.transport.authenticator;

import okhttp3.Response;

class AuthenticatorHelper {
    static int responseCount(Response response) {
        int result = 1;
        while ((response = response.priorResponse()) != null) {
            result++;
        }

        return result;
    }
}
