package org.ksoap2.transport.authenticator;

import okhttp3.*;

import java.io.IOException;
import java.util.List;

/**
 * A ntlm authenticator implementation of {@link Authenticator} interface to use with OkHttpTransport.
 */
public class NtlmAuthenticator implements Authenticator {
    private final String userName;
    private final String password;
    private final String ntDomain;
    private final String ntWorkstation;

    /**
     * @param userName      User name
     * @param password      Password
     * @param ntDomain      Domain
     * @param ntWorkstation Workstation
     */
    public NtlmAuthenticator(final String userName, final String password,
                             final String ntDomain, final String ntWorkstation) {
        this.userName = userName;
        this.password = password;
        this.ntDomain = ntDomain;
        this.ntWorkstation = ntWorkstation;
    }

    @Override
    public Request authenticate(final Route route, final Response response) throws IOException {
        final List<String> authHeaders = response.headers("WWW-Authenticate");
        if (authHeaders == null) {
            return null;
        }

        boolean negotiate = false;
        for (String authHeader : authHeaders) {
            if (authHeader.equalsIgnoreCase("Negotiate")) {
                negotiate = true;
            } else if (negotiate && authHeader.equalsIgnoreCase("NTLM")) {
                if (AuthenticatorHelper.responseCount(response) > 3) {
                    throw new AuthenticatorException("Failed NTLM Authentication.");
                }

                final String type1Msg = JCIFSEngine.generateType1Msg(ntDomain, ntWorkstation);
                return response.request().newBuilder().header("Authorization", "NTLM " + type1Msg).build();
            } else if (authHeader.startsWith("NTLM ")) {
                final String challenge = authHeader.substring(5);
                final String type3Msg = JCIFSEngine.generateType3Msg(
                        userName, password, ntDomain, ntWorkstation, challenge);
                return response.request().newBuilder().header("Authorization", "NTLM " + type3Msg).build();
            }
        }

        return null;
    }

}
