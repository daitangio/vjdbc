// VJDBC - Virtual JDBC
// Written by Michael Link
// Website: http://vjdbc.sourceforge.net

package de.simplicit.vjdbc.servlet;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

public class RelaxedHostnameVerifier implements HostnameVerifier {
    public boolean verify(String s, SSLSession sslSession) {
        return true;
    }
}
