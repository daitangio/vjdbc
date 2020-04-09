// VJDBC - Virtual JDBC
// Written by Michael Link
// Website: http://vjdbc.sourceforge.net

package de.simplicit.vjdbc.server.rmi;

import de.simplicit.vjdbc.rmi.SecureSocketFactory;
import de.simplicit.vjdbc.server.config.RmiConfiguration;
import de.simplicit.vjdbc.server.config.VJdbcConfiguration;
import java.io.File;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.RMISocketFactory;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

public class ConnectionServer {
	private static Logger _logger = Logger.getLogger(ConnectionServer.class.getName());

	private RmiConfiguration _rmiConfiguration;
	private Registry _registry;

	/**
	 * Main Entry point of RMI Server
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			//BasicConfigurator.configure();
			if(args.length == 1) {
                JAXBContext jaxbContext = JAXBContext.newInstance(VJdbcConfiguration.class);
                Unmarshaller vjdbcUnmarshaller = jaxbContext.createUnmarshaller();
                VJdbcConfiguration conf = (VJdbcConfiguration)vjdbcUnmarshaller.unmarshal(new File(args[0]));
                VJdbcConfiguration.set(conf);
			} else {
				throw new RuntimeException("You must specify a configuration file as the first parameter");
			}

			ConnectionServer connectionServer = new ConnectionServer();
			connectionServer.serve();
		} catch (Throwable e) {
			_logger.severe(e.getMessage());
            e.printStackTrace();
		}
	}

	public ConnectionServer() {
	}

	public void serve() throws IOException {
		_rmiConfiguration = VJdbcConfiguration.singleton().getRmi();

		if(_rmiConfiguration == null) {
			_logger.fine("No RMI-Configuration specified in VJdbc-Configuration, using default configuration");
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
		_logger.info("Server Started on port "+_rmiConfiguration.getPort());
	}

	private void installShutdownHook() {
		// Install the shutdown hook
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			public void run() {
				try {
					_logger.info("Unbinding remote object");
					_registry.unbind(_rmiConfiguration.getObjectName());
				} catch (RemoteException e) {
					_logger.severe("Remote exception");
                    e.printStackTrace();
				} catch (NotBoundException e) {
					_logger.severe("Not bound exception");
                    e.printStackTrace();
				}
			}
		}));
	}
}
