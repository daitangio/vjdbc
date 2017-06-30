package de.simplicit.vjdbc.test.junit.hsqldb;

import java.io.IOException;

import org.apache.log4j.BasicConfigurator;

import de.simplicit.vjdbc.server.config.ConfigurationException;
import de.simplicit.vjdbc.server.config.ConnectionConfiguration;
import de.simplicit.vjdbc.server.config.VJdbcConfiguration;
import de.simplicit.vjdbc.server.rmi.ConnectionServer;

public class HSqlDbDynamicConfigurationServer {
    public static void main(String[] args) throws ConfigurationException, IOException {
        BasicConfigurator.configure();
        
        VJdbcConfiguration.init("test/vjdbc_config.xml");
        
        ConnectionConfiguration dynCfg = new ConnectionConfiguration();
        dynCfg.setId("HSqlDB2");
        dynCfg.setUrl("jdbc:hsqldb:hsql://localhost/HSqlDb");
        dynCfg.setDriver("org.hsqldb.jdbcDriver");
        dynCfg.setUser("sa");
        dynCfg.setPassword("");
        VJdbcConfiguration.singleton().addConnection(dynCfg);
        
        ConnectionServer server = new ConnectionServer();
        server.serve();
    }
}
