package de.simplicit.vjdbc.test.junit.general;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.server.RMISocketFactory;
import java.util.LinkedList;
import java.util.List;

public class CustomRmiSocketFactory extends RMISocketFactory {
    private List _availablePorts = new LinkedList();

    public CustomRmiSocketFactory(int from, int to) {
        if(from <= to) {
            for(int port = from; port <= to; ++port) {
                _availablePorts.add(new Integer(port));
            }
        } else {
            throw new IllegalArgumentException("Invalid Port-Range");
        }
    }

    public Socket createSocket(String host, int port) throws IOException {
        synchronized (this) {
            if(_availablePorts.size() > 0) {
                Integer usedPort = (Integer) _availablePorts.remove(0);
                InetAddress ip = InetAddress.getLocalHost();
                return new CustomSocket(this, host, port, ip, usedPort.intValue());
            } else {
                throw new IOException("No more ports available");
            }
        }
    }

    public ServerSocket createServerSocket(int port) throws IOException {
        return new ServerSocket(port);
    }

    public void giveBack(int port) {
        _availablePorts.add(new Integer(port));
    }
}
