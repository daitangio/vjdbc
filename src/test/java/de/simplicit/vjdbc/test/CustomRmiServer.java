// VJDBC - Virtual JDBC
// Written by Michael Link
// Website: http://vjdbc.sourceforge.net

package de.simplicit.vjdbc.test;

import de.simplicit.vjdbc.server.config.ConnectionConfiguration;
import de.simplicit.vjdbc.server.config.NamedQueryConfiguration;
import de.simplicit.vjdbc.server.config.RmiConfiguration;
import de.simplicit.vjdbc.server.config.VJdbcConfiguration;
import de.simplicit.vjdbc.server.rmi.ConnectionServer;

public class CustomRmiServer {
    public static void main(String[] args) {
        try {
            // Initialize VJDBC programmatically
            System.out.println("Initializing VJDBC");
            VJdbcConfiguration vjdbcConfig = new VJdbcConfiguration();
            vjdbcConfig.setRmiConfiguration(new RmiConfiguration());
            
            // Connection-Configuration for HSQL-DB
            ConnectionConfiguration configHSql = new ConnectionConfiguration();
            configHSql.setDriver("org.hsqldb.jdbcDriver");
            configHSql.setId("HSqlDB");
            configHSql.setUrl("jdbc:hsqldb:hsql://localhost/HSqlDb");
            configHSql.setUser("sa");
            configHSql.setPassword("");
            configHSql.setRowPacketSize(1000);
            configHSql.setConnectionPooling(true);
            
            NamedQueryConfiguration namedQueryConfiguration = new NamedQueryConfiguration();
            namedQueryConfiguration.getQueryMap().put("selectAllAddresses", "select * from Address");
            namedQueryConfiguration.getQueryMap().put("selectAddress", "select * from Address where Id = ?");
            namedQueryConfiguration.getQueryMap().put("updateAllAddresses", "update Address set lastname = 'Balla' where lastname = 'Billi'");

            configHSql.setNamedQueries(namedQueryConfiguration);
            vjdbcConfig.addConnection(configHSql);

            VJdbcConfiguration.init(vjdbcConfig);
            ConnectionServer server = new ConnectionServer();
            server.serve();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
