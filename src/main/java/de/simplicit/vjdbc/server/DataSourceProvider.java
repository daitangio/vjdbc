// VJDBC - Virtual JDBC
// Written by Michael Link
// Website: http://vjdbc.sourceforge.net

package de.simplicit.vjdbc.server;

import javax.sql.DataSource;
import java.sql.SQLException;

/**
 * To use the DataSource-API with VJDBC a class must be provided
 * that implements the <code>DataSourceProvider</code> interface. 
 */
public interface DataSourceProvider {
    /**
     * Retrieves a DataSource object from the DataSourceProvider. This
     * will be used to create the JDBC connections.
     * @return DataSource to be used for creating the connections
     */
    DataSource getDataSource() throws SQLException;
}
