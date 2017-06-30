
package de.simplicit.vjdbc.test.junit.general;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class CustomSocket extends Socket {
    private CustomRmiSocketFactory _customRmiClientSocketFactory;
    private int _clientPort;
    
    CustomSocket(CustomRmiSocketFactory factory, String host, int port, InetAddress inet, int clientPort) throws UnknownHostException, IOException {
        super(host, port, inet, clientPort);
        _customRmiClientSocketFactory = factory;
        _clientPort = clientPort;
    }
    
    public synchronized void close() throws IOException {
        super.close();
        _customRmiClientSocketFactory.giveBack(_clientPort);
    }
}
