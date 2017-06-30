// VJDBC - Virtual JDBC
// Written by Michael Link
// Website: http://vjdbc.sourceforge.net

package de.simplicit.vjdbc.server.config;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class RmiConfiguration {
    private static Log _logger = LogFactory.getLog(RmiConfiguration.class);

    protected String _objectName = "VJdbc";
    protected int _registryPort = 2000;
    protected int _remotingPort = 0;
    protected boolean _createRegistry = true;
    protected boolean _useSSL = false;
    protected String _rmiClientSocketFactory = null;
    protected String _rmiServerSocketFactory = null;

    public RmiConfiguration() {
    }

    public RmiConfiguration(String objectName) {
        _objectName = objectName;
    }

    public RmiConfiguration(String objectName, int port) {
        _objectName = objectName;
        _registryPort = port;
    }

    public String getObjectName() {
        return _objectName;
    }

    public void setObjectName(String objectName) {
        _objectName = objectName;
    }

    // Support method for old configuration format
    public int getPort() {
        return _registryPort;
    }

    // Support method for old configuration format
    public void setPort(int port) {
        _registryPort = port;
    }
    
    public int getRegistryPort() {
        return _registryPort;
    }

    public void setRegistryPort(int registryPort) {
        _registryPort = registryPort;
    }

    public int getRemotingPort() {
        return _remotingPort;
    }

    public void setRemotingPort(int listenerPort) {
        _remotingPort = listenerPort;
    }

    public boolean isCreateRegistry() {
        return _createRegistry;
    }

    public void setCreateRegistry(boolean createRegistry) {
        _createRegistry = createRegistry;
    }

    public boolean isUseSSL() {
        return _useSSL;
    }

    public void setUseSSL(boolean useSSL) {
        _useSSL = useSSL;
    }

    public String getRmiClientSocketFactory() {
        return _rmiClientSocketFactory;
    }

    public void setRmiClientSocketFactory(String rmiClientSocketFactory) {
        _rmiClientSocketFactory = rmiClientSocketFactory;
    }

    public String getRmiServerSocketFactory() {
        return _rmiServerSocketFactory;
    }

    public void setRmiServerSocketFactory(String rmiServerSocketFactory) {
        _rmiServerSocketFactory = rmiServerSocketFactory;
    }

    void log() {
        _logger.info("RMI-Configuration");
        _logger.info("  ObjectName ............... " + _objectName);
        _logger.info("  Registry-Port ............ " + _registryPort);
        if(_remotingPort > 0) {
            _logger.info("  Remoting-Port ............ " + _remotingPort);
        }
        _logger.info("  Create Registry .......... " + _createRegistry);
        _logger.info("  Use SSL .................. " + _useSSL);
        if(_rmiClientSocketFactory != null) {
            _logger.info("  Socket-Factory (client) .. " + _rmiClientSocketFactory);
        }
        if(_rmiServerSocketFactory != null) {
            _logger.info("  Socket-Factory (server) .. " + _rmiServerSocketFactory);
        }
    }
}
