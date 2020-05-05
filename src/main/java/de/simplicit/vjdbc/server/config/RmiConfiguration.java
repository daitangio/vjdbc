// VJDBC - Virtual JDBC
// Written by Michael Link
// Website: http://vjdbc.sourceforge.net

package de.simplicit.vjdbc.server.config;

import java.util.logging.Logger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;

@XmlAccessorType(XmlAccessType.FIELD)
public class RmiConfiguration {
    private static Logger _logger = Logger.getLogger(RmiConfiguration.class.getName());

    @XmlAttribute(name = "objectName")
    protected String _objectName = "VJdbc";
    @XmlAttribute(name = "registryPort")
    protected int _registryPort = 2000;
    @XmlAttribute(name = "remotingPort")
    protected int _remotingPort = 0;
    @XmlTransient
    protected boolean _createRegistry = true;
    @XmlTransient
    protected boolean _useSSL = false;
    @XmlTransient
    protected String _rmiClientSocketFactory = null;
    @XmlTransient
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
