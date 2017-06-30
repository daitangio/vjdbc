// VJDBC - Virtual JDBC
// Written by Michael Link
// Website: http://vjdbc.sourceforge.net

package de.simplicit.vjdbc.server.rmi;

import de.simplicit.vjdbc.rmi.SecureSocketFactory;
import de.simplicit.vjdbc.server.config.RmiConfiguration;
import de.simplicit.vjdbc.server.config.VJdbcConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.RMISocketFactory;
import java.util.Properties;

public class ConnectionServer {
    private static Log _logger = LogFactory.getLog(ConnectionServer.class);

    private RmiConfiguration _rmiConfiguration;
    private Registry _registry;

    public static void main(String[] args) {
        try {
            if(args.length == 1) {
                VJdbcConfiguration.init(args[0]);
            } else if(args.length == 2) {
                // Second argument is a properties file with variables that are
                // replaced by Digester when the configuration is read in
                Properties props = new Properties();
                FileInputStream propsInputStream = null;
                try {
                    propsInputStream = new FileInputStream(args[1]);
                    props.load(propsInputStream);
                    VJdbcConfiguration.init(args[0], props);
                } finally {
                    if(propsInputStream != null) {
                        propsInputStream.close();
                    }
                }
            } else {
                throw new RuntimeException("You must specify a configuration file as the first parameter");
            }

            ConnectionServer connectionServer = new ConnectionServer();
            connectionServer.serve();
        } catch (Throwable e) {
            _logger.error(e.getMessage(), e);
        }
    }

    public ConnectionServer() {
    }

    public void serve() throws IOException {
        _rmiConfiguration = VJdbcConfiguration.singleton().getRmiConfiguration();

        if(_rmiConfiguration == null) {
            _logger.debug("No RMI-Configuration specified in VJdbc-Configuration, using default configuration");
            _rmiConfiguration = new RmiConfiguration();
        }

        if(_rmiConfiguration.isUseSSL()) {
            _logger.info("Using SSL sockets for communication");
            RMISocketFactory.setSocketFactory(new SecureSocketFactory());
        }

        if(_rmiConfiguration.isCreateRegistry()) {
            _logger.info("Starting RMI-Registry on port " + _rmiConfiguration.getPort());
            _registry = LocateRegistry.createRegistry(_rmiConfiguration.getPort());
        } else {
            _logger.info("Using RMI-Registry on port " + _rmiConfiguration.getPort());
            _registry = LocateRegistry.getRegistry(_rmiConfiguration.getPort());
        }

        installShutdownHook();

        _logger.info("Binding remote object to '" + _rmiConfiguration.getObjectName() + "'");
        _registry.rebind(_rmiConfiguration.getObjectName(), new ConnectionBrokerRmiImpl(_rmiConfiguration.getRemotingPort()));
    }

    private void installShutdownHook() {
        // Install the shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            public void run() {
                try {
                    _logger.info("Unbinding remote object");
                    _registry.unbind(_rmiConfiguration.getObjectName());
                } catch (RemoteException e) {
                    _logger.error("Remote exception", e);
                } catch (NotBoundException e) {
                    _logger.error("Not bound exception", e);
                }
            }
        }));
    }
}
