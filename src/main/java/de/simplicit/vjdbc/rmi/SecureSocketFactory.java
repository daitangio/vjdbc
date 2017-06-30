// VJDBC - Virtual JDBC
// Written by Michael Link
// Website: http://vjdbc.sourceforge.net

package de.simplicit.vjdbc.rmi;

import javax.net.ServerSocketFactory;
import javax.net.SocketFactory;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocketFactory;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.server.RMISocketFactory;

public class SecureSocketFactory extends RMISocketFactory {
    private SocketFactory _sslSocketFactory = SSLSocketFactory.getDefault();
    private ServerSocketFactory _sslServerSocketFactory = SSLServerSocketFactory.getDefault();

    public Socket createSocket(String host, int port)
            throws IOException {
        return _sslSocketFactory.createSocket(host, port);
    }

    public ServerSocket createServerSocket(int port)
            throws IOException {
        return _sslServerSocketFactory.createServerSocket(port);
    }
}
